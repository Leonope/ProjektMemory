package model.LogicGameComponent

import com.google.inject.Inject
import model.{ilogic, ICard, IScoringStrategy, ICardState}

case object FaceDownState extends ICardState {
  def click(card: Card): Card = card.copy(state = FaceUpState)
  def display(card: Card): String = "*"
  def isFaceUp: Boolean = false
}


