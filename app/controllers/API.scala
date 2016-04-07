package controllers

import models.{DestId, Destination, Vote}
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc.{Action, Controller}
import scalikejdbc._
import utils.WithSession

import scala.util.Try
import scala.concurrent.duration._

class API extends Controller {
  import API._
  def vote(destId: Long) = Action { implicit req =>
    DB localTx { implicit db =>
      WithSession.check { session =>
        val v = Vote.defaultAlias
        val ip = ipToLong(req.domain).getOrElse(req.domain.hashCode.toLong)
        val dayAgo = System.currentTimeMillis() - 1.day.toMillis
        val sessionCount = Vote.where(sqls.eq(v.sessionId, session.id.value).and.gt(v.created, dayAgo)).count()
        if(sessionCount > 0) Redirect(routes.View.index("Already voted today."))
        else {
          Vote.create(DestId(destId), session.id, ip)
          Redirect(routes.View.index())
        }
      }
    }
  }

  val form = Form("name" -> text.verifying(_.nonEmpty))
  def createDestination() = Action { implicit req =>
    DB localTx { implicit db =>
      WithSession { _ =>
        val name = form.bindFromRequest().get
        val count = Destination.where('name -> name).count()
        if(count != 0L) {
          Redirect(routes.View.index("Already exists"))
        } else {
          Destination.create(name)
          Redirect(routes.View.index())
        }
      }
    }
  }
}

object API {
  def ipToLong(ip: String): Option[Long] = Try {
    ip.split('.').map(_.toLong).reduceLeft { (orig, x) => orig * 256L + x }
  }.toOption
}
