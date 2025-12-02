package controllers

import org.apache.pekko.actor._
import play.api.libs.json._
import backend.SessionRegistry

object GameWebSocketActor {

  def props(out: ActorRef): Props = Props(new GameWebSocketActor(out))

  /** interne Repräsentation einer Verbindung */
  private case class Conn(ref: ActorRef, var sessionId: Option[String])

  // alle aktiven WebSocket-Verbindungen
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

  /** Broadcast eines JSON-Objekts an alle in einer Session (außer evtl. exclude). */
  private def broadcastToSession(
      sessionId: String,
      json: JsValue,
      exclude: Option[ActorRef] = None
  ): Unit = synchronized {
    val text = Json.stringify(json)
    connections
      .filter(_.sessionId.contains(sessionId))
      .foreach { c =>
        if (exclude.forall(_ != c.ref)) {
          c.ref ! text
        }
      }
  }

  /** Optionaler globaler Info-Broadcast */
  def broadcastInfo(text: String): Unit = synchronized {
    val json = Json.obj("type" -> "info", "text" -> text)
    val txt  = Json.stringify(json)
    connections.foreach(_.ref ! txt)
  }
}

class GameWebSocketActor(out: ActorRef) extends Actor {

  import GameWebSocketActor._

  private var currentSessionId: Option[String]  = None
  private var currentPlayerName: Option[String] = None

  override def preStart(): Unit = addConnection(out)

  override def postStop(): Unit = {
    removeConnection(out)
    // Spieler aus SessionRegistry austragen
    for {
      sid  <- currentSessionId
      name <- currentPlayerName
    } SessionRegistry.leave(sid, name)
  }

  def receive: Receive = {
    case msg: String =>
      scala.util.Try(Json.parse(msg)) match {
        case scala.util.Success(json) =>
          (json \ "type").asOpt[String] match {

            /** Spieler joint eine Session (kommt aus memory.js sendJoinOverWebSocket) */
            case Some("joinSession") =>
              val sessionId = (json \ "sessionId").asOpt[String].filter(_.nonEmpty).getOrElse("default")
              val name      = (json \ "playerName").asOpt[String].getOrElse("Player")
              val pairs     = (json \ "pairs").asOpt[Int].getOrElse(2)

              currentSessionId  = Some(sessionId)
              currentPlayerName = Some(name)
              setSession(out, sessionId)
              SessionRegistry.join(sessionId, name)

              // Info an andere Spieler dieser Session 
              broadcastToSession(
                sessionId,
                Json.obj(
                  "type" -> "info",
                  "text" -> s"$name ist der Session '$sessionId' mit $pairs Paar(en) beigetreten."
                ),
                exclude = Some(out)
              )

            /** Karten-Flip synchronisieren */
            case Some("flip") =>
              val sessionIdOpt = (json \ "sessionId").asOpt[String].filter(_.nonEmpty)
              val indexOpt     = (json \ "index").asOpt[Int]
              val stateOpt     = (json \ "state").asOpt[String]

              (sessionIdOpt, indexOpt, stateOpt) match {
                case (Some(sid), Some(idx), Some(st)) =>
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
                  // unvollständige Daten -
              }

            case Some("ping") =>
              out ! Json.obj("type" -> "pong").toString()

            case _ =>
              // unbekannter Typ -> ignorieren
          }

        case scala.util.Failure(_) =>
          // ungültiges JSON -> ignorieren
      }
  }
}


