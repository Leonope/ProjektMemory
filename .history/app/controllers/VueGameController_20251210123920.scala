package controllers

import javax.inject.*
import play.api.mvc.*
import play.api.libs.json.*
import backend.Backend

@Singleton
class VueGameController @Inject()(cc: ControllerComponents)
  extends AbstractController(cc) {

  /** Vue SPA Seite */
  def index = Action { implicit request =>
    Ok(views.html.game_vue())
  }

  /** Vue sendet Startdaten → Backend → Return Deck */
  def startGame = Action(parse.json) { implicit request =>
    val json = request.body

    val name        = (json \ "playerName").asOpt[String].getOrElse("Player")
    val pCount      = (json \ "playerCount").asOpt[Int].getOrElse(1)
    val pairs       = (json \ "pairs").asOpt[Int].getOrElse(4)

    val c = Backend.controller

    // Backend-Flow wie gefordert
    c.askPlayerCount(Some(pCount))
    c.askPlayerName(Some(name))
    c.askCardCount(Some(pairs))
    c.GameStarting()

    // Deck vom Backend holen
    val deck: Seq[Int] = c.gameLogic.getMatrixValues.getOrElse(Seq())

    Ok(
      Json.obj(
        "status" -> "ok",
        "deck"   -> deck
      )
    )
  }
}



