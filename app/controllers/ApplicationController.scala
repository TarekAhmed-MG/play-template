package controllers

import play.api.mvc._

import javax.inject._
import scala.concurrent.Future

@Singleton
class ApplicationController @Inject()(val controllerComponents: ControllerComponents) extends BaseController{

  // todo is a play feature that is a default package for actions that hasnt been completed yet
  def index(): Action[AnyContent] = Action.async{
    implicit request => Future.successful(Ok)
  }
  // to see the sToDO page we need to add an app route that references to the new controller and method


  def create(): Action[AnyContent] = TODO
  def read(id:String): Action[AnyContent] = TODO
  def update(id:String): Action[AnyContent] = TODO
  def delete(id:String): Action[AnyContent] = TODO


}
