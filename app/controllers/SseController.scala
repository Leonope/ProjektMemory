package controllers

import javax.inject._
import play.api.mvc._
import backend.SessionRegistry
import org.apache.pekko.stream.Materializer
import org.apache.pekko.stream.scaladsl.Source

import scala.concurrent.ExecutionContext

@Singleton
class SseController @Inject()(
    cc: ControllerComponents
  )(implicit
    mat: Materializer,
    ec: ExecutionContext
  ) extends AbstractController(cc) {

  /**
    * SSE-Stream mit allen aktiven Sessions + Spielern.
    * Content-Type: text/event-stream
    *
    * Client-Seite:
    *   const s = new EventSource("/sse/sessions");
    *   s.onmessage = e => { const data = JSON.parse(e.data); ... }
    */
  def sessionsStream: Action[AnyContent] = Action { implicit request =>
    val src: Source[String, _] = SessionRegistry.registerListener()

    val sse = src.map { json =>
      // Einfaches SSE-Format: "data: <json>\n\n"
      s"data: $json\n\n"
    }

    Ok.chunked(sse).as("text/event-stream")
  }
}

