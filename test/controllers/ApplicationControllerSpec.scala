package controllers

import baseSpec.{BaseSpec, BaseSpecWithApplication}
import play.api.test.{FakeRequest}
import play.api.http.Status
import play.api.test.Helpers._
import repositories.DataRepository

class ApplicationControllerSpec extends BaseSpecWithApplication {
  val TestApplicationController = new ApplicationController(component,repository)

  "ApplicationController .index()" should {
    val result = TestApplicationController.index()(FakeRequest()) // The FakeRequest() is needed to mimic an incoming HTTP request, the same as hitting the route in the browser.

    "return TODO" in {
      // status(result) shouldBe Status.NOT_IMPLEMENTED is the same as writing status(result) shouldBe 501
      status(result) shouldBe Status.OK // Creates a new value result and assigns it the outcome of calling the function index() on the controller

    }

  }

  "ApplicationController .Read(id:String)" should {

  }

  "ApplicationController .Update(id:String)" should {

  }

  "ApplicationController .Delete(id:String)" should {

  }

  "ApplicationController .create()" should {

  }

}
