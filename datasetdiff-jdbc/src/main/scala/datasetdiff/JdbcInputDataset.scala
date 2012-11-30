package datasetdiff

import java.sql.{ResultSetMetaData, ResultSet}

/**
 * @author agustafson
 */
class JdbcInputDataset(private val resultSet: ResultSet) extends InputDataset[AnyRef] {
  val resultSetMetaData: ResultSetMetaData = resultSet.getMetaData
  val columnCount: Int = resultSetMetaData.getColumnCount

  def extractDataRows(): Seq[Seq[AnyRef]] = {
    new Iterator[Seq[AnyRef]] {
      var hasTakenNext = false;
      var hasNextCached = false;

      def next(): Seq[AnyRef] = {
        if (!hasTakenNext) {
          resultSet.next();
        }
        hasTakenNext = false;

        for (columnNumber <- 1 to columnCount)
        yield {
          resultSet.getObject(columnNumber)
        }
      }

      def hasNext: Boolean = {
        if (!hasTakenNext) {
          hasNextCached = resultSet.next();
          hasTakenNext = true;
        }
        return hasNextCached;
      }
    }.toSeq
  }
}
