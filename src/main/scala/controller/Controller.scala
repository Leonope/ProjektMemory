import scala.io.StdIn.readLine
import scala.util.{Try, Success, Failure}
import util.{Observable, Observer, InputHandler, RealInputHandler}

class Controller extends Observable {
  var inputHandler: InputHandler = RealInputHandler
  var gameLogic: Logic = _

  private def withNotification[T](action: => Try[T]): Try[T] = {
    val result = action
    notifyObservers
    result
  }

  def GameStarting(): Try[Unit] = withNotification {
    Try {
      val gameOutcome = for {
        count <- askPlayerCount()
        check <- checkPlayerCount(count)
        name <- askPlayerName()
        cardCount <- askCardCount()
        // $COVERAGE-OFF$
        matrix <- Matrix.setupMatrix(cardCount).map(_.asInstanceOf[Matrix[Card]])
      } yield (name, matrix)

      gameOutcome match {
        case Success((name, matrix)) =>
          greetPlayer(name)
          updateGameView(name, s"Memory game setup:")
          println(matrix)
          //updateGameView(name, matrix, "Game setup completed successfully.")
          gameLogic = new Logic(matrix.asInstanceOf[Matrix[Card]], new BonusScoring())
          playGame()
        case Failure(e) =>
          updateGameView("", null, s"Error starting game: ${e.getMessage}")
          throw e
      }// $COVERAGE-ON$
    }
  }

  def askPlayerCount(): Try[Int] = withNotification {
  Try {
    val countInput = inputHandler.askForInput("How many players will play? (Choose between 1-3)")
    val count = countInput.toInt
    if (count < 1 || count > 3) {
      throw new IllegalArgumentException("Player count must be between 1 and 3.")
    }
    count
  }
}


  def checkPlayerCount(count: Int): Try[String] = withNotification {
    count match {
      case 1 => Success("One player has been selected.")
      case _ => Failure(new Exception("Only 1 player is allowed, as the function for more players is not yet implemented."))
    }
  }

  def askPlayerName(): Try[String] = withNotification {
    Try(inputHandler.askForInput("Please enter your name:"))
  }

  def askCardCount(): Try[Int] = withNotification {
    Try {
      val count = inputHandler.askForInput("How many unique cards will be in the game?").toInt
      if (count <= 0) throw new IllegalArgumentException("The count must be a positive integer greater than 0.")
      count * 2
    }match {
    case Success(cardCount) =>
      println(s"Success! The game will have $cardCount cards.")
      Success(cardCount)
    case Failure(exception) =>
      println(s"Failure: ${exception.getMessage}")
      Failure(exception)
    }
  }
// $COVERAGE-OFF$
  def playGame(): Unit = {
  var gameEnded = false
  while (!gameEnded) {
    try {
      // Nutze den InputHandler statt direct readLine
      val input = inputHandler.askForInput("Enter the row and column to turn a card (e.g., '1 2'): ").trim.split("\\s+")
      if (input.length != 2) {
        println("Invalid input: please enter two numbers separated by a space.")
      } else {
        val row = input(0).toInt - 1  // Assume user inputs are 1-based index
        val col = input(1).toInt - 1

        gameLogic.turnCard(row, col)
        println(gameLogic.getMatrix)

        if (gameLogic.checkGameEnd()) {
          gameEnded = true
          println("Congratulations! You have uncovered all the cards.")
          println(s"Your final score: ${gameLogic.getScore}")
        }
      }
    } catch {
      case _: NumberFormatException =>
        println("Invalid input: please enter valid numbers.")
      case _: IndexOutOfBoundsException =>
        println("Invalid indices: your input does not correspond to valid positions in the matrix.")
      case _: NullPointerException =>
        println("Game logic is not initialized.")
      case e: Exception =>
        println(s"An unexpected error occurred: ${e.getMessage}")
    }
  }
}
// $COVERAGE-ON$

  def greetPlayer(name: String): Unit = {
    updateGameView(name, s"Welcome to the Memory Game, $name! Have fun!")
    notifyObservers
  }

  def updateGameView(name: String, matrix: Matrix[String], message: String): Unit = {
    notifyObservers  
  }

  def updateGameView(name: String, message: String): Unit = {
  println(s"Message for $name: $message")
  notifyObservers
}
}



