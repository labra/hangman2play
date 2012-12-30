package models

import play.api.db._
import play.api.Play.current
import play.api.Logger

import anorm._
import anorm.SqlParser._

case class Student(
	id: Pk[Long], 
	dni: String,
    firstName: String,
    lastName: String,
    email: String,
    lat: Double,
    long: Double
)

object Student {

  val student = {
	get[Pk[Long]]("id") ~ 
	get[String]("dni") ~ 
	get[String]("firstName") ~ 
	get[String]("lastName") ~
	get[String]("email") ~
	get[Double]("lat") ~
	get[Double]("long") map {
  	  case id~dni~firstName~lastName~email~lat~long => Student(id, dni, firstName, lastName,email,lat,long)
  	}
  }
  
  def all(): List[Student] = DB.withConnection { implicit c =>
  	SQL("select * from student").as(student *)
  }
  
  def create(student: Student) {
    // Only creates a new language if it didn't exist
    if (lookup(student.dni) == None)
	  DB.withConnection { implicit c =>
	  	SQL("insert into student (dni, firstName, lastName, email, lat, long) values ('%s', '%s', '%s','%s',%s,%s)".
	  	     format(student.dni,student.firstName,student.lastName, student.email,student.lat,student.long)).executeUpdate()
	  }
  }
  
  def delete(id: Pk[Long]) {
		DB.withConnection { implicit c =>
    	SQL("delete from student where id = {id}").on(
    		'id -> id
    		).executeUpdate()
		}
  }

  def lookup(dni: String) : Option[Long] = {
    val ids = DB.withConnection { implicit c =>
    		  SQL("select id from student where dni = {dni}").on(
    				  'dni -> dni
    		  ).as(scalar[Long].*)
    		}
    ids.length match {
      case 0 => None
      case 1 => Some(ids.head)
      case _ => {
        Logger.warn("Lookup student: " + dni + ". More than one id (selected the first)")
        println("Lookup student: " + dni + ". More than one id (selected the first)")
        Some(ids.head)
      }
    }
  }
  
  def findDNI(id : Long) : Option[String] = {
    val found = DB.withConnection { implicit c =>
    SQL("select * from student where id = {id}").on(
    		'id -> id
    	).as(student *)
	}
    if (found.isEmpty) None
    else Some(found.head.dni)
  }

  def find(id : Long) : Option[Student] = {
    val found = DB.withConnection { implicit c =>
    SQL("select * from student where id = {id}").on(
    		'id -> id
    	).as(student *)
	}
    if (found.isEmpty) None
    else Some(found.head)
  }

}
