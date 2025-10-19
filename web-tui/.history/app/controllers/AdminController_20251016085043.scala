package controllers

import javax.inject._
import play.api.mvc._
import web.GuiStarter

@Singleton
class AdminController @Inject()(cc: ControllerComponents, gui: GuiStarter)
  extends AbstractController(cc) {

  def startGui: Action[AnyContent] = Action {
    gui.start()
    Ok("GUI gestartet.")
  }
}

