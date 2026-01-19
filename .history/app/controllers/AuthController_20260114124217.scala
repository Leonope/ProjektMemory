package controllers

import com.mohiva.play.silhouette.api._
import com.mohiva.play.silhouette.impl.providers.SocialProviderRegistry
import com.mohiva.play.silhouette.impl.providers.oauth2.GitHubProvider
import models.UserService
import play.api.mvc._

import javax.inject._
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class AuthController @Inject() (
  cc: ControllerComponents,
  silhouette: Silhouette[modules.DefaultEnv],
  socialProviderRegistry: SocialProviderRegistry,
  userService: UserService
)(implicit ec: ExecutionContext)
  extends AbstractController(cc) {

  /**
    * Startet OAuth Flow (redirect zu GitHub).
    */
  def githubLogin: Action[AnyContent] = Action.async { implicit request =>
    socialProviderRegistry.get[GitHubProvider] match {
      case Some(provider) =>
        provider.authenticate().map {
          case Left(result)   => result
          case Right(_)       => Redirect("/secure/game") // selten direkt
        }
      case None =>
        Future.successful(InternalServerError("GitHubProvider ist nicht konfiguriert"))
    }
  }

  /**
    * Callback von GitHub (code → token → profile).
    */
  def githubCallback: Action[AnyContent] = Action.async { implicit request =>
    socialProviderRegistry.get[GitHubProvider] match {
      case Some(provider) =>
        provider.authenticate().flatMap {
          case Left(result) =>
            Future.successful(result)

          case Right(authInfo) =>
            // Hol Profil
            provider.retrieveProfile(authInfo).flatMap { profile =>
              val username = profile.loginInfo.providerKey // GitHub username / id je nach Provider
              val email    = profile.email

              // In-Memory user erstellen / finden
              userService.findOrCreate(username, email).flatMap { user =>
                val loginInfo = LoginInfo(provider.id, user.id.toString)

                // CookieAuthenticator erstellen & im Response einbetten
                silhouette.env.authenticatorService.create(loginInfo).flatMap { authenticator =>
                  silhouette.env.authenticatorService.init(authenticator).flatMap { value =>
                    silhouette.env.authenticatorService.embed(
                      value,
                      Redirect("/secure/game")
                    )
                  }
                }
              }
            }
        }

      case None =>
        Future.successful(InternalServerError("GitHubProvider ist nicht konfiguriert"))
    }
  }

  /**
    * Logout (Cookie löschen)
    */
  def logout: Action[AnyContent] = silhouette.SecuredAction.async { implicit request =>
    silhouette.env.authenticatorService.discard(request.authenticator, Redirect("/"))
  }
}

