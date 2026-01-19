package controllers

import com.google.firebase.auth.{FirebaseAuth, SessionCookieOptions}
import javax.inject._
import play.api.mvc._
import services.FirebaseAdmin

import scala.concurrent.{ExecutionContext, Future}
import java.util.concurrent.TimeUnit

@Singleton
class AuthController @Inject()(
  cc: ControllerComponents,
  firebaseAdmin: FirebaseAdmin
)(implicit ec: ExecutionContext) extends AbstractController(cc) {

  private val auth = FirebaseAuth.getInstance(firebaseAdmin.firebaseApp)

  def login: Action[AnyContent] = Action { implicit request =>
    Ok(views.html.login())
  }

  def createSession: Action[AnyContent] = Action.async { request =>
    val maybeToken = request.headers.get("Authorization")
      .collect { case h if h.startsWith("Bearer ") => h.stripPrefix("Bearer ").trim }

    maybeToken match {
      case None =>
        Future.successful(Unauthorized("Missing Authorization: Bearer <token>"))

      case Some(idToken) =>
        try {
          // stellt sicher, dass Token gültig ist
          auth.verifyIdToken(idToken)

          val expiresInMillis = TimeUnit.DAYS.toMillis(5)

          val options = SessionCookieOptions.builder()
            .setExpiresIn(expiresInMillis)
            .build()

          val sessionCookie = auth.createSessionCookie(idToken, options)

          Future.successful(
            Ok("ok").withCookies(
              Cookie(
                name = "session",
                value = sessionCookie,
                httpOnly = true,
                secure = false, // lokal ok; prod: true (HTTPS)
                sameSite = Some(Cookie.SameSite.Lax),
                maxAge = Some((expiresInMillis / 1000).toInt),
                path = "/"
              )
            )
          )
        } catch {
          case _: Throwable =>
            Future.successful(Unauthorized("Invalid token"))
        }
    }
  }

  def logout: Action[AnyContent] = Action { _ =>
    Ok("ok").discardingCookies(DiscardingCookie("session", path = "/"))
  }
}
