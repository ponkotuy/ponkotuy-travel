package models

import java.sql.ResultSet

import scalikejdbc._
import skinny.orm.{Alias, SkinnyCRUDMapperWithId}

import scala.util.Random

case class Destination(
    id: DestId,
    name: String,
    money: Option[Int],
    state: DestState,
    url: Option[String],
    message: Option[String],
    created: Long) {
  lazy val vote: Long = {
    Vote.where('destId -> id.value).count()
  }

  def open: Boolean = state == DestState.Open
  def scheduling: Boolean = state == DestState.Scheduling
  def close: Boolean = state == DestState.Close
}

case class DestId(value: Long) extends AnyVal {
  override def toString = value.toString
}

object DestId {
  val typeBinder: TypeBinder[DestId] = new TypeBinder[DestId] {
    override def apply(rs: ResultSet, columnIndex: Int): DestId = DestId(rs.getLong(columnIndex))
    override def apply(rs: ResultSet, columnLabel: String): DestId = DestId(rs.getLong(columnLabel))
  }
}

sealed abstract class DestState(val value: Int)

object DestState {
  case object Open extends DestState(0)
  case object Scheduling extends DestState(1)
  case object Close extends DestState(2)

  val values = Vector(Open, Scheduling, Close)
  def fromInt(i: Int): Option[DestState] = values.find(_.value == i)

  val typeBinder: TypeBinder[DestState] = new TypeBinder[DestState] {
    override def apply(rs: ResultSet, columnIndex: Int): DestState =
      fromInt(rs.getInt(columnIndex)).getOrElse(Open)
    override def apply(rs: ResultSet, columnLabel: String): DestState =
      fromInt(rs.getInt(columnLabel)).getOrElse(Open)
  }
}

object Destination extends SkinnyCRUDMapperWithId[DestId, Destination] {
  implicit def destIdTypeBinder = DestId.typeBinder
  implicit def destStateTypeBinder = DestState.typeBinder
  override val useExternalIdGenerator = true

  override def idToRawValue(id: DestId): Any = id.value
  override def rawValueToId(value: Any): DestId = DestId(value.toString.toLong)

  override def generateId(): DestId = DestId(Random.nextLong())

  override def defaultAlias: Alias[Destination] = createAlias("d")

  override def extract(rs: WrappedResultSet, n: ResultName[Destination]): Destination = autoConstruct(rs, n)

  def create(name: String, close: Boolean = false)(implicit db: DBSession) =
    createWithAttributes('name -> name, 'close -> close, 'money -> None, 'created -> System.currentTimeMillis())
}
