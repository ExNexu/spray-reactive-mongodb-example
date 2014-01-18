package us.bleibinha.sprayreactivemongodbexample

import scala.concurrent.Future

import org.mockito.Mockito.mock
import org.mockito.Mockito.reset
import org.mockito.Mockito.verify
import org.mockito.Mockito.when
import org.scalatest.BeforeAndAfter
import org.scalatest.FunSpec
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.mock.MockitoSugar
import reactivemongo.core.commands.LastError
import spray.http.StatusCodes
import spray.testkit.ScalatestRouteTest

class MyServiceTest
    extends FunSpec
    with ShouldMatchers
    with BeforeAndAfter
    with MockitoSugar
    with ScalatestRouteTest
    with MyService {

  def actorRefFactory = system
  val lastError = mock[LastError]
  override def save() = Future.successful(lastError)

  describe("MyService") {

    it("should return OK on a put request (successful database insert)") {
      when(lastError.code).thenReturn(None)

      Put("/test") ~> myRoute ~> check {
        response.status should be(StatusCodes.OK)
      }

      verify(lastError).code
    }

    it("should return InternalServerError on a put request (unsuccessful database insert)") {
      when(lastError.code).thenReturn(Some(1))

      Put("/test") ~> myRoute ~> check {
        response.status should be(StatusCodes.InternalServerError)
      }

      verify(lastError).code
    }

  }

  before {
    reset(lastError)
  }
}
