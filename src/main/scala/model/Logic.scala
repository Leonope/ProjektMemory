class Logic(private val matrix: Matrix[Card], private var scoringStrategy: ScoringStrategy) {
  private var score: Int = 0
  private var firstCard: Option[(Int, Int)] = None
  private var consecutiveMatches: Int = 0
  
  def getMatrix: Matrix[Card] = matrix

  def turnCard(row: Int, col: Int): Unit = {
    val card = matrix(row, col)
    if (!card.isFaceUp) {
      val newCard = card.turnCard()  // This returns a new Card with the state changed
      matrix.update(row, col, newCard)  // Update the matrix with the new card
      if (firstCard.isEmpty) {
        firstCard = Some((row, col))
      } else {
        val matchFound = checkCards(firstCard.get._1, firstCard.get._2, row, col)
        if (matchFound) consecutiveMatches += 1 else consecutiveMatches = 0
        score = scoringStrategy.calculateScore(score, matchFound, consecutiveMatches)
        if (!matchFound) removeCards(firstCard.get._1, firstCard.get._2, row, col)
        firstCard = None
      }
    }
  }


  def checkCards(row1: Int, col1: Int, row2: Int, col2: Int): Boolean = {
    val card1 = matrix(row1, col1)
    val card2 = matrix(row2, col2)
    if (card1.id == card2.id) {
      println(s"Congratulations! You found a matching pair with ID ${card1.id}.")
      //score += 1
      true
    } else {
      println(s"Sorry, the cards with ID ${card1.id} and ${card2.id} do not match. Try again.")
      false
    }
  }

  def removeCards(row1: Int, col1: Int, row2: Int, col2: Int): Unit = {
  val card1 = matrix(row1, col1)
  val card2 = matrix(row2, col2)
  val newCard1 = card1.turnCard()  // Assuming turnCard() flips the card to the opposite state
  val newCard2 = card2.turnCard()
  
  // Update the matrix with the new card states
  matrix.update(row1, col1, newCard1)
  matrix.update(row2, col2, newCard2)
}


  def checkGameEnd(): Boolean = {
  matrix.getGrid.flatten.forall(_.isFaceUp)
}

  def getScore: Int = score
}
// id card is faceup it cant be selected again
  trait ScoringStrategy {
  def calculateScore(currentScore: Int, matchFound: Boolean, consecutiveMatches: Int): Int
}

class StandardScoring extends ScoringStrategy {
  def calculateScore(currentScore: Int, matchFound: Boolean, consecutiveMatches: Int): Int = {
    if (matchFound) currentScore + 10
    else currentScore
  }
}

class BonusScoring extends ScoringStrategy {
  def calculateScore(currentScore: Int, matchFound: Boolean, consecutiveMatches: Int): Int = {
    if (matchFound) currentScore + 10 + (consecutiveMatches * 5)
    else currentScore
  }
}

