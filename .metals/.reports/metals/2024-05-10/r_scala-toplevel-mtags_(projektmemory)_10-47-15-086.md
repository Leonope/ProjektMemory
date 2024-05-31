error id: file:///C:/Users/leo11/OneDrive/Desktop/HTWG/AIN/Semester%203/Software-Engineering/ProjektMemory/src/main/scala/model/Card.scala:[270..278) in Input.VirtualFile("file:///C:/Users/leo11/OneDrive/Desktop/HTWG/AIN/Semester%203/Software-Engineering/ProjektMemory/src/main/scala/model/Card.scala", "// Adjusted CardState trait and concrete states
sealed trait CardState {//state pattern
  def click(card: Card): Card
  def display(card: Card): String
  def isFaceUp: Boolean  //  to determine if the card is face up
}

case object FaceUpState extends CardState {
  def override click(card: Card): Card = card.copy(state = FaceDownState)
  def display(card: Card): String = card.id.toString
  def isFaceUp: Boolean = true
}

case object FaceDownState extends CardState {
  def click(card: Card): Card = card.copy(state = FaceUpState)
  def display(card: Card): String = "*"
  def isFaceUp: Boolean = false
}

case class Card(id: Int, state: CardState = FaceDownState) {
  def turnCard(): Card = {
    if (state == FaceDownState) copy(state = FaceUpState)
    else copy(state = FaceDownState)
  }
  def isFaceUp: Boolean = state.isFaceUp
  override def toString: String = state.display(this)
}")
file:///C:/Users/leo11/OneDrive/Desktop/HTWG/AIN/Semester%203/Software-Engineering/ProjektMemory/src/main/scala/model/Card.scala
file:///C:/Users/leo11/OneDrive/Desktop/HTWG/AIN/Semester%203/Software-Engineering/ProjektMemory/src/main/scala/model/Card.scala:9: error: expected identifier; obtained override
  def override click(card: Card): Card = card.copy(state = FaceDownState)
      ^
#### Short summary: 

expected identifier; obtained override