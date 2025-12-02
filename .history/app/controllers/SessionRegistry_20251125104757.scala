package backend

import play.api.libs.json._
import org.apache.pekko.stream.scaladsl.{Source, SourceQueueWithComplete}
import org.apache.pekko.stream.{Materializer, OverflowStrategy}

import scala.collection.mutable
import scala.concurrent.ExecutionContext

object SessionRegistry {

  // sessionId -> Set(playerName)
  private val sessions: mutable.Map[String, mutable.Set[String]] =
    mutable.Map.empty

  // alle SSE-Listener (jeder hat eine Queue für JSON-Strings)
  private val listeners: mutable.Set[SourceQueueWithComplete[String]] =
    mutable.Set.empty

  /** JSON-Snapshot aller Sessions + Spieler. */
  private def toJsonString(): String = synchronized {
    val sessList = sessions.toSeq.map { case (sid, players) =>
      Json.obj(
        "sessionId" -> sid,
        "players"   -> players.toSeq.sorted
      )
    }
    Json.obj(
      "activeSessionCount" -> sessions.size,
      "sessions"           -> sessList
    ).toString()
  }

  /** Spieler joint eine Session. */
  def join(sessionId: String, playerName: String): Unit = synchronized {
    val sid = if (sessionId.trim.isEmpty) "default" else sessionId.trim
    val name = if (playerName.trim.isEmpty) "Player" else playerName.trim

    val set = sessions.getOrElseUpdate(sid, mutable.Set.empty[String])
    set += name
    notifyListeners()
  }

  /** Spieler verlässt eine Session. */
  def leave(sessionId: String, playerName: String): Unit = synchronized {
    val sid = if (sessionId.trim.isEmpty) "default" else sessionId.trim
    val name = playerName.trim

    sessions.get(sid).foreach { set =>
      if (name.nonEmpty) set -= name
      if (set.isEmpty) sessions.remove(sid)
    }
    notifyListeners()
  }

  /** Alle Listener mit aktuellem Snapshot updaten. */
  private def notifyListeners(): Unit = {
    val json = toJsonString()
    listeners.foreach(_.offer(json))
  }

  /**
    * Einen neuen SSE-Listener registrieren.
    * Gibt einen Source[String] zurück, aus dem der Controller
    * "data: ...\n\n" Events macht.
    */
  def registerListener()(implicit mat: Materializer, ec: ExecutionContext): Source[String, _] = {
    val (queue, src) =
      Source.queue[String](bufferSize = 32, OverflowStrategy.dropHead).preMaterialize()

    synchronized {
      listeners += queue
    }

    // Initialen Snapshot sofort senden
    queue.offer(toJsonString())

    // Wenn der Client abbricht, Queue wieder entfernen
    src.watchTermination() { (_, done) =>
      done.onComplete { _ =>
        synchronized {
          listeners -= queue
        }
      }
    }

    src
  }
}

