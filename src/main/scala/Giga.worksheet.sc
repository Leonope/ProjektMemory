import scala.util.{Try, Success, Failure}

val x: Int = 28796

def frageSpielerAnzahlMock(eingabe: String): Try[Int] = Try {
  val anzahl = eingabe.toInt
  if (anzahl < 1 || anzahl > 3) throw new IllegalArgumentException("Spieleranzahl muss zwischen 1 und 3 liegen.")
  anzahl
}

def pruefeSpielerAnzahlMock(anzahl: Int): Try[String] = anzahl match {
  case 1 => Success("Ein Spieler wurde ausgewählt.")
  case _ => Failure(new Exception("Es ist nur 1 Spieler zugelassen, da die Funktion für mehr Spieler noch nicht implementiert ist."))
}

def frageSpielerNameMock(eingabe: String): String = eingabe

val test1 = frageSpielerAnzahlMock("2")
val test2 = frageSpielerAnzahlMock("1") 

val test3 = pruefeSpielerAnzahlMock(1) 
val test4 = pruefeSpielerAnzahlMock(2)

val test5 = frageSpielerNameMock("Alice")

// Zusammenstellung der Testergebnisse
test1
test2
test3
test4
test5
