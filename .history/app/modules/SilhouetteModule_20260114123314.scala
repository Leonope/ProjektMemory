package modules

import com.google.inject.AbstractModule
import com.mohiva.play.silhouette.api.{Silhouette => SilhouetteApi, _}
import com.mohiva.play.silhouette.api.crypto.CrypterAuthenticatorEncoder
import com.mohiva.play.silhouette.api.repositories.AuthenticatorRepository
import com.mohiva.play.silhouette.api.services.{AuthenticatorService, IdentityService}
import com.mohiva.play.silhouette.impl.authenticators.{CookieAuthenticator, CookieAuthenticatorService}
import com.mohiva.play.silhouette.impl.repositories.DelegableAuthInfoRepository
import com.mohiva.play.silhouette.impl.util.{DefaultFingerprintGenerator, SecureRandomIDGenerator}
import com.mohiva.play.silhouette.impl.providers.SocialProviderRegistry
import com.mohiva.play.silhouette.impl.providers.oauth2.GitHubProvider
import play.api.Configuration
import play.api.libs.ws.WSClient

import javax.inject._
import scala.concurrent.ExecutionContext
import models.{User, UserService}

import java.util.UUID

class SilhouetteModule extends AbstractModule {
  override def configure(): Unit = {
    bind(classOf[IdentityService[User]]).to(classOf[UserIdentityService])
    bind(classOf[SilhouetteApi[DefaultEnv]]).to(classOf[SilhouetteProvider[DefaultEnv]])
  }
}

trait DefaultEnv extends Env {
  type I = User
  type A = CookieAuthenticator
}

@Singleton
class UserIdentityService @Inject()(userService: UserService)(implicit ec: ExecutionContext)
  extends IdentityService[User] {

  override def retrieve(id: LoginInfo): scala.concurrent.Future[Option[User]] = {
    // Wir nutzen loginInfo.providerKey als UUID-String (siehe AuthController)
    scala.util.Try(UUID.fromString(id.providerKey)).toOption match {
      case Some(uuid) => userService.retrieve(uuid)
      case None       => scala.concurrent.Future.successful(None)
    }
  }
}

@Singleton
class SocialRegistryProvider @Inject()(github: GitHubProvider) {
  val registry: SocialProviderRegistry = SocialProviderRegistry(Seq(github))
}

