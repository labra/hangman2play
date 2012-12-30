package models
import org.specs2.mutable._

import play.api.test._
import play.api.test.Helpers._
import models._
import anorm._

class CourseSpec extends Specification {

  "create Course" in {
    running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
      Course.create(Course(Id(1),"xml","XML course","2012","2013"))
      val id = Course.lookup("xml")
	  id must beSome  
    }
   }

   "create the same Course several times" in {
    running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
      val code = "xml"
      Course.create(Course(Id(1),code,"xml 1","",""))
      Course.create(Course(Id(2),code,"xml 2","",""))
      Course.create(Course(Id(3),code,"xml 3","",""))
      val id = Course.lookup(code)
      id must beSome  
    }
   }

  "create other Course" in {
    running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
      val sizeInitial = Course.all().size
      Course.create(Course(Id(11),"a","xml","",""))
      Course.create(Course(Id(12),"b","xpath","",""))
      val id1 = Course.lookup("a")
      val id2 = Course.lookup("b")
	  id1 must beSome  
      id2 must beSome
      id1 must_!= id2
      (Course.all().size - sizeInitial) must be_==(2)
    }
   }

  "delete Course" in {
    running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
      val code1 = "code1"
      val code2 = "code2"
      Course.create(Course(Id(1),code1,"code 1","",""))
      Course.create(Course(Id(2),code2,"code 2","",""))

      val id = Course.lookup(code1)
      id must beSome  

      val id2 = Course.lookup(code2)
      id2 must beSome  

      Course.delete(Id(id.get))

      val id1b = Course.lookup(code1)
      id1b must beNone

      val id2b = Course.lookup(code2)
      id2b must beSome  
    }
   }

}