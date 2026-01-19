/*package modules

import com.google.inject.{AbstractModule, Provides}
import javax.inject._
import scala.concurrent.{ExecutionContext, Future}
import java.util.UUID

import models.{User, UserService}
import play.api.Configuration
import play.api.libs.ws.WSClient
import play.api.mvc.CookieHeaderEncoding

import play.silhouette.api._
import play.silhouette.api.crypto.{AuthenticatorEncoder, Base64AuthenticatorEncoder, Signer}
import play.silhouette.api.services.{AuthenticatorService, IdentityService}
import play.silhouette.api.util.{Clock, FingerprintGenerator, IDGenerator}

import play.silhouette.impl.authenticators._
import play.silhouette.impl.providers.{OAuth2Settings, SocialProviderRegistry}
import play.silhouette.impl.providers.oauth2.GitHubProvider
import play.silhouette.impl.util.{DefaultFingerprintGenerator, SecureRandomIDGenerator, PlayHTTPLayer}

import play.silhouette.crypto.{JcaSigner, JcaSignerSettings}

/* ========= ENV ========= */

trait DefaultEnv extends Env {
  type I = User
  type A = CookieAuthenticator
}

/* ========= MODULE ========= */

class SilhouetteModule extends AbstractModule {

  override def configure(): Unit =
    bind(classOf[IdentityService[User]]).to(classOf[UserIdentityService])

  @Provides @Singleton
  def clock: Clock = Clock()

  @Provides @Singleton
  def fingerprintGenerator: FingerprintGenerator =
    new DefaultFingerprintGenerator()

  @Provides @Singleton
  def idGenerator: IDGenerator =
    new SecureRandomIDGenerator()

  @Provides @Singleton
  def authenticatorEncoder: AuthenticatorEncoder =
    new Base64AuthenticatorEncoder()

  @Provides @Singleton
  def signer(config: Configuration): Signer =
    new JcaSigner(JcaSignerSettings(
      key = config.get[String]("play.http.secret.key")
    ))

  @Provides @Singleton
  def cookieSettings: CookieAuthenticatorSettings =
    CookieAuthenticatorSettings(
      cookieName = "MEMORY_AUTH",
      secureCookie = false,
      httpOnlyCookie = true
    )

  @Provides @Singleton
  def authenticatorService(
    settings: CookieAuthenticatorSettings,
    cookieHeaderEncoding: CookieHeaderEncoding,
    signer: Signer,
    encoder: AuthenticatorEncoder,
    fingerprintGenerator: FingerprintGenerator,
    idGenerator: IDGenerator,
    clock: Clock
  )(implicit ec: ExecutionContext): AuthenticatorService[CookieAuthenticator] =
    new CookieAuthenticatorService(
      settings,
      None,
      signer,
      cookieHeaderEncoding,
      encoder,
      fingerprintGenerator,
      idGenerator,
      clock
    )

  @Provides @Singleton
  def silhouette(
    identityService: IdentityService[User],
    authenticatorService: AuthenticatorService[CookieAuthenticator]
  ): Silhouette[DefaultEnv] =
    SilhouetteProvider[DefaultEnv](
      Environment(identityService, authenticatorService, Seq.empty, EventBus())
    )

  /* ========= GITHUB ========= */

  @Provides @Singleton
  def githubProvider(ws: WSClient, config: Configuration): GitHubProvider =
    new GitHubProvider(
      new PlayHTTPLayer(ws),
      OAuth2Settings(
        authorizationURL = "https://github.com/login/oauth/authorize",
        accessTokenURL   = "https://github.com/login/oauth/access_token",
        redirectURL      = Some(config.get[String]("github.callbackURL")),
        clientID         = config.get[String]("github.clientID"),
        clientSecret     = config.get[String]("github.clientSecret")
      )
    )

  @Provides @Singleton
  def socialRegistry(github: GitHubProvider): SocialProviderRegistry =
    SocialProviderRegistry(Seq(github))
}

/* ========= IDENTITY ========= */

@Singleton
class UserIdentityService @Inject()(users: UserService)(implicit ec: ExecutionContext)
  extends IdentityService[User] {

  override def retrieve(loginInfo: LoginInfo): Future[Option[User]] =
    scala.util.Try(UUID.fromString(loginInfo.providerKey)).toOption match {
      case Some(id) => users.retrieve(id)
      case None     => Future.successful(None)
    }
}*/
