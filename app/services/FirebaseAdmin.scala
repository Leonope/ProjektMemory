package services

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.{FirebaseApp, FirebaseOptions}
import javax.inject.{Inject, Singleton}
import play.api.{Configuration, Environment}

@Singleton
class FirebaseAdmin @Inject()(config: Configuration, env: Environment) {

  private val resourcePath = config.get[String]("firebase.serviceAccount.resource")

  val firebaseApp: FirebaseApp = {
    val is = env.resourceAsStream(resourcePath).getOrElse {
      throw new RuntimeException(
        s"Firebase service account JSON not found in resources: $resourcePath (place it under /conf)"
      )
    }

    val options = FirebaseOptions.builder()
      .setCredentials(GoogleCredentials.fromStream(is))
      .build()

    if (FirebaseApp.getApps.isEmpty) FirebaseApp.initializeApp(options)
    else FirebaseApp.getInstance()
  }
}
