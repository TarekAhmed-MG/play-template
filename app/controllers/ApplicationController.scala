package controllers

import cats.data.EitherT
import models.APIError.BadAPIResponse
import models.DataModel
import play.api.libs.json.{JsError, JsSuccess, JsValue, Json}
import play.api.mvc._
import repositories.DataRepository
import services.{LibraryService, RepositoryService}

import javax.inject._
import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try


@Singleton
class ApplicationController @Inject()(
                                       val controllerComponents: ControllerComponents,
                                       val dataRepository: DataRepository, // usually dont need a repository injection in the controller but will leave it here for the example
                                       val libraryService: LibraryService,
                                       val repositoryService: RepositoryService
                                     )
                                     (implicit val ec: ExecutionContext)
  extends BaseController{

  // todo is a play feature that is a default package for actions that hasnt been completed yet
  def index(): Action[AnyContent] = Action.async { implicit request =>
    repositoryService.index().map{
      case Right(item: Seq[DataModel]) => Ok {Json.toJson(item)}
      case Left(_) => Status(NOT_FOUND)(Json.toJson("Unable to find any books"))
    }
  }
  // to see the sToDO page we need to add an app route that references to the new controller and method

  def create(): Action[JsValue] = Action.async(parse.json) { implicit request =>
    request.body.validate[DataModel] match {
      case JsSuccess(dataModel, _) =>
//       repositoryService.create(dataModel).map(_ => Created)

        repositoryService.create(dataModel:DataModel) match {
          case Right(_) => Future.successful(Created)
          case Left(apiError) => Future.successful(Status(apiError.upstreamStatus)(apiError.upstreamMessage))
        }
      case JsError(_) => Future(BadRequest)
    }
  }

//  def read(id:String): Action[AnyContent] = Action.async{implicit request =>
//    dataRepository.read(id).map(item => Ok{Json.toJson(item)})// check this to give back a unable to find any books
//  }

  def read(idOrName:String): Action[AnyContent] = Action.async{implicit request =>
    repositoryService.read(idOrName).map{
      case Some(item: DataModel) => Ok{Json.toJson(item)}
      case None => Status(NOT_FOUND)(Json.toJson("Unable to find any books"))
    }
  }

//  def update(id:String): Action[JsValue] = Action.async(parse.json) { implicit request =>
//    request.body.validate[DataModel] match {
//      case JsSuccess(dataModel, _) =>
//        dataRepository.update(id,dataModel).map(_ => Accepted)
//      case JsError(_) => Future(BadRequest)
//    }
//  }

  def update(id:String, fieldName:String): Action[JsValue] = Action.async(parse.json) { implicit request =>
    request.body.validate[DataModel] match {
      case JsSuccess(dataModel, _) =>
        //repositoryService.update(id,fieldName,dataModel).map(_ => Accepted)
        repositoryService.update(id,fieldName,dataModel) match {
          case Right(_) => Future.successful(Accepted)
          case Left(apiError) => Future.successful(Status(apiError.upstreamStatus)(apiError.upstreamMessage))
        }
      case JsError(_) => Future(BadRequest)
    }
  }

//  def delete(id:String): Action[AnyContent] = Action.async{implicit request =>
//    dataRepository.delete(id).map(_ => Accepted) // check this to give back a unable to delete
//  }

//  def delete(id:String): Action[AnyContent] = Action.async{implicit request =>
//    repositoryService.read(id).map{ // look into using a BSON Filter.exists() method to check if its in the database https://www.baeldung.com/java-mongodb-filters instead of having to use the read method.
//      case Some(item: DataModel) =>
//        repositoryService.delete(item._id)
//        Status(ACCEPTED)
//      case None => Status(404)(Json.toJson("Unable to Delete Book"))
//    }
//  }

  def delete(id:String): Action[AnyContent] = Action.async{implicit request =>
    repositoryService.delete(id).map{
      case Right(result) => Status(ACCEPTED)
      case Left(apiError) => Status(apiError.upstreamStatus)(apiError.upstreamMessage)
    }
  }

  def getGoogleBook(search: String, term: String): Action[AnyContent] = Action.async { implicit request =>
    libraryService.getGoogleBook(search = search, term = term).value.map {
      case Right(book) =>  Ok {Json.toJson(book)} //Hint: This should be the same as before
      case Left(_) => Status(404)(Json.toJson("Unable to find any books"))
    }
  }

}
