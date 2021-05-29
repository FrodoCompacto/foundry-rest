package foundryrest.io

import io.ktor.server.engine.*
import io.ktor.server.netty.*
import foundryrest.io.plugins.*
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
        configureRouting()
        install(CORS){
            method(HttpMethod.Get)
            anyHost()
        }
    }.start(wait = true)
}
