package controllers

import javax.inject._
import play.api.mvc._

@Singleton
class ReactGameController @Inject()(cc: ControllerComponents)
  extends AbstractController(cc) {

  /** Zeigt die React-basierte SPA-Version des Spiels. */
  def index: Action[AnyContent] = Action { implicit request =>
    Ok(views.html.game_react())
  }
}

