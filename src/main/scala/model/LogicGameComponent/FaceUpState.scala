package model.LogicGameComponent

import com.google.inject.Inject
import model.{ilogic, ICard, IScoringStrategy, ICardState}

case object FaceUpState extends ICardState {
  def click(card: Card): Card = card.copy(state = FaceDownState)
  def display(card: Card): String = card.id.toString
  def isFaceUp: Boolean = true
}


