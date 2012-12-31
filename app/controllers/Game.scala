package controllers

import play.api._

import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.data.format.Formats._
import models._
import play.api.i18n._
import anorm._
import views.html.defaultpages.badRequest
import play.api.Play.current
import play.api.libs.json._

object Game extends Controller {
  
  def langs : Seq[Lang] = Lang.availables
   
  def index = Action { implicit request =>
    val lang = Words.getLang(request,session)
    Ok(views.html.game(langs,lang))
  }

  def about = Action { implicit request =>
    val lang = Words.getLang(request,session)
    Ok(views.html.about(lang))
  }
  
}