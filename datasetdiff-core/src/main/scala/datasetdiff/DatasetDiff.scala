package datasetdiff

import collection.immutable.Map

/**
 * @author agustafson
 */
class DatasetDiff[L, R](columnComparators: Map[Int, ColumnComparator[L, R]], defaultColumnComparator: ColumnComparator[L, R]) {

  def this(columnComparators: Map[Int, ColumnComparator[L, R]]) = {
    this(columnComparators, ColumnComparator.defaultComparator[L, R]())
  }

  def compareDatasets[DL <: InputDataset[L], DR <: InputDataset[R]](leftDataset: DL, rightDataset: DR): List[DiffResult] = {
    val leftRows: Seq[Seq[L]] = leftDataset.extractDataRows()
    val rightRows: Seq[Seq[R]] = rightDataset.extractDataRows()

    val columnDiff: Diff[L, R] = new Diff[L, R]() {
      override def compareValue(leftValue: Option[L], rightValue: Option[R], leftIndex: Int, rightIndex: Int): Boolean = {
        val columnNumber = math.min(leftIndex, rightIndex)
        val columnComparator: ColumnComparator[L, R] = columnComparators.getOrElse(columnNumber, defaultColumnComparator)
        val comparisonResult: ComparisonResult = columnComparator.compareColumn(leftValue, rightValue)
        comparisonResult.isMatched
      }
    }
    val diff: Diff[Seq[L], Seq[R]] = new Diff[Seq[L], Seq[R]]() {
      override def compareValue(leftRow: Option[Seq[L]], rightRow: Option[Seq[R]], leftRowNumber: Int, rightRowNumber: Int): Boolean = {
        val leftValues: Seq[L] = leftRow.getOrElse(Seq.empty)
        val rightValues: Seq[R] = rightRow.getOrElse(Seq.empty)
        val columnCount: Int = math.max(leftValues.size, rightValues.size)
        (0 until columnCount).forall(columnIndex => {
          val leftColumn = leftValues.lift(columnIndex)
          val rightColumn = rightValues.lift(columnIndex)
          columnDiff.compareValue(leftColumn, rightColumn, columnIndex, columnIndex)
        })
      }
    }
    diff.difference(leftRows.toArray, rightRows.toArray)
  }
}
