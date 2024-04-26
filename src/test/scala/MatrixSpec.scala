import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers

class MatrixSpec extends AnyWordSpec with Matchers {
  "A Matrix" when {
    "newly created" should {
      "fill all cells with the initial value" in {
        val matrix = new Matrix[Int](3, 3, 0)
        matrix(0, 0) should be(0)
        matrix(1, 1) should be(0)
        matrix(2, 2) should be(0)
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
  }
}
