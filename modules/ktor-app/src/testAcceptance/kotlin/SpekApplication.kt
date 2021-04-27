import io.ktor.application.*
import io.ktor.server.cio.*
import io.ktor.server.engine.*
import org.javafreedom.kcd.ktor.module
import org.javafreedom.kcd.adapters.persistence.memory.memoryModule
import org.javafreedom.kcd.ktor.baseDI
import org.javafreedom.kcd.ktor.controllerDI
import org.javafreedom.kcd.ktor.serviceModule
import org.kodein.di.DI

class SpekApplication {

    lateinit var engine: ApplicationEngine
    var started: Boolean = false

    fun start() {
        engine = embeddedServer(CIO, port = 8080, host = "localhost",
            watchPaths = emptyList(),
            module = Application::testModule)

        val disposable = engine.environment.monitor.subscribe(ApplicationStarted) {
            started = true
        }

        engine.start().addShutdownHook { stop() }

        while (!started) {
            // the start method should not exit until server is started successfully
            Thread.sleep(10)
        }

        disposable.dispose()
    }

    fun stop() {
        engine.stop(30, 50)
    }

    private fun loadModule() {
        engine.application.module(true, testDI)
    }
}

val testDI = DI.Module("test-application") {
    import(baseDI, allowOverride = false)
    import(controllerDI)

    import(memoryModule)
    import(serviceModule)
}

fun Application.testModule() {
    module(true, testDI)
}
