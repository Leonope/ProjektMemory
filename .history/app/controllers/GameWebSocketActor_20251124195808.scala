package controllers

import akka.actor._
import play.api.libs.json._

/**
  * Ein Actor pro WebSocket-Verbindung.
  * Die Companion-Object-Variablen verwalten eine globale Liste aller Verbindungen,
  * sodass wir einfache Broadcasts an alle Spieler schicken können.
  */
object GameWebSocketActor {
  def props(out: ActorRef): Props = Props(new GameWebSocketActor(out))

  // einfache globale Liste aller verbundenen Clients
  private var connections: Set[ActorRef] = Set.empty

  private def addConnection(ref: ActorRef): Unit = synchronized {
    connections += ref
  }

  private def removeConnection(ref: ActorRef): Unit = synchronized {
    connections -= ref
  }

  private def broadcast(json: JsValue): Unit = synchronized {
    val txt = Json.stringify(json)
    connections.foreach(_ ! txt)
  }

  /** Kann später auch vom Backend verwendet werden, um Infos zu pushen. */
  def broadcastInfo(text: String): Unit =
    broadcast(Json.obj("type" -> "info", "text" -> text))

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
            case Some("join") =>
              val name  = (json \ "playerName").asOpt[String].getOrElse("Player")
              val pairs = (json \ "pairs").asOpt[Int].getOrElse(2)

              // An ALLE Spieler eine Info pushen (ServerPush!)
              broadcast(
                Json.obj(
                  "type" -> "info",
                  "text" -> s"$name ist einem Spiel mit $pairs Paar(en) beigetreten."
                )
              )

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

