/*import org.scalatest.wordspec.AnyWordSpec
import scala.util.{Success, Failure}

class MainSpec extends AnyWordSpec {
  "Memory's runApp method" should {
    "handle valid input correctly" in {
      val dummyInputHandler = new DummyInputHandler()
      dummyInputHandler.setInput(Seq("1", "Alice"))
      Memory.runApp(dummyInputHandler)
      // Überprüfen Sie die Ausgabe oder den Zustand nach Ausführung
      assert(dummyInputHandler.output.contains("Welcome to the Memory Game, Alice! Have fun!"))
    }

    "handle invalid input correctly" in {
      val dummyInputHandler = new DummyInputHandler()
      dummyInputHandler.setInput(Seq("5")) // Ungültige Spieleranzahl
      Memory.runApp(dummyInputHandler)
      // Überprüfen Sie, dass die richtige Fehlermeldung ausgegeben wurde
      assert(dummyInputHandler.output.contains("Error starting game: Player count must be between 1 and 3"))
    }
  }
}*/
