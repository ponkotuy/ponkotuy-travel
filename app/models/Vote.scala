package models

import java.sql.ResultSet

import scalikejdbc._
import skinny.orm.{Alias, SkinnyCRUDMapperWithId}

import scala.util.Random

case class Vote(
    id: VoteId,
    destId: DestId,
    sessionId: SessionId,
    ip: Long,
    created: Long
)

case class VoteId(value: Long) extends AnyVal {
  override def toString = value.toString
}

object VoteId {
  val typeBinder: TypeBinder[VoteId] = new TypeBinder[VoteId] {
    override def apply(rs: ResultSet, columnIndex: Int): VoteId = VoteId(rs.getLong(columnIndex))
    override def apply(rs: ResultSet, columnLabel: String): VoteId = VoteId(rs.getLong(columnLabel))
  }
}

object Vote extends SkinnyCRUDMapperWithId[VoteId, Vote] {
  implicit def voteIdTypeBinder = VoteId.typeBinder
  implicit def destIdTypeBinder = DestId.typeBinder
  implicit def sessionIdTypeBinder = SessionId.typeBinder
  override val useExternalIdGenerator = true

  override def idToRawValue(id: VoteId): Any = id.value
  override def rawValueToId(value: Any): VoteId = VoteId(value.toString.toLong)

  override def generateId(): VoteId = VoteId(Random.nextLong())

  override def defaultAlias: Alias[Vote] = createAlias("v")

  override def extract(rs: WrappedResultSet, n: ResultName[Vote]): Vote = autoConstruct(rs, n)

  def create(destId: DestId, sessionId: SessionId, ip: Long)(implicit db: DBSession) =
    createWithAttributes(
      'id -> generateId(),
      'destId -> destId.value,
      'sessionId -> sessionId.value,
      'ip -> ip,
      'created -> System.currentTimeMillis()
    )
}
