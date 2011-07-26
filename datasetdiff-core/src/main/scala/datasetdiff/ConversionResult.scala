package datasetdiff

/**
 * Result of conversion.
 *
 * @author agustafson
 */
sealed abstract class ConversionResult[+T] {
  def isConverted: Boolean
}

case class SuccessfulConversionResult[+T](convertedValue: T) extends ConversionResult[T] {
  def isConverted: Boolean = true
}

case class FailedConversionResult(unconvertedValue: Any, exception: Throwable) extends ConversionResult[Nothing] {
  def isConverted: Boolean = false
}
