package org.datasetdiff

import java.sql.{ResultSetMetaData, ResultSet}

/**
 * @author agustafson
 */
class JdbcInputDataset(private val resultSet: ResultSet) extends InputDataset[AnyRef] {
  val resultSetMetaData: ResultSetMetaData = resultSet.getMetaData
  val columnCount: Int = resultSetMetaData.getColumnCount

  def extractDataRows(): Iterator[Seq[AnyRef]] = {
    new Iterator[Seq[AnyRef]] {
      def next(): Seq[AnyRef] = {
        for (columnNumber <- 1 to columnCount)
        yield {
          resultSet.getObject(columnNumber)
        }
      }

      def hasNext: Boolean = resultSet.next
    }
  }
}
