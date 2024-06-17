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
            webSocket("/") {
                println("Adding user!")
                val thisConnection = Connection(this)
                connections += thisConnection
                try {
                    send("initial_messages: $chatHistory")
                    for (frame in incoming) {
                        frame as? Frame.Text ?: continue
                        val receivedText = frame.readText()
                        val messageEvent = "text: $receivedText"
                        chatHistory.add(messageEvent)
                        connections.forEach {
                            it.session.send(messageEvent)
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

