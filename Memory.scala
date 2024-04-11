import scala.io.StdIn.readLine
import scala.util.{Try, Success, Failure}

object MemorySpiel extends App {//erlaubt direkte Ausführung

  def frageSpielerAnzahl(): Try[Int] = Try {
    println("Wie viele Spieler sollen mitspielen? (Wähle zwischen 1-3)")
    val anzahl = readLine().toInt
    if (anzahl < 1 || anzahl > 3) throw new IllegalArgumentException("Spieleranzahl muss zwischen 1 und 3 liegen.")//pls catch this :D
    anzahl
  }

  def pruefeSpielerAnzahl(anzahl: Int): Try[String] = anzahl match {//exception abfangen success oder failure
    case 1 => Success("Ein Spieler wurde ausgewählt.")
    case _ => Failure(new Exception("Es ist nur 1 Spieler zugelassen, da die Funktion für mehr Spieler noch nicht implementiert ist."))
  }

  def frageSpielerName(): String = {
    println("Bitte geben Sie Ihren Namen ein:")
    readLine()
  }

  /*def begruesseSpieler(name: String): Unit = { //anstelle von void?? i think
    println(s"Herzlich willkommen zum Memory Spiel $name! Viel Spaß!")//s für variable $name
  }*/

  val spielStarten = for {
    anzahl <- frageSpielerAnzahl()
    pruefung <- pruefeSpielerAnzahl(anzahl)
  } yield {// vorherige Processe succesfull? Iterator?
    val name = frageSpielerName()
    //begruesseSpieler(name)
    println(s"Herzlich willkommen zum Memory Spiel $name! Viel Spaß!")//s für variable $name
  }

  spielStarten match {
    case Success(_) =>//lauft weiter
    case Failure(e) => println(s"Fehler: ${e.getMessage}")
  }
}