package controllers

import baseSpec.BaseSpec
import models.DataModel
import org.scalamock.scalatest.MockFactory
import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import services.RepositoryService

class RepositoryServiceSpec extends BaseSpec with MockFactory with ScalaFutures with GuiceOneAppPerSuite{

  val mockRepositoryService: RepositoryService = mock[RepositoryService]
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
