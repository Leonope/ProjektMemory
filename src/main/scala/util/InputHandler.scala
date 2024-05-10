package util

trait InputHandler {
  def readLine(): String
  def askForInput(prompt: String): String // Diese Methode muss hinzugefügt werden
}


object RealInputHandler extends InputHandler {
  override def readLine(): String = scala.io.StdIn.readLine()

  override def askForInput(prompt: String): String = {
    println(prompt)// später prüfen auf fehler
    readLine()
  }
}
