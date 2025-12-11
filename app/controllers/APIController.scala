package controllers

import javax.inject._
import play.api.mvc._
import web.WebTUI

@Singleton
class ApiController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {

  // GUI/Client sendet den Logtext als Plain-Text
  def logLine: Action[String] = Action(parse.tolerantText) { req =>
    val line = req.body.trim
    if (line.nonEmpty) WebTUI.debug(line) // -> landet im gemeinsamen Buffer
    Ok("ok").as("text/plain; charset=utf-8")
  }
}

