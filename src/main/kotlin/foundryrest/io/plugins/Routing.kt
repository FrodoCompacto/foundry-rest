package foundryrest.io.plugins

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import java.io.*
import kotlin.io.copyTo

fun Application.configureRouting() {
    routing {
        get("/") {
            call.respond(HttpStatusCode.Accepted, "fOunDRy D0S IrmÃo 3Stá OonL1n3!!!")
        }
        get("/log/error") {
            call.respondFile(File("/home/ec2-user/foundrydata/logs/error.log"))
        }
        get("/log/debug") {
            call.respondFile(File("/home/ec2-user/foundrydata/logs/debug.log"))
        }

        get("/pwkill") {
            killSh()
            call.respond(HttpStatusCode.Accepted, "Está morto, não respira.")
        }
        get("/revivify"){
            call.respond(HttpStatusCode.Accepted, runSh())
        }
    }
}

fun runSh(): String{
    return if(processStatus("node foundryvtt") && processStatus("sh loop.sh")){
        val aux = getPidFromProcess("sh loop.sh")
        if (aux == readFile()) "Já ta online fera."
        else {
            killProcess(aux)
            "Já ta online mas não fui eu q abri, deu bug tbm, foda..."
        }
    } else if (processStatus("node foundryvtt") && !processStatus("sh loop.sh")){
        "Já ta online mas não fui eu q abri."
    } else {
        val pr = Runtime.getRuntime().exec("sh loop.sh")
        saveFile(pr.pid())
        "Criado irmão, tmj!"
    }

}

fun killSh(){
    val pid = readFile()

    if (processStatus("sh loop.sh", pid)) killProcess(pid)
    if (processStatus("node foundryvtt")){
        val id = getPidFromProcess("node foundryvtt")
        if (id != -1L){
            killProcess(id)
        }
    }
}

fun processStatus(name: String): Boolean{
    val pr = Runtime.getRuntime().exec("ps aux")

    val lines = BufferedReader(InputStreamReader(pr.inputStream)).lines()
    for (line in lines) {
        if (line.contains(name)) return true
    }
    return false
}

fun processStatus(name: String, pid: Long): Boolean{
    val pr = Runtime.getRuntime().exec("ps -p $pid")

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

fun saveFile(pid: Long){
    val fileName = "process.txt"
    val myfile = File(fileName)

    myfile.printWriter().use { out ->
        out.print(pid)
    }
}

fun readFile(): Long = File("process.txt").readText(Charsets.UTF_8).toLong()

fun killProcess(pid: Long): Process = Runtime.getRuntime().exec("kill -9 $pid")
