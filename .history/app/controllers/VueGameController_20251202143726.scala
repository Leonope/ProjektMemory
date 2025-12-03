package controllers

import javax.inject._
import play.api.mvc._

@Singleton
class VueGameController @Inject()(cc: ControllerComponents)
  extends AbstractController(cc) {

  /** Zeigt die Vue-basierte SPA-Version des Spiels. */
  def index: Action[AnyContent] = Action { implicit request =>
    Ok(views.html.game_vue())
  }
}

