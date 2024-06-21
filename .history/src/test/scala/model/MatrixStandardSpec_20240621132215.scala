package model.LogicGameComponent

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import scala.util.Random
import scala.util.Try



class MatrixStandardSpec extends AnyWordSpec with Matchers {

    object Matrix {
  def setupMatrix(cardCount: Int, factory: MatrixFactory[Card] = new StandardMatrixFactory()): Try[Matrix[Card]] = Try {
    val factors = (1 to cardCount).filter(cardCount % _ == 0)
    val (rows, cols) = factors.foldLeft((1, cardCount)) { (best, current) =>
      val other = cardCount / current
      if (Math.abs(current - other) < Math.abs(best._1 - best._2)) (current, other) else best
    }
    val (finalRows, finalCols) = if (rows > cols) (cols, rows) else (rows, cols)
    val cards = (1 to cardCount / 2).flatMap(id => Seq(new Card(id, FaceDownState), new Card(id, FaceDownState)))

    factory.create(finalRows, finalCols, new Card(0, FaceDownState), cards)
  }
}

  "A Matrix" when {
    "newly created" should {
      "fill all cells with the initial value" in {
        val matrix = new Matrix[Int](3, 3, 0)
        matrix(0, 0) should be(0)
        matrix(1, 1) should be(0)
        matrix(2, 2) should be(0)
      }
    }

    "toString" should {
  "return a correct string representation of the matrix" in {
    val matrix = new Matrix[Int](2, 2, 5)
    matrix.update(0, 1, 10)
    matrix.update(1, 0, 20)
    matrix.toString should be ("5 10\n20 5")
  }
}

    "updated at a specific cell" should {
      "reflect the new value in the updated cell" in {
        val matrix = new Matrix[Int](3, 3, 0)
        matrix.update(1, 1, 99)
        matrix(1, 1) should be(99)
      }

      "not affect other cells" in {
        val matrix = new Matrix[Int](3, 3, 0)
        matrix.update(1, 1, 99)
        matrix(0, 0) should be(0)
        matrix(2, 2) should be(0)
      }
    }

    "accessed out of bounds" should {
      "throw an IndexOutOfBoundsException" in {
        val matrix = new Matrix[Int](3, 3, 0)
        an[IndexOutOfBoundsException] should be thrownBy {
          matrix(3, 3)
        }
        an[IndexOutOfBoundsException] should be thrownBy {
          matrix(-1, -1)
        }
      }
    }

    "accessing its grid" should {
      "return the correct internal grid structure" in {
        val cards = Array(
        Array(new Card(1, FaceDownState), new Card(2, FaceDownState)), 
        Array(new Card(3, FaceDownState), new Card(4, FaceDownState)))
        val matrix = new Matrix[Card](2, 2, new Card(0, FaceDownState))  // Assuming Matrix initializes with dummy data
        for (i <- cards.indices; j <- cards(i).indices) {
          matrix.update(i, j, cards(i)(j))  // Assume update method sets the card at position (i, j)
        }
        matrix.getGrid should be (cards)
      }
    }

   "A Matrix with Cards" should {
    "correctly select and turn a card" in {
      val initialCard = new Card(1, FaceDownState) // Initially face down
      val matrix = new Matrix[Card](1, 1, initialCard)
      matrix.select(0, 0) // Should turn the card face up
      matrix(0, 0).isFaceUp should be(true) // Verify the card is now face up

      matrix.select(0, 0) // Should turn the card face down again
      matrix(0, 0).isFaceUp should be(false) // Verify the card is now face down again
    }
  }
  
      "selecting a non-card element" should {
  "do nothing if the selected element is not a card" in {
    val matrix = new Matrix[String](1, 1, "not a card")
    noException should be thrownBy matrix.select(0, 0)
    // Further verify that the grid remains unchanged
    matrix(0, 0) should be("not a card")
  }
}
  }

    "initialized with cards" should {
      "correctly place the cards in the matrix" in {
        val shuffledCards = Random.shuffle((1 to 4).map(id => new Card(id, FaceDownState)).toList)
        val matrix = new Matrix[Card](2, 2, shuffledCards.head)
        matrix.initializeWithCards(shuffledCards)

        for (i <- 0 until 2; j <- 0 until 2) {
          matrix(i, j) shouldBe shuffledCards(i * 2 + j)
        }
      }
    }

    "filled with a sequence of cards" should {
      "have all cells filled according to the sequence" in {
        val cards = Seq(new Card(1, FaceDownState), new Card(2, FaceUpState), new Card(3, FaceDownState), new Card(4, FaceUpState))
        val matrix = new Matrix[Card](2, 2, new Card(0, FaceDownState)).initializeWithCards(cards)
        matrix(0, 0).id should be(1)
        matrix(0, 1).id should be(2)
        matrix(1, 0).id should be(3)
        matrix(1, 1).id should be(4)
      }
    }

    "initialized with shuffled cards" should {
      "contain all the provided cards in a randomized order" in {
        val cards = (1 to 4).map(id => new Card(id, FaceDownState))
        val shuffledCards = Random.shuffle(cards)
        val matrix = new Matrix[Card](2, 2, shuffledCards.head).initializeWithCards(shuffledCards)

        matrix.getGrid.flatten.toSet shouldEqual cards.toSet
      }
    }

      "do nothing if the selected element is not a card" in {
        val matrix = new Matrix[String](1, 1, "not a card")
        noException should be thrownBy { matrix.select(0, 0) }
      }

      "setupMatrix" should {
  "correctly setup a matrix with the optimal rows and columns" in {
    val result = Matrix.setupMatrix(4)
    result.isSuccess shouldBe true
    val matrix = result.get
    matrix.rows should (be (2) or be (2))
    matrix.cols should (be (2) or be (2))
  }

  "initialize with shuffled cards correctly" in {
    val result = Matrix.setupMatrix(4)
    result.isSuccess shouldBe true
    val matrix = result.get
    // Ensure all IDs from 1 to 2 (doubled as per setup) are present
    val ids = matrix.getGrid.flatten.map(_.id).sorted
    ids should contain theSameElementsAs Seq(1, 1, 2, 2)
  }
}

    }

