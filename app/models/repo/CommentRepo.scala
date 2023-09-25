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
final class CommentRepo @Inject()(protected val dbConfigProvider: DatabaseConfigProvider, val photoRepo: PhotoRepo)(implicit ex: ExecutionContext) extends HasDatabaseConfigProvider[JdbcProfile]{

    import profile.api._

    protected class CommentTable (tag: Tag) extends Table[Comment](tag, "COMMENTS") {
      def id = column[UUID]("ID", O.PrimaryKey)
      def imageID = column[UUID]("IMAGE_ID")
      def comment = column[String]("IMAGE_COMMENT")
      def * = (id, imageID, comment).mapTo[Comment]

      def fkey = foreignKey("FK_IMAGE_ID", imageID, photoRepo.photos)(_.id, onDelete = ForeignKeyAction.Cascade)
    }


    val comments = TableQuery[CommentTable]

    def createCommentSchema = {
      db.run(comments.schema.createIfNotExists)
    }

}