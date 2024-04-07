package com.fastcampus.webfluxcoroutine.model

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.annotation.Version
import org.springframework.data.relational.core.mapping.Table
import java.io.Serializable
import java.time.LocalDateTime

@Table("TB_ARTICLE")
data class Article(

    @Id
    var id: Long = 0,

    var title: String? = null,

    var body: String? = null,

    var authorId: Long? = null,

    var balance: Long = 0,

    @Version
    var version: Int = 1,

    @CreatedDate
    var createdAt: LocalDateTime? = null,

    @LastModifiedDate
    var updatedAt: LocalDateTime? = null,
): Serializable
