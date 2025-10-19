package controllers

import javax.inject._
import play.api.mvc._
import web.GuiStarter

@Singleton
class AdminController @Inject()(cc: ControllerComponents, gui: GuiStarter)
  extends AbstractController(cc) {

  def startGui = Action {
  // WICHTIG: Tee zuerst sicher installieren
  WebTUI.render()              // installiert den Tee (ensureInstalled)
  gui.start()                  // startet dann die GUI im selben Prozess
  Ok("GUI gestartet.")
}
}

