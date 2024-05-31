package model

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers

class TestLogicDecoratorSpec extends AnyWordSpec with Matchers {
  "A LogicDecorator" should {
    "print the current score after turning a card" in {
      // Setup a simple Matrix and ScoringStrategy for testing
      val card1 = new Card(1, isFaceUp = false)
      val card2 = new Card(2, isFaceUp = false)
      val card3 = new Card(1, isFaceUp = false)
      val card4 = new Card(2, isFaceUp = false)
      val matrix: Matrix[Card] = new Matrix(Array(
        Array(card1, card2),
        Array(card3, card4)
      ))

      val standardScoring = new StandardScoring
      val logic = new Logic(matrix, standardScoring)
      val decoratedLogic = new LogicDecorator(logic)

      // Capture the output
      val output = new java.io.ByteArrayOutputStream()
      Console.withOut(output) {
        decoratedLogic.turnCard(0, 0)
        decoratedLogic.turnCard(1, 0)
      }

      val result = output.toString

      result should include ("Current score after turning card: 10")
    }
  }
}

// Mock implementations for Card and Matrix classes
class Card(val id: Int, val isFaceUp: Boolean) {
  def turnCard(): Card = new Card(id, !isFaceUp)
}

class Matrix[T](private val grid: Array[Array[T]]) {
  def apply(row: Int, col: Int): T = grid(row)(col)
  def update(row: Int, col: Int, value: T): Unit = grid(row)(col) = value
  def getGrid: Array[Array[T]] = grid
}
