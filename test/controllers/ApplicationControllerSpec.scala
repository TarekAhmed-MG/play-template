package controllers

import baseSpec.{BaseSpec, BaseSpecWithApplication}
import models.DataModel
import org.scalatest.matchers.must.Matchers.convertToAnyMustWrapper
import play.api.test.FakeRequest
import play.api.http.Status
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{ControllerComponents, Result}
import play.api.test.Helpers._
import repositories.DataRepository

import scala.concurrent.ExecutionContext.global
import scala.concurrent.Future

class ApplicationControllerSpec extends BaseSpecWithApplication {
  val TestApplicationController = new ApplicationController(component,repository)
  // create tests for bad requests too and make them pass errors so match error

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

    beforeEach()

    "find a book in the database by id" in {

      val request: FakeRequest[JsValue] = buildGet("/api/${dataModel._id}").withBody[JsValue](Json.toJson(dataModel))
      val createdResult: Future[Result] = TestApplicationController.create()(request)

      //Hint: You could use status(createdResult) shouldBe Status.CREATED to check this has worked again
      status(createdResult) shouldBe Status.CREATED

      val readResult: Future[Result] = TestApplicationController.read("abcd")(FakeRequest())

      status(readResult) shouldBe Status.OK
      contentAsJson(readResult).as[JsValue] shouldBe request.body

    }

    afterEach()

  }

  "ApplicationController .Update(id:String)" should {

    beforeEach()

    val updateDataModel: DataModel = DataModel(
      "abcd",
      "test name",
      "test description",
      200
    )

    "update a book in the database by id" in {

      val request: FakeRequest[JsValue] = buildGet("/api/${dataModel._id}").withBody[JsValue](Json.toJson(dataModel))
      val createdResult: Future[Result] = TestApplicationController.create()(request)

      //Hint: You could use status(createdResult) shouldBe Status.CREATED to check this has worked again
      status(createdResult) shouldBe Status.CREATED


      val updateRequest = buildGet("/api/${dataModel._id}").withBody[JsValue](Json.toJson(updateDataModel))
      val updatedResult = TestApplicationController.update("abcd")(updateRequest)
      status(updatedResult) shouldBe Status.ACCEPTED


      val readResult: Future[Result] = TestApplicationController.read("abcd")(FakeRequest())
      status(readResult) shouldBe Status.OK
      contentAsJson(readResult).as[JsValue] shouldBe updateRequest.body

    }

    afterEach()

  }

  "ApplicationController .Update(id:String) BadRequest" should {

    beforeEach()

    "update a book in the database by id" in {

      val updateRequestBad = buildGet("/api/${dataModel._id}").withBody[JsValue](Json.toJson("badRequest"))
      val updatedResult = TestApplicationController.update("abcd")(updateRequestBad)
      status(updatedResult) shouldBe Status.BAD_REQUEST

    }

    afterEach()

  }

  "ApplicationController .Delete(id:String)" should {
    beforeEach()

    "delete a book in the database by id" in {

      val request: FakeRequest[JsValue] = buildGet("/api/${dataModel._id}").withBody[JsValue](Json.toJson(dataModel))
      val createdResult: Future[Result] = TestApplicationController.create()(request)

      //Hint: You could use status(createdResult) shouldBe Status.CREATED to check this has worked again
      status(createdResult) shouldBe Status.CREATED

      val deletedResult = TestApplicationController.delete("abcd")(request)
      status(deletedResult) shouldBe Status.ACCEPTED

    }

    afterEach()

  }

  "ApplicationController .create()" should {

    beforeEach()

    "create a book in the database" in {

      val request: FakeRequest[JsValue] = buildPost("/api").withBody[JsValue](Json.toJson(dataModel))
      val createdResult: Future[Result] = TestApplicationController.create()(request)

      status(createdResult) shouldBe Status.CREATED
    }

    afterEach()

  }

  "ApplicationController .create() badRequest" should {

    beforeEach()

    "create a book in the database" in {

      val request: FakeRequest[JsValue] = buildPost("/api").withBody[JsValue](Json.toJson("BadRequest"))
      val createdResult: Future[Result] = TestApplicationController.create()(request)


      status(createdResult) shouldBe Status.BAD_REQUEST
    }

    afterEach()

  }

  override def beforeEach(): Unit = await(repository.deleteAll())
  override def afterEach(): Unit = await(repository.deleteAll())
}
