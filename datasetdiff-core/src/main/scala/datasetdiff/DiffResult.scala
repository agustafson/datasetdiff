package datasetdiff

/**
 * Comparison result for an index, side and value.
 */
case class DiffResult(elementNumber: Int, side: DifferenceSide.Value, value: Option[_]) extends Comparable[DiffResult]{
  override def compareTo(other: DiffResult): Int = {
    var result: Int = this.elementNumber - other.elementNumber
    if (result == 0) {
      result = this.side.compare(other.side)
    }
    return result
  }
}
