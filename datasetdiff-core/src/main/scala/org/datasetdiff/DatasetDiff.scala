package org.datasetdiff

import collection.mutable.ListBuffer

/**
 * @author: agustafson
 */
class DatasetDiff[L,R](columnComparators: Map[Int, ColumnComparator[L,R]], defaultColumnComparator: ColumnComparator[L,R]) {

  def this(columnComparators: Map[Int, ColumnComparator[L,R]]) = {
    this(columnComparators, ColumnComparator.defaultComparator[L,R]())
  }

  def compareDatasets[DL <: InputDataset[L], DR <: InputDataset[R]](leftDataset: DL, rightDataset: DR): List[Array[ComparisonResult]] = {
    val leftRows: Seq[Seq[L]] = leftDataset.extractDataRows
    val rightRows: Seq[Seq[R]] = rightDataset.extractDataRows

    val numberOfRows: Int = scala.math.max(leftRows.size, rightRows.size)

    val rowComparisonResults = new ListBuffer[Array[ComparisonResult]]()

    // scroll through each of the rows from both sources
    for (rowNumber <- 0 until numberOfRows) {
      val leftRow: Seq[L] = leftRows(rowNumber)
      val rightRow: Seq[R] = rightRows(rowNumber)

      val numberOfColumns: Int = scala.math.max(leftRow.size, rightRow.size)
      val rowComparison = Array.ofDim[ComparisonResult](numberOfColumns)
      for (columnNumber <- 0 until numberOfColumns) {
        val leftCell: Option[L] = leftRow.lift(columnNumber)
        val rightCell: Option[R] = rightRow.lift(columnNumber)

        val columnComparator: ColumnComparator[L,R] = columnComparators.getOrElse(columnNumber, defaultColumnComparator)
        val comparisonResult: ComparisonResult = columnComparator.compareColumn(leftCell, rightCell)

        rowComparison.update(columnNumber, comparisonResult)
      }

      rowComparisonResults += rowComparison
    }
    return rowComparisonResults.toList
  }
}
