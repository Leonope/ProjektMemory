import scala.io.StdIn.readLine
import scala.util.{Try, Success, Failure}
import util.InputHandler
import util.RealInputHandler

object Memory {
   // $COVERAGE-OFF$
  def main(args: Array[String]): Unit = {
    val controller = new Controller()
    //model instanz bei controller
    val tui = new Tui(controller)
    tui.run()
     // $COVERAGE-ON$
  }
}