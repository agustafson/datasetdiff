package datasetdiff

import collection.immutable.Map
import java.lang.Math

/**
 * @author agustafson
 */
class DatasetDiff[L, R](columnComparators: Map[Int, ColumnComparator[L, R]], defaultColumnComparator: ColumnComparator[L, R]) {

  def this(columnComparators: Map[Int, ColumnComparator[L, R]]) = {
    this (columnComparators, ColumnComparator.defaultComparator[L, R]())
  }

  def compareDatasets[DL <: InputDataset[L], DR <: InputDataset[R]](leftDataset: DL, rightDataset: DR): List[DiffResult] = {
    val leftRows: Seq[Seq[L]] = leftDataset.extractDataRows()
    val rightRows: Seq[Seq[R]] = rightDataset.extractDataRows()

    val columnDiff: Diff[L, R] = new Diff[L, R]() {
      override def compareValue(leftValue: Option[L], rightValue: Option[R], leftIndex: Int, rightIndex: Int): Boolean = {
        val columnNumber = Math.min(leftIndex, rightIndex)
        val columnComparator: ColumnComparator[L, R] = columnComparators.getOrElse(columnNumber, defaultColumnComparator)
        val comparisonResult: ComparisonResult = columnComparator.compareColumn(leftValue, rightValue)
        comparisonResult.isMatched
      }
    }
    val diff: Diff[Seq[L], Seq[R]] = new Diff[Seq[L], Seq[R]]() {
      override def compareValue(leftRow: Option[Seq[L]], rightRow: Option[Seq[R]], leftRowNumber: Int, rightRowNumber: Int): Boolean = {
        val leftValues: Seq[L] = leftRow.getOrElse(Seq.empty)
        val rightValues: Seq[R] = rightRow.getOrElse(Seq.empty)
        val columnCount: Int = Math.max(leftValues.size, rightValues.size)
        for (columnIndex <- 0 until columnCount) {
          val leftColumn: Option[L] = leftValues.lift(columnIndex)
          val rightColumn: Option[R] = rightValues.lift(columnIndex)
          val columnComparison: Boolean = columnDiff.compareValue(leftColumn, rightColumn, columnIndex, columnIndex)
          if (!columnComparison) {
            return false
          }
        }
        return true
      }
    }
    diff.difference(leftRows.toArray, rightRows.toArray)
  }
}
