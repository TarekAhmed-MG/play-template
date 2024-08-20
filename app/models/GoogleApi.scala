package models

import play.api.libs.json.{Json, OFormat}

case class GoogleApi(
                      id:String,
                      volumeInfo:VolumeInfo
                    ){
  val dataModel: DataModel = DataModel(id, volumeInfo.title, volumeInfo.description, volumeInfo.pageCount)
}

object GoogleApi{
  implicit val formatsGoogleApi: OFormat[GoogleApi] = Json.format[GoogleApi]
}
