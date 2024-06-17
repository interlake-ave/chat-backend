package org.example

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import java.util.*

//Code from https://ktor.io/docs/server-websockets.html#handle-multiple-session
fun main() {
    embeddedServer(Netty, port = 8080) {
        install(WebSockets)
        routing {
            val connections = Collections.synchronizedSet<Connection?>(LinkedHashSet())
            val chatHistory = Collections.synchronizedList(mutableListOf<String>())
            webSocket("/chat") {
                println("Adding user!")
                val thisConnection = Connection(this)
                connections += thisConnection
                try {
                    send(chatHistory.toString() + "You are connected! There are ${connections.count()} users here.")
                    for (frame in incoming) {
                        frame as? Frame.Text ?: continue
                        val receivedText = frame.readText()
                        val textWithUsername = "[${thisConnection.name}]: $receivedText"
                        connections.forEach {
                            it.session.send(textWithUsername)
                            chatHistory.add(textWithUsername)
                        }
                    }
                } catch (e: Exception) {
                    println(e.localizedMessage)
                } finally {
                    println("Removing $thisConnection!")
                    connections -= thisConnection
                }
            }
        }
    }.start(wait = true)
}

