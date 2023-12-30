package com.fastcampus.springmvc.model

import jakarta.persistence.MappedSuperclass
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import java.time.LocalDateTime

@MappedSuperclass
open class BaseEntity(

    @CreatedDate
    var createdAt : LocalDateTime = LocalDateTime.now(),
    @LastModifiedDate
    var updateAt : LocalDateTime = LocalDateTime.now(),
) {
    override fun toString(): String {
        return "createdAt=$createdAt, updateAt=$updateAt"
    }
}
