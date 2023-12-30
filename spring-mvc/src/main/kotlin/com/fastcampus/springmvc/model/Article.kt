package com.fastcampus.springmvc.model

import jakarta.persistence.Entity
import jakarta.persistence.EntityListeners
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import org.springframework.data.jpa.domain.support.AuditingEntityListener

@Entity
@EntityListeners(AuditingEntityListener::class)
class Article(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id : Long = 0L,
    var title : String,
    var body : String?,
    var authorId : Long?,
): BaseEntity() {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as Article
        return id == other.id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

    override fun toString(): String {
        return "Article(id=$id, title='$title', body='$body', authorId=$authorId, ${super.toString()})"
    }
}
