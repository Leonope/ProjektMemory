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

    // 1) Controller setzen – GANZ WICHTIG: vor dem Start!
    GUI.init(Backend.controller)

    // 2) GUI starten (Swing/scala-swing):
    Future {
      javax.swing.SwingUtilities.invokeLater(() => GUI.main(Array.empty))
    }
    // Falls ScalaFX/JavaFX: statt invokeLater => Future { GUI.main(Array.empty) }
  }
}



