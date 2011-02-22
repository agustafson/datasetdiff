package org.datasetdiff

/**
 * @author: agustafson
 */
trait ColumnComparator[L,R] {
  def compareColumn(leftValue: Option[L], rightValue: Option[R]): ComparisonResult
}

object ColumnComparator {
  def default[L,R](): ColumnComparator[L,R] = new ColumnComparator[L,R]() {
    def compareColumn(leftValue: Option[L], rightValue: Option[R]): ComparisonResult = {
      val leftConvertedValue: Option[String] = leftValue.map(_.toString())
      val rightConvertedValue: Option[String] = rightValue.map(_.toString())

      val areEqual: Boolean = Ordering.Option[String].equiv(leftConvertedValue, rightConvertedValue)
      val comparisonResult: ComparisonResult =
        if (areEqual) {
          new MatchedComparisonResult()
        } else {
          new UnmatchedComparisonResult(leftConvertedValue, rightConvertedValue)
        }
      comparisonResult
    }
  }
}
