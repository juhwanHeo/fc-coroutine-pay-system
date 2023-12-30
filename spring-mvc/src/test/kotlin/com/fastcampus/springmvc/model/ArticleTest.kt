package com.fastcampus.springmvc.model

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class ArticleTest {
    @Test
    fun printArticle() {
        Article(1, "title", "body", 1234).let {
            print("Article: $it")
        }
    }
}