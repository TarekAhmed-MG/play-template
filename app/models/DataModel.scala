package models

import play.api.libs.json.{Json, OFormat}

case class GoogleApi(id:String,VolumeInfo:VolumeInfo)

case class VolumeInfo(
                       title: String,
                       description: String,
                       pageCount: Int
                     )

case class DataModel(_id: String,
                     title: String,
                     description: String,
                     pageCount: Int)

object GoogleApi{
  implicit val formatsVolumeInfo: OFormat[VolumeInfo] = Json.format[VolumeInfo]
  implicit val formatsGoogleApi: OFormat[GoogleApi] = Json.format[GoogleApi]
}

// companion object
object DataModel { // This allows for easily transforming the model to and from JSON.

  implicit val formats: OFormat[DataModel] = Json.format[DataModel]

}
