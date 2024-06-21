package model.LogicGameComponent

import model.{ilogic, ICard, IMatrix, IScoringStrategy, ICardState}
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.BeforeAndAfterEach

class DecoratorSpec extends AnyWordSpec with Matchers with BeforeAndAfterEach {

  /*var matrix: Matrix[Card] = _
  var logic: Logic = _
  var logicDecorator: LogicDecorator = _

  override def beforeEach(): Unit = {
    // Initialize a mock or a test double for the Matrix and Cards
    matrix = new Matrix[Card](3, 3, new Card(0, FaceDownState))  // Typ für x spezifizieren und den richtigen Konstruktor verwenden
    logic = new Logic(matrix, new StandardScoring)
    logicDecorator = new LogicDecorator(logic)
  }

  "LogicDecorator" should {

    "turn a card and update the score" in {
      val row = 0
      val col = 0

      // Turn the first card
      logicDecorator.turnCard(row, col)
      
      // Check that the card is face up
      matrix(row, col).isFaceUp should be(true)

      // Turn a second card
      logicDecorator.turnCard(row, 1)
      
      // Assuming the cards don't match, score should remain 0
      logicDecorator.getScore should be(10)

      // Now turn two matching cards and check the score
      matrix.update(1, 0, new Card(1, FaceDownState))  // Richtige Parameter für Card verwenden
      logicDecorator.turnCard(1, 0)
      logicDecorator.turnCard(1, 1)

      // Assuming the cards dont match, score should be not updated
      logicDecorator.getScore should be(0)
    }

    "print current score after turning card" in {
      // Capture the printed output
      val stream = new java.io.ByteArrayOutputStream()
      Console.withOut(stream) {
        logicDecorator.turnCard(0, 0)
        logicDecorator.turnCard(0, 1)
      }
      val output = stream.toString

      // Verify the output contains the expected score
      output should include(s"Current score after turning card: ${logicDecorator.getScore}")
    }
  }*/
}



