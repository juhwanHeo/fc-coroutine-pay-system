package com.fastcampus.payment.advanced.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
class InvalidOrderStatus(message: String) : Throwable(message)