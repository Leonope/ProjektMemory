package model

trait IMatrix[T] {
  def rows: Int
  def cols: Int
  def apply(row: Int, col: Int): T
  def update(row: Int, col: Int, value: T): Unit
  def initializeWithCards(cards: Seq[T]): IMatrix[T]
  def select(row: Int, col: Int): Unit
  def getGrid: Array[Array[T]]
}



