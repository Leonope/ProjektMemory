package model.LogicGameComponent

import scala.util.{Try, Random}
import com.google.inject.Inject
import model.IMatrix
import scala.reflect.ClassTag

class Matrix[T: ClassTag] @Inject() (val rows: Int, val cols: Int, fill: T) extends IMatrix[T] {
  private val grid: Array[Array[T]] = Array.fill(rows, cols)(fill)

  def apply(row: Int, col: Int): T = grid(row)(col)
  def update(row: Int, col: Int, value: T): Unit = grid(row)(col) = value

  override def toString: String = {
    grid.map(row => row.map(_.toString).mkString(" ")).mkString("\n")
  }

  def initializeWithCards(cards: Seq[T]): IMatrix[T] = {
    require(cards.length == rows * cols)
    var index = 0
    for (i <- 0 until rows; j <- 0 until cols) {
      update(i, j, cards(index))
      index += 1
    }
    this // Return the modified matrix
  }

  def select(row: Int, col: Int): Unit = {
    grid(row)(col) match {
      case card: Card =>
        val newCard = card.turnCard()  // This returns a new Card with the state changed
        update(row, col, newCard.asInstanceOf[T])  // Update the matrix with the new card
      case _ => // Do nothing if not a Card
    }
  }

  def getGrid: Array[Array[T]] = grid
}

object Matrix {
  def setupMatrix(cardCount: Int, factory: MatrixFactory[Card] = new RandomMatrixFactory()): Try[Matrix[Card]] = Try {
    val factors = (1 to cardCount).filter(cardCount % _ == 0)
    val (rows, cols) = factors.foldLeft((1, cardCount)) { (best, current) =>
      val other = cardCount / current
      if (Math.abs(current - other) < Math.abs(best._1 - best._2)) (current, other) else best
    }
    val (finalRows, finalCols) = if (rows > cols) (cols, rows) else (rows, cols)
    val cards = (1 to cardCount / 2).flatMap(id => Seq(new Card(id, FaceDownState), new Card(id, FaceDownState)))

    factory.create(finalRows, finalCols, new Card(0, FaceDownState), cards)
  }
}

trait MatrixFactory[T] {
  def create(rows: Int, cols: Int, fill: T, cards: Seq[T]): Matrix[T]
}

class StandardMatrixFactory extends MatrixFactory[Card] {
  override def create(rows: Int, cols: Int, fill: Card, cards: Seq[Card]): Matrix[Card] = {
    val matrix = new Matrix[Card](rows, cols, fill)
    matrix.initializeWithCards((cards))
    matrix
  }
}

class RandomMatrixFactory extends MatrixFactory[Card] {
  override def create(rows: Int, cols: Int, fill: Card, cards: Seq[Card]): Matrix[Card] = {
    val matrix = new Matrix[Card](rows, cols, fill)
    val shuffledCards = Random.shuffle(cards)
    matrix.initializeWithCards(shuffledCards)
    matrix
  }
}