package services

import models.{APIError, DataModel}
import org.mongodb.scala.result.{DeleteResult, InsertOneResult, UpdateResult}
import repositories.{DataRepository, dataRepositoryTrait}

import javax.inject.Inject
import scala.concurrent.Future

class RepositoryService @Inject()(val dataRepositoryTrait: dataRepositoryTrait){

  def index(): Future[Either[APIError.BadAPIResponse, Seq[DataModel]]] = dataRepositoryTrait.index()

  def create(book:DataModel): Future[Either[APIError.BadAPIResponse, InsertOneResult]] = dataRepositoryTrait.create(book)

  def read(id:String): Future[Either[APIError.BadAPIResponse, Some[DataModel]]] = dataRepositoryTrait.read(id)

  def update(id:String,fieldName:String,book:DataModel): Future[Either[APIError.BadAPIResponse, UpdateResult]] = dataRepositoryTrait.update(id,fieldName,book)

  def delete(id:String): Future[Either[APIError.BadAPIResponse, DeleteResult]] = dataRepositoryTrait.delete(id)

  def deleteAll(): Future[Unit] = dataRepositoryTrait.deleteAll()

}
