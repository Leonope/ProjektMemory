package security

import com.google.firebase.auth.FirebaseAuth
import javax.inject.{Inject, Singleton}
import play.api.mvc._
import services.FirebaseAdmin

import scala.concurrent.{ExecutionContext, Future}

case class AuthenticatedUser(uid: String, email: Option[String])

class AuthenticatedRequest[A](val user: AuthenticatedUser, request: Request[A])
  extends WrappedRequest[A](request)

@Singleton
class RequireLogin @Inject()(
  val parser: BodyParsers.Default,
  firebaseAdmin: FirebaseAdmin
)(implicit val executionContext: ExecutionContext)
  extends ActionBuilder[AuthenticatedRequest, AnyContent] {

  private val auth = FirebaseAuth.getInstance(firebaseAdmin.firebaseApp)

  override def invokeBlock[A](
    request: Request[A],
    block: AuthenticatedRequest[A] => Future[Result]
  ): Future[Result] = {

    request.cookies.get("session") match {
      case None =>
        Future.successful(Results.Redirect("/login"))

      case Some(cookie) =>
        try {
          // checkRevoked=true: erkennt widerrufene Sessions (optional, aber gut)
          val decoded = auth.verifySessionCookie(cookie.value, true)
          val user = AuthenticatedUser(decoded.getUid, Option(decoded.getEmail))
          block(new AuthenticatedRequest(user, request))
        } catch {
          case _: Throwable =>
            Future.successful(Results.Redirect("/login"))
        }
    }
  }
}

