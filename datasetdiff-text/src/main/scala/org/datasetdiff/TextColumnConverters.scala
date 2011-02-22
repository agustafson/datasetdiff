package org.datasetdiff

import java.util.Date
import java.text.DateFormat

/**
 * @author: agustafson
 */
trait TextColumnConverter[O] extends ColumnConverter[String, O]

object TextColumnConverters {
  val BooleanConverter: (String => Boolean) = ((cell: String) => cell.toBoolean)

  def DateConverter(dateFormat: DateFormat = DateFormat.getInstance()): (String => Date) = ((cell: String) => dateFormat.parse(cell))

  val NumberConverter: (String => BigDecimal) = ((cell: String) => BigDecimal.apply(cell))

  val StringConverter: (String => String) = ((cell: String) => cell)
}
