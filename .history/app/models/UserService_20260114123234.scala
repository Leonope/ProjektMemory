package models

import java.util.UUID
import javax.inject._
import scala.concurrent.{ExecutionContext, Future}
import scala.collection.concurrent.TrieMap

@Singleton
class UserService @Inject()(implicit ec: ExecutionContext) {
  private val users = TrieMap.empty[UUID, User]

  def retrieve(id: UUID): Future[Option[User]] =
    Future.successful(users.get(id))

  def findOrCreate(login: String, email: Option[String]): Future[User] = Future {
    users.values.find(_.login == login) match {
      case Some(u) => u
      case None =>
        val u = User(UUID.randomUUID(), login, email)
        users.put(u.id, u)
        u
    }
  }
}

