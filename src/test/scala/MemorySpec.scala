import org.scalatest.wordspec.AnyWordSpec
import org.scalamock.scalatest.MockFactory
import scala.util.{Success, Failure}
import java.io.{ByteArrayOutputStream,ByteArrayInputStream, PrintStream}
import Memory.MemoryInput
import org.scalatest.matchers.should.Matchers

class MemoryGameSpec extends AnyWordSpec with Matchers with MockFactory {

  "MemoryGameSpec" when {
    "askPlayerCount is called" should {
      "return a valid count within range" in {
        val mockInputHandler = mock[Memory.InputHandler]
        (mockInputHandler.readLine _).expects().returning("2")
        Memory.MemoryInput.askPlayerCount(mockInputHandler) should be (Success(2))
      }

      "return failure on invalid number" in {
        val mockInputHandler = mock[Memory.InputHandler]
        (mockInputHandler.readLine _).expects().returning("4")
        Memory.MemoryInput.askPlayerCount(mockInputHandler).isFailure should be (true)
      }

      "return success for edge cases" in {
        val mockInputHandler = mock[Memory.InputHandler]
        (mockInputHandler.readLine _).expects().returning("1")
        Memory.MemoryInput.askPlayerCount(mockInputHandler) should be (Success(1))
        (mockInputHandler.readLine _).expects().returning("3")
        Memory.MemoryInput.askPlayerCount(mockInputHandler) should be (Success(3))
      }
    }

    "checkPlayerCount is called" should {
      "handle only one player" in {
        Memory.MemoryInput.checkPlayerCount(1) should be (Success("One player has been selected."))
      }
      
      "fail for more than one player" in {
        Memory.MemoryInput.checkPlayerCount(2).isFailure should be (true)
        Memory.MemoryInput.checkPlayerCount(3).isFailure should be (true)
      }
    }

    "GameStarting is called" should {
      "handle valid player count and name" in {
        val mockInputHandler = mock[Memory.InputHandler]
        (mockInputHandler.readLine _).expects().returning("1")
        (mockInputHandler.readLine _).expects().returning("Bob")
        (mockInputHandler.readLine _).expects().returning("2").once()
        Memory.MemoryInput.GameStarting(mockInputHandler).isSuccess should be (true)
      }

      "fail with invalid player count" in {
        val mockInputHandler = mock[Memory.InputHandler]
        (mockInputHandler.readLine _).expects().returning("4")
        Memory.MemoryInput.GameStarting(mockInputHandler).isFailure should be (true)
      }
      "fail with invalid card count" in {
        val mockInputHandler = mock[Memory.InputHandler]
        (mockInputHandler.readLine _).expects().returning("1")
        (mockInputHandler.readLine _).expects().returning("Bob")
        (mockInputHandler.readLine _).expects().returning("0")
        Memory.MemoryInput.GameStarting(mockInputHandler).isFailure should be (true)
      }
    }

    "greetPlayer is called" should {
      "print the correct greeting" in {
        val name = "Bob"
        val outCapture = new ByteArrayOutputStream()
        Console.withOut(new PrintStream(outCapture)) {
          Memory.MemoryInput.greetPlayer(name)
        }
       outCapture.toString should include ("Welcome to the Memory Game, Bob! Have fun!")
      }
    }
  }
}
