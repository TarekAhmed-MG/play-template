package controllers

import models.{APIError, DataModel}
import models.DataModel.dataModelForm
import play.api.libs.json.{JsError, JsSuccess, JsValue, Json}
import play.api.mvc._
import play.filters.csrf.CSRF
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
  extends BaseController with play.api.i18n.I18nSupport{

  // todo is a play feature that is a default package for actions that hasnt been completed yet
  def index(): Action[AnyContent] = Action.async { implicit request =>
    repositoryService.index().map{
      case Right(item: Seq[DataModel]) => Ok {Json.toJson(item)}
      case Left(_) => Status(NOT_FOUND)(Json.toJson("Unable to find any books"))
    }
  }

  def create(): Action[JsValue] = Action.async(parse.json) { implicit request =>
    request.body.validate[DataModel] match {
      case JsSuccess(dataModel, _) =>
        repositoryService.create(dataModel:DataModel).map {
          case Right(_) => Status(CREATED)
          case Left(apiError) => Status(apiError.upstreamStatus)(apiError.upstreamMessage)
        }
      case JsError(_) => Future(BadRequest)
    }
  }

  def read(idOrName:String): Action[AnyContent] = Action.async{implicit request =>
    repositoryService.read(idOrName).map{
      case Right(Some(item: DataModel)) => Ok{Json.toJson(item)}
      case Left(apiError) => Status(apiError.upstreamStatus)(Json.toJson(apiError.upstreamMessage))
    }
  }

  def update(id:String, fieldName:String): Action[JsValue] = Action.async(parse.json) { implicit request =>
    request.body.validate[DataModel] match {
      case JsSuccess(dataModel, _) =>
        repositoryService.update(id,fieldName,dataModel).map{
          case Right(_) => Status(ACCEPTED)
          case Left(apiError) => Status(apiError.upstreamStatus)(Json.toJson(apiError.upstreamMessage))
        }
      case JsError(_) => Future(BadRequest)
    }
  }

  def delete(id:String): Action[AnyContent] = Action.async{implicit request =>
    repositoryService.delete(id).map{
      case Right(_) => Status(ACCEPTED)
      case Left(apiError) => Status(apiError.upstreamStatus)(Json.toJson(apiError.upstreamMessage))
    }
  }

  def getGoogleBook(search: String, term: String): Action[AnyContent] = Action.async { implicit request =>
    libraryService.getGoogleBook(search = search, term = term).value.map {
      case Right(book) =>  Ok {Json.toJson(book.dataModel)}
      case Left(_) => Status(404)(Json.toJson("Unable to find any books"))
    }
  }

  def example(): Action[AnyContent] = Action.async {implicit request =>
    Future.successful(Ok(views.html.example()))
  }

  def accessToken(implicit request: Request[_]) = {
    CSRF.getToken
  }

  def addDataModel(): Action[AnyContent] = Action.async { implicit request =>
    Future.successful(Ok(views.html.newdatamodel(DataModel.dataModelForm)))
  }

  def addDataModelForm(): Action[AnyContent] =  Action.async { implicit request =>
    accessToken //call the accessToken method
    dataModelForm.bindFromRequest().fold( //from the implicit request we want to bind this to the form in our companion object
      formWithErrors => {
        //here write what you want to do if the form has errors
        Future.successful(Status(BAD_REQUEST))
      },
      formData => {
        //here write how you would use this data to create a new book (DataModel)
        repositoryService.create(formData).map{
          case Right(_) => Status(CREATED)
          case Left(apiError) => Status(apiError.upstreamStatus)(apiError.upstreamMessage)
        }
      }
    )
  }
}


