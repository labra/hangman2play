package models
import org.specs2.mutable._

import play.api.test._
import play.api.test.Helpers._
import anorm._

class EnrolmentSpec extends Specification {

  "create an enrolment" in {
    running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
      val dni = "22"
      Student.create(Student(Id(1),dni,"Jose","Labra","jose@ex.org",2.3,3.4))
      val idStudent = Student.lookup(dni)
      
      val code = "xml"
      Course.create(Course(Id(1),code,"XML Course", "", ""))
      val idCourse = Course.lookup(code)
      
      Enrolment.create(idCourse.get,idStudent.get,4.5)

      val idt = Enrolment.lookupIds(idCourse.get,idStudent.get)
	  idt must beSome
	  val t = Enrolment.findById(idt.get)
	  t must beSome
	  t.get.grade must beEqualTo(4.5)
    }
   }


}