package controllers

import javax.inject._
import play.api.mvc._
import web.WebTUI
import play.api.libs.json._
import backend.Backend
import java.nio.file.{Files, Paths}

// Pekko / WebSocket 
import org.apache.pekko.actor.ActorSystem
import org.apache.pekko.stream.Materializer
import play.api.mvc.WebSocket
import play.api.libs.streams.ActorFlow

// Companion-Object als simpler In-Memory-Store für Highscores
object GameController {
  case class HighscoreEntry(playerName: String, pairs: Int, moves: Int)

  @volatile private var highscores: List[HighscoreEntry] = Nil

  def addHighscore(e: HighscoreEntry): Unit = synchronized {
    highscores = (e :: highscores)
      .sortBy(h => (h.moves, -h.pairs))
      .take(5)
  }

  def getHighscores: List[HighscoreEntry] = synchronized {
    highscores
  }
}

@Singleton
class GameController @Inject()(cc: ControllerComponents)
                              (implicit system: ActorSystem, mat: Materializer)
  extends AbstractController(cc) {

  import GameController.HighscoreEntry

  // ==== WebSocket: /ws/game ==========================================
  def gameSocket: WebSocket = WebSocket.accept[String, String] { _ =>
    ActorFlow.actorRef(out => GameWebSocketActor.props(out))
  }
  // ===================================================================

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
        pairs        = 2,
        playerName   = "",
        playerCount  = 1
      )
    )
  }

  /** Klassischer HTML-POST  */
  def newGameBootstrap: Action[AnyContent] = Action { implicit request =>
    val data        = request.body.asFormUrlEncoded.getOrElse(Map.empty)
    val name        = data.get("playerName").flatMap(_.headOption).filter(_.trim.nonEmpty).getOrElse("Player")
    val pairs: Int  = data.get("pairs").flatMap(_.headOption).flatMap(s => scala.util.Try(s.toInt).toOption).getOrElse(2)
    val pCount: Int = data.get("playerCount").flatMap(_.headOption).flatMap(s => scala.util.Try(s.toInt).toOption).getOrElse(1)

    val c = Backend.controller
    c.askPlayerCount(Some(pCount))
    c.askPlayerName(Some(name))
    c.askCardCount(Some(pairs))
    c.GameStarting()

    Ok(
      views.html.game_bootstrap(
        message      = s"Spiel für $name mit $pairs Paaren und $pCount Spieler(n) initialisiert.",
        pairs        = pairs,
        playerName   = name,
        playerCount  = pCount
      )
    )
  }

  /** JSON-Endpoint für jQuery/Ajax zum Starten eines Spiels */
  def newGameBootstrapJson: Action[JsValue] = Action(parse.json) { implicit request =>
    val json        = request.body
    val name        = (json \ "playerName").asOpt[String].filter(_.trim.nonEmpty).getOrElse("Player")
    val pairs       = (json \ "pairs").asOpt[Int].getOrElse(2)
    val playerCount = (json \ "playerCount").asOpt[Int].getOrElse(1)

    val c = Backend.controller
    c.askPlayerCount(Some(playerCount))
    c.askPlayerName(Some(name))
    c.askCardCount(Some(pairs))
    c.GameStarting()

    Ok(
      Json.obj(
        "playerName"  -> name,
        "pairs"       -> pairs,
        "playerCount" -> playerCount,
        "message"     -> s"Spiel für $name mit $pairs Paaren und $playerCount Spieler(n) initialisiert."
      )
    )
  }

  /** XML für Matrix */
  def getXml: Action[AnyContent] = Action {
    val path = Paths.get("matrix.xml")
    if (Files.exists(path)) {
      val data = Files.readAllBytes(path)
      Ok(data).as("application/xml; charset=utf-8")
    } else {
      NotFound("XML not found")
    }
  }

  // --- Presets als JSON ---
  def preset(key: String): Action[AnyContent] = Action {
    // Name leer, Spieler immer 1
    val presetOpt: Option[(String, Int, Int)] = key match {
      case "easy"     => Some(("", 4, 1))
      case "standard" => Some(("", 8, 1))
      case "pro"      => Some(("", 12, 1))
      case _          => None
    }

    presetOpt match {
      case Some((name, pairs, pCount)) =>
        Ok(
          Json.obj(
            "playerName"  -> name,
            "pairs"       -> pairs,
            "playerCount" -> pCount
          )
        )
      case None =>
        NotFound(Json.obj("error" -> s"Unbekanntes Preset: $key"))
    }
  }

  // --- Highscore-JSON: POST + GET ---
  def submitHighscore: Action[JsValue] = Action(parse.json) { implicit request =>
    val json   = request.body
    val name   = (json \ "playerName").asOpt[String].filter(_.trim.nonEmpty).getOrElse("Player")
    val pairs  = (json \ "pairs").asOpt[Int].getOrElse(2)
    val moves  = (json \ "moves").asOpt[Int].getOrElse(0)

    GameController.addHighscore(HighscoreEntry(name, pairs, moves))

    Ok(Json.obj("status" -> "ok"))
  }

  def getHighscoresJson: Action[AnyContent] = Action { implicit request =>
    val filterPairsOpt: Option[Int] =
      request.getQueryString("pairs").flatMap(s => scala.util.Try(s.toInt).toOption)

    val baseList = GameController.getHighscores
    val list = filterPairsOpt match {
      case Some(p) => baseList.filter(_.pairs == p)
      case None    => baseList
    }

    val jsonList = list.map { e =>
      Json.obj(
        "playerName" -> e.playerName,
        "pairs"      -> e.pairs,
        "moves"      -> e.moves
      )
    }
    Ok(Json.toJson(jsonList))
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



