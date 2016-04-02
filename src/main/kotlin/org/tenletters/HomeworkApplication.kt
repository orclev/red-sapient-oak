package org.tenletters

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
open class HomeworkApplication

fun main(args: Array<String>) {
    SpringApplication.run(HomeworkApplication::class.java, *args)
}
