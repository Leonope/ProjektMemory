package web

import javax.inject._
import scala.concurrent.{ExecutionContext, Future}

// Wenn du den gemeinsamen Controller nutzt:
import backend.Backend
import main.GUI

@Singleton
class GuiStarter @Inject()(implicit ec: ExecutionContext) {
  @volatile private var started = false

  def start(): Unit = if (!started) {
    started = true

    // Headless sicher ausschalten, bevor AWT/FX initialisiert wird
    System.setProperty("java.awt.headless", "false")

    // Wenn deine GUI eine Swing/scala-swing "SimpleSwingApplication" ist:
    Future {
      javax.swing.SwingUtilities.invokeLater(() => GUI.main(Array.empty))
    }

  }
}


