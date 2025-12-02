package controllers

import org.apache.pekko.actor._
import play.api.libs.json._

/**
  * Ein Actor pro WebSocket-Verbindung.
  * Das Companion-Object verwaltet alle Verbindungen samt Session-IDs,
  * sodass wir Broadcasts pro Session machen können.
  */
object GameWebSocketActor {

  def props(out: ActorRef): Props = Props(new GameWebSocketActor(out))

  private case class Conn(ref: ActorRef, var sessionId: Option[String])

  // globale Liste aller verbundenen Clients
  private var connections: Set[Conn] = Set.empty

  private def addConnection(ref: ActorRef): Unit = synchronized {
    connections += Conn(ref, None)
  }

  private def removeConnection(ref: ActorRef): Unit = synchronized {
    connections = connections.filterNot(_.ref == ref)
  }

  private def setSession(ref: ActorRef, sid: String): Unit = synchronized {
    connections.find(_.ref == ref).foreach(_.sessionId = Some(sid))
  }

  private def broadcastToSession(sessionId: String, json: JsValue, exclude: Option[ActorRef] = None): Unit = synchronized {
    val text = Json.stringify(json)
    connections
      .filter(c => c.sessionId.contains(sessionId))
      .foreach { c =>
        if (exclude.forall(_ != c.ref)) {
          c.ref ! text
        }
      }
  }

  /** Optional: globaler Broadcast an alle (ohne Sessionfilter) */
  def broadcastInfo(text: String): Unit = synchronized {
    val json = Json.obj("type" -> "info", "text" -> text)
    val txt  = Json.stringify(json)
    connections.foreach(_.ref ! txt)
  }
}

class GameWebSocketActor(out: ActorRef) extends Actor {

  import GameWebSocketActor._

  override def preStart(): Unit = addConnection(out)
  override def postStop(): Unit = removeConnection(out)

  def receive: Receive = {
    case msg: String =>
      scala.util.Try(Json.parse(msg)) match {
        case scala.util.Success(json) =>
          (json \ "type").asOpt[String] match {

            // Spieler joint eine Session
            case Some("joinSession") =>
              val sessionId = (json \ "sessionId").asOpt[String].filter(_.nonEmpty).getOrElse("default")
              val name      = (json \ "playerName").asOpt[String].getOrElse("Player")
              val pairs     = (json \ "pairs").asOpt[Int].getOrElse(2)

              setSession(out, sessionId)

              // Info an alle anderen in dieser Session
              broadcastToSession(
                sessionId,
                Json.obj(
                  "type" -> "info",
                  "text" -> s"$name ist der Session '$sessionId' mit $pairs Paar(en) beigetreten."
                ),
                exclude = Some(out)
              )

            // Karten-Flip synchronisieren
            case Some("flip") =>
              val sessionIdOpt = (json \ "sessionId").asOpt[String].filter(_.nonEmpty)
              val indexOpt     = (json \ "index").asOpt[Int]
              val stateOpt     = (json \ "state").asOpt[String]

              (sessionIdOpt, indexOpt, stateOpt) match {
                case (Some(sid), Some(idx), Some(st)) =>
                  // An alle anderen Spieler in derselben Session senden
                  broadcastToSession(
                    sid,
                    Json.obj(
                      "type"  -> "flip",
                      "index" -> idx,
                      "state" -> st
                    ),
                    exclude = Some(out)
                  )
                case _ =>
                  // unvollständige Daten -> ignorieren
              }

            case Some("ping") =>
              out ! Json.obj("type" -> "pong").toString()

            case _ =>
              // Unbekannter Typ → ignorieren
          }

        case scala.util.Failure(_) =>
          // Ungültiges JSON → ignorieren
      }
  }
}

