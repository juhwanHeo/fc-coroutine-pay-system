package com.fastcampus.springmvc.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EntityListeners
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime


@Entity(name = "TB_ARTICLE")
@EntityListeners(AuditingEntityListener::class)
data class Article(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    var id: Long = 0,

    @Column(name="title")
    var title: String? = null,

    @Column(name="body")
    var body: String? = null,

    @Column(name="author_id")
    var authorId: Long? = null,

    @CreatedDate
    @Column(name="created_at")
    var createdAt: LocalDateTime? = null,

    @LastModifiedDate
    @Column(name="updated_at")
    var updatedAt: LocalDateTime? = null,
)
