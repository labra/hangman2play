package models

import play.api.db._
import play.api.Play.current
import play.api.Logger

import anorm._
import anorm.SqlParser._

case class Word (
	id: Pk[Long],
	languageId : Long, 
    word : String
)

object Word {

  val word = {
	get[Pk[Long]]("id") ~ 
	get[Long]("languageId") ~ 
	get[String]("word") map {
  	  case id ~
  	  	   languageId ~
  	  	   word => {
  	  		 Word(id, languageId, word)
  	  	   }
  	}
  }
  
  val simpleWord = {
	get[String]("word") map {
  	  case word => word
  	}
  }
  
  val viewWord = {
	get[Pk[Long]]("id") ~ 
	get[String]("code") ~ 
	get[String]("word") map {
  	  case id ~
  	  	   code ~
  	  	   word => {
  	  		 (code,word)
  	  	   }
  	}
  }
  
  def all(): List[Word] = DB.withConnection { implicit c =>
  	SQL("select * from words").as(word *)
  }


  def create(languageId: Long, word: String) {
    DB.withConnection { implicit c =>
      SQL("insert into words (languageId,word) values (%s, '%s')".
	  	     format(languageId,word)).executeUpdate()
    }
  }

  def insert(word: Word) {
	  DB.withConnection { implicit c =>
	  	SQL("insert into words (languageId,word) values (%s, '%s')".
	  	     format(word.languageId,
	  	            word.word)).executeUpdate()
	  }
  }
  
  def delete(id: Pk[Long]) {
		DB.withConnection { implicit c =>
    	SQL("delete from words where id = {id}").on(
    		'id -> id
    		).executeUpdate()
		}
	}

  def lookupIds(languageId : Long) : Option[Long] = {
        val query = "SELECT id FROM words WHERE { languageId = %s } ORDER BY word DESC;".format(languageId)
        val ids : List[Long]= DB.withConnection { implicit c =>
    		SQL(query).as(scalar[Long].*)
		}
        if (ids.isEmpty) None
        else Some(ids.head)
  }
    
  def lookupWords(code : String) : List[String] = {
    val maybeLanguage = Language.lookup(code)
    maybeLanguage match {
      case None => { // Logger.warn("Language " + code + " not found in database"); 
    		  	     List() 
      }	  	   
      case Some(languageId) => {
    	  val query = 
    			  """SELECT word
    			  		FROM words
    			  		WHERE 
    			  			words.languageid = '%s' """.format(languageId)
    	  DB.withConnection { implicit c => SQL(query).as(simpleWord *) }
		}
        
      }
      
    }
  
  
  def findById(id : Long) : Option[Word] = {
   val findWords = DB.withConnection { implicit c =>
  		 SQL("select * from words where id = {id}").on('id -> id).as(word *)
       }
   if (findWords.isEmpty) None
   else Some(findWords.head)
  }

}

case class ViewWord(
	id: Long,
	language: String,
    word : String
)
