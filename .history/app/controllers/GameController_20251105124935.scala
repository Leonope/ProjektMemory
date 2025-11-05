package controllers

import javax.inject._
import play.api.mvc._
import scala.util.Try
import backend.Backend  // dein Singleton-Controller

@Singleton
class GameController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {

  /** Zeigt die Web-UI mit den aktuell im Backend gesetzten Werten. */
  def webUi = Action { implicit req =>
  val c = Backend.controller
  val name   = c.getPlayerName.getOrElse("")
  val count  = c.getPlayerCount.getOrElse(2)
  val cards  = c.getCardCount.getOrElse(16)

  Ok(views.html.game_webui(
  req.flash.get("msg").getOrElse("Konfigurieren und Speichern."),
  name,
  count,
  cards
))
}

  /** Nimmt Formularwerte entgegen und setzt sie via askPlayerName/askPlayerCount/askCardCount. */
  def configureFromWeb = Action { implicit req =>
    val form = req.body.asFormUrlEncoded.getOrElse(Map.empty)

    val nameOpt       = form.get("playerName").flatMap(_.headOption).filter(_.nonEmpty)
    val playersOpt    = form.get("numPlayers").flatMap(_.headOption).flatMap(s => Try(s.toInt).toOption)
    val cardCountOpt  = form.get("cardCount").flatMap(_.headOption).flatMap(s => Try(s.toInt).toOption)

    val c = Backend.controller

    // Nur setzen, wenn im Formular vorhanden. (Dein Interface erwartet Option[...] – wir geben Some(...) weiter)
    nameOpt.foreach(n  => c.askPlayerName(Some(n)))
    playersOpt.foreach(p => c.askPlayerCount(Some(p)))
    cardCountOpt.foreach(k => c.askCardCount(Some(k)))

    // Optional: wenn du bei jeder Änderung gleich den Start vorbereiten willst:
    // c.GameStarting()

    val msg =
      s"Einstellungen gespeichert: " +
      s"Name=${nameOpt.getOrElse(c.getPlayerName.getOrElse("Player"))}, " +
      s"Spieler=${playersOpt.getOrElse(c.getPlayerCount.getOrElse(2))}, " +
      s"Karten=${cardCountOpt.getOrElse(c.getCardCount.getOrElse(16))}"

    Redirect(routes.GameController.webUi).flashing("msg" -> msg)
  }
}

