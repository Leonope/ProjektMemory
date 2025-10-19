package controllers

import javax.inject._
import play.api.mvc._
import web.{GuiStarter, WebTUI}

@Singleton
class AdminController @Inject()(cc: ControllerComponents, gui: GuiStarter)
  extends AbstractController(cc) {

  def startGui: Action[AnyContent] = Action {
    // installiert den Output-Hook & startet Game beim ersten Aufruf
    WebTUI.render()
    gui.start()
    Ok("GUI gestartet.")
  }
}

