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

object Application extends Controller {
  
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
  		  Redirect(routes.Application.index).withSession("prefLang" -> lang)
  		}
    )
  }
  
  def index = Action { implicit request =>
    val lang = getLang(request,session)
    Ok(views.html.index(courses, langs, lang, None, List(),searchForm))
  }

  def about = Action { implicit request =>
    val lang = getLang(request,session)
    Ok(views.html.about(lang))
  }
  
  def searchEnrolment = Action { implicit request =>
    searchForm.bindFromRequest.fold(
    errors => BadRequest(views.html.index(courses,langs,lang,None,List(),errors)),
    searchField => {
      val maybeCourse = Course.findCourse(searchField.course)
      maybeCourse match {
        case None => NotFound(Messages("CourseNotFound"))
        case Some(course) => {
           val enrols = Enrolment.lookupEnrolment(course.code)
           request match {
             case Accepts.Html() => Ok(views.html.index(courses,langs,
            		 					getLang(request,session),
            		 					Some(course), 
            		 					enrols, 
            		 					searchForm))
             case Accepts.Json() => Ok(prepareJson(course,enrols))
             case Accepts.Xml() => Ok(prepareXML(course,enrols))
           }        
        }
      }
    }
   ) 
  }

  def courseXML(code: String) = Action { implicit request =>
      val maybeCourse = Course.findCourse(code)
      maybeCourse match {
        case None => NotFound(Messages("CourseNotFound"))
        case Some(course) => {
           val enrols = Enrolment.lookupEnrolment(course.code)
           Ok(prepareXML(course,enrols))
        }
      }
  }

  def prepareJson(course: Course, enrols: List[(Student,Double)]) : String = {
    val json = Json.toJson(Map (
    			"course" -> Json.toJson(course.code),
                "name" -> Json.toJson(course.name),
                "alumnos" -> Json.toJson (enrols.map(pair => 
                  				Json.toJson (Map("FirstName" -> Json.toJson (pair._1.firstName),
                  								 "LastName" -> Json.toJson (pair._1.lastName),
                  								 "email" -> Json.toJson (pair._1.email),
                  								 "lat" -> Json.toJson (pair._1.lat),
                  								 "long" -> Json.toJson (pair._1.long),
                  				                 "Grade" -> Json.toJson(pair._2)
                  				                 )
                  				            )
                  				         )
                             ) 
               ))
    Json.stringify(json)
  }
  
  def prepareXML(course: Course, enrols: List[(Student,Double)]) : String = {
    val xmlOut = 
    <alumnos xmlns="http://www.uniovi.es/alumnos">
     <curso href={course.code}>{course.name}</curso> {
       enrols.map { e => 
       <alumno dni={e._1.dni}>
        <nombre>{e._1.firstName}</nombre>
        <apellidos> {e._1.lastName}</apellidos>
        <email>{e._1.email}</email>
        <lat>{e._1.lat}</lat>
        <long>{e._1.long}</long>
        <nota>{e._2}</nota>
     </alumno>}
      }
    </alumnos>
    xmlOut.toString
  }
  
  val searchForm : Form[SearchField] = Form (
     mapping(
      "course" -> nonEmptyText
     )(SearchField.apply)(SearchField.unapply)
  )

  val langForm : Form[String] = Form (
      "lang" -> nonEmptyText
  )

  def courses = Course.all
}