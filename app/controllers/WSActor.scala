package controllers

import akka.actor._
import GameController._
import backend.Backend.controller
import scala.swing.Reactor

class SudokuWebSocketActor(out: ActorRef) extends Actor with Reactor {
    listenTo(Backend.controller);

    def receive = {
        case msg: String =>
            //out ! (gameController.toJson.toString)
            println("Sent Json to Client"+ msg)
    }

    reactions += {
        case event: askPlayerCount => sendJsonToClient
        case event: askPlayerName => sendJsonToClient
        case event: askCardCount => sendJsonToClient
        case event: restartGame => sendJsonToClient
        case event: _ => sendJsonToClient
    }

    def sendJsonToClient = {
        println("Received event from Controller")
        //out ! (gameController.toJson.toString)
    }
}