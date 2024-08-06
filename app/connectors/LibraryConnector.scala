package connectors

import play.api.libs.json.OFormat
import play.api.libs.ws.WSClient

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class LibraryConnector @Inject()(ws: WSClient) {

  //In order to communicate with other services we need to make HTTP requests through urls, we will do that through a connector.

  def get[Response](url: String)(implicit rds: OFormat[Response], ec: ExecutionContext): Future[Response] = {
    val request = ws.url(url)
    val response = request.get()
    response.map {
      result =>
        result.json.as[Response]
    }
  }
}