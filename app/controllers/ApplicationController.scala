package controllers

import com.google.inject.Singleton
import play.api.mvc.{Action, AnyContent, BaseController, ControllerComponents}

import javax.inject.Inject

@Singleton
class ApplicationController @Inject()(val controllerComponents: ControllerComponents) extends BaseController{

  def index(): Action[AnyContent] = TODO // todo is a play feature that is a default package for actions that hasnt been completed yet
  // to see the sToDO page we need to add an app route that references to the new controller and method

  def create(): Action[AnyContent] = TODO
  def read(id:String): Action[AnyContent] = TODO
  def update(id:String): Action[AnyContent] = TODO
  def delete(id:String): Action[AnyContent] = TODO


}
