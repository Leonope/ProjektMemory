package controllers

import javax.inject._
import play.api.mvc._
import web.WebTUI


@Singleton
class GameController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {
  def index: Action[AnyContent] = Action { implicit request =>
    Ok(views.html.game(WebTUI.render()))
  }
  def state: Action[AnyContent] = Action {
    Ok(WebTUI.currentLog).as("text/plain; charset=utf-8")
  }
  def cmd: Action[AnyContent] = Action { implicit request =>
    val input = request.body.asFormUrlEncoded.flatMap(_("cmd").headOption).getOrElse("")
    WebTUI.handle(input)
    Redirect(routes.GameController.index)
  }
  def newGame: Action[AnyContent] = Action { implicit request =>
    WebTUI.newGame()
    Redirect(routes.GameController.index)
  }
  def quit: Action[AnyContent] = Action { implicit request =>
    Ok("<h1>Danke!</h1>").as(HTML)
  }

  def test: Action[AnyContent] = Action {
  println("TEST from Play")           // bleibt fürs Terminal
  WebTUI.debug("TEST from Play")      // <- geht DIREKT in den Web-Puffer
  Ok("ok")
}
def state = Action {
    Ok(WebTUI.currentLog).as("text/plain; charset=utf-8")
  }
}
