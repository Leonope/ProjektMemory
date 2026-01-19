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
import play.silhouette.api.crypto.{AuthenticatorEncoder, Base64AuthenticatorEncoder, Signer}
import play.silhouette.api.services.{AuthenticatorService, IdentityService}
import play.silhouette.api.util.{Clock, FingerprintGenerator, IDGenerator, HTTPLayer}

import play.silhouette.impl.authenticators.{CookieAuthenticator, CookieAuthenticatorService, CookieAuthenticatorSettings}
import play.silhouette.impl.providers.{OAuth2Settings, SocialProviderRegistry, SocialStateHandler, DefaultSocialStateHandler}
import play.silhouette.impl.providers.oauth2.GitHubProvider
import play.silhouette.impl.providers.state.{CsrfStateItemHandler}
import play.silhouette.impl.util.{DefaultFingerprintGenerator, SecureRandomIDGenerator, PlayHTTPLayer}

import play.silhouette.crypto.{JcaSigner, JcaSignerSettings}

trait DefaultEnv extends Env {
  type I = User
  type A = CookieAuthenticator
}

class SilhouetteModule extends AbstractModule {

  override def configure(): Unit = {
    bind(classOf[IdentityService[User]]).to(classOf[UserIdentityService])
    bind(classOf[Silhouette[DefaultEnv]]).to(classOf[SilhouetteProvider[DefaultEnv]])
  }

  // ---- Basics ----
  @Provides @Singleton
  def provideClock(): Clock = Clock()

  @Provides @Singleton
  def provideFingerprintGenerator(): FingerprintGenerator =
    new DefaultFingerprintGenerator()

  @Provides @Singleton
  def provideIdGenerator()(implicit ec: ExecutionContext): IDGenerator =
    new SecureRandomIDGenerator()

  // ---- Cookie Authenticator ----
  @Provides @Singleton
  def provideCookieAuthenticatorSettings(): CookieAuthenticatorSettings =
    CookieAuthenticatorSettings(
      cookieName     = "MEMORY_AUTH",
      secureCookie   = false,
      httpOnlyCookie = true
    )

  @Provides @Singleton
  def provideAuthenticatorEncoder(): AuthenticatorEncoder =
    new Base64AuthenticatorEncoder()

  @Provides @Singleton
  def provideSigner(config: Configuration): Signer = {
    val secret = config.getOptional[String]("play.http.secret.key").getOrElse("dev-secret-change-me")
    new JcaSigner(JcaSignerSettings(key = secret))
  }

  @Provides @Singleton
  def provideAuthenticatorService(
    settings: CookieAuthenticatorSettings,
    cookieHeaderEncoding: CookieHeaderEncoding,
    signer: Signer,
    authenticatorEncoder: AuthenticatorEncoder,
    fingerprintGenerator: FingerprintGenerator,
    idGenerator: IDGenerator,
    clock: Clock
  )(implicit ec: ExecutionContext): AuthenticatorService[CookieAuthenticator] =
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

  // ---- EventBus + Environment ----
  @Provides @Singleton
  def provideEventBus(): EventBus = EventBus()

  @Provides @Singleton
  def provideEnvironment(
    identityService: IdentityService[User],
    authenticatorService: AuthenticatorService[CookieAuthenticator],
    eventBus: EventBus
  )(implicit ec: ExecutionContext): Environment[DefaultEnv] =
    Environment[DefaultEnv](
      identityService,
      authenticatorService,
      Seq.empty,
      eventBus
    )

  // ---- HTTPLayer + SocialState für OAuth2 Provider ----
  @Provides @Singleton
  def provideHTTPLayer(ws: WSClient): HTTPLayer =
    new PlayHTTPLayer(ws)

  @Provides @Singleton
  def provideCsrfStateItemHandler(
    idGenerator: IDGenerator,
    signer: Signer,
    clock: Clock
  ): CsrfStateItemHandler =
    new CsrfStateItemHandler(idGenerator, signer, clock)

  // ✅ HIER ist der Fix: DefaultSocialStateHandler kommt aus play.silhouette.impl.providers (nicht ...providers.state)
  @Provides @Singleton
  def provideSocialStateHandler(
    csrfStateItemHandler: CsrfStateItemHandler,
    signer: Signer
  ): SocialStateHandler =
    new DefaultSocialStateHandler(Set(csrfStateItemHandler), signer)

  // ---- GitHub Provider + Registry ----
  @Provides @Singleton
  def provideGitHubProvider(
    httpLayer: HTTPLayer,
    socialStateHandler: SocialStateHandler,
    config: Configuration
  )(implicit ec: ExecutionContext): GitHubProvider = {

    val clientId     = config.get[String]("github.clientID")
    val clientSecret = config.get[String]("github.clientSecret")
    val callbackUrl  = config.get[String]("github.callbackURL")

    val settings = OAuth2Settings(
      authorizationURL = "https://github.com/login/oauth/authorize",
      accessTokenURL   = "https://github.com/login/oauth/access_token",
      redirectURL      = Some(callbackUrl),         // Option[String] (wie dein Compiler es verlangt)
      apiURL           = "https://api.github.com/user",
      clientID         = clientId,
      clientSecret     = clientSecret,
      scope            = Some("user:email"),
      authorizationParams = Map.empty
    )

    new GitHubProvider(httpLayer, socialStateHandler, settings)
  }

  @Provides @Singleton
  def provideSocialProviderRegistry(github: GitHubProvider): SocialProviderRegistry =
    SocialProviderRegistry(Seq(github))
}

@Singleton
class UserIdentityService @Inject()(users: UserService)(implicit ec: ExecutionContext)
  extends IdentityService[User] {

  override def retrieve(loginInfo: LoginInfo): Future[Option[User]] =
    scala.util.Try(UUID.fromString(loginInfo.providerKey)).toOption match {
      case Some(id) => users.retrieve(id)
      case None     => Future.successful(None)
    }
}
