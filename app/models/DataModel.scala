package models

import play.api.libs.json.{Json, OFormat}



case class VolumeInfo(
                       title: String,
                       description: Option[String],
                       pageCount: Option[Int]
                     )

case class GoogleApi(
                      id:String,
                      volumeInfo:VolumeInfo
                    ){
  val dataModel: DataModel = DataModel(id, volumeInfo.title, volumeInfo.description, volumeInfo.pageCount)
}

case class DataModel(
                      _id: String,
                      title: String,
                      description: Option[String],
                      pageCount: Option[Int]
                    )

object GoogleApi{
  implicit val formatsGoogleApi: OFormat[GoogleApi] = Json.format[GoogleApi]
}

object VolumeInfo{
  implicit val formatsVolumeInfo: OFormat[VolumeInfo] = Json.format[VolumeInfo]
}

// companion object
object DataModel{ // This allows for easily transforming the model to and from JSON.
  implicit val formats: OFormat[DataModel] = Json.format[DataModel]
}
