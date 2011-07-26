package datasetdiff

import collection.mutable.ListBuffer

/**
 * @author agustafson
 */
class Diff[L, R] {
  def difference(left: Array[L], right: Array[R]): List[DiffResult] = {
    val longestCommonSequences: Array[Array[Int]] = getLongestCommonSequences(left, right);

    val leftLength: Int = left.length
    val rightLength: Int = right.length
    val differences = new ListBuffer[DiffResult]
    
    var leftIndex: Int = 0
    var rightIndex: Int = 0
    def addLeftDifference(leftValue: Option[L]) {
      differences += new DiffResult(leftIndex, DifferenceSide.LEFT, leftValue)
      leftIndex += 1;
    }
    def addRightDifference(rightValue: Option[R]) {
      differences += new DiffResult(rightIndex, DifferenceSide.RIGHT, rightValue)
      rightIndex += 1;
    }
    while (leftIndex < leftLength && rightIndex < rightLength) {
      val leftValue = left.lift(leftIndex)
      val rightValue = right.lift(rightIndex)
      if (compareValue(leftValue, rightValue, leftIndex, rightIndex)) {
        leftIndex += 1;
        rightIndex += 1;
      }
      else if (longestCommonSequences(leftIndex + 1)(rightIndex) >= longestCommonSequences(leftIndex)(rightIndex + 1)) {
        addLeftDifference(leftValue)
      }
      else {
        addRightDifference(rightValue)
      }
    }
    while (leftIndex < leftLength || rightIndex < rightLength) {
      if (leftIndex == leftLength) {
        val rightValue: Option[R] = right.lift(rightIndex)
        addRightDifference(rightValue)
      }
      else if (rightIndex == rightLength) {
        val leftValue: Option[L] = left.lift(leftIndex)
        addLeftDifference(leftValue)
      }
    }
    differences.toList
  }

  def compareValue(leftValue: Option[L], rightValue: Option[R], leftIndex: Int, rightIndex: Int): Boolean = {
    leftValue == rightValue
  }

  private def getLongestCommonSequences(left: Array[L], right: Array[R]): Array[Array[Int]] = {
    val leftLength: Int = left.length
    val rightLength: Int = right.length
    val longestCommonSequences: Array[Array[Int]] = Array.fill(leftLength + 1, rightLength + 1)(0)
    for (i <- (leftLength - 1) to 0 by -1;
         j <- (rightLength - 1) to 0 by -1
    ) {
      val leftIndex: Int = i
      val rightIndex: Int = j
      val leftValue = left.lift(leftIndex)
      val rightValue = right.lift(rightIndex)
      val comparisonResult: Boolean = compareValue(leftValue, rightValue, leftIndex, rightIndex)
      longestCommonSequences(leftIndex)(rightIndex) =
        if (comparisonResult) {
          longestCommonSequences(leftIndex + 1)(rightIndex + 1) + 1
        }
        else {
          Math.max(longestCommonSequences(leftIndex + 1)(rightIndex), longestCommonSequences(leftIndex)(rightIndex + 1))
        }
    }
    longestCommonSequences
  }
}