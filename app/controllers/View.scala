package controllers

import models.Destination
import play.api.mvc.{Action, Controller}
import scalikejdbc.DB
import utils.WithSession

class View extends Controller {
  def index(message: String = "") = Action { implicit request =>
    DB localTx { implicit db =>
      WithSession { _ =>
        val destAll = Destination.findAll().sortBy { d => d.money.isEmpty -> -d.vote }
        val open = destAll.filter(_.open)
        val scheduling = destAll.filter(_.scheduling)
        val close = destAll.filter(_.close)
        Ok(views.html.index(message, open, scheduling, close))
      }
    }
  }
}
