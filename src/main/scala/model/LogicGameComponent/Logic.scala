package model.LogicGameComponent

import com.google.inject.Inject
import model.{ilogic, ICard, IScoringStrategy, ICardState}

class Logic @Inject()(private val matrix: Matrix[Card], private var scoringStrategy: IScoringStrategy) extends ilogic {
  private var score: Int = 0
  private var firstCard: Option[(Int, Int)] = None
  private var consecutiveMatches: Int = 0
  
  override def getMatrix: Matrix[Card] = matrix

  override def turnCard(row: Int, col: Int): Unit = {
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
  val newCard1 = card1.turnCard() 
  val newCard2 = card2.turnCard()
  
  // Update the matrix with the new card states
  matrix.update(row1, col1, newCard1)
  matrix.update(row2, col2, newCard2)
}


  override def checkGameEnd(): Boolean = {
  matrix.getGrid.flatten.forall(_.isFaceUp)
}

  override def getScore: Int = score
}

