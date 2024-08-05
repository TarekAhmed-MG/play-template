package controllers

import baseSpec.{BaseSpec, BaseSpecWithApplication}
import models.DataModel
import play.api.test.FakeRequest
import play.api.http.Status
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.Result
import play.api.test.Helpers._
import repositories.DataRepository

import scala.concurrent.Future

class ApplicationControllerSpec extends BaseSpecWithApplication {
  val TestApplicationController = new ApplicationController(component,repository)

  private val dataModel: DataModel = DataModel(
    "abcd",
    "test name",
    "test description",
    100
  )

  "ApplicationController .index()" should {
    beforeEach()
    val result = TestApplicationController.index()(FakeRequest()) // The FakeRequest() is needed to mimic an incoming HTTP request, the same as hitting the route in the browser.

    "return TODO" in {
      // status(result) shouldBe Status.NOT_IMPLEMENTED is the same as writing status(result) shouldBe 501
      status(result) shouldBe Status.OK // Creates a new value result and assigns it the outcome of calling the function index() on the controller

      afterEach()
    }

  }

  "ApplicationController .Read(id:String)" should {

    "find a book in the database by id" in {

      val request: FakeRequest[JsValue] = buildGet("/api/${dataModel._id}").withBody[JsValue](Json.toJson(dataModel))
      val createdResult: Future[Result] = TestApplicationController.create()(request)

      //Hint: You could use status(createdResult) shouldBe Status.CREATED to check this has worked again

      val readResult: Future[Result] = TestApplicationController.read("abcd")(FakeRequest())

      status(readResult) shouldBe Status.OK
      contentAsJson(readResult).as[JsValue] shouldBe request.body
    }

  }

  "ApplicationController .Update(id:String)" should {

  }

  "ApplicationController .Delete(id:String)" should {

  }

  "ApplicationController .create()" should {

    "create a book in the database" in {
      beforeEach()

      val request: FakeRequest[JsValue] = buildPost("/api").withBody[JsValue](Json.toJson(dataModel))
      val createdResult: Future[Result] = TestApplicationController.create()(request)

      status(createdResult) shouldBe Status.CREATED
      afterEach()
    }

  }

  override def beforeEach(): Unit = await(repository.deleteAll())
  override def afterEach(): Unit = await(repository.deleteAll())
}
