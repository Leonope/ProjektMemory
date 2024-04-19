import org.scalatest.wordspec.AnyWordSpec
import org.scalamock.scalatest.MockFactory
import scala.util.{Success, Failure}
import java.io.{ByteArrayOutputStream,ByteArrayInputStream, PrintStream}
import Memory.MemoryInput

class MemorySpec extends AnyWordSpec with MockFactory {

  "Memory" when {
    "runApp is called" should {
      "print success message when game starts successfully" in {
        val mockInputHandler = mock[InputHandler]
        (mockInputHandler.readLine _).expects().returning("1").once()
        (mockInputHandler.readLine _).expects().returning("Alice").once()

        val outCapture = new java.io.ByteArrayOutputStream()
        Console.withOut(new java.io.PrintStream(outCapture)) {
          MemoryInput.runApp(mockInputHandler)
        }

        assert(outCapture.toString.contains("Game started successfully."))
      }

      "print error message when game fails to start" in {
        val mockInputHandler = mock[InputHandler]
        (mockInputHandler.readLine _).expects().returning("4").once() // Invalid number of players

        val outCapture = new java.io.ByteArrayOutputStream()
        Console.withOut(new java.io.PrintStream(outCapture)) {
          MemoryInput.runApp(mockInputHandler)
        }

        assert(outCapture.toString.contains("Error starting game"))
      }

      "RealInputHandler" should {
    "return the correct string when readLine is called" in {
      val testInput = "test input string\n"
      val inStream = new ByteArrayInputStream(testInput.getBytes)
      Console.withIn(inStream) {
        assert(RealInputHandler.readLine() === "test input string")
      }
    }
  }

      /*"handle unhandled exceptions" in {
        val mockInputHandler = mock[InputHandler]
        (mockInputHandler.readLine _).expects().throwing(new RuntimeException("Unexpected error"))

        val outCapture = new java.io.ByteArrayOutputStream()
        Console.withOut(new java.io.PrintStream(outCapture)) {
          MemoryInput.runApp(mockInputHandler)
        }

        assert(outCapture.toString.contains("Unhandled error"))
      }*/
    }
  }
}

