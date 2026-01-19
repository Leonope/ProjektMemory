package controllers

import play.silhouette.api._
import javax.inject._
import play.api.mvc._

import scala.concurrent.ExecutionContext

@Singleton
class SecuredGameController @Inject() (
  cc: ControllerComponents,
  silhouette: Silhouette[modules.DefaultEnv]
)(implicit ec: ExecutionContext) extends AbstractController(cc) {

  def index: Action[AnyContent] = silhouette.SecuredAction { implicit request =>
    // Du wolltest die bestehende /game View → wir leiten einfach um
    Redirect(routes.GameController.index)
  }
}

