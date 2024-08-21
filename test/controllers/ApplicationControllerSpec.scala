package controllers

import baseSpec.{BaseSpec, BaseSpecWithApplication}
import models.DataModel
import org.scalatest.matchers.must.Matchers.convertToAnyMustWrapper
import play.api.test.FakeRequest
import play.api.http.Status
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{ControllerComponents, Result}
import play.api.test.CSRFTokenHelper.CSRFFRequestHeader
import play.api.test.Helpers._
import repositories.DataRepository
import play.api.test.Helpers._
import scala.concurrent.ExecutionContext.global
import scala.concurrent.Future
import play.api.mvc._

class ApplicationControllerSpec extends BaseSpecWithApplication {
  val TestApplicationController = new ApplicationController(component,repository,service,repoService)
  // create tests for bad requests too and make them pass errors so match error

  private val dataModel: DataModel = DataModel(
    "abcd",
    "test name",
    Option("test description"),
    Option(100)
  )

  "ApplicationController .index()" should {
    beforeEach()
    val result = TestApplicationController.index()(FakeRequest()) // The FakeRequest() is needed to mimic an incoming HTTP request, the same as hitting the route in the browser.
    "return Books" in {
      status(result) shouldBe Status.OK // Creates a new value result and assigns it the outcome of calling the function index() on the controller
    }
    afterEach()
  }

  "ApplicationController .Read(id:String)" should {
    beforeEach()
    "find a book in the database by id" in {
      val request: FakeRequest[JsValue] = buildGet("/api/${dataModel._id}").withBody[JsValue](Json.toJson(dataModel))
      val createdResult: Future[Result] = TestApplicationController.create()(request)
      status(createdResult) shouldBe Status.CREATED

      val readResult: Future[Result] = TestApplicationController.read("abcd")(FakeRequest())
      status(readResult) shouldBe Status.OK
      contentAsJson(readResult).as[JsValue] shouldBe request.body
    }
    afterEach()
  }

  "ApplicationController .Read(name:String)" should {
    beforeEach()
    "find a book in the database by name" in {
      val request: FakeRequest[JsValue] = buildGet("/api/${dataModel._id}").withBody[JsValue](Json.toJson(dataModel))
      val createdResult: Future[Result] = TestApplicationController.create()(request)
      status(createdResult) shouldBe Status.CREATED

      val readResult: Future[Result] = TestApplicationController.read("test name")(FakeRequest())
      status(readResult) shouldBe Status.OK
      contentAsJson(readResult).as[JsValue] shouldBe request.body
    }
    afterEach()
  }

  "ApplicationController.Read(wrongIdOrName:String)" should {
    beforeEach()
    "return Unable to find any books when given wrong id or name " in {
      val request: FakeRequest[JsValue] = buildGet("/api/${dataModel._id}").withBody[JsValue](Json.toJson(dataModel))
      val createdResult: Future[Result] = TestApplicationController.create()(request)
      status(createdResult) shouldBe Status.CREATED

      val readResult: Future[Result] = TestApplicationController.read("sas")(FakeRequest())
      status(readResult) shouldBe Status.NOT_FOUND
      contentAsJson(readResult).as[JsValue] shouldBe Json.toJson("Unable to find any books")
    }
    afterEach()
  }

