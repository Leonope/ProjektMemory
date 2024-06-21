import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import org.scalamock.scalatest.MockFactory
import java.io.{ByteArrayOutputStream, PrintStream}
import util.{InputHandler, RealInputHandler}


class TuiSpec extends AnyWordSpec with Matchers with MockFactory {

  "A Tui" when {
    "update is called" should {
      "print that the game state has been updated" in {
        val controller = mock[Controller]
        (controller.add _).expects(*).once() //for any kind of update?

        val tui = new Tui(controller)
        val stream = new ByteArrayOutputStream()
        Console.withOut(new PrintStream(stream)) {
          tui.update
        }
        stream.toString should include("The game state has been updated.")
      }
    }

    "run is called" should {
      "handle exceptions by printing them" in {
        val controller = stub[Controller]
        (controller.add _).when(*) // any kind

        val exceptionMessage = "An error occurred"
        (controller.GameStarting _).when().throws(new RuntimeException(exceptionMessage))
        
        val tui = new Tui(controller)
        val stream = new ByteArrayOutputStream()
        Console.withOut(new PrintStream(stream)) {
          tui.run()
        }
        stream.toString should include(exceptionMessage)
      }

      "execute GameStarting without any exceptions" in {
        val controller = mock[Controller]
        (controller.add _).expects(*).once() // any
        (controller.GameStarting _).expects().once()

        val tui = new Tui(controller)
        noException should be thrownBy tui.run()
      }
    }

    "displayMessage is called" should {
      "print the provided message" in {
        val controller = mock[Controller]
        (controller.add _).expects(*).once() 

        val tui = new Tui(controller)
        val message = "Test message"
        val stream = new ByteArrayOutputStream()
        Console.withOut(new PrintStream(stream)) {
          tui.displayMessage(message)
        }
        stream.toString should include(message)
      }
    }
  /*
     "invoke GameStarting at the beginning" in {
      val controller = mock[Controller]
      val gameLogic = mock[Logic]

      // Assuming that gameLogic is a public mutable field
      controller.gameLogic = gameLogic // Directly assign mocked Logic instance
      
      (controller.add _).expects(*).once()
      (controller.GameStarting _).expects().once()
      (gameLogic.checkGameEnd _).expects().returning(false)

      val tui = new Tui(controller)
      tui.run()
    }

      /*"check if the game has ended and handle true correctly" in {
        val controller = mock[Controller]
        val gameLogic = mock[Logic]

        (controller.gameLogic _).expects().returning(gameLogic).anyNumberOfTimes()
        (gameLogic.checkGameEnd _).expects().returning(true).once()  // Expect this to be true

        val tui = new Tui(controller)
        val stream = new ByteArrayOutputStream()
        Console.withOut(new PrintStream(stream)) {
          tui.run()  // This should trigger the checkGameEnd logic
        }
        assert(stream.toString.contains("The game has ended."))
      }
    }*/


 "terminate the application" in {
    val originalSecurityManager = System.getSecurityManager // Save the original security manager
    System.setSecurityManager(new NoExitSecurityManager()) // Set the custom security manager

    val controller = mock[Controller]
    (controller.add _).expects(*).once()  // Expecting the add call

    val tui = new Tui(controller) // Creates an instance of Tui which calls controller.add
    val quitGameCommand = new tui.QuitGameCommand()

    val stream = new ByteArrayOutputStream()
    try {
      Console.withOut(new PrintStream(stream)) {
        intercept[SecurityException] {
          quitGameCommand.execute()
        }
      }
      stream.toString should include("Game exiting")
    } finally {
      System.setSecurityManager(originalSecurityManager) // Restore the original security manager
    }
  }

  "RestartGameCommand is executed" should {
    "restart the game" in {
      val controller = mock[Controller]
      (controller.add _).expects(*).once()  // Expecting the add call
      (controller.GameStarting _).expects().once()  // Expecting GameStarting to be called

      val tui = new Tui(controller) // Creates an instance of Tui which calls controller.add
      val restartGameCommand = new tui.RestartGameCommand(controller)

      val stream = new ByteArrayOutputStream()
      Console.withOut(new PrintStream(stream)) {
        restartGameCommand.execute()
      }
      stream.toString should include("Game is starting again")
    }
  }

     "endGamePrompt is called" should {
      "execute restart command when 'restart' is input" in {
        val controller = stub[Controller]
        val tui = new Tui(controller)
        val inputHandler = stub[InputHandler]
        tui.inputHandler = inputHandler
        
        (inputHandler.readLine _).when().returns("restart")
        (controller.GameStarting _).when()

        val stream = new ByteArrayOutputStream()
        Console.withOut(new PrintStream(stream)) {
          tui.endGamePrompt()
        }
        stream.toString should include("Game is starting again")
      }

      "execute quit command when 'quit' is input" in {
  val originalSecurityManager = System.getSecurityManager
  System.setSecurityManager(new NoExitSecurityManager())

  val controller = stub[Controller]
  val tui = new Tui(controller)
  val inputHandler = stub[InputHandler]
  tui.inputHandler = inputHandler
  
  (inputHandler.readLine _).when().returns("quit")
  
  val stream = new ByteArrayOutputStream()
  try {
    Console.withOut(new PrintStream(stream)) {
      intercept[SecurityException] {
        tui.endGamePrompt()
      }
    }
    stream.toString should include("Game exiting")
  } finally {
    System.setSecurityManager(originalSecurityManager)
  }
}

      "prompt again when an unknown command is input" in {
  val originalSecurityManager = System.getSecurityManager
  System.setSecurityManager(new NoExitSecurityManager())

  val controller = stub[Controller]
  val tui = new Tui(controller)
  val inputHandler = stub[InputHandler]
  tui.inputHandler = inputHandler
  
  (inputHandler.readLine _).when().returns("unknown")
  //(inputHandler.readLine _).when().returns("quit")

  val stream = new ByteArrayOutputStream()
        Console.withOut(new PrintStream(stream)) {
          tui.endGamePrompt()
    }
    stream.toString should include("Unknown Command pls type either 'quit' or 'restart'.")
    //stream.toString should include("Game exiting")
  
}
  }
}
class NoExitSecurityManager extends SecurityManager {
  override def checkPermission(perm: java.security.Permission): Unit = {
    if ("exitVM.0".equals(perm.getName)) {
      throw new SecurityException("System.exit attempted and blocked")
    }
  }*/
}
}