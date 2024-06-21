package model.LogicGameComponent

import com.google.inject.Inject
import model.{ilogic, ICard, IScoringStrategy}

class BonusScoring  @Inject() extends IScoringStrategy {
  def calculateScore(currentScore: Int, matchFound: Boolean, consecutiveMatches: Int): Int = {
    if (matchFound) currentScore + 10 + (consecutiveMatches * 5)
    else currentScore
  }
}