  "ApplicationController .Update(id:String)" should {
    beforeEach()
    val updateDataModelName: DataModel = DataModel(
      "abcd",
      "just checking if one item changed",
      Option("test description"),
      Option(100)
    )
    val updateDataModelPageCount: DataModel = DataModel(
      "abcd",
      "test name",
      Option("test description"),
      Option(300)
    )

    "update the name of the book in the database by id and name" in {
      val request: FakeRequest[JsValue] = buildGet("/api/${dataModel._id}").withBody[JsValue](Json.toJson(dataModel))
      val createdResult: Future[Result] = TestApplicationController.create()(request)
      status(createdResult) shouldBe Status.CREATED

      val updateRequest = buildGet("/api/${dataModel._id}").withBody[JsValue](Json.toJson(updateDataModelName))
      val updatedResult = TestApplicationController.update("abcd","title")(updateRequest)
      status(updatedResult) shouldBe Status.ACCEPTED

      val readResult: Future[Result] = TestApplicationController.read("abcd")(FakeRequest())
      status(readResult) shouldBe Status.OK
      contentAsJson(readResult).as[JsValue] shouldBe updateRequest.body
    }

    "update the page count in the database by id and pageCount" in {
      val request: FakeRequest[JsValue] = buildGet("/api/${dataModel._id}").withBody[JsValue](Json.toJson(dataModel))
      val createdResult: Future[Result] = TestApplicationController.create()(request)
      status(createdResult) shouldBe Status.CREATED

      val updateRequest = buildGet("/api/${dataModel._id}").withBody[JsValue](Json.toJson(updateDataModelPageCount))
      val updatedResult = TestApplicationController.update("abcd","pageCount")(updateRequest)
      status(updatedResult) shouldBe Status.ACCEPTED

      val readResult: Future[Result] = TestApplicationController.read("abcd")(FakeRequest())
      status(readResult) shouldBe Status.OK
      contentAsJson(readResult).as[JsValue] shouldBe updateRequest.body
    }

    "ApplicationController .Update(id:String) wrong id should return 404 " in {
      val request: FakeRequest[JsValue] = buildGet("/api/${dataModel._id}").withBody[JsValue](Json.toJson(dataModel))
      val createdResult: Future[Result] = TestApplicationController.create()(request)
      status(createdResult) shouldBe Status.CREATED

      val updateRequest = buildGet("/api/${dataModel._id}").withBody[JsValue](Json.toJson(updateDataModelPageCount))
      val updatedResult = TestApplicationController.update("sass","pageCount")(updateRequest)
      status(updatedResult) shouldBe 404
      contentAsJson(updatedResult).as[JsValue] shouldBe Json.toJson("Unable to Update Book")
    }
    afterEach()
  }

  "ApplicationController .Update(id:String) BadRequest" should {
    beforeEach()
    "update a book in the database by id" in {
      val updateRequestBad = buildGet("/api/${dataModel._id}/${fieldName}").withBody[JsValue](Json.toJson("badRequest"))
      val updatedResult = TestApplicationController.update("abcd","name")(updateRequestBad)
      status(updatedResult) shouldBe Status.BAD_REQUEST
    }
    afterEach()
  }

  "ApplicationController .Delete(id:String)" should {
    beforeEach()
    "delete a book in the database by id" in {
      val request: FakeRequest[JsValue] = buildGet("/api/${dataModel._id}").withBody[JsValue](Json.toJson(dataModel))
      val createdResult: Future[Result] = TestApplicationController.create()(request)
      status(createdResult) shouldBe Status.CREATED

      val deletedResult = TestApplicationController.delete("abcd")(request)
      status(deletedResult) shouldBe Status.ACCEPTED
    }
    afterEach()
  }

  "ApplicationController.Delete(wrongId:String)" should {
    beforeEach()
    "return 404 unable to delete book when given and id of a book that doesn't exist" in {
      val request: FakeRequest[JsValue] = buildGet("/api/${dataModel._id}").withBody[JsValue](Json.toJson(dataModel))
      val createdResult: Future[Result] = TestApplicationController.create()(request)
      status(createdResult) shouldBe Status.CREATED

      val deletedResult = TestApplicationController.delete("sass")(request)
      status(deletedResult) shouldBe 404
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

//----------

  "ApplicationController .addDataModelForm()" should {
    beforeEach()

    val formData = DataModel("someId", "Title", Some("Description"), Some(100))

    "create a book from a form in the database" in {
      val request= buildPost("/api")
        .withFormUrlEncodedBody(
        "_id" -> formData._id,
        "title" -> formData.title,
        "description" -> Some("Description").getOrElse(""),
        "pageCount" ->  Some(100).getOrElse("").toString
      )
      //.withCSRFToken some reason i cant put this before the .withFormUrlEncodedBody()

      val createdResult = TestApplicationController.addDataModelForm()(request)

      status(createdResult) shouldBe Status.CREATED
    }
    afterEach()
  }

  "ApplicationController .create() badRequest" should {
    beforeEach()
    "return a error message" in {
      val request= buildPost("/api")
        .withFormUrlEncodedBody(
          "_id" -> "",
          "pageCount" ->  ""
        )
      //.withCSRFToken some reason i cant put this before the .withFormUrlEncodedBody()

      val createdResult = TestApplicationController.addDataModelForm()(request)
      status(createdResult) shouldBe Status.BAD_REQUEST
    }
    afterEach()
  }

  override def beforeEach(): Unit = await(repository.deleteAll())
  override def afterEach(): Unit = await(repository.deleteAll())
}
