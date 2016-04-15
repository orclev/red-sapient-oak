package org.tenletters.controller

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "Entry not found in queue")
class EntryNotFoundException(id: Int): Exception("ID $id not found in queue") {}