package backend

import com.google.inject.Guice
import di.MemoryModule
import controller.IController

/** Eine Injector-/Controller-Instanz für den gesamten Play-Prozess. */
object Backend {
  private val injector = Guice.createInjector(new MemoryModule())
  val controller: IController = injector.getInstance(classOf[IController])
}
