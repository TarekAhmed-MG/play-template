package controllers

import models.DataModel
import play.api.libs.json.{JsError, JsSuccess, JsValue, Json}
import play.api.mvc._
import repositories.DataRepository
import services.LibraryService

import javax.inject._
import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try


@Singleton
class ApplicationController @Inject()(val controllerComponents: ControllerComponents, val dataRepository: DataRepository, val libraryService: LibraryService)(implicit val ec: ExecutionContext) extends BaseController{

  // todo is a play feature that is a default package for actions that hasnt been completed yet
  def index(): Action[AnyContent] = Action.async { implicit request =>
    dataRepository.index().map{
      case Right(item: Seq[DataModel]) => Ok {Json.toJson(item)}
      case Left(_) => Status(404)(Json.toJson("Unable to find any books"))
    }
  }
  // to see the sToDO page we need to add an app route that references to the new controller and method

  def create(): Action[JsValue] = Action.async(parse.json) { implicit request =>
    request.body.validate[DataModel] match {
      case JsSuccess(dataModel, _) =>
        dataRepository.create(dataModel).map(_ => Created)
      case JsError(_) => Future(BadRequest)
    }
  }

//  def read(id:String): Action[AnyContent] = Action.async{implicit request =>
//    dataRepository.read(id).map(item => Ok{Json.toJson(item)})// check this to give back a unable to find any books
//  }

  def read(id:String): Action[AnyContent] = Action.async{implicit request =>
    dataRepository.read(id).map{
      case Some(item: DataModel) => Ok{Json.toJson(item)}
      case None => Status(404)(Json.toJson("Unable to find any books"))
    }
  }

  def update(id:String): Action[JsValue] = Action.async(parse.json) { implicit request =>
    request.body.validate[DataModel] match {
      case JsSuccess(dataModel, _) =>
        dataRepository.update(id,dataModel).map(_ => Accepted)
      case JsError(_) => Future(BadRequest)
    }
  }

//  def delete(id:String): Action[AnyContent] = Action.async{implicit request =>
//    dataRepository.delete(id).map(_ => Accepted) // check this to give back a unable to delete
//  }

  def delete(id:String): Action[AnyContent] = Action.async{implicit request =>
    dataRepository.read(id).map{
      case Some(item: DataModel) =>
        dataRepository.delete(item._id)
        Status(ACCEPTED)
      case None => Status(404)(Json.toJson("Unable to Delete Book"))
    }
  }


  def getGoogleBook(search: String, term: String): Action[AnyContent] = Action.async { implicit request =>
    libraryService.getGoogleBook(search = search, term = term).value.map {
      case Right(book) =>  Ok {Json.toJson(book)} //Hint: This should be the same as before
      case Left(_) => Status(404)(Json.toJson("Unable to find any books"))
    }
  }

}
