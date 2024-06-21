package model

trait IScoringStrategy {
  def calculateScore(currentScore: Int, matchFound: Boolean, consecutiveMatches: Int): Int
}


