import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import org.scalamock.scalatest.MockFactory
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.PrintStream
import util.{InputHandler, RealInputHandler, Observable, Observer}

class InputHandlerSpec extends AnyWordSpec with Matchers with MockFactory {

  "RealInputHandler" should {
    "read a line of input without a prompt" in {
      val input = "Test input"
      val inStream = new ByteArrayInputStream(input.getBytes)
      Console.withIn(inStream) {
        val handler = RealInputHandler
        handler.readLine() should be(input)
      }
    }

    "ask for input with a prompt and return the user input" in {
      val input = "User input here"
      val prompt = "Please enter your input:"
      val inStream = new ByteArrayInputStream(input.getBytes)
      val outStream = new ByteArrayOutputStream()
      Console.withIn(inStream) {
        Console.withOut(new PrintStream(outStream)) {
          val handler = RealInputHandler
          val receivedInput = handler.askForInput(prompt)
          receivedInput should be(input)
          outStream.toString should include(prompt) // Check if the prompt was printed
        }
      }
    }
  }
}



