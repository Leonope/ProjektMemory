package controllers

import play.api.mvc._
import javax.inject._
import services.FirebaseAdmin
import com.google.firebase.auth.FirebaseAuth

import scala.concurrent.{ExecutionContext, Future}
import scala.jdk.CollectionConverters._

@Singleton
class AuthController @Inject()(
  cc: ControllerComponents,
  firebaseAdmin: FirebaseAdmin
)(implicit ec: ExecutionContext) extends AbstractController(cc) {

  private val auth = FirebaseAuth.getInstance(firebaseAdmin.firebaseApp)

  def loginPage: Action[AnyContent] =
    Action { Ok(views.html.login()) } // oder eine statische Seite / Vue

  def createSession: Action[AnyContent] =
    Action.async { request =>
      val maybeIdToken = request.headers.get("Authorization")
        .collect { case h if h.startsWith("Bearer ") => h.stripPrefix("Bearer ").trim }

      maybeIdToken match {
        case None => Future.successful(Unauthorized("Missing Bearer token"))
        case Some(idToken) =>
          try {
            // optional: erst ID token verifizieren (stellt sicher, dass es gültig ist)
            val decoded = auth.verifyIdToken(idToken)

            // Session cookie erstellen (z.B. 5 Tage)
            val expiresInMillis = 5L * 24 * 60 * 60 * 1000
            val sessionCookie = auth.createSessionCookie(idToken, expiresInMillis)

            Future.successful(
              Ok("ok")
                .withCookies(
                  Cookie(
                    name     = "session",
                    value    = sessionCookie,
                    httpOnly = true,
                    secure   = false, // in Prod true (HTTPS)
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

  def logout: Action[AnyContent] =
    Action { Ok("ok").discardingCookies(DiscardingCookie("session", path = "/")) }
}
