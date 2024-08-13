package services

import cats.data.EitherT
import models.{APIError, DataModel}
import org.mongodb.scala.result.{DeleteResult, UpdateResult}
import repositories.DataRepository

import javax.inject.Inject
import scala.concurrent.Future

class RepositoryService @Inject()(val dataRepository: DataRepository){

  def index(): Future[Either[APIError.BadAPIResponse, Seq[DataModel]]] = dataRepository.index()

  def create(book:DataModel): Either[APIError.BadAPIResponse, Future[DataModel]] = dataRepository.create(book)

  def read(id:String): Future[Option[DataModel]] = dataRepository.read(id)

  def update(id:String,fieldName:String,book:DataModel): Either[APIError.BadAPIResponse, Future[UpdateResult]] = dataRepository.update(id,fieldName,book)

  def delete(id:String): Future[Either[APIError.BadAPIResponse, DeleteResult]] = dataRepository.delete(id)

  def deleteAll(): Future[Unit] = dataRepository.deleteAll()

}
