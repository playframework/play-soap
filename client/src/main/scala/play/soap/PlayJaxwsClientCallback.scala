/*
 * Copyright (C) 2015-2017 Lightbend Inc. <https://www.lightbend.com>
 */
package play.soap

import java.util

import org.apache.cxf.endpoint.ClientCallback

import scala.concurrent.Promise

/**
 * A client callback based on a promise
 *
 * @param promise The promise
 * @param noResponseValue If no response comes back, redeem the future with this value
 */
private[soap] class PlayJaxwsClientCallback(promise: Promise[Any], noResponseValue: Any = null) extends ClientCallback {

  override def handleResponse(ctx: util.Map[String, AnyRef], response: Array[AnyRef]) = {
    // If there's no return value, the response will be null
    if (response != null) {
      promise.trySuccess(response(0))
    } else {
      promise.trySuccess(noResponseValue)
    }
  }

  override def handleException(ctx: util.Map[String, AnyRef], ex: Throwable) = {
    promise.tryFailure(ex)
  }
}
