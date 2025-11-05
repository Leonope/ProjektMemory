error id: file:///C:/Users/leo11/OneDrive/Desktop/HTWG/AIN/Semester5/Web-apps/play-server/ProjektMemory/app/controllers/GameController.scala:`<none>`.
file:///C:/Users/leo11/OneDrive/Desktop/HTWG/AIN/Semester5/Web-apps/play-server/ProjektMemory/app/controllers/GameController.scala
empty definition using pc, found symbol in pc: `<none>`.
empty definition using semanticdb
empty definition using fallback
non-local guesses:
	 -javax/inject/name.
	 -javax/inject/name#
	 -javax/inject/name().
	 -play/api/mvc/name.
	 -play/api/mvc/name#
	 -play/api/mvc/name().
	 -name.
	 -name#
	 -name().
	 -scala/Predef.name.
	 -scala/Predef.name#
	 -scala/Predef.name().
offset: 1064
uri: file:///C:/Users/leo11/OneDrive/Desktop/HTWG/AIN/Semester5/Web-apps/play-server/ProjektMemory/app/controllers/GameController.scala
text:
```scala
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
//new bootstrap stuff here
  def indexBootstrap = Action { implicit request =>
  Ok(views.html.game_bootstrap("Willkommen beim Memory!", pairs = 8, playerName = ""))
}

def newGameBootstrap = Action { implicit request =>
  val data = request.body.asFormUrlEncoded.getOrElse(Map.empty)
  val nam@@e  = data.get("playerName").flatMap(_.headOption).getOrElse("Player")
  val pairs = data.get("pairs").flatMap(_.headOption).flatMap(s => scala.util.Try(s.toInt).toOption).getOrElse(8)

  Ok(views.html.game_bootstrap(s"Spiel für $name mit $pairs Paaren gestartet!", pairs = pairs, playerName = name))
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

```


#### Short summary: 

empty definition using pc, found symbol in pc: `<none>`.