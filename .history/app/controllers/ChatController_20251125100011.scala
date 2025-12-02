package controllers

import javax.inject._
import play.api.mvc._
import play.api.libs.json._

import org.apache.pekko.stream.Materializer
import org.apache.pekko.stream.scaladsl._
import org.apache.pekko.stream.OverflowStrategy

import scala.collection.mutable
import scala.concurrent.ExecutionContext

@Singleton
class ChatController @Inject()(
    cc: ControllerComponents
  )(implicit
    mat: Materializer,
    ec: ExecutionContext
  ) extends AbstractController(cc) {

  // ==== Kleiner In-Memory "ChatHub" pro Session (Comet) ====

  private case class SessionQueues(
    queues: mutable.Set[SourceQueueWithComplete[String]]
  )

  // sessionId -> Queues
  private val sessions = mutable.Map.empty[String, SessionQueues]

  private def getOrCreate(sessionId: String): SessionQueues =
    sessions.getOrElseUpdate(sessionId, SessionQueues(mutable.Set.empty))

  /** Einen neuen Stream für eine Session registrieren (für einen Client) */
  private def register(sessionId: String): Source[String, _] = {
    val sq = getOrCreate(sessionId)

    val (queue, src) =
      Source
        .queue[String](bufferSize = 32, OverflowStrategy.dropHead)
        .preMaterialize() // gibt (Queue, Source)

    sq.queues += queue

    // Wenn der Client weg ist: Queue wieder entfernen
    src.watchTermination() { (_, done) =>
      done.onComplete { _ =>
        synchronized {
          sessions.get(sessionId).foreach { s =>
            s.queues -= queue
            if (s.queues.isEmpty) sessions.remove(sessionId)
          }
        }
      }
    }

    src
  }

  /** Nachricht an alle Clients in einer Session pushen */
  private def publish(sessionId: String, msg: String): Unit = synchronized {
    sessions.get(sessionId).foreach { s =>
      s.queues.foreach(_.offer(msg))
    }
  }

  // ==== Comet-Endpunkte ====

  /**
    * Langer HTTP-Stream für Comet:
    * Server sendet mehrere <script>parent.appendChat("...")</script>-Chunks.
    *
    * URL: /game/chat/comet?sessionId=...
    */
  def chatComet(sessionId: String): Action[AnyContent] = Action { implicit request =>
    val sid = Option(sessionId).filter(_.nonEmpty).getOrElse("default")

    val src: Source[String, _] = register(sid)

    // Jeden Chat-JSON-String in ein <script>...</script> packen
    val scripted: Source[String, _] = src.map { rawJson =>
      // Minimal HTML-escaping für JS-String
      val safe = rawJson
        .replace("\\", "\\\\")
        .replace("\"", "\\\"")
        .replace("\n", "\\n")
        .replace("\r", "")

      s"<script>parent.appendChat(\"$safe\");</script>\n"
    }

    Ok.chunked(scripted).as("text/html; charset=utf-8")
  }

  /**
    * Chat-Nachricht eines Clients:
    * POST /game/chat/send
    *
    * Body JSON: { "sessionId": "...", "playerName": "...", "text": "..." }
    */
  def chatSend: Action[JsValue] = Action(parse.json) { implicit request =>
    val json       = request.body
    val sessionId  = (json \ "sessionId").asOpt[String].filter(_.nonEmpty).getOrElse("default")
    val playerName = (json \ "playerName").asOpt[String].filter(_.nonEmpty).getOrElse("Player")
    val text       = (json \ "text").asOpt[String].map(_.trim).getOrElse("")

    if (text.nonEmpty) {
      // Das ist der Payload, der später bei appendChat(raw) im Browser ankommt
      val payload = Json.obj(
        "playerName" -> playerName,
        "text"       -> text,
        "sessionId"  -> sessionId
      ).toString()

      publish(sessionId, payload)
    }

    Ok(Json.obj("status" -> "ok"))
  }
}
