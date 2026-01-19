package modules

import com.google.inject.{AbstractModule, Provides}
import javax.inject._
import scala.concurrent.{ExecutionContext, Future}
import java.util.UUID

import models.{User, UserService}
import play.api.{Configuration}
import play.api.libs.ws.WSClient
import play.api.mvc.{CookieHeaderEncoding, DefaultActionBuilder}

import play.silhouette.api._
import play.silhouette.api.actions._
import play.silhouette.api.crypto.{AuthenticatorEncoder, Base64AuthenticatorEncoder, Signer}
import play.silhouette.api.services.{AuthenticatorService, IdentityService}
import play.silhouette.api.util.{Clock, FingerprintGenerator, IDGenerator}

import play.silhouette.impl.authenticators._
import play.silhouette.impl.providers.{OAuth2Settings, SocialProviderRegistry}
import play.silhouette.impl.providers.oauth2.GitHubProvider
import play.silhouette.impl.util.{DefaultFingerprintGenerator, SecureRandomIDGenerator}

import play.silhouette.crypto.{JcaSigner, JcaSignerSettings}

import play.silhouette.api.repositories.AuthenticatorRepository
import play.silhouette.impl.util.PlayWSHTTPClient

/* =========================
   Silhouette Environment
   ========================= */

trait DefaultEnv extends Env {
  type I = User
  type A = CookieAuthenticator
}

/* =========================
   Guice Module
   ========================= */

class SilhouetteModule extends AbstractModule {

  override def configure(): Unit = {
    bind(classOf[IdentityService[User]]).to(classOf[UserIdentityService])
  }

  /* ---------- Basics ---------- */

  @Provides
  @Singleton
  def provideClock(): Clock = Clock()

  @Provides
  @Singleton
  def provideFingerprintGenerator(): FingerprintGenerator =
    new DefaultFingerprintGenerator()

  @Provides
  @Singleton
  def provideIdGenerator()(implicit ec: ExecutionContext): IDGenerator =
    new SecureRandomIDGenerator()

  /* ---------- Cookie Authenticator ---------- */

  @Provides
  @Singleton
  def provideCookieAuthenticatorSettings(): CookieAuthenticatorSettings =
    CookieAuthenticatorSettings(
      cookieName     = "MEMORY_AUTH",
      secureCookie   = false,
      httpOnlyCookie = true
    )

  @Provides
  @Singleton
  def provideAuthenticatorEncoder(): AuthenticatorEncoder =
    new Base64AuthenticatorEncoder()

  @Provides
  @Singleton
  def provideSigner(config: Configuration): Signer = {
    val secret = config
      .getOptional[String]("play.http.secret.key")
      .getOrElse("dev-secret-change-me")

    new JcaSigner(JcaSignerSettings(key = secret))
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

  /* ---------- RequestHandler Builder ---------- */

  @Provides
  @Singleton
  def provideSecuredRequestHandlerBuilder(
    actionBuilder: DefaultActionBuilder
  )(implicit ec: ExecutionContext): SecuredRequestHandlerBuilder[DefaultEnv] =
    new DefaultSecuredRequestHandlerBuilder[DefaultEnv](actionBuilder)

  @Provides
  @Singleton
  def provideUnsecuredRequestHandlerBuilder(
    actionBuilder: DefaultActionBuilder
  )(implicit ec: ExecutionContext): UnsecuredRequestHandlerBuilder[DefaultEnv] =
    new DefaultUnsecuredRequestHandlerBuilder[DefaultEnv](actionBuilder)

  @Provides
  @Singleton
  def provideUserAwareRequestHandlerBuilder(
    actionBuilder: DefaultActionBuilder
  )(implicit ec: ExecutionContext): UserAwareRequestHandlerBuilder[DefaultEnv] =
    new DefaultUserAwareRequestHandlerBuilder[DefaultEnv](actionBuilder)

  /* ---------- Actions ---------- */

  class SomeController @Inject()(silhouette: Silhouette[DefaultEnv], cc: ControllerComponents)
  extends AbstractController(cc) {

  def index = silhouette.SecuredAction { implicit req =>
    Ok("hi")
  }
}


  /* ---------- Silhouette ---------- */

  @Provides
  @Singleton
  def provideSilhouette(
    identityService: IdentityService[User],
    authenticatorService: AuthenticatorService[CookieAuthenticator],
    eventBus: EventBus,
    securedAction: SecuredAction,
    unsecuredAction: UnsecuredAction,
    userAwareAction: UserAwareAction
  ): Silhouette[DefaultEnv] = {

    val env = Environment[DefaultEnv](
      identityService,
      authenticatorService,
      Seq.empty,
      eventBus
    )

    new SilhouetteProvider[DefaultEnv](
      env,
      securedAction,
      unsecuredAction,
      userAwareAction
    )
  }

  /* ---------- GitHub OAuth ---------- */

  @Provides
  @Singleton
  def provideGitHubProvider(
    ws: WSClient,
    config: Configuration
  )(implicit ec: ExecutionContext): GitHubProvider = {

    val settings = OAuth2Settings(
      authorizationURL     = GitHubProvider.SpecifiedSettings.authorizationURL,
      accessTokenURL       = GitHubProvider.SpecifiedSettings.accessTokenURL,
      redirectURL          = config.get[String]("github.callbackURL"),
      apiURL               = GitHubProvider.SpecifiedSettings.apiURL,
      clientID             = config.get[String]("github.clientID"),
      clientSecret         = config.get[String]("github.clientSecret"),
      scope                = Some(GitHubProvider.SpecifiedSettings.scope),
      authorizationParams  = Map.empty
    )

    new GitHubProvider(new PlayWSHTTPClient(ws), settings)
  }

  @Provides
  @Singleton
  def provideSocialProviderRegistry(
    github: GitHubProvider
  ): SocialProviderRegistry =
    SocialProviderRegistry(Seq(github))
}

/* =========================
   Identity Service
   ========================= */

@Singleton
class UserIdentityService @Inject()(
  users: UserService
)(implicit ec: ExecutionContext)
  extends IdentityService[User] {

  override def retrieve(loginInfo: LoginInfo): Future[Option[User]] =
    scala.util.Try(UUID.fromString(loginInfo.providerKey)).toOption match {
      case Some(id) => users.retrieve(id)
      case None     => Future.successful(None)
    }
}
