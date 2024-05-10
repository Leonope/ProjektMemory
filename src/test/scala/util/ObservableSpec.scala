import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import util.{Observable, Observer}

class ObserverSpec extends AnyWordSpec with Matchers {

  "An Observable" should {

    "allow adding observers" in {
      val observable = new Observable
      val observer = new TestObserver
      observable.add(observer)
      observable.subscribers should contain(observer)
    }

    "allow removing observers" in {
      val observable = new Observable
      val observer = new TestObserver
      observable.add(observer)
      observable.remove(observer)
      observable.subscribers should not contain observer
    }

    "notify all its observers" in {
      val observable = new Observable
      val observer1 = new TestObserver
      val observer2 = new TestObserver
      observable.add(observer1)
      observable.add(observer2)
      observable.notifyObservers
      observer1.updated shouldBe true
      observer2.updated shouldBe true
    }
  }

  // Helper class to test Observer implementation
  class TestObserver extends Observer {
    var updated: Boolean = false
    def update: Unit = updated = true
  }
}



