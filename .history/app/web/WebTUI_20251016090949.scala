package web

import java.io.{OutputStream, PrintStream}
import java.nio.charset.StandardCharsets
import java.util.concurrent.atomic.AtomicBoolean
import com.google.inject.Guice
import di.MemoryModule
import controller.IController
import main.Tui

final class WebTUI {

  private val injector = Guice.createInjector(new MemoryModule())
  private val controller: IController = injector.getInstance(classOf[IController])
  private val tui = new Tui(controller) // registriert sich als Observer

  private val started = new AtomicBoolean(false)

  // --- Thread-sicherer Log-Puffer
  private val buf = new StringBuilder(8192)
  private val lock = new Object
  private def append(s: String): Unit = lock.synchronized { buf.append(s) }
  def currentLog: String = lock.synchronized { buf.toString }

  // --- Globaler Tee für System.out/err (alle Threads)
  private val originalOut: PrintStream = System.out
  private val originalErr: PrintStream = System.err

  /** OutputStream, der in unseren Log schreibt und gleichzeitig an die Original-Streams spiegelt */
  private class TeeStream(target: PrintStream) extends OutputStream {
    override def write(b: Int): Unit = {
      val ch = b.toChar
      append(String.valueOf(ch))
      target.write(b)
    }
    override def write(b: Array[Byte], off: Int, len: Int): Unit = {
      val s = new String(b, off, len, StandardCharsets.UTF_8)
      append(s)
      target.write(b, off, len)
    }
    override def flush(): Unit = target.flush()
    override def close(): Unit = target.close()
  }

  // Beim Erzeugen sofort aktivieren
  initTee()

  private def initTee(): Unit = {
    // Headless sicherheitshalber ausschalten, falls GUI
    System.setProperty("java.awt.headless", "false")
    val teeOut = new PrintStream(new TeeStream(originalOut), true, "UTF-8")
    val teeErr = new PrintStream(new TeeStream(originalErr), true, "UTF-8")
    System.setOut(teeOut)
    System.setErr(teeErr)
    Console.setOut(teeOut) // Scala Console
    Console.setErr(teeErr)
  }

  /** Erste Initialisierung (einmal). Alles was gedruckt wird, landet jetzt automatisch im Log. */
  def render(): String = {
    if (started.compareAndSet(false, true)) {
      try controller.GameStarting() catch { case _: Throwable => () }
      if (currentLog.trim.isEmpty) append("TUI gestartet.\n")
    }
    currentLog
  }

  // Optional – falls du später Web-Commands anbinden willst:
  def handle(cmd: String): Unit = {
    // Hier NICHTS extra capturen – das Tee fängt schon alles ab.
    println(s">>> $cmd")
    // z.B. controller.restartGame() etc.
  }

  def newGame(): Unit = {
    println(">>> restart")
    try controller.restartGame() catch { case _: Throwable => () }
  }
}

