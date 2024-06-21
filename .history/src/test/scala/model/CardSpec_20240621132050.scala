package model.LogicGameComponent

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers

class CardSpec extends AnyWordSpec with Matchers {

  "A Card" when {
    "newly created" should {
      "be face down by default" in {
        val card = Card(1, FaceDownState)
        card.isFaceUp should be(false)
      }
    }

    "turnCard is called on a face down card" should {
      "turn the card face up" in {
        val card = Card(1, FaceDownState)
        val turnedCard = card.turnCard()
        turnedCard.isFaceUp should be(true)
      }
    }

    "turnCard is called on a face up card" should {
      "turn the card face down" in {
        val card = Card(1, FaceUpState)
        val turnedCard = card.turnCard()
        turnedCard.isFaceUp should be(false)
      }
    }

    "its face is down" should {
      "return '*' as its string representation" in {
        val card = Card(1, FaceDownState)
        card.toString should be("*")
      }
    }

    "its face is up" should {
      "return its ID as string representation" in {
        val card = Card(1, FaceUpState)
        card.toString should be("1")
      }
    }

    "clicked while face down" should {
      "turn face up" in {
        val card = Card(1, FaceDownState)
        val newCard = FaceDownState.click(card)
        newCard.isFaceUp should be(true)
        newCard.state shouldBe FaceUpState
      }
    }

    "clicked while face up" should {
      "turn face down" in {
        val card = Card(1, FaceUpState)
        val newCard = FaceUpState.click(card)
        newCard.isFaceUp should be(false)
        newCard.state shouldBe FaceDownState
      }
    }
  }
}




