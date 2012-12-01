package datasetdiff

/**
 * @author agustafson
 */
class ConvertingColumnComparator[T, L, R](convertLeft: (L => T), convertRight: (R => T), valueComparator: (T, T) => Boolean)
  extends ColumnComparator[L, R] {

  def this(convertLeft: (L => T), convertRight: (R => T))(implicit ord: Ordering[T]) = {
    this(convertLeft, convertRight, (leftRaw: T, rightRaw: T) => ord.compare(leftRaw, rightRaw) == 0)
  }

  def this(convertLeft: (L => T), convertRight: (R => T), comparatorFunction: ComparatorFunction[T]) = {
    this(convertLeft, convertRight, (leftRaw: T, rightRaw: T) => comparatorFunction.compare(leftRaw, rightRaw))
  }

  def compareColumn(leftValue: Option[L], rightValue: Option[R]): ComparisonResult = {
    val leftConvertedOption = convertValue(leftValue, convertLeft)
    val rightConvertedOption = convertValue(rightValue, convertRight)

    val comparisonResult: ComparisonResult = (leftConvertedOption, rightConvertedOption) match {
      case (Some(SuccessfulConversionResult(leftConvertedValue)), Some(SuccessfulConversionResult(rightConvertedValue))) => {
        if (areEqual(leftConvertedValue, rightConvertedValue)) {
          MatchedComparisonResult
        } else {
          UnmatchedComparisonResult(leftConvertedOption, rightConvertedOption)
        }
      }
      case (None, None) =>
        MatchedComparisonResult
      case _ =>
        UnmatchedComparisonResult(leftConvertedOption, rightConvertedOption)
    }
    comparisonResult
  }

  private def convertValue[I](rawValue: Option[I], converter: I => T): Option[ConversionResult[T]] = {
    try {
      if (rawValue.isDefined && rawValue.get != null) {
        Some(SuccessfulConversionResult(converter(rawValue.get)))
      } else {
        None
      }
    }
    catch {
      case exception: Throwable => Some(FailedConversionResult(rawValue, exception))
    }
  }

  protected def areEqual(leftConvertedValue: T, rightConvertedValue: T): Boolean = {
    valueComparator(leftConvertedValue, rightConvertedValue)
  }
}
