package controller

import scala.util.{Try, Success, Failure}
import util.{Observable, Observer, InputHandler, RealInputHandler}
import javax.swing.SwingUtilities
import model.{ilogic, ICard, IScoringStrategy}
import model.LogicGameComponent.Matrix
import model.LogicGameComponent.{Logic, Card, BonusScoring}
import com.google.inject.Inject
import net.codingwell.scalaguice.InjectorExtensions._
import model.FileIOComponent.FileIOInterface

// $COVERAGE-OFF$

trait Command {
  def doStep(): Unit
  def undoStep(): Unit
  def redoStep(): Unit
}

class SetPlayerCountCommand(controller: Controller, newCount: Int) extends Command {
  private var previousCount: Option[Int] = controller.playerCount

  override def doStep(): Unit = {
    previousCount = controller.playerCount
    controller.playerCount = Some(newCount)
    controller.notifyObservers
  }

  override def undoStep(): Unit = {
    controller.playerCount = previousCount
    controller.notifyObservers
  }

  override def redoStep(): Unit = {
    controller.playerCount = Some(newCount)
    controller.notifyObservers
  }
}

class SetPlayerNameCommand(controller: Controller, newName: String) extends Command {
  private var previousName: Option[String] = controller.playerName

  override def doStep(): Unit = {
    previousName = controller.playerName
    controller.playerName = Some(newName)
    controller.notifyObservers
  }

  override def undoStep(): Unit = {
    controller.playerName = previousName
    controller.notifyObservers
  }

  override def redoStep(): Unit = {
    controller.playerName = Some(newName)
    controller.notifyObservers
  }
}

class SetCardCountCommand(controller: Controller, newCount: Int) extends Command {
  private var previousCardCount: Option[Int] = controller.cardCount

  override def doStep(): Unit = {
    previousCardCount = controller.cardCount
    controller.cardCount = Some(newCount)
    controller.notifyObservers
  }

  override def undoStep(): Unit = {
    controller.cardCount = previousCardCount
    controller.notifyObservers
  }

  override def redoStep(): Unit = {
    controller.cardCount = Some(newCount)
    controller.notifyObservers
  }
}

class UndoManager {
  private var undoStack: List[Command] = Nil
  private var redoStack: List[Command] = Nil

  def doStep(command: Command): Unit = {
    undoStack = command :: undoStack
    command.doStep()
    redoStack = Nil
  }

  def undoStep(): Unit = {
    undoStack match {
      case Nil => // no-op
      case head :: stack =>
        head.undoStep()
        undoStack = stack
        redoStack = head :: redoStack
    }
  }

  def redoStep(): Unit = {
    redoStack match {
      case Nil => // no-op
      case head :: stack =>
        head.redoStep()
        redoStack = stack
        undoStack = head :: undoStack
    }
  }
}

class Controller @Inject()(var gameLogic: ilogic, fileIO: FileIOInterface[Card]) extends Observable with IController {
  var inputHandler: InputHandler = RealInputHandler
  //var gameLogic: ilogic = _
  var loadedMatrix: Matrix[Card] = _
  var playerCount: Option[Int] = None
  var playerName: Option[String] = None
  var cardCount: Option[Int] = None
  val undoManager = new UndoManager
  var currentStep: Int = 0
  var commandHistory: List[String] = List()
  
  private def withNotification[T](action: => Try[T]): Try[T] = {
    val result = action
    result match {
      case Success(_) => notifyObserversOnEDT()
      case Failure(_) => // handle failure if needed
    }
    result
  }

  private def notifyObserversOnEDT(): Unit = {
    SwingUtilities.invokeLater(new Runnable {
      def run(): Unit = {
        notifyObservers
      }
    })
  }

  def GameStarting(): Unit = {
    currentStep = 0
    commandHistory = List()
    askPlayerCount()
  }

  def load(): Unit = {
    loadedMatrix = fileIO.load.asInstanceOf[Matrix[Card]]
    println("Matrix loaded:")
    println(loadedMatrix)
  }

  def save(): Unit = {
    fileIO.save(loadedMatrix)
    println("Matrix saved:")
    println(loadedMatrix)
  }

  def askPlayerCount(count: Option[Int] = None): Unit = {
    count match {
      case Some(value) =>
        if (value < 1 || value > 3) {
          println("Player count must be between 1 and 3.")
          return
        }
        val command = new SetPlayerCountCommand(this, value)
        undoManager.doStep(command)
        commandHistory = commandHistory :+ "askPlayerCount"
        notifyObserversOnEDT()
        askPlayerName()
      case None =>
        println("How many players will play? (Choose between 1-3)")
    }
  }

