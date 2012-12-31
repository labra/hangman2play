package models

import play.api.db._
import play.api.Logger
import play.api.Play.current

import anorm._
import anorm.SqlParser._

case class Language(
	id: Pk[Long], 
	code: String,
	name: String
)

object Language{

  val language = {
	get[Pk[Long]]("id") ~ get[String]("code")~ get[String]("name") map {
  	  case id~code~name => Language(id, code, name)
  	}
  }
  
def all(): List[Language] = DB.withConnection { implicit c =>
  	SQL("select * from language").as(language *)
  }
  
def create(language: Language) {
  // Insert a course only if it did not exist
  if (lookup(language.code) == None) 
   DB.withConnection { implicit c =>
     SQL("insert into language (code,name) values ({code},{name})").on(
       'code -> language.code,
       'name -> language.name
     ).executeUpdate()
   }
}

def delete(id: Pk[Long]) {
  DB.withConnection { implicit c =>
    SQL("delete from language where id = {id}").on(
    		'id -> id
    		).executeUpdate()
		}
	}

 def lookup(code : String) : Option[Long] = {
    val ids = DB.withConnection { implicit c =>
    	SQL("select id from language where code = {code}").on(
    		'code -> code
    		).as(scalar[Long].*)
		}
    ids.length match {
      case 0 => None
      case 1 => Some(ids.head)
      case _ => {
        Logger.warn("Lookup language: " + code + ". More than one id (selected the first)")
        Some(ids.head)
      }
    }
  }

 def findLanguage(code : String) : Option[Language] = {
   for {
     id <- lookup(code)
     language <- find(id)
   } yield language
 }

 def findLanguageName(id : Long) : Option[String] = {
   for {
     language <- find(id)
   } yield language.name
 }

 def find(id : Long) : Option[Language] = {
    val found = DB.withConnection { implicit c =>
    SQL("select * from language where id = {id}").on(
    		'id -> id
    	).as(language *)
	}
    if (found.isEmpty) None
    else Some(found.head)
  }

}