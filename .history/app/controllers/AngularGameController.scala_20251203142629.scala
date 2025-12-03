package controllers

import javax.inject._
import play.api.mvc._

@Singleton
class AngularGameController @Inject()(cc: ControllerComponents)
  extends AbstractController(cc) {

  /** Zeigt die Angular-basierte SPA-Version des Spiels. */
  def index: Action[AnyContent] = Action { implicit request =>
    Ok(views.html.game_angular())
  }
}

