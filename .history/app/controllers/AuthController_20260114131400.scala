package controllers

import javax.inject._
import play.api.mvc._
import scala.concurrent.{ExecutionContext, Future}

import models.UserService

import com.mohiva.play.silhouette.api._
import com.mohiva.play.silhouette.impl.providers.SocialProviderRegistry
import com.mohiva.play.silhouette.impl.providers.oauth2.GitHubProvider

@Singleton
class AuthController @Inject()(
  cc: ControllerComponents,
  silhouette: Silhouette[modules.DefaultEnv],
  socialProviderRegistry: SocialProviderRegistry,
  userService: UserService
)(implicit ec: ExecutionContext) extends AbstractController(cc) {

  def githubLogin: Action[AnyContent] = Action.async { implicit request =>
    socialProviderRegistry.get[GitHubProvider] match {
      case Some(provider) =>
        provider.authenticate().map {
          case Left(result) => result
          case Right(_)     => Redirect("/secure/game")
        }
      case None =>
        Future.successful(InternalServerError("GitHubProvider ist nicht konfiguriert"))
    }
  }

  def githubCallback: Action[AnyContent] = Action.async { implicit request =>
    socialProviderRegistry.get[GitHubProvider] match {
      case Some(provider) =>
        provider.authenticate().flatMap {
          case Left(result) =>
            Future.successful(result)

          case Right(authInfo) =>
            provider.retrieveProfile(authInfo).flatMap { profile =>
              val username = profile.loginInfo.providerKey
              val email    = profile.email

              userService.findOrCreate(username, email).flatMap { user =>
                val loginInfo = LoginInfo(provider.id, user.id.toString)

                silhouette.env.authenticatorService.create(loginInfo).flatMap { authenticator =>
                  silhouette.env.authenticatorService.init(authenticator).flatMap { value =>
                    silhouette.env.authenticatorService.embed(value, Redirect("/secure/game"))
                  }
                }
              }
            }
        }

      case None =>
        Future.successful(InternalServerError("GitHubProvider ist nicht konfiguriert"))
    }
  }

  def logout: Action[AnyContent] = silhouette.SecuredAction.async { implicit request =>
    silhouette.env.authenticatorService.discard(request.authenticator, Redirect("/"))
  }
}



