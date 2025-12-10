package controllers

import javax.inject.{Inject, Singleton}

import play.api.mvc.{Action, AnyContent, ControllerComponents, AbstractController}
import play.api.libs.json.{JsValue, Json}

import backend.Backend

@Singleton
class VueGameController @Inject()(cc: ControllerComponents)
  extends AbstractController(cc) {

  /** Vue SPA Version */
  def index: Action[AnyContent] = Action { implicit request =>
    Ok(views.html.game_vue())
  }

  /** Startet ein Spiel über Vue → Backend → liefert Deck zurück */
  def startGame: Action[JsValue] = Action(parse.json) { implicit request =>
    val json = request.body

    val name   = (json \ "playerName").asOpt[String].getOrElse("Player")
    val pCount = (json \ "playerCount").asOpt[Int].getOrElse(1)
    val pairs  = (json \ "pairs").asOpt[Int].getOrElse(4)

    val c = Backend.controller
    c.askPlayerCount(Some(pCount))
    c.askPlayerName(Some(name))
    c.askCardCount(Some(pairs))
    c.GameStarting()

    val deck = c.gameLogic.getMatrixValues.getOrElse(Seq())

    Ok(
      Json.obj(
        "status" -> "ok",
        "deck"   -> deck
      )
    )
  }
}
