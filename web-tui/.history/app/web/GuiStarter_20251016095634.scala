package web

import javax.inject._
import scala.concurrent.{ExecutionContext, Future}
import backend.Backend
import main.GUI

@Singleton
class GuiStarter @Inject()(implicit ec: ExecutionContext) {
  @volatile private var started = false
  def start(): Unit = if (!started) {
    started = true
    System.setProperty("java.awt.headless","false")
    // Falls deine GUI init(controller) braucht, hier machen:
    // GUI.init(Backend.controller)
    javax.swing.SwingUtilities.invokeLater(() => GUI.main(Array.empty))
  }
}



