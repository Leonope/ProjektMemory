import util.Observer
import util.{InputHandler, RealInputHandler}

class Tui(controller: Controller) extends Observer {
  var inputHandler: InputHandler = RealInputHandler
  controller.add(this) // Add Tui as an observer to the controller

   override def update: Unit = {
    // Handle updates from the controller
    println("The game state has been updated.")
  }

  trait Command {//Command Pattern
  def execute(): Unit
}

class RestartGameCommand(controller: Controller) extends Command {
  def execute(): Unit = {
    println("Game is starting again")
    controller.GameStarting()
  }
}

class QuitGameCommand extends Command {
  def execute(): Unit = {
    println("Game exiting")
    System.exit(0)
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
        println("Unknown Command pls type either 'quit' or 'restart'.")
    }
  }

  def run(): Unit = {
    try {
      controller.GameStarting()
      if (controller.gameLogic.checkGameEnd()) {
      // $COVERAGE-OFF$
      endGamePrompt()  // Ruft die Methode auf, die den Benutzer nach dem Spielende fragt 
      // $COVERAGE-ON$
    }
  } catch {
    case e: Exception => println(e.getMessage)  
  }
}

  def displayMessage(message: String): Unit = { //Message for every function
    println(message)
  }
}
