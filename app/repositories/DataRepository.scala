package repositories

import cats.data.EitherT
import com.google.inject.ImplementedBy
import models.{APIError, DataModel}
import org.mongodb.scala.bson.BsonObjectId
import org.mongodb.scala.bson.conversions.Bson
import org.mongodb.scala.model.Filters.empty
import org.mongodb.scala.model.Updates.set
import org.mongodb.scala.model._
import org.mongodb.scala.result
import org.mongodb.scala.result.{DeleteResult, InsertOneResult, UpdateResult}
import play.api.libs.json.OFormat
import play.api.libs.ws.WSResponse
import uk.gov.hmrc.mongo.MongoComponent
import uk.gov.hmrc.mongo.play.json.PlayMongoRepository

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

/**
 * This section creates a new DataRepository class and injects dependencies into it required for every Mongo Repository.
 * extends PlayMongoRepository[DataModel] tells the library what the structure of our data looks like by using our newly created DataModel.
 * This means that every document inserted into the database has the same structure, with _id, name, description and pageCount properties.
 * For this database structure, each document will be identified by the _id.
 * @param mongoComponent
 * @param ec
 */

@ImplementedBy(classOf[DataRepository])
trait dataRepositoryTrait {

  def index(): Future[Either[APIError.BadAPIResponse, Seq[DataModel]]]
  def create(book: DataModel): Future[Either[APIError.BadAPIResponse, InsertOneResult]]
  def read(id: String): Future[Either[APIError.BadAPIResponse, Some[DataModel]]]
  def update(id: String,fieldName:String, book:DataModel): Future[Either[APIError.BadAPIResponse, UpdateResult]]
  def delete(id: String): Future[Either[APIError.BadAPIResponse, DeleteResult]]
  def deleteAll(): Future[Unit]

}

@Singleton
class DataRepository @Inject()(mongoComponent: MongoComponent)(implicit ec: ExecutionContext) extends PlayMongoRepository[DataModel](
  collectionName = "dataModels", // "dataModels" is the name of the collection (you can set this to whatever you like).
  mongoComponent = mongoComponent,
  domainFormat = DataModel.formats, // DataModel.formats uses the implicit val formats we created earlier. It tells the driver how to read and write between a DataModel and JSON (the format that data is stored in Mongo)
  indexes = Seq(IndexModel( //indexes is shows the structure of the data stored in Mongo, notice we can ensure the bookId to be unique
    Indexes.ascending("_id")
  )),
  replaceIndexes = false
) with dataRepositoryTrait {

  // All of the return types of these functions are asynchronous futures.

  def index(): Future[Either[APIError.BadAPIResponse, Seq[DataModel]]] =
    collection.find().toFuture().map{
      case books: Seq[DataModel] => Right(books)
      case _ => Left(APIError.BadAPIResponse(404, "Books cannot be found"))
    }

//  def create(book: DataModel): Either[APIError.BadAPIResponse, Future[DataModel]] = {

  override def create(book: DataModel): Future[Either[APIError.BadAPIResponse, InsertOneResult]] =
    collection.insertOne(book).toFuture().map{ createdResult =>
      if (createdResult.wasAcknowledged())
        Right(createdResult)
      else
        Left(APIError.BadAPIResponse(500, "Could not Create Book"))
    }

  private def byIDorName(idOrName: String): Bson =
    Filters.or(
      Filters.equal("_id", idOrName), Filters.equal("name", idOrName)
    )

  // retrieves a DataModel object from the database. It uses an id parameter to find the data its looking for

  override def read(id: String): Future[Either[APIError.BadAPIResponse, Some[DataModel]]] =
    collection.find(byIDorName(id)).headOption flatMap {
      case Some(data) => Future(Right(Some(data)))
      case None => Future(Left(APIError.BadAPIResponse(404, "Unable to find any books")))
    }

  // takes in a DataModel, finds a matching document with the same id and updates the document. It then returns the updated DataModel

  override def update(id: String,fieldName:String, book:DataModel): Future[Either[APIError.BadAPIResponse, UpdateResult]] = {

    val change = fieldName match {
      case "_id" => book._id
      case "title" => book.title
      case "description" => book.description
      case "pageCount" => book.pageCount
    }
    collection.updateOne(filter = byIDorName(id), update=set(fieldName, change)).toFuture().map{
      updatedResult =>
        if (updatedResult.getMatchedCount != 0)
          Right(updatedResult)
        else
          Left(APIError.BadAPIResponse(404, "Unable to Update Book"))
    }
  }

//  // deletes a document in the database that matches the id passed in

  override def delete(id: String): Future[Either[APIError.BadAPIResponse, DeleteResult]] =
    collection.deleteOne(filter = byIDorName(id)).toFuture().map{
      deletedResult =>
        if (deletedResult.getDeletedCount != 0)
          Right(deletedResult)
        else
          Left(APIError.BadAPIResponse(404, "Unable to Delete Book"))
    }

  // is similar to delete, this removes all data from Mongo with the same collection name
  def deleteAll(): Future[Unit] = collection.deleteMany(empty()).toFuture().map(_ => ()) //Hint: needed for tests

}

