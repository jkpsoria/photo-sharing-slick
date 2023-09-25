package controllers

import javax.inject._
import play.api._
import play.api.mvc._
import models.domain._
import models.repo._
import models.queries._
import scala.concurrent.ExecutionContext
import play.api.i18n.I18nSupport
import play.api.libs.Files._
import scala.concurrent.Future
import play.api.data._
import play.api.data.Forms._
import java.nio.file.Files
import java.util.UUID
import java.nio.file.Paths

/** This controller creates an `Action` to handle HTTP requests to the
  * application's home page.
  */
@Singleton
class CommentController @Inject() (
    val controllerComponents: ControllerComponents,
    implicit val ec: ExecutionContext,
    photoRepo: PhotoRepo,
    commentRepo: CommentRepo,
    queries: Queries
) extends BaseController
    with I18nSupport {
        
        val commentForm = Form(
            mapping(
                "id" -> ignored(UUID.randomUUID()),
                "imageID" -> ignored(UUID.randomUUID()),
                "comment" -> nonEmptyText
            )(Comment.apply)(Comment.unapply)
        )


        def show() = Action.async { implicit request =>

            queries.getComments.map { comments =>
                Ok(comments.mkString("\n"))
            }
        }


        def create(id: UUID) = Action.async { implicit request =>
            commentForm.bindFromRequest().fold(
                formWithErrors => {
                    Future.successful(BadRequest)
                },
                comment => {
                    queries.addComment(Comment(UUID.randomUUID(), id, comment.comment)).map(_ => Ok)
                }
            )
        }



    }