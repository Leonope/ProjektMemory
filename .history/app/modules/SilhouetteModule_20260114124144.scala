package modules

import com.google.inject.{AbstractModule, Provides}
import com.mohiva.play.silhouette.api._
import com.mohiva.play.silhouette.api.crypto.{Base64AuthenticatorEncoder, CrypterAuthenticatorEncoder}
import com.mohiva.play.silhouette.api.repositories.AuthenticatorRepository
import com.mohiva.play.silhouette.api.services.{AuthenticatorService, IdentityService}
import com.mohiva.play.silhouette.impl.authenticators.{CookieAuthenticator, CookieAuthenticatorService}
import com.mohiva.play.silhouette.impl.providers.SocialProviderRegistry
import com.mohiva.play.silhouette.impl.providers.oauth2.{GitHubProvider, OAuth2Settings}
import com.mohiva.play.silhouette.impl.util.{DefaultFingerprintGenerator, SecureRandomIDGenerator}
import com.mohiva.play.silhouette.impl.crypto.JcaCrypter

import javax.inject._
import models.{User, UserService}
import play.api.Configuration
import play.api.libs.ws.WSClient

import java.util.UUID
import scala.concurrent.{ExecutionContext, Future}

trait DefaultEnv extends Env {
  type I = User
  type A = CookieAuthenticator
}

/**
  * Bindings für Silhouette.
  */
class SilhouetteModule extends AbstractModule {
  override def configure(): Unit = {
    bind(classOf[IdentityService[User]]).to(classOf[UserIdentityService])
  }

  @Provides
  @Singleton
  def provideAuthenticatorService(
    config: Configuration,
    clock: Clock
  )(implicit ec: ExecutionContext): AuthenticatorService[CookieAuthenticator] = {

    // CookieAuthenticator settings (Basic Defaults, gut genug für Demo)
    val cookieSettings = CookieAuthenticatorSettings(
      cookieName = "MEMORY_AUTH",
      secureCookie = false, // in HTTPS prod true; für Demo ok
      httpOnlyCookie = true
    )

    new CookieAuthenticatorService(
      cookieSettings,
      None, // kein Repository → stateless cookie authenticator (für Demo ok)
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
      Seq(),
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

    // Wir lesen diese Keys aus application.conf (die wiederum Env Vars nutzt)
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

    val httpLayer = new com.mohiva.play.silhouette.impl.providers.oauth2.PlayWSHTTPClient(ws)
    new GitHubProvider(httpLayer, settings)
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
    // Wir speichern die UUID des Users als providerKey
    scala.util.Try(UUID.fromString(loginInfo.providerKey)).toOption match {
      case Some(id) => users.retrieve(id)
      case None     => Future.successful(None)
    }
  }
}


