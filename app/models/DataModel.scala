package models

import play.api.libs.json.{Json, OFormat}
import play.api.data._
import play.api.data.Forms._



case class DataModel(
                      _id: String,
                      title: String,
                      description: Option[String],
                      pageCount: Option[Int]
                    )

// companion object
object DataModel{ // This allows for easily transforming the model to and from JSON.
  implicit val formats: OFormat[DataModel] = Json.format[DataModel]

  val dataModelForm: Form[DataModel] = Form(
    mapping(
      "_id" -> text,
      "title" -> text,
      "description" -> optional(text),
      "pageCount" -> optional(number)
    )(DataModel.apply)(DataModel.unapply)
  )
}
