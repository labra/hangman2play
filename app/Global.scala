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
        Course(Id(1),"xml","Curso XML","2012","2013"),
        Course(Id(2),"html5","Curso HTML5","2011","2012")
      ).foreach(Course.create)

      Seq(
        Student(Id(1),"123","Jose","Torres","jose@abc.org",43.363129,-5.847645),
        Student(Id(2),"124","Juan","Camino","juan@abc.org",43.5,-5.847645),
        Student(Id(3),"125","Luis","Morcilla","luis@abc.org",43.36,-5.47)
      ).foreach(Student.create)

      Seq(
        ViewEnrolment(1,"xml","123",4.5),
        ViewEnrolment(2,"xml","124",7.9),
        ViewEnrolment(3,"xml","125",9),
        ViewEnrolment(4,"html5","123",5.5),
        ViewEnrolment(4,"html5","125",7.5)
      ).foreach(ve => Enrolment.create(Course.lookup(ve.course).get,
    		  						   Student.lookup(ve.dni).get, 
    		  						   ve.grade))

    }
  }
}