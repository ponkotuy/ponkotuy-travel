package controllers

import forms.UpdateDestination
import models.{DestId, Destination}
import play.api.mvc.{Action, Controller, Result}
import scalikejdbc.{DB, DBSession}
import utils.WithSession

class Admin extends Controller {
  def index(message: String) = Action { implicit req =>
    DB localTx { implicit db =>
      WithSession { _ =>
        val d = Destination.defaultAlias
        val dests = Destination.findAll(Seq(d.state))
        Ok(views.html.admin(message, dests))
      }
    }
  }

  def updateDestination(destId: Long) = Action { implicit req =>
    DB localTx { implicit db =>
      WithSession { _ =>
        UpdateDestination.form(DestId(destId)).bindFromRequest()
            .fold(
              form => Redirect(routes.Admin.index(form.errors.mkString("\n"))),
              update
            )
      }
    }
  }

  def update(dest: UpdateDestination)(implicit session: DBSession): Result = {
    dest.update()
    Redirect(routes.Admin.index())
  }
}
