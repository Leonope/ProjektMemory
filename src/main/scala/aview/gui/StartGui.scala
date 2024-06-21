package main

import scala.swing._
import scala.swing.event._
import model.{ilogic, ICard, IScoringStrategy}
import model.LogicGameComponent.Matrix
import model.LogicGameComponent.{Logic, Card, BonusScoring, FaceDownState}
import util.Observer
import javax.swing.SwingUtilities
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import controller.IController

object GUI extends SimpleSwingApplication {
  // $COVERAGE-OFF$
  private var controller: IController = _
  private var staticMatrix: Option[Matrix[Card]] = None

  def init(controller: IController): Unit = {
    this.controller = controller
    controller.add(new Observer {
      override def update: Unit = {
        SwingUtilities.invokeLater(new Runnable {
          def run(): Unit = {
            if (controller.getPlayerCount.isEmpty || controller.getPlayerName.isEmpty || controller.getCardCount.isEmpty) {
              // Reset GUI
              staticMatrix = None
              gameGridPanel.contents.clear()
              println("The game state has been reset (gui).")
            } else {
              // Update GUI components
              println("The game state has been updated (gui).")
              if (controller.gameLogic != null && staticMatrix.isEmpty) {
                staticMatrix = Some(createStaticMatrix())
                updateGameGrid()
              }
            }
          }
        })
      }
    })
  }

  val gameGridPanel = new BoxPanel(Orientation.Vertical)
  var revealedButtons: Seq[CardButton] = Seq()

  def createStaticMatrix(): Matrix[Card] = {
    val originalMatrix = controller.gameLogic.getMatrix  // Annahme: Diese Methode gibt die Matrix zurück
    val rows = originalMatrix.rows
    val cols = originalMatrix.cols
    val cards = for {
      i <- 0 until rows
      j <- 0 until cols
    } yield originalMatrix(i, j).asInstanceOf[Card]  // Sicherstellen, dass es sich um Card-Objekte handelt

    // Neue Instanz der Matrix erstellen und mit den gleichen Karten initialisieren
    val newMatrix = new Matrix[Card](rows, cols, Card(0, FaceDownState))
    newMatrix.initializeWithCards(cards)
    newMatrix
  }

  def updateGameGrid(): Unit = {
    gameGridPanel.contents.clear()
    val matrix = staticMatrix.get
    val rows = matrix.rows
    val cols = matrix.cols
    val cards = for {
      i <- 0 until rows
      j <- 0 until cols
    } yield matrix(i, j)

    val gameGrid = new GridPanel(rows, cols) {
      border = Swing.EmptyBorder(10, 10, 10, 10)
      for ((card, index) <- cards.zipWithIndex) {
        val row = index / cols
        val col = index % cols
        val cardButton = new CardButton(row, col, card)
        contents += cardButton
      }
    }
    gameGridPanel.contents += gameGrid

    gameGridPanel.revalidate()
    gameGridPanel.repaint()
  }

  def top: MainFrame = new MainFrame {
    title = "Scala Swing Memory Game"

    val playerCountField = new TextField()
    val playerNameField = new TextField()
    val cardCountField = new TextField()
    val userInputField = new TextField() // TextField for user input
    val messageLabel = new Label()

    val playerCountButton = new Button("Set Player Count") {
      reactions += {
        case ButtonClicked(_) =>
          SwingUtilities.invokeLater(new Runnable {
            def run(): Unit = {
              val playerCount = playerCountField.text.toInt
              controller.askPlayerCount(Some(playerCount))
            }
          })
      }
    }

    val playerNameButton = new Button("Set Player Name") {
      reactions += {
        case ButtonClicked(_) =>
          SwingUtilities.invokeLater(new Runnable {
            def run(): Unit = {
              val playerName = playerNameField.text
              controller.askPlayerName(Some(playerName))
            }
          })
      }
    }

    val cardCountButton = new Button("Set Card Count") {
      reactions += {
        case ButtonClicked(_) =>
          SwingUtilities.invokeLater(new Runnable {
            def run(): Unit = {
              val cardCount = cardCountField.text.toInt
              controller.askCardCount(Some(cardCount))
              // Ensure the game logic is initialized before updating the game grid
              if (controller.gameLogic != null && staticMatrix.isEmpty) {
                staticMatrix = Some(createStaticMatrix())
                updateGameGrid()  // Call updateGameGrid after setting the card count
              }
            }
          })
      }
    }

    contents = new BoxPanel(Orientation.Vertical) {
      contents += new Label("Enter number of players:")
      contents += playerCountField
      contents += playerCountButton
      contents += new Label("Enter player name:")
      contents += playerNameField
      contents += playerNameButton
      contents += new Label("Enter number of unique cards:")
      contents += cardCountField
      contents += cardCountButton
      contents += new Label("Game Board:")
      contents += gameGridPanel
      border = Swing.EmptyBorder(10, 10, 10, 10)
    }

    listenTo(playerCountButton)
    listenTo(playerNameButton)
    listenTo(cardCountButton)
  }

  class CardButton(val row: Int, val col: Int, val card: Card) extends Button {
    preferredSize = new Dimension(100, 100)
    private var isRevealed = false

    reactions += {
      case ButtonClicked(_) =>
        if (!isRevealed) {
          revealCard()
          revealedButtons :+= this
          val input = s"${row + 1} ${col + 1}"
          // Ensure that the card remains revealed before updating the game
          Future {
            controller.playGame(input)  // Update the game in the TUI as well
            SwingUtilities.invokeLater(new Runnable {
              def run(): Unit = {
                if (revealedButtons.size == 2) {
                  val firstButton = revealedButtons.head
                  val secondButton = revealedButtons.last
                  if (firstButton.card.id == secondButton.card.id) {
                    // Beide Karten stimmen überein, bleiben aufgedeckt
                    revealedButtons = Seq()
                  } else {
                    // Karten stimmen nicht überein, nach 1 Sekunde wieder umdrehen
                    Future {
                      Thread.sleep(1000)
                      SwingUtilities.invokeLater(new Runnable {
                        def run(): Unit = {
                          firstButton.hideCard()
                          secondButton.hideCard()
                          revealedButtons = Seq()
                        }
                      })
                    }
                  }
                }
              }
            })
          }
        }
    }

    def revealCard(): Unit = {
      isRevealed = true
      text = card.id.toString
    }

    def hideCard(): Unit = {
      isRevealed = false
      text = ""
    }
  }
}
// $COVERAGE-ON$


