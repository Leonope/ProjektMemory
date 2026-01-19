package modules

import com.google.inject.{AbstractModule, Provides}
import javax.inject._
import scala.concurrent.{ExecutionContext, Future}
import java.util.UUID

import models.{User, UserService}
import play.api.Configuration
import play.api.libs.ws.WSClient
import play.api.mvc.CookieHeaderEncoding

import play.silhouette.api._
import play.silhouette.api.crypto.{AuthenticatorEncoder, Signer}
import play.silhouette.api.repositories.AuthenticatorRepository
import play.silhouette.api.services.{AuthenticatorService, IdentityService}
import play.silhouette.api.util.{Clock, FingerprintGenerator, IDGenerator}

import play.silhouette.impl.authenticators.{CookieAuthenticator, CookieAuthenticatorService, CookieAuthenticatorSettings}
import play.silhouette.impl.providers.SocialProviderRegistry
import play.silhouette.impl.providers.OAuth2Settings
import play.silhouette.impl.providers.oauth2.{GitHubProvider}
import play.silhouette.impl.util.{DefaultFingerprintGenerator, SecureRandomIDGenerator}
import play.silhouette.crypto.JcaSigner
import play.silhouette.crypto.Base64AuthenticatorEncoder

trait DefaultEnv extends Env {
  type I = User
  type A = CookieAuthenticator
}

class SilhouetteModule extends AbstractModule {
  override def configure(): Unit = {
    bind(classOf[IdentityService[User]]).to(classOf[UserIdentityService])
  }

  // ---- Cookie Authenticator Service ----
  @Provides
  @Singleton
  def provideCookieAuthenticatorSettings(): CookieAuthenticatorSettings = {
    CookieAuthenticatorSettings(
      cookieName = "MEMORY_AUTH",
      secureCookie = false,
      httpOnlyCookie = true
    )
  }

  @Provides
  @Singleton
  def provideClock(): Clock = Clock()

  @Provides
  @Singleton
  def provideFingerprintGenerator(): FingerprintGenerator =
    new DefaultFingerprintGenerator()

  @Provides
  @Singleton
  def provideIdGenerator(): IDGenerator =
    new SecureRandomIDGenerator()

  @Provides
  @Singleton
  def provideAuthenticatorEncoder(): AuthenticatorEncoder =
    new Base64AuthenticatorEncoder()

  @Provides
  @Singleton
  def provideSigner(config: Configuration): Signer = {
    // Nutzt Play Secret (in dev kann der automatisch gesetzt sein, aber wir nehmen explizit play.http.secret.key)
    // Falls du PLAY_HTTP_SECRET_KEY nicht setzt, nimm in dev eine feste Demo-Variante.
    val secret = config.getOptional[String]("play.http.secret.key").getOrElse("dev-secret-change-me")
    new JcaSigner(secret)
  }

  @Provides
  @Singleton
  def provideAuthenticatorService(
    settings: CookieAuthenticatorSettings,
    cookieHeaderEncoding: CookieHeaderEncoding,
    signer: Signer,
    authenticatorEncoder: AuthenticatorEncoder,
    fingerprintGenerator: FingerprintGenerator,
    idGenerator: IDGenerator,
    clock: Clock
  )(implicit ec: ExecutionContext): AuthenticatorService[CookieAuthenticator] = {

    new CookieAuthenticatorService(
      settings = settings,
      repository = None, // stateless cookie
      signer = signer,
      cookieHeaderEncoding = cookieHeaderEncoding,
      authenticatorEncoder = authenticatorEncoder,
      fingerprintGenerator = fingerprintGenerator,
      idGenerator = idGenerator,
      clock = clock
    )
  }

  // ---- Silhouette Env ----
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

  // ---- GitHub Provider + Registry ----
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
