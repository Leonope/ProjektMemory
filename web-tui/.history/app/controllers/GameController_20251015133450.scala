package controllers

import javax.inject._
import play.api.mvc._
import java.util.concurrent.atomic.AtomicReference

import web.WebTUI

@Singleton
class GameController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {

  private val tuiRef = new AtomicReference(new WebTUI())

  private def renderState: String = tuiRef.get().render()

  def index: Action[AnyContent] = Action {
    Ok(views.html.game(renderState))
  }

  def cmd: Action[AnyContent] = Action { request =>
  val input = request.body.asFormUrlEncoded
    .flatMap(_("cmd").headOption)
    .getOrElse("")
  tuiRef.get().handle(input)
  Redirect(routes.GameController.index)   // <-- ohne ()
}

def newGame: Action[AnyContent] = Action {
  tuiRef.get().newGame()
  Redirect(routes.GameController.index)   // <-- ohne ()
}

}
