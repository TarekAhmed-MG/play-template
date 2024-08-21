package services

import cats.data.EitherT
import com.google.inject.{Inject, Singleton}
import connectors.LibraryConnector
import models.{APIError, DataModel, GoogleApi, Items}
import repositories.dataRepositoryTrait

import java.awt.print.Book
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class LibraryService @Inject()(connector: LibraryConnector,val dataRepositoryTrait: dataRepositoryTrait) {

  /**
   * @param urlOverride
   * @param search
   * @param term
   * @param ec
   * @return
   */

  def getGoogleBook(urlOverride: Option[String] = None, search: String, term: String)(implicit ec: ExecutionContext): EitherT[Future, APIError, GoogleApi] = {
    connector.get[Items](urlOverride.getOrElse(s"https://www.googleapis.com/books/v1/volumes?q=$search%$term"))
      .map { items =>
        items.items.headOption.get
      }
  }

  //example call http://localhost:9000/library/google/isbn/1452140907

//  def getGoogleBook(urlOverride: Option[String] = None, search: String)(implicit ec: ExecutionContext): EitherT[Future, APIError, GoogleApi]=
//    connector.get[GoogleApi](urlOverride.getOrElse(s"https://www.googleapis.com/books/v1/volumes/$search"))
}
