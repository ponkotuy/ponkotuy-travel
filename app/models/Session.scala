package models

import java.sql.ResultSet

import scalikejdbc._
import skinny.orm.{Alias, SkinnyCRUDMapperWithId}

case class Session(id: SessionId, created: Long) {
  def save()(implicit session: DBSession) = Session.create(this)
}

case class SessionId(value: Long) extends AnyVal {
  override def toString: String = value.toString
}

object SessionId {
  val typeBinder: TypeBinder[SessionId] = new TypeBinder[SessionId] {
    override def apply(rs: ResultSet, columnIndex: Int): SessionId = SessionId(rs.getLong(columnIndex))
    override def apply(rs: ResultSet, columnLabel: String): SessionId = SessionId(rs.getLong(columnLabel))
  }
}

object Session extends SkinnyCRUDMapperWithId[SessionId, Session] {
  implicit def sessionIdTypeBinder = SessionId.typeBinder
  override val useExternalIdGenerator = true

  override def idToRawValue(id: SessionId): Any = id.value
  override def rawValueToId(value: Any): SessionId = SessionId(value.toString.toLong)

  override def defaultAlias: Alias[Session] = createAlias("s")

  override def extract(rs: WrappedResultSet, n: ResultName[Session]): Session = autoConstruct(rs, n)

  def create(session: Session)(implicit db: DBSession) =
    createWithAttributes('id -> session.id, 'created -> session.created)
}
