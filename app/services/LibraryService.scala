package services

import cats.data.EitherT
import com.google.inject.{Inject, Singleton}
import connectors.LibraryConnector
import models.{APIError, DataModel, GoogleApi}

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
//  def getGoogleBook(urlOverride: Option[String] = None, search: String, term: String)(implicit ec: ExecutionContext): EitherT[Future, APIError, GoogleApi]=
//    connector.get[GoogleApi](urlOverride.getOrElse(s"https://www.googleapis.com/books/v1/volumes?q=$search%$term"))

  def getGoogleBook(urlOverride: Option[String] = None, search: String)(implicit ec: ExecutionContext): EitherT[Future, APIError, GoogleApi]=
    connector.get[GoogleApi](urlOverride.getOrElse(s"https://www.googleapis.com/books/v1/volumes?q=$search"))
}
