package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.data.format.Formats._
import models._
import play.api.i18n._
import anorm._
import play.api.Play.current

object Admin extends Controller  with Secured {
  
  def langs = Lang.availables
  
  def defaultLang = Lang.preferred(Lang.availables).language
  
  val loginForm = Form(
    tuple(
      "email" -> text,
      "password" -> text
    ) verifying ("Invalid email or password", result => result match {
      case (email, password) => User.authenticate(email, password).isDefined
    })
  )

  /**
   * Login page.
   */
  def login = Action { implicit request =>
    Ok(views.html.login(lang,loginForm))
  }

  /**
   * Handle login form submission.
   */
  def authenticate = Action { implicit request =>
    loginForm.bindFromRequest.fold(
      formWithErrors => BadRequest(views.html.login(lang,formWithErrors)),
      user => Redirect(routes.Admin.admin).withSession("email" -> user._1)
    )
  }

  /**
   * Logout and clean the session.
   */
  def logout = Action {
    Redirect(routes.Admin.login).withNewSession.flashing(
      "success" -> "You've been logged out"
    )
  }

  
  def admin = IsAuthenticated { username => request =>
    User.findByEmail(username).map { user =>
      val lang = request.session.get("prefLang").getOrElse(defaultLang)
      println("Admin Session: " + request.session.get("prefLang"))
    
      Ok(views.html.admin(langs,Lang(lang)))
    }.getOrElse(Forbidden)
  }
  
  
  def newLanguage = IsAuthenticated { username => implicit request =>
  	languageForm.bindFromRequest.fold(
    errors => Ok("Errors" + errors), // BadRequest(views.html.index(searchForm)),
    language => {
      Language.create(language)
      Redirect(routes.Admin.languages)
    }
   )
  }	  
 
   def newWord = IsAuthenticated { username =>
    implicit request =>
  	 wordForm.bindFromRequest.fold(
      errors => Ok("Error " + errors.toString()), // BadRequest(views.html.index(Trans.all(), errors)),
      vw => createFromView(vw)
   )
  }

  def createFromView(vw: ViewWord) : Result = {
      Language.lookup(vw.language) match {
              case None => Ok("Language " + vw.language + " not found. Create Language before")
              case Some(languageId) => 
                Word.create(languageId,vw.word)
                Redirect(routes.Admin.words)
            }
  }
  
   
  def deleteLanguage(id: Long) = IsAuthenticated { username => implicit request =>
	  Language.delete(Id(id))
	  Redirect(routes.Admin.languages)
  }

 def deleteWord(id: Long) = IsAuthenticated { username => implicit request =>
  Word.delete(Id(id))
  Ok("Deleted word " + id)
  Redirect(routes.Admin.words)
}

  val languageForm : Form[Language] = Form(
      mapping(
      "id" -> ignored(NotAssigned:Pk[Long]),
      "code" -> nonEmptyText,
      "name" -> nonEmptyText,
      "alphabet" -> nonEmptyText
      )(Language.apply)(Language.unapply)
  )

  
  val wordForm : Form[ViewWord] = Form(
     mapping(
      "id" -> of[Long],
      "language" -> nonEmptyText,
      "word" -> nonEmptyText
     )(ViewWord.apply)(ViewWord.unapply)
  )
  
   def languages = Action { implicit request => 
      val lang = request.session.get("prefLang").getOrElse(defaultLang)
	  Ok(views.html.languages(Lang(lang), Language.all(), languageForm))
  }

  def viewWords : List[ViewWord] = {
    Word.all().map(t => ViewWord(t.id.get,
        Language.findLanguageName(t.languageId).getOrElse("Not found"), 
        t.word)
    )
  }

  def words = Action { request =>
    val lang = request.session.get("prefLang").getOrElse(defaultLang)
    Ok(views.html.words(Lang(lang), viewWords, wordForm))
  }

}


trait Secured {
  
  /**
   * Retrieve the connected user email.
   */
  private def username(request: RequestHeader) = request.session.get("email")

  /**
   * Redirect to login if the user in not authorized.
   */
  private def onUnauthorized(request: RequestHeader) = Results.Redirect(routes.Admin.login)
  
  // --
  
  /** 
   * Action for authenticated users.
   */
  def IsAuthenticated(f: => String => Request[AnyContent] => Result) = Security.Authenticated(username, onUnauthorized) { user =>
    Action(request => f(user)(request))
  }

}
