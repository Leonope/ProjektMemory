package model.LogicGameComponent

import model.{ilogic, ICard, IScoringStrategy, ICardState}

class StandardScoring extends IScoringStrategy {
  def calculateScore(currentScore: Int, matchFound: Boolean, consecutiveMatches: Int): Int = {
    if (matchFound) currentScore + 10
    else currentScore
  }
}


