package models

import play.api.libs.json.{Json, OFormat}

case class DataModel(_id: String,
                     name: String,
                     description: String,
                     pageCount: Int)

// companion object
object DataModel { // This allows for easily transforming the model to and from JSON.
  implicit val formats: OFormat[DataModel] = Json.format[DataModel]
}
