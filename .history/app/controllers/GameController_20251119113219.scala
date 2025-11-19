package controllers

import javax.inject._
import play.api.mvc._
import web.WebTUI
import backend.Backend
import play.api.libs.json._

@Singleton
class GameController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {

  // Alte Text-UI
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

  // --- NEW UI (Bootstrap) ---

  def indexBootstrap: Action[AnyContent] = Action { implicit request =>
    Ok(
      views.html.game_bootstrap(
        message     = "Willkommen beim Memory!",
        pairs       = 8,
        playerName  = "",
        playerCount = 1
      )
    )
  }

  // klassische Variante: rendert HTML (kannst du behalten, wenn du willst)
  def newGameBootstrap: Action[AnyContent] = Action { implicit request =>
    val data        = request.body.asFormUrlEncoded.getOrElse(Map.empty)
    val name        = data.get("playerName").flatMap(_.headOption).filter(_.trim.nonEmpty).getOrElse("Player")
    val pairs: Int  = data.get("pairs").flatMap(_.headOption).flatMap(s => scala.util.Try(s.toInt).toOption).getOrElse(8)
    val pCount: Int = data.get("playerCount").flatMap(_.headOption).flatMap(s => scala.util.Try(s.toInt).toOption).getOrElse(1)

    val c = Backend.controller
    c.askPlayerCount(Some(pCount))
    c.askPlayerName(Some(name))
    c.askCardCount(Some(pairs))
    c.GameStarting()

    Ok(
      views.html.game_bootstrap(
        message     = s"Spiel für $name mit $pairs Paaren und $pCount Spieler(n) initialisiert.",
        pairs       = pairs,
        playerName  = name,
        playerCount = pCount
      )
    )
  }

  // NEU: JSON-Endpoint für Ajax mit jQuery
  def newGameBootstrapJson: Action[JsValue] = Action(parse.json) { implicit request =>
    val json = request.body

    val name        = (json \ "playerName").asOpt[String].filter(_.trim.nonEmpty).getOrElse("Player")
    val pairs       = (json \ "pairs").asOpt[Int].getOrElse(8)
    val playerCount = (json \ "playerCount").asOpt[Int].getOrElse(1)

    val c = Backend.controller
    c.askPlayerCount(Some(playerCount))
    c.askPlayerName(Some(name))
    c.askCardCount(Some(pairs))
    c.GameStarting()

    Ok(
      Json.obj(
        "message"     -> s"Spiel für $name mit $pairs Paaren und $playerCount Spieler(n) initialisiert.",
        "playerName"  -> name,
        "pairs"       -> pairs,
        "playerCount" -> playerCount
      )
    )
  }

  def test: Action[AnyContent] = Action {
    println("TEST from Play")
    WebTUI.debug("TEST from Play")
    Ok("ok")
  }

  def state: Action[AnyContent] = Action {
    Ok(WebTUI.currentLog).as("text/plain; charset=utf-8")
  }
}



