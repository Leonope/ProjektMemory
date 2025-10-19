// app/web/GuiStarter.scala
import javax.inject._
import scala.concurrent.{ExecutionContext, Future}
import backend.Backend
import web.GuiStarter 
import main.GUI

@Singleton
class GuiStarter @Inject()(implicit ec: ExecutionContext) {
  @volatile private var started = false

  def start(): Unit = if (!started) {
    started = true
    // Make 100% sure headless is off BEFORE AWT is initialized:
    System.setProperty("java.awt.headless", "false")

    // If your GUI is Swing (SimpleSwingApplication):
    Future {
      // optional: run on EDT
      javax.swing.SwingUtilities.invokeLater(() => GUI.main(Array.empty))
    }


  }
}

