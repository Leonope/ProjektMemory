package model.FileIOComponent.FileIOJson

import com.google.inject.Guice
import com.google.inject.name.Names
import net.codingwell.scalaguice.InjectorExtensions._
import model.FileIOComponent.FileIOInterface
import model.{IMatrix, LogicGameComponent}
import model.LogicGameComponent.{Matrix, Card, FaceDownState, FaceUpState}
import play.api.libs.json._

import scala.io.Source

class FileIO extends FileIOInterface[Card] {

  override def load: IMatrix[Card] = {
    var matrix: IMatrix[Card] = null
    val source: String = Source.fromFile("matrix.json").getLines.mkString
    val json: JsValue = Json.parse(source)
    val rows = (json \ "matrix" \ "rows").as[Int]
    val cols = (json \ "matrix" \ "cols").as[Int]
    matrix = new Matrix[Card](rows, cols, new Card(0, FaceDownState))
    
    for (index <- 0 until rows * cols) {
      val row = (json \\ "row")(index).as[Int]
      val col = (json \\ "col")(index).as[Int]
      val cell = (json \\ "cell")(index)
      val id = (cell \ "id").as[Int]
      val state = (cell \ "state").as[String] match {
        case "FaceDownState" => FaceDownState
        case "FaceUpState" => FaceUpState
      }
      val card = new Card(id, state)
      matrix.update(row, col, card)
    }
    matrix
  }

  override def save(matrix: IMatrix[Card]): Unit = {
    import java.io._
    val pw = new PrintWriter(new File("matrix.json"))
    pw.write(Json.prettyPrint(matrixToJson(matrix)))
    pw.close()
  }

  /*implicit val cardWrites = new Writes[Card] {
    def writes(card: Card) = Json.obj(
      "id" -> card.id,
      "state" -> card.state.toString
    )
  }*/

  def matrixToJson(matrix: IMatrix[Card]) = {
    Json.obj(
      "matrix" -> Json.obj(
        "rows" -> JsNumber(matrix.rows),
        "cols" -> JsNumber(matrix.cols),
        "cells" -> Json.toJson(
          for {
            row <- 0 until matrix.rows;
            col <- 0 until matrix.cols
          } yield {
            Json.obj(
              "row" -> row,
              "col" -> col,
              "cell" -> Json.toJson(matrix(row, col))
            )
          }
        )
      )
    )
  }
}



