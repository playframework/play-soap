package play.soap

import java.util

import org.apache.cxf.endpoint.ClientCallback

import scala.concurrent.Promise

class PlayJaxwsClientCallback(promise: Promise[AnyRef]) extends ClientCallback {

  override def handleResponse(ctx: util.Map[String, AnyRef], response: Array[AnyRef]) = {
    promise.trySuccess(response(0))
  }

  override def handleException(ctx: util.Map[String, AnyRef], ex: Throwable) = {
    promise.tryFailure(ex)
  }
}
