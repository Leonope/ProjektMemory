package services

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.{FirebaseApp, FirebaseOptions}
import javax.inject.{Inject, Singleton}
import play.api.Configuration

import java.io.FileInputStream

@Singleton
class FirebaseAdmin @Inject()(config: Configuration) {

  private val credentialPath = config.get[String]("firebase.serviceAccount.path")

  val firebaseApp: FirebaseApp = {
    val serviceAccount = new FileInputStream(credentialPath)
    val options = FirebaseOptions.builder()
      .setCredentials(GoogleCredentials.fromStream(serviceAccount))
      .build()

    if (FirebaseApp.getApps.isEmpty) FirebaseApp.initializeApp(options)
    else FirebaseApp.getInstance()
  }
}

