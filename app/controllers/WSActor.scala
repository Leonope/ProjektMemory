package controllers

import GameController._
import backend.Backend.controller
import scala.swing.Reactor
import play.api.libs.streams.ActorFlow
import org.apache.pekko.actor._
import org.apache.pekko.stream._

class WSActor(out: ActorRef) extends Actor {
    //listenTo(controller);

    def receive = {
        case msg: String =>
            //out ! (gameController.toJson.toString)
            println("Sent Json to Client"+ msg)
    }

    /*reactions += {
        case event: askPlayerCount => sendJsonToClient
        case event: askPlayerName => sendJsonToClient
        case event: askCardCount => sendJsonToClient
        case event: restartGame => sendJsonToClient
    }*/

    def sendJsonToClient = {
        println("Received event from Controller")
        //out ! (gameController.toJson.toString)
    }
}