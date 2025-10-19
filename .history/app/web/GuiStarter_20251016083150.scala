package web

import javax.inject._
import backend.Backend
import scala.concurrent.{ExecutionContext, Future}
import main.GUI

@Singleton
class GuiStarter @Inject()(implicit ec: ExecutionContext) {
  @volatile private var started = false

  /** Startet die ScalaFX/JavaFX-GUI einmalig mit dem *gleichen* Controller. */
  def start(): Unit = if (!started) {
    started = true
    GUI.init(Backend.controller)          // deine bestehende Methode
    Future { GUI.main(Array.empty) }      // JavaFX im eigenen Thread starten
  }
}
