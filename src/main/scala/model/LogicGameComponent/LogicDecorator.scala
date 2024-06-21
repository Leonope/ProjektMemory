package model.LogicGameComponent

import model.{ilogic, ICard, IScoringStrategy, ICardState}

class LogicDecorator(private val decoratedLogic: ilogic) extends ilogic {
  override def getMatrix: Matrix[Card] = decoratedLogic.getMatrix

  override def turnCard(row: Int, col: Int): Unit = {
    decoratedLogic.turnCard(row, col)
    println(s"Current score after turning card: ${getScore}")
  }

  override def checkGameEnd(): Boolean = decoratedLogic.checkGameEnd()

  override def getScore: Int = decoratedLogic.getScore
}

