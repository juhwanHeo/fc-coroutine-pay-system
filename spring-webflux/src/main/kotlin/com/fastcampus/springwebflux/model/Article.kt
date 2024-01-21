package com.fastcampus.springwebflux.model

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table("TB_ARTICLE")
data class Article(

    @Id
    var id: Long = 0,

    var title: String? = null,

    var body: String? = null,

    var authorId: Long? = null,

    @CreatedDate
    var createdAt: LocalDateTime? = null,

    @LastModifiedDate
    var updatedAt: LocalDateTime? = null,
)
