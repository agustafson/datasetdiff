package datasetdiff

import scala.collection.JavaConversions._

/**
 * Builder for comparing datasets.
 *
 * @author agustafson
 */
class DatasetDiffBuilder[L, R] {

  var columnComparators: collection.mutable.Map[Int, ColumnComparator[L, R]] = new collection.mutable.HashMap[Int, ColumnComparator[L, R]]()
  var defaultColumnComparator: ColumnComparator[L, R] = ColumnComparator.defaultComparator[L, R]()

  /**
   * Add a ColumnComparator.
   */
  def withColumnComparator(columnNumber: Int, columnComparator: ColumnComparator[L, R]): DatasetDiffBuilder[L, R] = {
    columnComparators.put(columnNumber, columnComparator)
    this
  }

  /**
   * Compare the 2 datasets.
   */
  def compare[DL <: InputDataset[L], DR <: InputDataset[R]](leftDataset: DL, rightDataset: DR): java.util.List[DiffResult] = {
    val datasetDiff: DatasetDiff[L, R] = new DatasetDiff[L, R](columnComparators.toMap, defaultColumnComparator)
    val comparisonResults: List[DiffResult] = datasetDiff.compareDatasets[DL, DR](leftDataset, rightDataset)
    asJavaList[DiffResult](comparisonResults)
  }
}
