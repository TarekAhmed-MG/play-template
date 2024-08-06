package controllers

import baseSpec.BaseSpec
import cats.data.EitherT
import connectors.LibraryConnector
import models.{APIError, DataModel}
import org.scalamock.scalatest.MockFactory
import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.http.Status
import play.api.libs.json.{JsValue, Json, OFormat}
import play.api.test.Helpers.status
import services.LibraryService

import scala.concurrent.{ExecutionContext, Future}


class LibraryServiceSpec extends BaseSpec with MockFactory with ScalaFutures with GuiceOneAppPerSuite{

  /*
    We've created an ExecutionContext class in the same way as before, but our LibraryConnector is different.

    Why?
    We explicitly call methods on the LibraryConnector class in the service and these methods can return different responses based off what you call it with
    We don't want to test our LibraryConnector functionality as part of this spec, we only care about the LibraryServiceSpec

    The solution to these problems is a concept known as mocking, where you explicitly tell the methods in LibraryConnector what to return,
    so that you can test how your service responds independently (usually known as unit testing). In this case, instead of making a call to the Google Books API,
    we can pretend to have received the gameOfThrones JSON from the API and test the functionality of our code.
 */

  val mockConnector = mock[LibraryConnector]
  implicit val executionContext: ExecutionContext = app.injector.instanceOf[ExecutionContext]
  val testService = new LibraryService(mockConnector)

  val gameOfThrones: JsValue = Json.obj(
    "_id" -> "someId",
    "name" -> "A Game of Thrones",
    "description" -> "The best book!!!",
    "pageCount" -> 100
  )

  "getGoogleBook" should {
    val url: String = "testUrl"

    "return a book" in {
      (mockConnector.get[JsValue](_: String)(_: OFormat[JsValue], _: ExecutionContext))
        .expects(url, *, *)
        .returning(EitherT.rightT[Future, APIError](gameOfThrones.as[JsValue]))
        .once()

      whenReady(testService.getGoogleBook(urlOverride = Some(url), search = "", term = "").value) { result => // had to remove the .value
        result shouldBe Right(gameOfThrones)
      }
    }
  }

//  "return an error" in {
//    val url: String = "testUrl"
//
//    (mockConnector.get[JsValue](_: String)(_: OFormat[JsValue], _: ExecutionContext))
//      .expects(url, *, *)
//      .returning(EitherT.leftT[Future,JsValue](APIError))// How do we return an error?
//      .once()
//
//    whenReady(testService.getGoogleBook(urlOverride = Some(url), search = "", term = "").value) { result =>
//      result shouldBe APIError
//    }
//  }





}
