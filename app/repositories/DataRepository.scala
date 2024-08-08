package repositories

import models.{APIError, DataModel}
import org.mongodb.scala.bson.conversions.Bson
import org.mongodb.scala.model.Filters.empty
import org.mongodb.scala.model._
import org.mongodb.scala.result
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
@Singleton
class DataRepository @Inject()(mongoComponent: MongoComponent)(implicit ec: ExecutionContext) extends PlayMongoRepository[DataModel](
  collectionName = "dataModels", // "dataModels" is the name of the collection (you can set this to whatever you like).
  mongoComponent = mongoComponent,
  domainFormat = DataModel.formats, // DataModel.formats uses the implicit val formats we created earlier. It tells the driver how to read and write between a DataModel and JSON (the format that data is stored in Mongo)
  indexes = Seq(IndexModel( //indexes is shows the structure of the data stored in Mongo, notice we can ensure the bookId to be unique
    Indexes.ascending("_id")
  )),
  replaceIndexes = false
) {

  // All of the return types of these functions are asynchronous futures.

//  def index(): Future[Either[Int, Seq[DataModel]]]  =
//    collection.find().toFuture().map{
//      case books: Seq[DataModel] => Right(books)
//      case _ => Left(404)
//    }

  def index(): Future[Either[APIError.BadAPIResponse, Seq[DataModel]]] =
    collection.find().toFuture().map {
      case books: Seq[DataModel] => Right(books)
      case _ => Left(APIError.BadAPIResponse(404, "Books cannot be found"))
    }

  // adds a DataModel object to the database
  def create(book: DataModel): Future[DataModel] = //
    collection
      .insertOne(book)
      .toFuture()
      .map(_ => book)

  private def byIDorName(idOrName: String): Bson =
    Filters.or(
      Filters.equal("_id", idOrName), Filters.equal("name", idOrName)
    )

//  // retrieves a DataModel object from the database. It uses an id parameter to find the data its looking for
//  def read(id: String): Future[DataModel] =
//    collection.find(byIDorName(id)).headOption flatMap {
//      case Some(data) => Future(data)
//    }

  // retrieves a DataModel object from the database. It uses an id parameter to find the data its looking for
  def read(id: String): Future[Option[DataModel]] =
    collection.find(byIDorName(id)).headOption flatMap {
      case Some(data) => Future(Some(data))
      case None => Future(None)
    }

  // takes in a DataModel, finds a matching document with the same id and updates the document. It then returns the updated DataModel
//  def update(id: String, book: DataModel): Future[result.UpdateResult] =
//    collection.replaceOne(
//      filter = byIDorName(id),
//      replacement = book,
//      options = new ReplaceOptions().upsert(true) //What happens when we set this to false?
//    ).toFuture()

  // create a filter Bson method that filters with the field

  def update(id: String,fieldName:String, change:String): Future[result.UpdateResult] =
    collection.updateOne(filter = byIDorName(id), update=byIDorName(id)).toFuture() // need to change the update to take in BSON fields

  // deletes a document in the database that matches the id passed
  def delete(id: String): Future[result.DeleteResult] =
    collection.deleteOne(
      filter = byIDorName(id)
    ).toFuture()

  // is similar to delete, this removes all data from Mongo with the same collection name
  def deleteAll(): Future[Unit] = collection.deleteMany(empty()).toFuture().map(_ => ()) //Hint: needed for tests

}
