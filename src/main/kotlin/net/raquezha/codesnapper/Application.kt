package net.raquezha.codesnapper

import io.ktor.server.application.*
import io.ktor.server.netty.EngineMain
import net.raquezha.codesnapper.di.appModule
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger

fun main(args: Array<String>) {
    EngineMain.main(args)
}

fun Application.module() {
    install(Koin) {
        slf4jLogger()
        modules(appModule)
    }
    configureHTTP()
    configureMonitoring()
    configureSerialization()
    configureRouting()
}
