package controllers

import baseSpec.BaseSpec
import cats.data.EitherT
import com.mongodb.client.result.{DeleteResult, InsertOneResult, UpdateResult}
import models.{APIError, DataModel}
import org.mongodb.scala.bson.{BsonObjectId, BsonValue, ObjectId}
import org.mongodb.scala.result.InsertOneResult
import org.scalamock.scalatest.MockFactory
import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.http.Status
import play.api.http.Status.CREATED
import play.api.libs.json.{JsValue, OFormat}
import play.api.mvc.Results.Status
import play.api.test.FakeRequest
import play.api.test.Helpers.status
import repositories.{DataRepository, dataRepositoryTrait}
import services.RepositoryService
import uk.gov.hmrc.mongo.MongoComponent

import scala.concurrent.{ExecutionContext, Future}

class RepositoryServiceSpec extends BaseSpec with MockFactory with ScalaFutures with GuiceOneAppPerSuite{

  val mongoComponent: MongoComponent = app.injector.instanceOf[MongoComponent]
  //val mockRepository: DataRepository = mock[DataRepository](mongoComponent)
  val mockDataRepositoryTrait: dataRepositoryTrait = mock[dataRepositoryTrait]
  implicit val executionContext: ExecutionContext = app.injector.instanceOf[ExecutionContext]
  val testService = new RepositoryService(mockDataRepositoryTrait)

  private val dataModel: DataModel = DataModel(
    "abcd",
    "test name",
    Some("test description"),
    Some(100)
  )

  "Repository Service CRUD functions" should {

    "Index() should return books" in {

      (mockDataRepositoryTrait.index: ()=> Future[Either[APIError.BadAPIResponse, Seq[DataModel]]])
        .expects()
        .returning(Future(Right(Seq(dataModel))))
        .once()

      whenReady(testService.index()) { result =>
        result shouldBe Right(Seq(dataModel))
      }

    }

    "Index() should return Api Error" in {

      (mockDataRepositoryTrait.index: ()=> Future[Either[APIError.BadAPIResponse, Seq[DataModel]]])
        .expects()
        .returning(Future(Left(APIError.BadAPIResponse(404, "Books cannot be found"))))
        .once()

      whenReady(testService.index()) { result =>
        result shouldBe Left(APIError.BadAPIResponse(404, "Books cannot be found"))
      }
    }

    "Create() should return result" in {

      val testObject = BsonObjectId(new ObjectId())

      (mockDataRepositoryTrait.create(_:DataModel))
        .expects(dataModel)
        .returning(Future(Right(InsertOneResult.acknowledged(testObject))))
        .once()

      whenReady(testService.create(dataModel)) { result => // had to remove the .value
        result shouldBe Right(InsertOneResult.acknowledged(testObject))
      }

    }


    "Create() should return Api Error" in {

      (mockDataRepositoryTrait.create(_:DataModel))
        .expects(dataModel)
        .returning(Future(Left(APIError.BadAPIResponse(500, "Could not Create Book"))))
        .once()

      whenReady(testService.create(dataModel)) { result => // had to remove the .value
        result shouldBe Left(APIError.BadAPIResponse(500, "Could not Create Book"))
      }

    }

    "Read() should return Book" in {

      (mockDataRepositoryTrait.read(_:String))
        .expects("abcd")
        .returning(Future(Right(Some(dataModel))))
        .once()

      whenReady(testService.read("abcd")) { result => // had to remove the .value
        result shouldBe Right(Some(dataModel))
      }

    }

    "Read() should return Api Error" in {

      (mockDataRepositoryTrait.read(_:String))
        .expects("abcd")
        .returning(Future(Left(APIError.BadAPIResponse(404, "Unable to find any books"))))
        .once()

      whenReady(testService.read("abcd")) { result => // had to remove the .value
        result shouldBe Left(APIError.BadAPIResponse(404, "Unable to find any books"))
      }

    }

    "Update() should return return result" in {

      val testObject = BsonObjectId(new ObjectId())

      (mockDataRepositoryTrait.update(_:String,_:String,_:DataModel))
        .expects("test1","test2",dataModel)
        .returning(Future(Right(UpdateResult.acknowledged(1L,1L,testObject))))
        .once()

      whenReady(testService.update("test1","test2",dataModel)) { result => // had to remove the .value
        result shouldBe Right(UpdateResult.acknowledged(1L,1L,testObject))
      }

    }

    "Update() should return Api Error" in {

      (mockDataRepositoryTrait.update(_:String,_:String,_:DataModel))
        .expects("test1","test2",dataModel)
        .returning(Future(Left(APIError.BadAPIResponse(404, "Unable to Update Book"))))
        .once()

      whenReady(testService.update("test1","test2",dataModel)) { result => // had to remove the .value
        result shouldBe Left(APIError.BadAPIResponse(404, "Unable to Update Book"))
      }

    }

    "Delete() should return result" in {

      (mockDataRepositoryTrait.delete(_:String))
        .expects("abcd")
        .returning(Future(Right(DeleteResult.acknowledged(1))))
        .once()

      whenReady(testService.delete("abcd")) { result => // had to remove the .value
        result shouldBe Right(DeleteResult.acknowledged(1))
      }
    }

    "Delete() should return Api Error" in {

      (mockDataRepositoryTrait.delete(_:String))
        .expects("abcd")
        .returning(Future(Left(APIError.BadAPIResponse(404, "Unable to Delete Book"))))
        .once()

      whenReady(testService.delete("abcd")) { result => // had to remove the .value
        result shouldBe Left(APIError.BadAPIResponse(404, "Unable to Delete Book"))
      }
    }

  }

}
