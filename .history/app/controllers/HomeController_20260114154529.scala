package controllers

import javax.inject._
import play.api.mvc._
import security.RequireLogin

@Singleton
class HomeController @Inject()(
  val controllerComponents: ControllerComponents,
  requireLogin: RequireLogin
) extends BaseController {

  def index() = requireLogin { implicit request =>
    Ok(views.html.index())
  }

  def explanation() = requireLogin { implicit request =>
    Ok(views.html.explanation())
  }
}
