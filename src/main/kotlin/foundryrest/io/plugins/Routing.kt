package foundryrest.io.plugins

import io.ktor.routing.*
import io.ktor.http.*
import io.ktor.application.*
import io.ktor.response.*
import kotlinx.coroutines.async
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.FileInputStream

var pid: Long = -1

fun Application.configureRouting() {
    routing {
        get("/") {
            call.respond(HttpStatusCode.Accepted, "fOunDRy D0S IrmÃo 3Stá OonL1n3!!!")
        }
        get("/revivify"){
            val response: String
            if (processStatus()){
                response = "Já ta online fera."
            } else {
                response = "Criado irmão, tmj!"
                async { runNode() }
            }

            call.respond(HttpStatusCode.Accepted, response)
        }
        get("/log/error") {
            val fstream = FileInputStream("/home/ec2-user/foundrydata/logs/error.log")
            //val br = BufferedReader(InputStreamReader(fstream))

            call.respond(HttpStatusCode.Accepted, fstream)
        }
        get("/log/debug") {
            val fstream = FileInputStream("/home/ec2-user/foundrydata/logs/debug.log")
            //val br = BufferedReader(InputStreamReader(fstream))

            call.respond(HttpStatusCode.Accepted, fstream)
        }
        get("/pwkill") {
            pid = -1
            call.respond(HttpStatusCode.Accepted, "Está morto, não respira.")
        }

    }
}

fun runNode(){
    pid = Runtime.getRuntime().exec("node /home/ec2-user/foundryvtt/resources/app/main.js --dataPath=\$HOME/foundrydata").pid()

    while (true){
        if (pid == -1L) break
        if (!processStatus()) pid = Runtime.getRuntime().exec("node /home/ec2-user/foundryvtt/resources/app/main.js --dataPath=\$HOME/foundrydata").pid()

        Thread.sleep(10000)
    }
}

fun processStatus(): Boolean{
    if (pid == -1L) return false

    val line = "ps -p $pid"

    val rt = Runtime.getRuntime()
    val pr = rt.exec(line)

    val lines = BufferedReader(InputStreamReader(pr.inputStream)).lines()

    for (line in lines) {
        if (line.contains("node foundryvtt")) return true
    }

    return false;
}
