package models
import org.specs2.mutable._

import play.api.test._
import play.api.test.Helpers._
import anorm._

class StudentSpec extends Specification {

  "create Student" in {
    running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
      val dni = "22"
      val firstName = "Jose"
      val lastName = "Jose"
      Student.create(Student(Id(1),dni,firstName,lastName,"jose@ex.org",2.3,3.4))
      val id = Student.lookup(dni)
	  id must beSome  
    }
   }
  
  "create the same student several times" in {
    running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
      val dni = "22"
      val firstName = "Jose"
      val lastName = "Jose"
      Student.create(Student(Id(1),dni,firstName,lastName,"j@kiko.org",2.3,3.4))
      Student.create(Student(Id(2),dni,firstName,lastName,"j@kiko.org",2.3,3.4))
      Student.create(Student(Id(3),dni,firstName,lastName,"j@kiko.org",2.3,3.4))
      val id = Student.lookup(dni)
	  id must beSome  
    }
   }

    "delete Student" in {
    running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
      Student.create(Student(Id(1),"22","Jose","Labra","j@kiko.org",2.3,3.4))
      Student.create(Student(Id(2),"33", "Juan","Torre","j@kiko.org",2.3,3.4))

      val id = Student.lookup("22")
      id must beSome  
      Student.delete(Id(id.get))
      val id2 = Student.lookup("22")
      id2 must beNone
    }
   }

}