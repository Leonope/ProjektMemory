package model.FileIOComponent.FileIOXml

import model.FileIOComponent.FileIOInterface
import model.{IMatrix, LogicGameComponent}
import model.LogicGameComponent.{Matrix, Card, FaceDownState, FaceUpState}

import scala.xml._

import java.io._

class FileIO extends FileIOInterface[Card] {

  override def load: IMatrix[Card] = {
    var matrix: IMatrix[Card] = null
    val file = XML.loadFile("matrix.xml")
    val rows = (file \\ "rows").text.toInt
    val cols = (file \\ "cols").text.toInt
    matrix = new Matrix[Card](rows, cols, new Card(0, FaceDownState))

    val cells = (file \\ "cell")
    for (cell <- cells) {
      val row = (cell \ "@row").text.toInt
      val col = (cell \ "@col").text.toInt
      val id = (cell \ "id").text.toInt
      val state = (cell \ "state").text match {
        case "FaceDownState" => FaceDownState
        case "FaceUpState" => FaceUpState
      }
      val card = new Card(id, state)
      matrix.update(row, col, card)
    }
    matrix
  }

  override def save(matrix: IMatrix[Card]): Unit = {
    val xml = 
      <matrix>
        <rows>{matrix.rows}</rows>
        <cols>{matrix.cols}</cols>
        <cells>
          {
            for {
              row <- 0 until matrix.rows
              col <- 0 until matrix.cols
            } yield {
              <cell row={row.toString} col={col.toString}>
                <id>{matrix(row, col).id}</id>
                <state>{matrix(row, col).state.toString}</state>
              </cell>
            }
          }
        </cells>
      </matrix>
    
    val pw = new PrintWriter(new File("matrix.xml"))
    pw.write(xml.toString())
    pw.close()
  }
}

