package controllers

import javax.inject._
import play.api.mvc._
import web.WebTUI


@Singleton
class GameController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {
  def index: Action[AnyContent] = Action { implicit request =>
    Ok(views.html.game(WebTUI.render()))
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
    Ok(views.html.quit())
  }

  def indexBootstrap = Action { implicit request =>
  Ok(views.html.game_bootstrap("Noch kein Spiel gestartet"))
}

def newGameBootstrap = Action { implicit request =>
  val data = request.body.asFormUrlEncoded.getOrElse(Map.empty)
  val name  = data.get("playerName").flatMap(_.headOption).getOrElse("Player")
  val pairs = data.get("pairs").flatMap(_.headOption).flatMap(s => scala.util.Try(s.toInt).toOption).getOrElse(8)
  Ok(views.html.game_bootstrap(s"Spiel für $name mit $pairs Paaren gestartet!"))
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
