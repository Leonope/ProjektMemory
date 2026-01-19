package controllers

import com.google.firebase.auth.FirebaseAuth
import javax.inject._
import play.api.mvc._
import services.FirebaseAdmin
import com.google.firebase.auth.SessionCookieOptions
import java.util.concurrent.TimeUnit


import scala.concurrent.{ExecutionContext, Future}

@Singleton
class AuthController @Inject()(
  cc: ControllerComponents,
  firebaseAdmin: FirebaseAdmin
)(implicit ec: ExecutionContext) extends AbstractController(cc) {

  private val auth = FirebaseAuth.getInstance(firebaseAdmin.firebaseApp)

  def login = Action { implicit request =>
  Ok(views.html.login())
}

  def createSession: Action[AnyContent] = Action.async { request =>
    val maybeToken = request.headers.get("Authorization")
      .collect { case h if h.startsWith("Bearer ") => h.stripPrefix("Bearer ").trim }

    maybeToken match {
      case None => Future.successful(Unauthorized("Missing Authorization: Bearer <token>"))
      case Some(idToken) =>
        try {
          // Verifiziert ID Token (stellt sicher, es ist echt)
          auth.verifyIdToken(idToken)

          val expiresInMillis = 5L * 24 * 60 * 60 * 1000 // 5 Tage
          val sessionCookie = auth.createSessionCookie(idToken, expiresInMillis)

          val secureCookie = false // локal ok; in Produktion auf true (HTTPS!)
          Future.successful(
            Ok("ok").withCookies(
              Cookie(
                name     = "session",
                value    = sessionCookie,
                httpOnly = true,
                secure   = secureCookie,
                sameSite = Some(Cookie.SameSite.Lax),
                maxAge   = Some((expiresInMillis / 1000).toInt),
                path     = "/"
              )
            )
          )
        } catch {
          case _: Throwable => Future.successful(Unauthorized("Invalid token"))
        }
    }
  }

  def logout: Action[AnyContent] = Action { _ =>
    Ok("ok").discardingCookies(DiscardingCookie("session", path = "/"))
  }
}
