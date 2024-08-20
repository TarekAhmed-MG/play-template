package models

import play.api.libs.json.{Json, OFormat}

case class VolumeInfo(
                       title: String,
                       description: Option[String],
                       pageCount: Option[Int]
                     )

object VolumeInfo{
  implicit val formatsVolumeInfo: OFormat[VolumeInfo] = Json.format[VolumeInfo]
}


