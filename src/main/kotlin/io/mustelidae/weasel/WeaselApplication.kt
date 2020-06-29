package io.mustelidae.weasel

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class WeaselApplication

fun main(args: Array<String>) {
    runApplication<WeaselApplication>(*args)
}
