package services

import models.{APIError, DataModel}
import org.mongodb.scala.result.{DeleteResult, InsertOneResult, UpdateResult}
import repositories.DataRepository
import javax.inject.Inject
import scala.concurrent.Future

class RepositoryService @Inject()(val dataRepository: DataRepository){

  def index(): Future[Either[APIError.BadAPIResponse, Seq[DataModel]]] = dataRepository.index()

  def create(book:DataModel): Future[Either[APIError.BadAPIResponse, InsertOneResult]] = dataRepository.create(book)

  def read(id:String): Future[Either[APIError.BadAPIResponse, Some[DataModel]]] = dataRepository.read(id)

  def update(id:String,fieldName:String,book:DataModel): Future[Either[APIError.BadAPIResponse, UpdateResult]] = dataRepository.update(id,fieldName,book)

  def delete(id:String): Future[Either[APIError.BadAPIResponse, DeleteResult]] = dataRepository.delete(id)

  def deleteAll(): Future[Unit] = dataRepository.deleteAll()

}
