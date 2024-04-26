import org.scalatest.wordspec.AnyWordSpec
import org.scalamock.scalatest.MockFactory
import scala.util.{Success, Failure}
import java.io.{ByteArrayOutputStream,ByteArrayInputStream, PrintStream}
import Memory.MemoryInput
import org.scalatest.matchers.should.Matchers

class MemoryMainSpec extends AnyWordSpec with MockFactory with Matchers {

  "MainSpec" when {
    "runApp is called" should {
      "print success message when game starts successfully" in {
        val mockInputHandler = mock[Memory.InputHandler]
        (mockInputHandler.readLine _).expects().returning("1").once()//players
        (mockInputHandler.readLine _).expects().returning("Alice").once()//name
        (mockInputHandler.readLine _).expects().returning("2").once()//card

        val outCapture = new java.io.ByteArrayOutputStream()
        Console.withOut(new java.io.PrintStream(outCapture)) {
          MemoryInput.runApp(mockInputHandler)
        }

        outCapture.toString should include ("Game setup completed successfully.")
      }

      "print error message when game fails to start" in {
        val mockInputHandler = mock[Memory.InputHandler]
        (mockInputHandler.readLine _).expects().returning("4").once() // Invalid number of players

        val outCapture = new java.io.ByteArrayOutputStream()
        Console.withOut(new java.io.PrintStream(outCapture)) {
          MemoryInput.runApp(mockInputHandler)
        }

        outCapture.toString should include ("Error starting game")
      }

      "RealInputHandler" should {
    "return the correct string when readLine is called" in {
      val testInput = "test input string\n"
      val inStream = new ByteArrayInputStream(testInput.getBytes)
      Console.withIn(inStream) {
        Memory.RealInputHandler.readLine() should be ("test input string")
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

