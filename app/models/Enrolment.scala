package models

import play.api.db._
import play.api.Play.current
import play.api.Logger

import anorm._
import anorm.SqlParser._

case class Enrolment(
	id: Pk[Long],
	courseId : Long, 
	studentId: Long,
    grade : Double
)

object Enrolment {

  val enrolment = {
	get[Pk[Long]]("id") ~ 
	get[Long]("courseId") ~ 
	get[Long]("studentId") ~ 
	get[Double]("grade") map {
  	  case id ~
  	  	   courseId ~
  	  	   studentId ~
  	  	   grade => {
  	  		 Enrolment(id, courseId, studentId, grade)
  	  	   }
  	}
  }
  
  
  val viewEnrolment = {
	get[Pk[Long]]("id") ~ 
	get[String]("dni") ~ 
	get[String]("firstName") ~ 
	get[String]("lastName") ~ 
	get[String]("email") ~ 
	get[Double]("lat") ~ 
	get[Double]("long") ~ 
	get[Double]("grade") map {
  	  case id ~
  	  	   dni ~
  	  	   firstName ~
  	  	   lastName ~
  	  	   email ~
  	  	   lat ~
  	  	   long ~
  	  	   grade => {
  	  		 (Student(id,dni,firstName,lastName,email,lat,long),grade)
  	  	   }
  	}
  }
  
  def all(): List[Enrolment] = DB.withConnection { implicit c =>
  	SQL("select * from enrolment").as(enrolment *)
  }


  def create(courseId: Long, studentId: Long, grade: Double) {
    DB.withConnection { implicit c =>
      SQL("insert into enrolment (courseId,studentId,grade) values (%s, %s, %s)".
	  	     format(courseId,studentId,grade)).executeUpdate()
    }
  }

  def insert(enrolment: Enrolment) {
	  DB.withConnection { implicit c =>
	  	SQL("insert into enrolment (courseId,studentId,grade) values (%s, %s, %s)".
	  	     format(enrolment.courseId,
	  	            enrolment.studentId,
	  	            enrolment.grade)).executeUpdate()
	  }
  }
  
  def delete(id: Pk[Long]) {
		DB.withConnection { implicit c =>
    	SQL("delete from enrolment where id = {id}").on(
    		'id -> id
    		).executeUpdate()
		}
	}

  def lookupIds(courseId : Long, studentId: Long) : Option[Long] = {
        val query = "SELECT id FROM enrolment WHERE { courseId = %s and studentId = %s } ORDER BY grade DESC;".format(courseId,studentId)
        val ids : List[Long]= DB.withConnection { implicit c =>
    		SQL(query).as(scalar[Long].*)
		}
        if (ids.isEmpty) None
        else Some(ids.head)
  }
    
  def lookupEnrolment(code : String) : List[(Student,Double)] = {
    val maybeCourse = Course.lookup(code)
    maybeCourse match {
      case None => { // Logger.warn("Course " + code + " not found in database"); 
    		  	     List() 
      }	  	   
      case Some(courseId) => {
    	  val query = 
    			  """SELECT DISTINCT student.id, student.dni, student.firstName,student.lastName, student.email, student.lat, student.long, enrolment.grade
    			  		FROM enrolment, course, student
    			  		WHERE 
    			  			enrolment.courseid = '%s' and
    			  			enrolment.studentid = student.id""".format(courseId);
    	  DB.withConnection { implicit c =>
    	  	SQL(query).as(viewEnrolment *)
    	  }
		}
        
      }
      
    }
  
  
  def findById(id : Long) : Option[Enrolment] = {
   val enrols = DB.withConnection { implicit c =>
  		 SQL("select * from enrolment where id = {id}").on('id -> id).as(enrolment *)
       }
   if (enrols.isEmpty) None
   else Some(enrols.head)
  }

}

case class ViewEnrolment(
	id: Long,
	course: String,
    dni : String, 
	grade : Double
)
