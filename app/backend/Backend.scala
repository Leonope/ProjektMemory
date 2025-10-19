package backend

import com.google.inject.Guice
import di.MemoryModule
import controller.IController

/** Eine gemeinsame Controller-Instanz für den ganzen Play-Prozess. */
object Backend {
  private val injector = Guice.createInjector(new MemoryModule())
  val controller: IController = injector.getInstance(classOf[IController])
}

