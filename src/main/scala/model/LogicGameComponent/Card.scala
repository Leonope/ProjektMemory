package model.LogicGameComponent

import com.google.inject.Inject
import model.{ilogic, ICard, IScoringStrategy, ICardState}

case class Card @Inject()(id: Int, state: ICardState = FaceDownState) extends ICard {
  def turnCard(): Card = {
    if (state == FaceDownState) copy(state = FaceUpState)
    else copy(state = FaceDownState)
  }
  def isFaceUp: Boolean = state.isFaceUp
  override def toString: String = state.display(this)
}