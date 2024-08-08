package connectors

import cats.data.EitherT
import models.APIError
import play.api.libs.json.OFormat
import play.api.libs.ws.{WSClient, WSResponse}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class LibraryConnector @Inject()(ws: WSClient) {

  //In order to communicate with other services we need to make HTTP requests through urls, we will do that through a connector.

//  def get[Response](url: String)(implicit rds: OFormat[Response], ec: ExecutionContext): Future[Response] = {
//    val request = ws.url(url)
//    val response = request.get()
//    response.map {
//      result =>
//        result.json.as[Response]
//    }
//  }

  //uses the APIError we created
  def get[Response](url: String)(implicit rds: OFormat[Response], ec: ExecutionContext): EitherT[Future, APIError, Response] = {
    val request = ws.url(url)
    val response = request.get()
    EitherT {
      response
        .map {
          result =>
            Right(result.json.as[Response])
        }
        .recover { case _: WSResponse =>
          Left(APIError.BadAPIResponse(500, "Could not connect"))
        }
    }
  }

  /*
      EitherT will look really confusing right now, so let's unpack it.

      The response of our request.get() will give a WSResponse, we then try to parse the Json body as type Response
      So at first this was a Future[Response].
      When that doesn't work, we catch the error with the .recover method, this returns a type Future[APIError]
      EitherT allows us to return either of the two, Future[APIError] or Future[Response].
      Note that you cannot have APIError[Response] or Response[APIError, the syntax of EitherT only allows the first type, i.e. the Future, to be applied over the remaining two types.
      (It is also standard to have the error as the Left and not the Right type, since the error is never right!)
   */

}