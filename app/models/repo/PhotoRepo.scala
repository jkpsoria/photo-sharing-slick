package models.repo

import javax.inject._
import play.api.db.slick.DatabaseConfigProvider
import play.api.db.slick.HasDatabaseConfigProvider
import slick.jdbc.JdbcProfile
import scala.concurrent.{ExecutionContext, Future}
import models.domain._
import java.util.UUID
import java.time.LocalDateTime

@Singleton
final class PhotoRepo @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(implicit ex: ExecutionContext) extends HasDatabaseConfigProvider[JdbcProfile]{

    import profile.api._

    protected class PhotoTable (tag: Tag) extends Table[Photo](tag, "PHOTOS") {
      def id = column[UUID]("ID", O.PrimaryKey)
      def image = column[Array[Byte]]("IMAGE")
      def title = column[String]("IMAGE_TITLE")
      def extension = column[String]("IMAGE_EXTENSION")
      def * = (id, image, title, extension).mapTo[Photo]
      
    }


    val photos = TableQuery[PhotoTable]

    def createTaskSchema = {
      db.run(photos.schema.createIfNotExists)
    }



}