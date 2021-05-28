package foundryrest.io.plugins

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import java.io.*
import java.net.URI

const val foundryProcess = "node /home/"
const val loop = "sh loop.sh"

fun Application.configureRouting() {
    routing {
        get("/") {
            call.respond(HttpStatusCode.OK, "sV TA funcional parssa")
        }
        get("/status/foundry") {
            if (processStatus(foundryProcess)) call.respond(HttpStatusCode.OK)
            else call.respond(HttpStatusCode.Forbidden)
        }
        get("/status/loop") {
            if (processStatus(loop)) call.respond(HttpStatusCode.OK)
            else call.respond(HttpStatusCode.Forbidden)
        }
        get("/log/error") {
            call.respondFile(File(URI.create("file:///home/ec2-user/foundrydata/Logs/error.log")))
        }
        get("/log/debug") {
            call.respondFile(File(URI.create("file:///home/ec2-user/foundrydata/Logs/debug.log")))
        }
        get("/pwkill") {
            if (killAll()) {
                call.respond(HttpStatusCode.OK, "Está morto, não respira.")
            } else call.respond(HttpStatusCode.Forbidden, "faio")
        }
        get("/revivify"){
            val isLoop = processStatus(loop)
            val isNode = processStatus(foundryProcess)

            if (isLoop && isNode) call.respond(HttpStatusCode.Forbidden, "Já ta rodando, para aí dps me avisa q eu rodo.")
            else if (!isLoop && isNode) call.respond(HttpStatusCode.Forbidden, "Já ta rodando, mas ta sem o script pra deixar on, deve ter aberto por outro lugar. Fecha tudo se quiser usar aqui dnv.")
            else if (isLoop && !isNode) call.respond(HttpStatusCode.Forbidden, "Deu bug no bagui me avisa no zap. fechar td se pa resolve mas não garanto.")
            else {
                if(runSh()) call.respond(HttpStatusCode.OK, "fOunDRy D0S IrmÃo 3Stá OonL1n3!!!")
                else call.respond(HttpStatusCode.Forbidden, "Não deu p abrir o foundry, deu bug sla")
            }
        }
    }
}

fun killAll(): Boolean{
    while (processStatus(loop)){
        getPidFromProcess(loop).let { if (it != -1L) killProcess(it) }
    }
    while (processStatus(foundryProcess)){
        getPidFromProcess(foundryProcess).let { if (it != -1L) killProcess(it) }
    }
    if (processStatus(loop) || processStatus(foundryProcess)) return false

    return true
}

fun runSh(): Boolean{
    Runtime.getRuntime().exec(loop)
    Thread.sleep(2000)
    if (processStatus(loop) && processStatus(foundryProcess)) return true

    return false
}

fun processStatus(name: String): Boolean{
    val pr = Runtime.getRuntime().exec("ps aux")

    val lines = BufferedReader(InputStreamReader(pr.inputStream)).lines()
    for (line in lines) {
        if (line.contains(name)) return true
    }
    return false
}

fun getPidFromProcess(name: String): Long{
    val pr = Runtime.getRuntime().exec("ps aux")

    val lines = BufferedReader(InputStreamReader(pr.inputStream)).lines()
    for (line in lines) {
        if (line.contains(name)) return line.split("\\s+".toRegex())[1].toLong()
    }

    return -1
}

fun killProcess(pid: Long): Process = Runtime.getRuntime().exec("kill -9 $pid")
