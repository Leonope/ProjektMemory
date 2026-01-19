package controllers

import javax.inject._
import play.api.mvc._
import scala.concurrent.{ExecutionContext, Future}

// Silhouette imports (Namen können je nach Version leicht variieren)
import com.mohiva.play.silhouette.api.Silhouette
import com.mohiva.play.silhouette.impl.providers.SocialProviderRegistry
import com.mohiva.play.silhouette.impl.providers.oauth2.GitHubProvider

import models.UserEnv // dein Silhouette Env (siehe Schritt 6)

@Singleton
class AuthController @Inject() (
  cc: ControllerComponents,
  silhouette: Silhouette[UserEnv],
  socialProviderRegistry: SocialProviderRegistry
)(implicit ec: ExecutionContext) extends AbstractController(cc) {

  def githubLogin: Action[AnyContent] = Action.async { implicit request =>
    socialProviderRegistry.get[GitHubProvider] match {
      case Some(provider) =>
        provider.authenticate().flatMap {
          case Left(result)  => Future.successful(result) // Redirect zu GitHub
          case Right(authInfo) =>
            // sollte i.d.R. nicht direkt passieren (meist kommt es im callback)
            Future.successful(Redirect("/"))
        }
      case None =>
        Future.successful(InternalServerError("GitHubProvider not configured"))
    }
  }

  def githubCallback: Action[AnyContent] = Action.async { implicit request =>
    socialProviderRegistry.get[GitHubProvider] match {
      case Some(provider) =>
        provider.authenticate().flatMap {
          case Left(result) => Future.successful(result)
          case Right(authInfo) =>
            // Minimal: hier würdest du Profil laden + User erstellen/finde
            // Dann Authenticator erstellen und im Cookie speichern.
            Future.successful(Redirect("/")) // Platzhalter
        }
      case None =>
        Future.successful(InternalServerError("GitHubProvider not configured"))
    }
  }
}

