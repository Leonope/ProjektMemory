package model

import model.LogicGameComponent.Card

trait ICardState {
  def click(card: Card): Card
  def display(card: Card): String
  def isFaceUp: Boolean
}


