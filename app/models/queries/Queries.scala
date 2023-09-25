package models.queries

import java.time.LocalDate
import models.domain._
import models.repo._
import slick.jdbc.PostgresProfile.api._
import scala.concurrent.ExecutionContext
import javax.inject._
import play.api.db.slick.DatabaseConfigProvider
import play.api.db.slick.HasDatabaseConfigProvider
import slick.jdbc.JdbcProfile
import scala.concurrent.Future
import java.util.UUID

@Singleton
final class Queries @Inject()(protected val dbConfigProvider: DatabaseConfigProvider, val photoRepo: PhotoRepo, val commentRepo: CommentRepo)(implicit ex: ExecutionContext) extends HasDatabaseConfigProvider[JdbcProfile]{
    import profile.api._


    //photos
    def getPhotos: Future[Seq[Photo]] = db.run(photoRepo.photos.result)
    def getPhotoByID(id: UUID) = db.run(photoRepo.photos.filter(_.id === id).result.head)
    def addPhoto(photo: Photo): Future[Int] = db.run(photoRepo.photos += photo)
    def downloadPhoto(id: UUID): Future[Option[Photo]] = db.run(photoRepo.photos.filter(_.id === id).result.headOption)


    //comments
    def getComments: Future[Seq[Comment]] = db.run(commentRepo.comments.result)
    def addComment(comment: Comment): Future[Int] = db.run(commentRepo.comments += comment)
    def deleteComment(id: UUID): Future[Int] = db.run(commentRepo.comments.filter(_.id === id).delete)
}