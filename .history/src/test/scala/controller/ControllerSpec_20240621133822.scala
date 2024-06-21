/*package model
import org.scalatest.wordspec.AnyWordSpec
import org.scalamock.scalatest.MockFactory
import scala.util.{Success, Failure}
import org.scalatest.matchers.should.Matchers
import java.io.{ByteArrayOutputStream, PrintStream}
import util.{InputHandler, RealInputHandler, Observable, Observer}

class ControllerSpec extends AnyWordSpec with Matchers with MockFactory {

  var mockMatrix: Matrix[Card] = _

  "ControllerSpec" when {
    val controller = new Controller()

    // Testing input handler for player count
    "askPlayerCount is called" should {
      "return a valid count within range" in {
        val mockInputHandler = mock[InputHandler]
        controller.inputHandler = mockInputHandler
        (mockInputHandler.askForInput _).expects("How many players will play? (Choose between 1-3)").returning("2").once()
        controller.askPlayerCount() should be(Success(2))
      }

      "return failure on invalid number" in {
        val mockInputHandler = mock[InputHandler]
        controller.inputHandler = mockInputHandler
        (mockInputHandler.askForInput _).expects("How many players will play? (Choose between 1-3)").returning("4").once()
        controller.askPlayerCount().isFailure should be(true)
      }
    }

    // Checking the player count
    "checkPlayerCount is called" should {
      "handle only one player" in {
        controller.checkPlayerCount(1) should be(Success("One player has been selected."))
      }

      "fail for more than one player" in {
        controller.checkPlayerCount(2).isFailure should be(true)
        controller.checkPlayerCount(3).isFailure should be(true)
      }
    }

   "GameStarting is called" should {
      "fail with invalid player count" in {
        val mockInputHandler = mock[InputHandler]
        controller.inputHandler = mockInputHandler
        (mockInputHandler.askForInput _).expects("How many players will play? (Choose between 1-3)").returning("4").once()

        controller.GameStarting().isFailure should be(true)
      }

      "fail with invalid card count" in {
        val mockInputHandler = mock[InputHandler]
        controller.inputHandler = mockInputHandler
        (mockInputHandler.askForInput _).expects("How many players will play? (Choose between 1-3)").returning("1").once()
        (mockInputHandler.askForInput _).expects("Please enter your name:").returning("Alice").once()
        (mockInputHandler.askForInput _).expects("How many unique cards will be in the game?").returning("0").once()

        controller.GameStarting().isFailure should be(true)
      }
    }

    // Testing card count input
    "askCardCount" should {
  "return the correct card count for valid input" in {
    val mockInputHandler = mock[InputHandler]
    controller.inputHandler = mockInputHandler
    (mockInputHandler.askForInput _).expects("How many unique cards will be in the game?").returning("3").once()

    val result = controller.askCardCount()
    result.isSuccess should be(true)
    result.get should be(6) // Expected card count for pairs
  }
}

"askCardCount" should {
  "fail with invalid input" in {
    val mockInputHandler = mock[InputHandler]
    controller.inputHandler = mockInputHandler
    (mockInputHandler.askForInput _).expects("How many unique cards will be in the game?").returning("abc").once()

    val result = controller.askCardCount()
    result.isFailure should be(true)
    result.failed.get shouldBe a[NumberFormatException] // Expected exception for invalid input
  }

  "fail with zero card count" in {
    val mockInputHandler = mock[InputHandler]
    controller.inputHandler = mockInputHandler
    (mockInputHandler.askForInput _).expects("How many unique cards will be in the game?").returning("0").once()

    val result = controller.askCardCount()
    result.isFailure should be(true)
    result.failed.get shouldBe a[IllegalArgumentException] // Expected exception for zero card count
  }
}

"askCardCount" should {
  "print success message for valid input" in {
    val mockInputHandler = mock[InputHandler]
    controller.inputHandler = mockInputHandler
    (mockInputHandler.askForInput _).expects("How many unique cards will be in the game?").returning("3").once()

    val outCapture = new ByteArrayOutputStream()
    Console.withOut(new PrintStream(outCapture)) {
      controller.askCardCount()
    }
    outCapture.toString.trim should include("Success! The game will have 6 cards.")
  }

  "print failure message for invalid input" in {
    val mockInputHandler = mock[InputHandler]
    controller.inputHandler = mockInputHandler
    (mockInputHandler.askForInput _).expects("How many unique cards will be in the game?").returning("abc").once()

    val outCapture = new ByteArrayOutputStream()
    Console.withOut(new PrintStream(outCapture)) {
      controller.askCardCount()
    }
    outCapture.toString.trim should include("Failure:")
  }
}

    // Testing greeting function
    "greetPlayer is called" should {
      "print the correct greeting" in {
        val name = "Alice"
        val outCapture = new ByteArrayOutputStream()
        Console.withOut(new PrintStream(outCapture)) {
          controller.greetPlayer(name)
        }
        outCapture.toString.trim should be("Message for Alice: Welcome to the Memory Game, Alice! Have fun!")
      }
    }
*/
  /*"GameStarting is invoked" should {
      "correctly initialize the game on valid inputs" in {
        val controller = new Controller()
        val mockInputHandler = mock[InputHandler]
        val mockMatrix = new Matrix[Card](2, 2, new Card(1, FaceDownState))  // Assuming Matrix can be directly instantiated
        val mockGameLogic = mock[Logic]

        controller.inputHandler = mockInputHandler

        // Mocking inputs
        (mockInputHandler.askForInput _).expects("How many players will play? (Choose between 1-3)").returning("1")
        (mockInputHandler.askForInput _).expects("Please enter your name:").returning("Alice")
        (mockInputHandler.askForInput _).expects("How many unique cards will be in the game?").returning("4")

        (Matrix.setupMatrix(_: Int, _: MatrixFactory[Card])).expects(8, *).returning(Success(mockMatrix))

        // Assuming setupMatrix should correctly set up and return a mocked Matrix
        controller.gameLogic = mockGameLogic

        (mockGameLogic.turnCard _).expects(*, *).anyNumberOfTimes()

        val outCapture = new ByteArrayOutputStream()
        Console.withOut(new PrintStream(outCapture)) {
          controller.GameStarting() shouldBe a[Success[_]]
        }

        // Verify expected outputs
        outCapture.toString should include("Welcome to the Memory Game, Alice! Have fun!")
        outCapture.toString should include("Memory game setup:")
      }
    }*/
  /*}
  }*/

