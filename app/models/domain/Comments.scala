package models.domain

import java.util.UUID

case class Comment(id: UUID, imageID: UUID, comment: String)

object Comment {
    val tupled = (apply: (UUID, UUID, String) => Comment).tupled
    def apply(imageID: UUID, comment: String ): Comment = apply(UUID.randomUUID(), imageID, comment)
}