package main

import com.google.inject.Guice
import di.MemoryModule
import controller.IController
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
//import gui.GUII

object Memory {
  def main(args: Array[String]): Unit = {
    // $COVERAGE-OFF$
    try{
    val injector = Guice.createInjector(new MemoryModule())
    val controller = injector.getInstance(classOf[IController])
    

    //val controller = new Controller()
    val tui = new Tui(controller)

    // Initialisieren die GUI mit dem Controlleru
    GUI.init(controller)

    // Start TUI 
    Future {
      tui.run()
    }

    // Start GUI
    GUI.main(args)
  }catch {
      case e: Exception =>
        e.printStackTrace()
        // $COVERAGE-ON$
    }
  }
}