error id: file:///C:/Users/leo11/OneDrive/Desktop/HTWG/AIN/Semester%203/Software-Engineering/ProjektMemory/src/test/scala/MemorySpec.scala:[215..216) in Input.VirtualFile("file:///C:/Users/leo11/OneDrive/Desktop/HTWG/AIN/Semester%203/Software-Engineering/ProjektMemory/src/test/scala/MemorySpec.scala", "import org.scalatest.wordspec.AnyWordSpec
import org.scalamock.scalatest.MockFactory
import scala.util.{Success, Failure}
import java.io.{ByteArrayOutputStream,ByteArrayInputStream, PrintStream}
package your.Memory._

class MemoryGameSpec extends AnyWordSpec with MockFactory {

  "MemorySpec" when {
    "askPlayerCount is called" should {
      "return a valid count within range" in {
        val mockInputHandler = mock[Memory.InputHandler]
        (mockInputHandler.readLine _).expects().returning("2")
        assert(Memory.MemoryInput.askPlayerCount(mockInputHandler) == Success(2))
      }

      "return failure on invalid number" in {
        val mockInputHandler = mock[Memory.InputHandler]
        (mockInputHandler.readLine _).expects().returning("4")
        assert(Memory.MemoryInput.askPlayerCount(mockInputHandler).isFailure)
      }

      "return success for edge cases" in {
        val mockInputHandler = mock[Memory.InputHandler]
        (mockInputHandler.readLine _).expects().returning("1")
        assert(Memory.MemoryInput.askPlayerCount(mockInputHandler) == Success(1))
        //shouldbe
        (mockInputHandler.readLine _).expects().returning("3")
        assert(Memory.MemoryInput.askPlayerCount(mockInputHandler) == Success(3))
      }
    }

    "checkPlayerCount is called" should {
      "handle only one player" in {
        assert(checkPlayerCount(1) == Success("One player has been selected."))
      }
      
      "fail for more than one player" in {
        assert(Memory.MemoryInput.checkPlayerCount(2).isFailure)
        assert(Memory.MemoryInput.checkPlayerCount(3).isFailure)
      }
    }

    "GameStarting is called" should {
      "handle valid player count and name" in {
        val mockInputHandler = mock[Memory.InputHandler]
        (mockInputHandler.readLine _).expects().returning("1")
        (mockInputHandler.readLine _).expects().returning("Bob")
        assert(Memory.MemoryInput.GameStarting(mockInputHandler).isSuccess)
      }

      "fail with invalid player count" in {
        val mockInputHandler = mock[Memory.InputHandler]
        (mockInputHandler.readLine _).expects().returning("4")
        assert(Memory.MemoryInput.GameStarting(mockInputHandler).isFailure)
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

")
file:///C:/Users/leo11/OneDrive/Desktop/HTWG/AIN/Semester%203/Software-Engineering/ProjektMemory/src/test/scala/MemorySpec.scala
file:///C:/Users/leo11/OneDrive/Desktop/HTWG/AIN/Semester%203/Software-Engineering/ProjektMemory/src/test/scala/MemorySpec.scala:5: error: expected identifier; obtained uscore
package your.Memory._
                    ^
#### Short summary: 

expected identifier; obtained uscore