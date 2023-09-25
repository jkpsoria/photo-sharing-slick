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
class PhotoController @Inject() (
    val controllerComponents: ControllerComponents,
    implicit val ec: ExecutionContext,
    photoRepo: PhotoRepo,
    commentRepo: CommentRepo,
    queries: Queries
) extends BaseController
    with I18nSupport {

  /** Create an Action to render an HTML page.
    *
    * The configuration in the `routes` file means that this method will be
    * called when the application receives a `GET` request with a path of `/`.
    */

  val photoForm = Form(
    mapping(
      "id" -> ignored(UUID.randomUUID()),
      "image" -> ignored(Array[Byte]()),
      "title" -> nonEmptyText,
      "extension" -> ignored("")
    )(Photo.apply)(Photo.unapply)
  )

  // def index() = Action.async { implicit request: Request[AnyContent] =>
  //   photoRepo.createTaskSchema.map(photos => Ok(views.html.index(photos, photoForm)))
  // }

  def index() = Action.async { implicit request =>
    photoRepo.createTaskSchema.flatMap { _ =>
      commentRepo.createCommentSchema
      queries.getPhotos.map { photos =>
        Ok(views.html.index(photos, photoForm))  
      }
    }
  }

  //   def index() = Action.async { implicit request =>
    
  //   photoRepo.createTaskSchema.flatMap{ _ =>
  //     commentRepo.createCommentSchema
  //   }.map(_ => Ok)
  // }

  def upload() = Action.async(parse.multipartFormData) { implicit request =>
    photoForm.bindFromRequest().fold(
      formWithErrors => {
        Future.successful(BadRequest)
      },
      photo => {
        request.body
        .file("picture")
        .map {
          picture => 
            val filename = Files.readAllBytes(picture.ref)
            val extension = picture.filename.split("\\.+").toList.last
            val tempPhoto = Photo(UUID.randomUUID(), filename, photo.title, extension)
            queries.addPhoto(tempPhoto).flatMap { _ =>
              queries.getPhotos.map(response => Redirect(routes.PhotoController.index()))
          }
        }.getOrElse {
          photoRepo.createTaskSchema.map(_ => Redirect(routes.PhotoController.index()))
        }
      }
    )
  }

  def showByID(id: UUID) = Action.async { implicit request =>
    queries.getPhotoByID(id).map(photo => 
      Ok(photo.image).as(s"image/${photo.extension}")
    )
  }

  def show() = Action.async{ implicit request =>
    queries.getPhotos.map(photos => Ok(photos.mkString("\n")))  
  }
}
