package web

import java.io.{OutputStream, PrintStream}
import java.nio.charset.StandardCharsets
import java.util.concurrent.atomic.AtomicBoolean
import com.google.inject.Guice
import di.MemoryModule
import controller.IController
import main.Tui

/** Globaler, prozessweiter Tee für System.out/err + gemeinsamer Spielstart. */
object WebTUI {
  // innerhalb von object WebTUI
private val lock = new Object
private val buf  = new StringBuilder(8192)

private def append(s: String): Unit = lock.synchronized { buf.append(s) }
def currentLog: String = lock.synchronized { buf.toString }

// NEU: sichere, direkte Helpers
def appendLine(line: String): Unit = lock.synchronized { buf.append(line).append('\n') }
def debug(line: String): Unit = appendLine(line)

  // --- einmalig Out/Err umbiegen
  private val installed = new AtomicBoolean(false)
  private val started   = new AtomicBoolean(false)

  private lazy val injector   = Guice.createInjector(new MemoryModule())
  private lazy val controller : IController = injector.getInstance(classOf[IController])
  private lazy val tui        = new Tui(controller) // registriert Observer

  private class TeeStream(target: PrintStream) extends OutputStream {
    override def write(b: Int): Unit = {
      append(String.valueOf(b.toChar))
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

  private def installTeeOnce(): Unit = if (installed.compareAndSet(false, true)) {
    System.setProperty("java.awt.headless", "false") // falls GUI
    val teeOut = new PrintStream(new TeeStream(System.out), true, "UTF-8")
    val teeErr = new PrintStream(new TeeStream(System.err), true, "UTF-8")
    System.setOut(teeOut)
    System.setErr(teeErr)
  }

  /** Von der View aufgerufen: installiert Tee & startet das Spiel einmal. */
  def render(): String = {
    installTeeOnce()
    if (started.compareAndSet(false, true)) {
      try controller.GameStarting() catch { case _: Throwable => () }
      if (currentLog.trim.isEmpty) append("TUI gestartet.\n")
    }
    currentLog
  }

  // Optional: Hooks, falls du Buttons/Eingaben beibehältst:
  def handle(cmd: String): Unit = {
    installTeeOnce()
    println(s">>> $cmd") // alles wird global mitgeloggt
    // hier ggf. controller.* aufrufen, wenn du willst
  }
  def newGame(): Unit = {
    installTeeOnce()
    println(">>> restart")
    try controller.restartGame() catch { case _: Throwable => () }
  }

  def state: Action[AnyContent] = Action {
  Ok(WebTUI.currentLog).as("text/plain; charset=utf-8")
}


}

