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
    val leftRows = leftDataset.extractDataRows()
    val rightRows = rightDataset.extractDataRows()

    val columnDiff: Diff[L, R] = new Diff[L, R]() {
      override def compareValue(leftValue: Option[L], rightValue: Option[R], leftIndex: Int, rightIndex: Int): Boolean = {
        val columnNumber = math.min(leftIndex, rightIndex)
        val columnComparator = columnComparators.getOrElse(columnNumber, defaultColumnComparator)
        columnComparator.compareColumn(leftValue, rightValue).isMatched
      }
    }
    val diff: Diff[Seq[L], Seq[R]] = new Diff[Seq[L], Seq[R]]() {
      override def compareValue(leftRow: Option[Seq[L]], rightRow: Option[Seq[R]], leftRowNumber: Int, rightRowNumber: Int): Boolean = {
        val leftValues = leftRow.getOrElse(Seq.empty)
        val rightValues = rightRow.getOrElse(Seq.empty)
        val columnCount = math.max(leftValues.size, rightValues.size)
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
