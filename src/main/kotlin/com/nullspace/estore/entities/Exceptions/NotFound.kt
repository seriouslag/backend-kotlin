package com.nullspace.estore.entities.Exceptions

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.NOT_FOUND)
class NotFound(exception: String) : RuntimeException(exception)
