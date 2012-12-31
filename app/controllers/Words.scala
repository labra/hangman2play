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

object Words extends Controller {
  
  def langs : Seq[Lang] = Lang.availables

  def getLang (request: Request[AnyContent], session : Session) = {
    val langsReq = request.acceptLanguages
    val defaultLang = Lang.preferred(langsReq.++(Lang.availables)).language
    val lang = session.get("prefLang").getOrElse(defaultLang)
    Lang(lang)
  }   
    
  def setLang = Action { implicit request =>
  	langForm.bindFromRequest.fold(
  		errors => NotFound("Cannot select language" + errors),
  		lang => { 
  		  Redirect(routes.Words.index).withSession("prefLang" -> lang)
  		}
    )
  }
  
  def index = Action { implicit request =>
    val lang = getLang(request,session)
    Ok(views.html.index(languages, langs, lang, None, List(),searchForm))
  }

  def about = Action { implicit request =>
    val lang = getLang(request,session)
    Ok(views.html.about(lang))
  }
  
  def searchWords = Action { implicit request =>
    searchForm.bindFromRequest.fold(
    errors => BadRequest(views.html.index(languages,langs,lang,None,List(),errors)),
    searchField => {
      val maybeLanguage = Language.findLanguage(searchField.language)
      maybeLanguage match {
        case None => NotFound(Messages("LanguageNotFound"))
        case Some(language) => {
           val words = Word.lookupWords(language.code)
           Ok(views.html.index(languages,langs,
            		 					getLang(request,session),
            		 					Some(language), 
            		 					words, 
            		 					searchForm))
                   
        }
      }
    }
   ) 
  }

  def searchWordsJson (language: String) = Action { implicit request =>
      val maybeLanguage = Language.findLanguage(language)
      maybeLanguage match {
        case None => NotFound(Messages("LanguageNotFound"))
        case Some(language) => {
           val words = Word.lookupWords(language.code)
           Ok(prepareJson(language,words))
        }
      }
  }

  def prepareJson(language: Language, words: List[String]) : String = {
    val json = Json.toJson(Map (
    			"language" -> Json.toJson(language.code),
                "words" -> Json.toJson (words)))
                
    Json.stringify(json)
  }
  
  val searchForm : Form[SearchField] = Form (
     mapping(
      "language" -> nonEmptyText
     )(SearchField.apply)(SearchField.unapply)
  )

  val langForm : Form[String] = Form (
      "lang" -> nonEmptyText
  )

  def languages = Language.all
}