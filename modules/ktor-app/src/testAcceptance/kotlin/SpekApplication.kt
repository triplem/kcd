import io.ktor.application.*
import io.ktor.server.cio.*
import io.ktor.server.engine.*
import org.javafreedom.kcd.adapters.persistence.memory.memoryModule
import org.javafreedom.kcd.ktor.baseDI
import org.javafreedom.kcd.ktor.controllerDI
import org.javafreedom.kcd.ktor.module
import org.javafreedom.kcd.ktor.serviceModule
import org.kodein.di.DI

class SpekApplication {

    lateinit var engine: ApplicationEngine
    var started: Boolean = false

    fun start() {
        val configPath = ClassLoader.getSystemResource("application-acceptanceTest.conf").file
        var appEnvironment = commandLineEnvironment(arrayOf("-config=$configPath"))

        engine = embeddedServer(CIO, appEnvironment)
        engine.addShutdownHook {
            stop()
        }

        engine.start()

        val disposable = engine.environment.monitor.subscribe(ApplicationStarted) {
            started = true
        }

        while (!started) {
            // the start method should not exit until server is started successfully
            Thread.sleep(10)
        }

        disposable.dispose()
    }

    fun stop() {
        engine.stop(30, 50)
    }
}

val testDI = DI.Module("test-application") {
    import(baseDI, allowOverride = false)
    import(controllerDI)

    import(memoryModule)
    import(serviceModule)
}

fun Application.module(testing: Boolean = false) {
    module(testing, testDI)
}
