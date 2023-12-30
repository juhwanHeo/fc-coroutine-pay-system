package com.fastcampus.springmvc.repository

import com.fastcampus.springmvc.model.Article
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ArticleRepository : JpaRepository<Article, Long> {
    fun findAllByTitleContains(title: String): List<Article>
}