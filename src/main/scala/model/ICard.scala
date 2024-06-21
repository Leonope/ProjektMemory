package model

trait ICard {
  def id: Int
  def turnCard(): ICard
  def isFaceUp: Boolean
  override def toString: String
}


