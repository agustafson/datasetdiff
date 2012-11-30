package datasetdiff

/**
 * Column comparator: compare left and right values and return a ComparisonResult.
 *
 * @author agustafson
 */
trait ColumnComparator[L, R] {
  /**
   * Compare left and right values and return a ComparisonResult
   */
  def compareColumn(leftValue: Option[L], rightValue: Option[R]): ComparisonResult
}

object ColumnComparator {
  /**
   * Default comparator. Compares the toString value.
   */
  def defaultComparator[L, R](): ColumnComparator[L, R] = new ColumnComparator[L, R]() {
    def compareColumn(leftValue: Option[L], rightValue: Option[R]): ComparisonResult = {
      val leftConvertedValue: Option[String] = leftValue.map(_.toString)
      val rightConvertedValue: Option[String] = rightValue.map(_.toString)

      val areEqual: Boolean = Ordering.Option[String].equiv(leftConvertedValue, rightConvertedValue)
      val comparisonResult: ComparisonResult =
        if (areEqual) {
          MatchedComparisonResult
        } else {
          UnmatchedComparisonResult(leftConvertedValue, rightConvertedValue)
        }
      comparisonResult
    }
  }
}
