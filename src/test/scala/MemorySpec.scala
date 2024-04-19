import org.scalatest.wordspec.AnyWordSpec
import org.scalamock.scalatest.MockFactory
import scala.util.{Success, Failure}
import java.io.{ByteArrayOutputStream,ByteArrayInputStream, PrintStream}

class MemoryGameSpec extends AnyWordSpec with MockFactory {

"RealInputHandler" should {
    "return the correct string when readLine is called" in {
      val testInput = "test input string\n"
      val inStream = new ByteArrayInputStream(testInput.getBytes)
      Console.withIn(inStream) {
        assert(RealInputHandler.readLine() === "test input string")
      }
    }
  }

  "Memory" when {
    "askPlayerCount is called" should {
      "return a valid count within range" in {
        val mockInputHandler = mock[InputHandler]
        (mockInputHandler.readLine _).expects().returning("2")
        assert(Memory.askPlayerCount(mockInputHandler) == Success(2))
      }

      "return failure on invalid number" in {
        val mockInputHandler = mock[InputHandler]
        (mockInputHandler.readLine _).expects().returning("4")
        assert(Memory.askPlayerCount(mockInputHandler).isFailure)
      }

      "return success for edge cases" in {
        val mockInputHandler = mock[InputHandler]
        (mockInputHandler.readLine _).expects().returning("1")
        assert(Memory.askPlayerCount(mockInputHandler) == Success(1))
        (mockInputHandler.readLine _).expects().returning("3")
        assert(Memory.askPlayerCount(mockInputHandler) == Success(3))
      }
    }

    "checkPlayerCount is called" should {
      "handle only one player" in {
        assert(Memory.checkPlayerCount(1) == Success("One player has been selected."))
      }
      
      "fail for more than one player" in {
        assert(Memory.checkPlayerCount(2).isFailure)
        assert(Memory.checkPlayerCount(3).isFailure)
      }
    }

    "GameStarting is called" should {
      "handle valid player count and name" in {
        val mockInputHandler = mock[InputHandler]
        (mockInputHandler.readLine _).expects().returning("1")
        (mockInputHandler.readLine _).expects().returning("Bob")
        assert(Memory.GameStarting(mockInputHandler).isSuccess)
      }

      "fail with invalid player count" in {
        val mockInputHandler = mock[InputHandler]
        (mockInputHandler.readLine _).expects().returning("4")
        assert(Memory.GameStarting(mockInputHandler).isFailure)
      }
    }

    "greetPlayer is called" should {
      "print the correct greeting" in {
        val name = "Bob"
        val outCapture = new ByteArrayOutputStream()
        Console.withOut(new PrintStream(outCapture)) {
          Memory.greetPlayer(name)
        }
        assert(outCapture.toString.contains("Welcome to the Memory Game, Bob! Have fun!"))
      }
    }
  }
}

