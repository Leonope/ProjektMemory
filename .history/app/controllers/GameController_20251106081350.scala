package controllers

import javax.inject._
import play.api.mvc._
import web.WebTUI
import backend.Backend // <- Hier holen wir uns die gemeinsame Controller-Instanz

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

  // --- Neue Bootstrap-UI ---
  def indexBootstrap: Action[AnyContent] = Action { implicit request =>
    Ok(
      views.html.game_bootstrap(
        message      = "Willkommen beim Memory!",
        pairs        = 8,
        playerName   = "",
        playerCount  = 1 // Default
      )
    )
  }

  /**
    * Nimmt die Form-Werte entgegen, leitet sie an Backend weiter und ruft
    * - askPlayerCount
    * - askPlayerName
    * - askCardCount
    * - GameStarting
    * in genau dieser Reihenfolge auf.
    */
  def newGameBootstrap: Action[AnyContent] = Action { implicit request =>
    val data        = request.body.asFormUrlEncoded.getOrElse(Map.empty)
    val name        = data.get("playerName").flatMap(_.headOption).filter(_.trim.nonEmpty).getOrElse("Player")
    val pairs: Int  = data.get("pairs").flatMap(_.headOption).flatMap(s => scala.util.Try(s.toInt).toOption).getOrElse(8)
    val pCount: Int = data.get("playerCount").flatMap(_.headOption).flatMap(s => scala.util.Try(s.toInt).toOption).getOrElse(1)

    // --- Übergabe an Backend (IController) ---
    // bis zu den Methoden:
    //   askPlayerCount / askPlayerName / askCardCount / GameStarting
    val c = Backend.controller
    c.askPlayerCount(Some(pCount))
    c.askPlayerName(Some(name))
    c.askCardCount(Some(pairs))
    c.GameStarting()

    // UI-Antwort
    Ok(
      views.html.game_bootstrap(
        message      = s"Spiel für $name mit $pairs Paaren und $pCount Spieler(n) initialisiert.",
        pairs        = pairs,
        playerName   = name,
        playerCount  = pCount
      )
    )
  }

  def test: Action[AnyContent] = Action {
    println("TEST from Play")           // fürs Terminal
    WebTUI.debug("TEST from Play")      // geht in den Web-Puffer
    Ok("ok")
  }

  def state: Action[AnyContent] = Action {
    Ok(WebTUI.currentLog).as("text/plain; charset=utf-8")
  }
}


