package models

import play.api.libs.json.{Json, OFormat}

case class Items(items:Seq[GoogleApi])

object Items{
  implicit val formatsItems: OFormat[Items] = Json.format[Items]
}
