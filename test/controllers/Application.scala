package controllers
import org.specs2._

import play.api.test._
import play.api.test.Helpers._
import models._


class ApplicationSpec extends Specification { def is = 
  
  "Specification of Application" 							^
  															p^
  "The application should" 									^
  	"respond to plain index with html"						!e1^
  															end

  def e1 = {
		running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
			val result = Application.index(FakeRequest())
			status(result) must equalTo(OK)
			contentType(result) must beSome("text/html")
			charset(result) must beSome("utf-8")
		}
  }  
  


}