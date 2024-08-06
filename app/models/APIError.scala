package models

import play.api.http.Status

//In this instance, we are using an abstract class over a trait as an abstract class can pass constructor parameters
sealed abstract class APIError(
                                val httpResponseStatus: Int,
                                val reason: String
                              )

object APIError {

  final case class BadAPIResponse(upstreamStatus: Int, upstreamMessage: String)
    extends APIError(
      Status.INTERNAL_SERVER_ERROR,
      s"Bad response from upstream; got status: $upstreamStatus, and got reason $upstreamMessage"
    )

}