package controllers

import baseSpec.BaseSpec
import models.{APIError, DataModel}
import org.scalamock.scalatest.MockFactory
import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.http.Status.CREATED
import play.api.mvc.Results.Status
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
    "test description",
    100
  )

  "Repository Service CRUD functions" should {

    "Index() should return books" in {

    }

    "Index() should return Api Error" in {

    }

    "Create() should return result" in {

//
//      val createBookModel = testService.create(dataModel).map {
//        case Right(_) => Status(CREATED)
//        case Left(apiError) => Status(apiError.upstreamStatus)(apiError.upstreamMessage)
//      }
//
//      whenReady(createBookModel) { result =>
//        result shouldBe Status(CREATED)
//      }

    }

    "Create() should return Api Error" in {

    }

    "Read() should return Book" in {

    }

    "Read() should return Api Error" in {

    }

    "Update() should return return result" in {

    }

    "Update() should return Api Error" in {

    }

    "Delete() should return result" in {

    }

    "Delete() should return Api Error" in {

    }

  }

}
