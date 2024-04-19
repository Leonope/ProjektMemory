import scala.io.StdIn.readLine
import scala.util.{Try, Success, Failure}

object Memory {
  // $COVERAGE-OFF$
  def main(args: Array[String]): Unit = {
    runApp(RealInputHandler)
  }
  def runApp(inputHandler: InputHandler): Unit = {
    try {
      val gameOutcome = GameStarting(inputHandler)
      gameOutcome match {
        case Success(_) => println("Game started successfully.")
        case Failure(e) => println(s"Error starting game: ${e.getMessage}")
      }
    } catch {
      case e: Exception => println(s"Unhandled error: ${e.getMessage}")
    }
  }
// $COVERAGE-ON$

  // Asks for the number of players and validates the input
  def askPlayerCount(inputHandler: InputHandler): Try[Int] = Try {
    println("How many players will play? (Choose between 1-3)")
    val count = inputHandler.readLine().toInt
    if (count < 1 || count > 3) throw new IllegalArgumentException("Player count must be between 1 and 3.")
    count
  }

  // Checks the number of players
  def checkPlayerCount(count: Int): Try[String] = count match {
    case 1 => Success("One player has been selected.")
    case _ => Failure(new Exception("Only 1 player is allowed, as the function for more players is not yet implemented."))
  }

  // Asks for the player's name
  def askPlayerName(inputHandler: InputHandler): String = {
    println("Please enter your name:")
    inputHandler.readLine()
  }

  // Greets the player
  def greetPlayer(name: String): Unit = {
    println(s"Welcome to the Memory Game, $name! Have fun!") // 's' for variable interpolation
  }

  // Start the game, checks if previous processes were successful
  def GameStarting(inputHandler: InputHandler): Try[Unit] = for {
    count <- askPlayerCount(inputHandler)
    check <- checkPlayerCount(count)
    name = askPlayerName(inputHandler)
  } yield {
    greetPlayer(name)
  }

}

trait InputHandler {
  def readLine(): String
}

object RealInputHandler extends InputHandler {
  def readLine(): String = scala.io.StdIn.readLine()
}