  def askPlayerName(name: Option[String] = None): Unit = {
    name match {
      case Some(value) =>
        if (value.isEmpty) {
          println("Name cannot be empty.")
          return
        }
        val command = new SetPlayerNameCommand(this, value)
        undoManager.doStep(command)
        commandHistory = commandHistory :+ "askPlayerName"
        notifyObserversOnEDT()
        askCardCount()
      case None =>
        println("Please enter your name:")
    }
  }

  def askCardCount(count: Option[Int] = None): Unit = {
    count match {
      case Some(value) =>
        if (value <= 0) {
          println("The count must be a positive integer greater than 0.")
          return
        }
        val command = new SetCardCountCommand(this, value * 2)
        undoManager.doStep(command)
        commandHistory = commandHistory :+ "askCardCount"
        notifyObserversOnEDT() // Notify observers after setting the card count
        startGameLogic()
      case None =>
        println("How many unique cards will be in the game?")
    }
  }

def getCardCount: Option[Int] = cardCount
def getPlayerName: Option[String] = playerName
def getPlayerCount: Option[Int] = playerCount

  def startGameLogic(): Unit = {
    val gameOutcome = for {
      count <- Try(playerCount.get)
      check <- checkPlayerCount(count)
      name <- Try(playerName.get)
      cards <- Try(cardCount.get)
      matrix <- Matrix.setupMatrix(cards).map(_.asInstanceOf[Matrix[Card]])
    } yield (name, matrix)

    gameOutcome match {
      case Success((name, matrix)) =>
        greetPlayer(name)
        updateGameView(name, s"Memory game setup:")
        println(matrix)
        gameLogic = new Logic(matrix.asInstanceOf[Matrix[Card]], new BonusScoring())
        save(matrix) //test save
        load() //test load
        //playGame()
      case Failure(e) =>
        updateGameView("", null, s"Error starting game: ${e.getMessage}")
        throw e
    }
  }

  def checkPlayerCount(count: Int): Try[String] = withNotification {
    count match {
      case 1 => Success("One player has been selected.")
      case _ => Failure(new Exception("Only 1 player is allowed, as the function for more players is not yet implemented."))
    }
  }

  def greetPlayer(name: String): Unit = {
    updateGameView(name, s"Welcome to the Memory Game, $name! Have fun!")
  }

  def updateGameView(name: String, matrix: Matrix[String], message: String): Unit = {
    //notifyObservers()
  }

  def updateGameView(name: String, message: String): Unit = {
    println(s"Message for $name: $message")
  }

  def playGame(input: String): Unit = {
    val command = input.trim.split("\\s+")
    if (command.length != 2) {
      println("Invalid input: please enter two numbers separated by a space.")
    } else {
      try {
        val row = command(0).toInt - 1
        val col = command(1).toInt - 1
        gameLogic.turnCard(row, col)
        println(gameLogic.getMatrix)
        notifyObserversOnEDT()
        if (gameLogic.checkGameEnd()) {
          println("Congratulations! You have uncovered all the cards.")
          println(s"Your final score: ${gameLogic.getScore}")
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

  def restartGame(): Unit = {
    // Setze den Spielzustand zurück
    playerCount = None
    playerName = None
    cardCount = None
    gameLogic = null
    // Benachrichtige alle Beobachter (TUI und GUI), dass das Spiel neu gestartet wurde
    notifyObserversOnEDT()
    GameStarting()
  }

  // Additional methods to execute commands
  def executeCommand(command: Command): Unit = {
    undoManager.doStep(command)
  }

  def undo(): Unit = {
    if (currentStep > 0) {
      currentStep -= 1
      undoManager.undoStep()
      commandHistory.lift(currentStep) match {
        case Some("askPlayerCount") => playerCount = None
        case Some("askPlayerName") => playerName = None
        case Some("askCardCount") => cardCount = None
        case _ =>
      }
      commandHistory.lift(currentStep) match {
        case Some("askPlayerCount") => askPlayerCount()
        case Some("askPlayerName") => askPlayerName()
        case Some("askCardCount") => askCardCount()
        case _ =>
      }
    } else {
      println("No more steps to undo.")
    }
  }

  def redo(): Unit = {
    if (currentStep < commandHistory.length) {
      commandHistory.lift(currentStep) match {
        case Some("askPlayerCount") =>
          executeCommand(new SetPlayerCountCommand(this, playerCount.getOrElse(1)))
        case Some("askPlayerName") =>
          executeCommand(new SetPlayerNameCommand(this, playerName.getOrElse("")))
        case Some("askCardCount") =>
          executeCommand(new SetCardCountCommand(this, cardCount.getOrElse(2)))
        case _ =>
      }
      currentStep += 1
      commandHistory.lift(currentStep) match {
        case Some("askPlayerCount") => askPlayerCount()
        case Some("askPlayerName") => askPlayerName()
        case Some("askCardCount") => askCardCount()
        case _ =>
      }
      undoManager.redoStep()
    } else {
      println("No more steps to redo.")
    }
  }
}
// $COVERAGE-ON$

