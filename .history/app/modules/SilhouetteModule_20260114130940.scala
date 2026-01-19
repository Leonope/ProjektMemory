package modules

import com.google.inject.{AbstractModule, Provides}
import javax.inject._
import scala.concurrent.{ExecutionContext, Future}
import java.util.UUID

import models.{User, UserService}
import play.api.Configuration
import play.api.libs.ws.WSClient

import org.playframework.silhouette.api._
import org.playframework.silhouette.api.services.{AuthenticatorService, IdentityService}
import org.playframework.silhouette.impl.authenticators.{CookieAuthenticator, CookieAuthenticatorService}
import org.playframework.silhouette.impl.providers.SocialProviderRegistry
import org.playframework.silhouette.impl.providers.oauth2.{GitHubProvider, OAuth2Settings}
import org.playframework.silhouette.impl.util.{DefaultFingerprintGenerator, SecureRandomIDGenerator}

/**
  * Silhouette Environment: User + Cookie Authenticator
  */
trait DefaultEnv extends Env {
  type I = User
  type A = CookieAuthenticator
}

/**
  * Guice Module: verdrahtet Silhouette (IdentityService, AuthenticatorService, GitHubProvider, Registry, Silhouette)
  */
class SilhouetteModule extends AbstractModule {
  override def configure(): Unit = {
    bind(classOf[IdentityService[User]]).to(classOf[UserIdentityService])
  }

  @Provides
  @Singleton
  def provideAuthenticatorService(clock: Clock)(implicit ec: ExecutionContext): AuthenticatorService[CookieAuthenticator] = {
    val settings = CookieAuthenticatorSettings(
      cookieName = "MEMORY_AUTH",
      secureCookie = false,   // lokal ok; in HTTPS prod true
      httpOnlyCookie = true
    )

    new CookieAuthenticatorService(
      settings,
      None, // kein Repository -> stateless cookie (für Demo ok)
      new SecureRandomIDGenerator(),
      new DefaultFingerprintGenerator(),
      clock
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
  def provideGitHubProvider(
    ws: WSClient,
    config: Configuration
  )(implicit ec: ExecutionContext): GitHubProvider = {

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

    // Silhouette nutzt eine HTTP-Schicht; für Play/WS gibt es einen WS-basierten Client.
    // Je nach Silhouette 10.x Patch kann der Klassenname minimal variieren.
    val httpClient = new org.playframework.silhouette.impl.providers.oauth2.PlayWSHTTPClient(ws)

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
