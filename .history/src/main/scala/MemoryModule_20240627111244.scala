package di

import com.google.inject.{AbstractModule, Provides, Singleton, TypeLiteral}
import model.{ilogic, ICardState, ICard, IScoringStrategy, IMatrix}
import model.LogicGameComponent.{Logic, Card, Matrix, BonusScoring, StandardScoring, FaceDownState, FaceUpState}
import controller.{IController, Controller}
import scala.reflect.ClassTag
import model.FileIOComponent.FileIOInterface
import model.FileIOComponent.FileIOJson.FileIO

class MemoryModule extends AbstractModule {
  // $COVERAGE-OFF$
  override def configure(): Unit = {
    bind(classOf[ilogic]).to(classOf[Logic])
    bind(classOf[IController]).to(classOf[Controller])
    //bind(classOf[ICardState]).to(classOf[ICardState])
    //bind(classOf[ICard]).to(classOf[Card])
    bind(classOf[IScoringStrategy]).to(classOf[BonusScoring])
    bind(new TypeLiteral[Matrix[Card]]() {}).toInstance(provideMatrix())


  @Provides
  def provideMatrix(): Matrix[Card] = {
    new Matrix[Card](4, 4, new Card(0, FaceDownState))
  }
  
  @Provides
  def provideCardState(): ICardState = FaceDownState

  @Provides
  def provideInteger(): Integer = 4
}
// $COVERAGE-ON$
}
