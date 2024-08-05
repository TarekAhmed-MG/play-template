package controllers

import models.DataModel
import play.api.libs.json.{JsError, JsSuccess, JsValue, Json}
import play.api.mvc._
import repositories.DataRepository

import javax.inject._
import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

@Singleton
class ApplicationController @Inject()(val controllerComponents: ControllerComponents, val dataRepository: DataRepository)(implicit val ec: ExecutionContext) extends BaseController{

  // todo is a play feature that is a default package for actions that hasnt been completed yet
  def index(): Action[AnyContent] = Action.async { implicit request =>
    dataRepository.index().map{
      case Right(item: Seq[DataModel]) => Ok {Json.toJson(item)}
      case Left(error) => Status(error)(Json.toJson("Unable to find any books"))
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

  def read(id:String): Action[AnyContent] = Action.async{implicit request =>
    dataRepository.read(id).map(item => Ok{Json.toJson(item)})// check this to give back a unable to find any books
  }

  def update(id:String): Action[JsValue] = Action.async(parse.json) { implicit request =>
    request.body.validate[DataModel] match {
      case JsSuccess(dataModel, _) =>
        dataRepository.update(id,dataModel).map(_ => Accepted)
      case JsError(_) => Future(BadRequest)
    }
  }

  def delete(id:String): Action[AnyContent] = Action.async{implicit request =>
    dataRepository.delete(id).map(_ => Accepted) // check this to give back a unable to delete
  }

}
