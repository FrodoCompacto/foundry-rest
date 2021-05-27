package foundryrest.io.plugins

import io.ktor.routing.*
import io.ktor.http.*
import io.ktor.application.*
import io.ktor.response.*
import kotlinx.coroutines.async
import java.io.BufferedReader
import java.io.InputStreamReader


var pid = -1

fun Application.configureRouting() {
    routing {
        get("/") {
            call.respond(HttpStatusCode.Accepted, "fOunDRy D0S IrmÃo 3Stá OonL1n3!!!")
        }

        get("/revivify"){
            var response: String
            if (processStatus(pid)){
                response = "Já ta online fera."
            } else {
                response = "Criado irmão, tmj!"
                async { runNode() }
            }

            call.respond(HttpStatusCode.Accepted, response)
        }
    }
}

fun runNode(){

}

fun processStatus(pid: Int): Boolean{
    val line = "ps -p " + 19474;

    val rt = Runtime.getRuntime()
    val pr: Process = rt.exec(line)

    val isReader = InputStreamReader(pr.inputStream)
    val bReader = BufferedReader(isReader)
    var strLine = bReader.readLine()

    do {
        if (strLine.contains(" " + pid + " ")) {
            return true;
        }
        strLine = bReader.readLine()
    } while (strLine.length() > 0)

    println(strLine)

    return false;
}
