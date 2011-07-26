package datasetdiff

/**
 * @author agustafson
 */
sealed abstract class ComparisonResult() {
  val isMatched: Boolean
}

case object MatchedComparisonResult extends ComparisonResult {
  val isMatched: Boolean = true

  override def toString: String = "MatchedComparisonResult"
}

case class UnmatchedComparisonResult[T](leftData: Option[T], rightData: Option[T]) extends ComparisonResult {
  val isMatched: Boolean = false

  override def toString: String = "UnmatchedComparisonResult(" + leftData + "," + rightData + ")"
}
