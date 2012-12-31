import play.api._
import play.api.mvc._

import models._
import anorm._

object Global extends GlobalSettings {
  
  override def onStart(app: Application) {
    InitialData.insert()
  }
  
/*  override def onRouteRequest(request: RequestHeader): Option[Handler] = {
     println("executed before every request:" + request)
     super.onRouteRequest(request)
  } */
}

object InitialData {
  
  def insert() = {
    
    if(User.findAll.isEmpty) {
      
      Seq(
        User("pepe@abc.org", "Jose Torre", "abc"),
        User("luis@abc.org", "Luis Tamargo", "luis")
      ).foreach(User.create)

      Seq(
        Language(Id(1),"en","English"),
        Language(Id(2),"es","Spanish")
      ).foreach(Language.create)

      Seq(
        ViewWord(1,"en","stop"),
        ViewWord(2,"es","parada"),
        ViewWord(3,"en","word"),
        ViewWord(4,"es","palabra"),
        ViewWord(4,"en","english")
      ).foreach(vw => Word.create(Language.lookup(vw.language).get,vw.word))

    }
  }
}