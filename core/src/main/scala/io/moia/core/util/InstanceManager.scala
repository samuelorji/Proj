package io.moia.core.util

/**
  * This is to ensure that there is only one Instance of
  * @tparam T
  */
trait InstanceManager[T] {
  private var instance : Option[T] = None

  protected def setInstance(t : T) = {
    instance match {
      case Some(_) => throw new Exception("Instance already set")
      case None    => instance = Some(t)
    }
  }

  def getInstance : T  = instance match {
    case Some(x) => x
    case None    => throw new Exception("Instance not set")
  }

}
