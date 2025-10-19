package web

import java.io.{ByteArrayOutputStream, PrintStream}
import java.util.concurrent.atomic.AtomicBoolean
import com.google.inject.Guice
import di.MemoryModule
import controller.IController
import main.Tui

/** Fängt alle println-Ausgaben (z.B. aus TUI.update) ab und sammelt sie für die Web-Ansicht. */
final class WebTUI {

  private val injector = Guice.createInjector(new MemoryModule())
  private val controller: IController = injector.getInstance(classOf[IController])
  // TUI registriert sich als Observer am Controller und println't in update()
  private val tui = new Tui(controller)

  // Log-Puffer für alles, was die TUI/Controller drucken
  private val log = new StringBuilder(4096)
  private val started = new AtomicBoolean(false)
  private val originalOut: PrintStream = System.out  // damit wir parallel weiterhin in der Konsole sehen

  /** Hilfsfunktion: führt f aus und fängt dabei Console.out ein; schreibt in log und in die originale Konsole. */
  private def capture[A](f: => A): A = {
    val baos = new ByteArrayOutputStream()
    val ps   = new PrintStream(baos, true, "UTF-8")
    val res: A = Console.withOut(ps) {
      f // innerhalb dieses Blocks gehen alle println nach 'ps' -> baos
    }
    ps.flush()
    val s = baos.toString("UTF-8")
    this.synchronized {
      if (s.nonEmpty) log.append(s)
    }
    // auch in die echte Konsole spiegeln (optional)
    if (s.nonEmpty) originalOut.print(s)
    res
  }

  /** Beim ersten Aufruf: Spielstart triggern und Startausgaben capturen. Danach: nur Log anzeigen. */
  def render(): String = {
    if (started.compareAndSet(false, true)) {
      // entspricht deiner TUI.run() ohne die StdIn-Schleife
      capture {
        try controller.GameStarting()
        catch { case _: Throwable => () }
      }
      if (log.isEmpty) {
        this.synchronized {
          log.append("TUI gestartet. (Keine Startausgaben gefunden)\n")
        }
      }
    }
    this.synchronized { log.toString }
  }

  /** Optional: später, wenn du Befehle anbinden willst, hier Controller-Aufrufe wrappen.
    * Aktuell nur als Platzhalter: wir hängen den Text ans Log (sichtbar im Browser).
    */
  def handle(cmd: String): Unit = {
    capture {
      println(s">>> $cmd")
      // Beispiel: wenn du später echte Kommandos hast, ruf sie hier auf:
      // controller.restartGame()
      // controller.open(r, c)
      // usw. – alles wird automatisch geloggt.
    }
  }

  /** Neues Spiel: Controller aufrufen und dabei Ausgaben mitloggen. */
  def newGame(): Unit = {
    capture {
      println(">>> restart")
      try controller.restartGame()
      catch { case _: Throwable => () }
    }
  }
}

