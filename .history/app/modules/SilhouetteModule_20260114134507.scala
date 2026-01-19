package modules

import com.google.inject.{AbstractModule, Provides}
import javax.inject._
import scala.concurrent.{ExecutionContext, Future}
import java.util.UUID

import models.{User, UserService}
import play.api.Configuration
import play.api.libs.ws.WSClient

import play.silhouette.api._
import play.silhouette.api.services.{AuthenticatorService, IdentityService}
import play.silhouette.impl.authenticators.{CookieAuthenticator, CookieAuthenticatorService}
import play.silhouette.impl.providers.SocialProviderRegistry
import play.silhouette.impl.providers.OAuth2Settings
import play.silhouette.impl.providers.oauth2.{GitHubProvider}
import play.silhouette.impl.util.{DefaultFingerprintGenerator, SecureRandomIDGenerator}

trait DefaultEnv extends Env {
  type I = User
  type A = CookieAuthenticator
}

class SilhouetteModule extends AbstractModule {
  override def configure(): Unit = {
    bind(classOf[IdentityService[User]]).to(classOf[UserIdentityService])
  }

  @Provides
  @Singleton
  def provideAuthenticatorService()(implicit ec: ExecutionContext): AuthenticatorService[CookieAuthenticator] = {
    val settings = CookieAuthenticatorSettings(
      cookieName = "MEMORY_AUTH",
      secureCookie = false,
      httpOnlyCookie = true
    )

    // Kein Clock-Inject nötig -> Default-Clock wird intern genutzt / system clock
    new CookieAuthenticatorService(
      settings,
      None,
      new SecureRandomIDGenerator(),
      new DefaultFingerprintGenerator()
    )
  }

  @Provides
  @Singleton
  def provideSilhouette(
    identityService: IdentityService[User],
    authenticatorService: AuthenticatorService[CookieAuthenticator],
    eventBus: EventBus
  )(implicit ec: ExecutionContext): Silhouette[DefaultEnv] = {

    val env = Environment[DefaultEnv](
      identityService,
      authenticatorService,
      Seq.empty,
      eventBus
    )

    new SilhouetteProvider[DefaultEnv](env)
  }

  @Provides
  @Singleton
  def provideGitHubProvider(ws: WSClient, config: Configuration)(implicit ec: ExecutionContext): GitHubProvider = {
    val clientId     = config.get[String]("github.clientID")
    val clientSecret = config.get[String]("github.clientSecret")
    val callbackUrl  = config.get[String]("github.callbackURL")

    val settings = OAuth2Settings(
      authorizationURL = GitHubProvider.SpecifiedSettings.authorizationURL,
      accessTokenURL   = GitHubProvider.SpecifiedSettings.accessTokenURL,
      redirectURL      = callbackUrl,
      apiURL           = GitHubProvider.SpecifiedSettings.apiURL,
      clientID         = clientId,
      clientSecret     = clientSecret,
      scope            = Some(GitHubProvider.SpecifiedSettings.scope),
      authorizationParams = Map.empty
    )

    val httpClient = new PlayWSHTTPClient(ws)
    new GitHubProvider(httpClient, settings)
  }

  @Provides
  @Singleton
  def provideSocialProviderRegistry(github: GitHubProvider): SocialProviderRegistry =
    SocialProviderRegistry(Seq(github))
}

@Singleton
class UserIdentityService @Inject()(users: UserService)(implicit ec: ExecutionContext)
  extends IdentityService[User] {

  override def retrieve(loginInfo: LoginInfo): Future[Option[User]] = {
    scala.util.Try(UUID.fromString(loginInfo.providerKey)).toOption match {
      case Some(id) => users.retrieve(id)
      case None     => Future.successful(None)
    }
  }
}
