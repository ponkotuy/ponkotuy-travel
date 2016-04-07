package utils

import java.security.SecureRandom

import models.{Session, SessionId}
import play.api.mvc.{Request, Result, Results}
import scalikejdbc._

import scala.util.Try

object WithSession {
  val SessionName = "key"
  val random = new SecureRandom()

  def apply[T](f: Session => Result)(implicit db: DBSession, request: Request[T]): Result = {
    val session = getSession().getOrElse(newSession())
    f(session).withSession(SessionName -> session.id.toString)
  }

  def check[T](f: Session => Result)(implicit db: DBSession, request: Request[T]): Result = {
    getSession().fold(Results.Unauthorized("Session failed")) { session =>
      f(session).withSession(SessionName -> session.id.toString)
    }
  }

  def getSession[T]()(implicit db: DBSession, request: Request[T]): Option[Session] = {
    val id = request.session.get(SessionName).flatMap { it => Try { SessionId(it.toLong) }.toOption }
    id.flatMap(Session.findById)
  }

  def newSession()(implicit db: DBSession): Session = {
    val session = Session(SessionId(random.nextLong()), System.currentTimeMillis())
    session.save()
    session
  }
}
