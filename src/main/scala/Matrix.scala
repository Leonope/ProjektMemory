import scala.reflect.ClassTag
import scala.util.Try

class Matrix[T: ClassTag](val rows: Int, val cols: Int, fill: T) {
  private val grid: Array[Array[T]] = Array.fill(rows, cols)(fill)

  def apply(row: Int, col: Int): T = grid(row)(col)
  def update(row: Int, col: Int, value: T): Unit = grid(row)(col) = value
  override def toString: String = grid.map(row => row.mkString(" ")).mkString("\n")
}

object Matrix {
  def setupMatrix(cardCount: Int): Try[Matrix[String]] = Try {
    val factors = (1 to cardCount).filter(cardCount % _ == 0)
    val (rows, cols) = factors.foldLeft((1, cardCount)) { (best, current) =>
      val other = cardCount / current
      if (Math.abs(current - other) < Math.abs(best._1 - best._2)) (current, other) else best
    }

    val (finalRows, finalCols) = if (rows > cols) (cols, rows) else (rows, cols)
    new Matrix[String](finalRows, finalCols, "_")
  }
}
