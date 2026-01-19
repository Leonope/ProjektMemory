package models

import java.util.UUID
import javax.inject._
import scala.collection.concurrent.TrieMap
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class UserService @Inject()(implicit ec: ExecutionContext) {

  // In-Memory "DB" 
  private val usersById   = TrieMap.empty[UUID, User]
  private val usersByName = TrieMap.empty[String, UUID]

  def retrieve(id: UUID): Future[Option[User]] =
    Future.successful(usersById.get(id))

  def findOrCreate(username: String, email: Option[String]): Future[User] = Future {
    usersByName.get(username).flatMap(usersById.get) match {
      case Some(u) => u
      case None =>
        val u = User(UUID.randomUUID(), username, email)
        usersById.put(u.id, u)
        usersByName.put(username, u.id)
        u
    }
  }
}

