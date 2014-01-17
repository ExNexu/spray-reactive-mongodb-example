package us.bleibinha.sprayreactivemongodbexample

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.concurrent.Future

import org.scalatest.FunSpec
import org.scalatest.matchers.ShouldMatchers
import reactivemongo.bson.BSONDocument
import reactivemongo.core.commands.LastError
import spray.http.StatusCodes
import spray.testkit.ScalatestRouteTest

class MyServiceIntTest
    extends FunSpec
    with ShouldMatchers
    with ScalatestRouteTest
    with MyService {

  def actorRefFactory = system

  describe("MyService") {

    it("should return OK on a put request and perform a databse insert") {

      val query = BSONDocument("firstName" -> "Jack")

      val resultBeforeList: Future[List[BSONDocument]] =
        collection.
          find(query).
          cursor[BSONDocument].
          collect[List]()

      val resultsBefore = Await.result(resultBeforeList, 5 seconds).size

      Put() ~> myRoute ~> check {
        response.status should be(StatusCodes.OK)
      }

      val resultAfterList: Future[List[BSONDocument]] =
        collection.
          find(query).
          cursor[BSONDocument].
          collect[List]()

      val resultsAfter = Await.result(resultAfterList, 5 seconds).size

      resultsAfter should be(resultsBefore + 1)
    }

  }

}
