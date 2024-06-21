package model

trait ilogic {

  def getMatrix: Matrix[Card]

  def turnCard(row: Int, col: Int): Unit

  def checkGameEnd(): Boolean

  def getScore: Int
}



