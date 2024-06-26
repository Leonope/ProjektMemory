package main

import util.Observer
import util.{InputHandler, RealInputHandler}
import scala.util.{Try, Success, Failure}
import controller.IController

class Tui(controller: IController) extends Observer {
  // $COVERAGE-OFF$
  var inputHandler: InputHandler = RealInputHandler
  controller.add(this)

  override def update: Unit = {
    println("The game state has been updated(tui).")
    checkGameEnd()
  }

  trait Command {
    def execute(): Unit
  }

  class RestartGameCommand(controller: IController) extends Command {
    def execute(): Unit = {
      println("Game is starting again")
      controller.restartGame()
    }
  }

  class QuitGameCommand extends Command {
    def execute(): Unit = {
      println("Game exiting")
      System.exit(0)
    }
  }

  def checkGameEnd(): Unit = {
    if (controller.gameLogic != null && controller.gameLogic.checkGameEnd()) {
      endGamePrompt()
    }
  }

  def endGamePrompt(): Unit = {
    println("Play again? (restart/quit)")
    val action = inputHandler.readLine().trim.toLowerCase
    action match {
      case "restart" =>
        val restartCommand = new RestartGameCommand(controller)
        restartCommand.execute()
      case "quit" =>
        val quitCommand = new QuitGameCommand()
        quitCommand.execute()
      case _ =>
        println("Unknown Command. Please type either 'quit' or 'restart'.")
        endGamePrompt() // Retry prompt
    }
  }

  def run(): Unit = {
    try {
      controller.GameStarting()
      // Wait for the game to actually start before checking game end
      while (controller.gameLogic == null) {
        Thread.sleep(100)
      }
      // Check if the game has ended after starting
      checkGameEnd()
    } catch {
      case e: Exception => println(e.getMessage)
    }
  }

  def displayMessage(message: String): Unit = {
    println(message)
  }
  // $COVERAGE-ON$
}
