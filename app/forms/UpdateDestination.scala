package forms

import models.{DestId, DestState, Destination}
import play.api.data.Form
import play.api.data.Forms._
import scalikejdbc.DBSession

case class UpdateDestination(
    id: DestId,
    money: Option[Int],
    state: DestState,
    url: Option[String],
    message: Option[String]) {
  def update()(implicit session: DBSession): Unit = {
    Destination.updateById(id).withAttributes('money -> money, 'state -> state.value, 'url -> url, 'message -> message)
  }
}

object UpdateDestination {
  def form(id: DestId) = Form(
    mapping(
      "money" -> optional(number(min = 0)),
      "state" -> number(min = 0, max = 3),
      "url" -> optional(nonEmptyText),
      "message" -> optional(nonEmptyText)
    )(fromForm(id))(toForm)
  )

  def fromForm(id: DestId)(money: Option[Int], state: Int, url: Option[String], message: Option[String]): UpdateDestination = {
    new UpdateDestination(id, money.filter(_ > 0), DestState.fromInt(state).get, url, message)
  }

  def toForm(update: UpdateDestination): Option[(Option[Int], Int, Option[String], Option[String])] = {
    Some(update.money, update.state.value, update.url, update.message)
  }
}
