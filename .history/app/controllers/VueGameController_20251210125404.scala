package controllers

import javax.inject.{Inject, Singleton}

import play.api.mvc.{Action, AnyContent, ControllerComponents, AbstractController}
import play.api.libs.json.{JsValue, Json}

import backend.Backend

@Singleton
class VueGameController @Inject()(cc: ControllerComponents)
  extends AbstractController(cc) {

  /** Vue + Vuetify SPA Seite */
  def index: Action[AnyContent] = Action { implicit request =>
    Ok(views.html.game_vue())
  }

  /**
    * Wird von der Vue-Komponente beim Start aufgerufen.
    *
    * Body (JSON):
    * {
    *   "playerName":  "Alice",
    *   "playerCount": 2,
    *   "pairs":       6
    * }
    *
    * Wir rufen:
    *   - askPlayerCount(Some(playerCount))
    *   - askPlayerName(Some(name))
    *   - askCardCount(Some(pairs))
    *   - GameStarting()
    *
    * Das Deck bleibt clientseitig – hier wird nur die Logik informiert.
    */
  def startGameJson: Action[JsValue] = Action(parse.json) { implicit request =>
    val json = request.body

    val name =
      (json \ "playerName").asOpt[String]
        .map(_.trim)
        .filter(_.nonEmpty)
        .getOrElse("Player")

    val playerCount =
      (json \ "playerCount").asOpt[Int]
        .filter(_ >= 1)
        .getOrElse(1)

    val pairs =
      (json \ "pairs").asOpt[Int]
        .filter(_ > 0)
        .getOrElse(4)

    val c = Backend.controller
    c.askPlayerCount(Some(playerCount))
    c.askPlayerName(Some(name))
    c.askCardCount(Some(pairs))
    c.GameStarting()

    Ok(
      Json.obj(
        "status"      -> "ok",
        "playerName"  -> name,
        "playerCount" -> playerCount,
        "pairs"       -> pairs
      )
    )
  }
}
