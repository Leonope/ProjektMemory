import scala.io.StdIn.readLine
import scala.util.{Try, Success, Failure}

object Memory {
  def main(args: Array[String]): Unit = {
     // $COVERAGE-OFF$
    MemoryInput.runApp(RealInputHandler)
    // $COVERAGE-ON$
  }

  object MemoryInput {
    def runApp(inputHandler: InputHandler): Unit = {
      val gameOutcome = GameStarting(inputHandler)
      gameOutcome match {
        case Success(_) => println("Game setup completed successfully.")
        case Failure(e) => println(s"Error starting game: ${e.getMessage}")
      }
    }

    def GameStarting(inputHandler: InputHandler): Try[Unit] = for {
      count <- askPlayerCount(inputHandler)
      check <- checkPlayerCount(count) // Check the number of players and proceed only if it passes
      name = askPlayerName(inputHandler)
      cardCount <- askCardCount(inputHandler)
      matrix <- Matrix.setupMatrix(cardCount)
    } yield {
      greetPlayer(name)
      println("Memory game setup:")
      println(matrix)
    }

    def askPlayerCount(inputHandler: InputHandler): Try[Int] = Try {
      println("How many players will play? (Choose between 1-3)")
      val count = inputHandler.readLine().toInt
      if (count < 1 || count > 3) throw new IllegalArgumentException("Player count must be between 1 and 3.")
      count
    }

   def checkPlayerCount(count: Int): Try[String] = count match {
    case 1 => Success("One player has been selected.")
    case _ => Failure(new Exception("Only 1 player is allowed, as the function for more players is not yet implemented."))
  }

    def askCardCount(inputHandler: InputHandler): Try[Int] = Try {
      println("How many unique cards will be in the game?")
      val count = inputHandler.readLine().toInt
      if (count <= 0) throw new IllegalArgumentException("The count must be a positive integer greater than 0.")
      count * 2
    }

    def askPlayerName(inputHandler: InputHandler): String = {
      println("Please enter your name:")
      inputHandler.readLine()
    }

    def greetPlayer(name: String): Unit = {
      println(s"Welcome to the Memory Game, $name! Have fun!")
    }
  }

  trait InputHandler {
    def readLine(): String
  }

  object RealInputHandler extends InputHandler {
    def readLine(): String = scala.io.StdIn.readLine()
  }
}