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

  private val auth: FirebaseAuth = FirebaseAuth.getInstance(firebaseAdmin.firebaseApp)

  /**
   * Renders the login page (Twirl).
   * IMPORTANT: pass request explicitly to avoid implicit-RequestHeader issues.
   */
  def login: Action[AnyContent] = Action { request: Request[AnyContent] =>
    Ok(views.html.login()(request))
  }

  /**
   * Creates a Firebase Session Cookie from an ID token.
   * Expects: Authorization: Bearer <FIREBASE_ID_TOKEN>
   * Returns: Set-Cookie: session=<FIREBASE_SESSION_COOKIE>
   */
  def createSession: Action[AnyContent] = Action.async { request =>
    val maybeIdToken: Option[String] =
      request.headers.get("Authorization")
        .collect { case h if h.startsWith("Bearer ") => h.stripPrefix("Bearer ").trim }
        .filter(_.nonEmpty)

    maybeIdToken match {
      case None =>
        Future.successful(Unauthorized("Missing Authorization: Bearer <token>"))

      case Some(idToken) =>
        try {
          // Verify the ID token first
          auth.verifyIdToken(idToken)

          // Session cookie lifetime
          val expiresInMillis: Long = TimeUnit.DAYS.toMillis(5)

          val options: SessionCookieOptions =
            SessionCookieOptions.builder()
              .setExpiresIn(expiresInMillis)
              .build()

          val sessionCookie: String = auth.createSessionCookie(idToken, options)

          val secureCookie = false // local dev; PROD: true (HTTPS)

          Future.successful(
            Ok("ok").withCookies(
              Cookie(
                name = "session",
                value = sessionCookie,
                httpOnly = true,
                secure = secureCookie,
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

  /**
   * Logs out server-side by removing the session cookie.
   * (Client should also signOut from Firebase.)
   */
  def logout: Action[AnyContent] = Action { _ =>
    Ok("ok").discardingCookies(DiscardingCookie("session", path = "/"))
  }
}
