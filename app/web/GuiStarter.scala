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
    System.setProperty("java.awt.headless", "false")

    // 1) GANZ WICHTIG: Controller VOR dem Start setzen
    GUI.init(Backend.controller)

    // 2) Dann die GUI starten (Swing/scala-swing auf dem EDT)
    Future {
      javax.swing.SwingUtilities.invokeLater(() => GUI.main(Array.empty))
    }
  }
}




