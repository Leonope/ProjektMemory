import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.BeforeAndAfter

class StandardScoringSpec extends AnyWordSpec with Matchers with BeforeAndAfter {

  var matrix: Matrix[Card] = _
  var logic: Logic = _
  private var firstCard: Option[(Int, Int)] = None

  before {
    // Setup a matrix with some cards
    matrix = new Matrix[Card](2, 2, new Card(1, FaceDownState))
    matrix.update(0, 0, new Card(1, FaceDownState))
    matrix.update(0, 1, new Card(2, FaceDownState))
    matrix.update(1, 0, new Card(1, FaceDownState))
    matrix.update(1, 1, new Card(2, FaceDownState))
    logic = new Logic(matrix, new StandardScoring())
  }

  "Logic" should {
    "get the current state of the matrix" in {
      logic.getMatrix should be(matrix)
    }

   "turn a card and check for first card selection" in {
  logic.turnCard(0, 0)
  matrix.apply(0, 0).isFaceUp shouldBe true // Directly check if the card is face up
}


    "turn a second card and check for a match" in {
      logic.turnCard(0, 0) // Turn first card
      logic.turnCard(1, 0) // Turn second card which matches the first
      logic.getScore shouldBe 10
    }

    "turn a second card that does not match and remove both cards" in {
      logic.turnCard(0, 0) // Turn first card
      logic.turnCard(0, 1) // Turn second card which does not match
      matrix.apply(0, 0).isFaceUp shouldBe false
      matrix.apply(0, 1).isFaceUp shouldBe false
    }

    "not increase score when cards do not match" in {
      logic.turnCard(0, 0) // Turn first card
      logic.turnCard(0, 1) // Turn second card which does not match
      logic.getScore shouldBe 0
    }

    "check if the game ends when all cards are face up" in {
      logic.turnCard(0, 0)
      logic.turnCard(1, 0)
      logic.turnCard(0, 1)
      logic.turnCard(1, 1)
      logic.checkGameEnd() shouldBe true
    }

    "return the correct score" in {
      logic.turnCard(0, 0)
      logic.turnCard(1, 0)
      logic.getScore shouldBe 10
    }
  }
}


