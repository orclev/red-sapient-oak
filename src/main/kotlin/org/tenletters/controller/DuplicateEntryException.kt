package org.tenletters.controller

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(code = HttpStatus.CONFLICT, reason = "Entry Already Exists")
class DuplicateEntryException(public val id : Int) : Exception("Duplicate entry: " + id) {}