package io.sphere.sdk.client

import java.io.Closeable
import java.util.concurrent.CompletionStage


import scala.concurrent.Future

object ScalaClient {
  def apply(sphereClient: SphereClient): ScalaClient = new ScalaClientImpl(sphereClient)
}

trait ScalaClient extends Closeable {
  def execute[T](sphereRequest: SphereRequest[T]): Future[T]
  
  def apply[T](sphereRequest: SphereRequest[T]): Future[T] = execute(sphereRequest)
  
  def close(): Unit
}

private[client] class ScalaClientImpl(sphereClient: SphereClient) extends ScalaClient {

  import ScalaAsync._

  override def execute[T](SphereRequest: SphereRequest[T]): Future[T] = sphereClient.execute(SphereRequest).asScala

  override def close(): Unit = sphereClient.close()
}

private[client] object ScalaAsync {
  import scala.concurrent.{Promise => ScalaPromise, Future}

  implicit class RichCompletableFuture[T](future: CompletionStage[T]) {
    def asScala = asFuture(future)
  }

  def asFuture[T](completableFuture: CompletionStage[T]): Future[T] = {
    val promise: ScalaPromise[T] = ScalaPromise()
    completableFuture.whenComplete(new CompletableFutureMapper(promise))
    return promise.future
  }
}