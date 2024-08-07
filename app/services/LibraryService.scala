package services

import cats.data.EitherT
import com.google.inject.{Inject, Singleton}
import connectors.LibraryConnector
import models.{APIError, DataModel}

import java.awt.print.Book
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class LibraryService @Inject()(connector: LibraryConnector) {

  /**
   * @param urlOverride
   * @param search
   * @param term
   * @param ec
   * @return
   */
  def getGoogleBook(urlOverride: Option[String] = None, search: String, term: String)(implicit ec: ExecutionContext): EitherT[Future, APIError, DataModel]=
    connector.get[DataModel](urlOverride.getOrElse(s"https://www.googleapis.com/books/v1/volumes?q=$search%$term"))

}
