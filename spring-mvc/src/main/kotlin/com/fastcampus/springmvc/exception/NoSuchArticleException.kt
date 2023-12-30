package com.fastcampus.springmvc.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.NOT_FOUND)
class NoSuchArticleException(message: String?): RuntimeException(message)