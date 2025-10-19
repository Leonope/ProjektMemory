package controller

import scala.util.Try
import model.ilogic
import util.Observer

trait IController {
  def GameStarting(): Unit
  def askPlayerCount(count: Option[Int] = None): Unit
  def askPlayerName(name: Option[String] = None): Unit
  def askCardCount(count: Option[Int] = None): Unit
  def startGameLogic(): Unit
  def playGame(input: String): Unit
  def restartGame(): Unit
  def undo(): Unit
  def redo(): Unit
  def getCardCount: Option[Int]

  def add(observer: Observer): Unit
  def remove(observer: Observer): Unit
  def notifyObservers: Unit
  def gameLogic: ilogic

  def getPlayerCount: Option[Int]
  def getPlayerName: Option[String]

  def load(): Unit
  def save(): Unit

  
}


