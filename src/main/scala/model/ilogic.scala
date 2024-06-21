package model

import model.LogicGameComponent.{Matrix, Card}

trait ilogic {
  def getMatrix: Matrix[Card]
  def turnCard(row: Int, col: Int): Unit
  def checkGameEnd(): Boolean
  def getScore: Int
}



