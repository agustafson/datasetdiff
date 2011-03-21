package org.datasetdiff

/**
 * @author agustafson
 */
sealed abstract class ComparisonResult() {
  val isMatched: Boolean
}

case class MatchedComparisonResult extends ComparisonResult() {
  val isMatched: Boolean = true
}

case class UnmatchedComparisonResult[T](leftData: Option[T], rightData: Option[T]) extends ComparisonResult() {
  val isMatched: Boolean = false
}
