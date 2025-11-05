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

  // --- NEW UI ---
  def indexBootstrap = Action { implicit request =>
    Ok(views.html.game_bootstrap(
      message     = "Willkommen beim Memory!",
      pairs       = 8,
      playerName  = "",
      playerCount = 1 // Default
    ))
  }

  def newGameBootstrap = Action { implicit request =>
    val data  = request.body.asFormUrlEncoded.getOrElse(Map.empty)
    val name  = data.get("playerName").flatMap(_.headOption).getOrElse("Player")
    val pairs = data.get("pairs").flatMap(_.headOption).flatMap(s => scala.util.Try(s.toInt).toOption).getOrElse(8)
    val playerCount = data.get("playerCount").flatMap(_.headOption).flatMap(s => scala.util.Try(s.toInt).toOption).getOrElse(1)

    // TODO: falls du die Spielerzahl im Backend brauchst, hier an deinen Controller/Service weitergeben.

    Ok(views.html.game_bootstrap(
      message     = s"Spiel für $name mit $pairs Paaren und $playerCount Spieler(n) gestartet!",
      pairs       = pairs,
      playerName  = name,
      playerCount = playerCount
    ))
  }

  def test: Action[AnyContent] = Action {
    println("TEST from Play")
    WebTUI.debug("TEST from Play")
    Ok("ok")
  }

  def state = Action {
    Ok(WebTUI.currentLog).as("text/plain; charset=utf-8")
  }
}

