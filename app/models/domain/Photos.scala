package models.domain

import java.util.UUID

case class Photo(id: UUID, image: Array[Byte], title: String, extension: String)

object Photo {
    val tupled = (apply: (UUID, Array[Byte], String, String) => Photo).tupled
    def apply(image: Array[Byte], title: String, extension: String): Photo = apply(UUID.randomUUID(), image, title, extension)
}