package web

import com.google.inject.Guice

// Aus deiner JAR:
import di.MemoryModule
import controller.IController

final class WebTUI {

  // --- Controller aus deinem SE-Projekt via Guice holen ---
  private val injector   = Guice.createInjector(new MemoryModule())
  private val controller = injector.getInstance(classOf[IController])

  @volatile private var started = false

  private def ensureStarted(): Unit =
    if (!started) {
      safeCallUnit(controller, "GameStarting") // IController.GameStarting()
      started = true
    }

  // --- Aktuellen Zustand als String für die View liefern ---
  def render(): String = {
    ensureStarted()

    // Versuche ein paar übliche Renderer in sinnvoller Reihenfolge:
    val tries = List(
      safeCallString(controller, "boardString"),
      safeCallString(controller, "stateString"),
      safeCallString(controller, "toString")
    ).filter(_.nonEmpty)

    tries.headOption.getOrElse("Kein Renderer gefunden – implementiere z. B. controller.boardString/stateString oder überschreibe toString.")
  }

  // --- Einen einzelnen Befehl verarbeiten ---
  def handle(cmd: String): Unit = {
    ensureStarted()
    cmd.trim.toLowerCase match {
      case "restart" | "new" | "newgame" =>
        // typischer Reset
        if (!safeCallUnit(controller, "restartGame"))
          () // wenn es die Methode nicht gibt, ignoriere still
      case "start" =>
        safeCallUnit(controller, "GameStarting")
      case "quit" | "exit" =>
        // NICHT System.exit im Web! -> ignorieren
        ()
      case other =>
        // Wenn dein Controller eine "handle(String)" oder "processInputLine(String)" o.ä. hat:
        val handled =
          safeCallUnit(controller, "handle", classOf[String], other) ||
          safeCallUnit(controller, "processInputLine", classOf[String], other) ||
          safeCallUnit(controller, "input", classOf[String], other)

        if (!handled) {
          // Fallback: nichts tun
          ()
        }
    }
  }

  def newGame(): Unit = {
    if (!safeCallUnit(controller, "restartGame"))
      safeCallUnit(controller, "GameStarting")
  }

  // ---------- kleine Reflection-Helper ----------
  private def safeCallUnit(target: AnyRef, name: String): Boolean =
    try {
      val m = target.getClass.getMethod(name)
      m.invoke(target)
      true
    } catch {
      case _: Throwable => false
    }

  private def safeCallUnit(target: AnyRef, name: String, p1: Class[_], arg1: AnyRef): Boolean =
    try {
      val m = target.getClass.getMethod(name, p1)
      m.invoke(target, arg1)
      true
    } catch {
      case _: Throwable => false
    }

  private def safeCallString(target: AnyRef, name: String): String =
    try {
      val m = target.getClass.getMethod(name)
      val res = m.invoke(target)
      if (res == null) "" else res.toString
    } catch {
      case _: Throwable => ""
    }
}
