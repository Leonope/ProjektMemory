package main

import java.net.URI
import java.net.http.{HttpClient, HttpRequest, HttpResponse}
import java.net.http.HttpRequest.BodyPublishers
import java.net.http.HttpResponse.BodyHandlers

object WebLog {
  private val base = "http://localhost:9000"
  private val client = HttpClient.newHttpClient()

  def log(line: String): Unit = {
    val body = if (line.endsWith("\n")) line else line + "\n"
    val req = HttpRequest.newBuilder(URI.create(s"$base/api/log"))
      .header("Content-Type", "text/plain; charset=utf-8")
      .header("Csrf-Token", "nocheck")        // CSRF im Play-Filter umgehen
      .POST(BodyPublishers.ofString(body))
      .build()
    client.sendAsync(req, BodyHandlers.discarding()) // async, blockiert die GUI nicht
    () // Unit
  }
}
