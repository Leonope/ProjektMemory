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

  // ✅ Debug endpoint
  def ping: Action[AnyContent] = Action {
    Ok("pong")
  }

  def login: Action[AnyContent] = Action { implicit request =>
    Ok(views.html.login())
  }

  def createSession: Action[AnyContent] = Action.async { request =>
    println(">>> HIT /auth/session")

    val maybeIdToken: Option[String] =
      request.headers.get("Authorization")
        .collect { case h if h.startsWith("Bearer ") => h.stripPrefix("Bearer ").trim }
        .filter(_.nonEmpty)

    maybeIdToken match {
      case None =>
        println(">>> Missing Bearer token")
        Future.successful(Unauthorized("Missing Authorization: Bearer <token>"))

      case Some(idToken) =>
        try {
          auth.verifyIdToken(idToken)

          val expiresInMillis: Long = TimeUnit.DAYS.toMillis(5)
          val options: SessionCookieOptions =
            SessionCookieOptions.builder().setExpiresIn(expiresInMillis).build()

          val sessionCookie: String = auth.createSessionCookie(idToken, options)

          val secureCookie = false // локал ok; prod true

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
          case e: Throwable =>
            e.printStackTrace()
            Future.successful(Unauthorized("Invalid token: " + e.getMessage))
        }
    }
  }

  def logout: Action[AnyContent] = Action { _ =>
    Ok("ok").discardingCookies(DiscardingCookie("session", path = "/"))
  }
}
