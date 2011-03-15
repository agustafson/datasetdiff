package org.datasetdiff

import java.util.Date
import java.text.DateFormat

/**
 * Text columns converters.
 *
 * @author: agustafson
 */
object TextColumnConverters {
  def BooleanConverter(): (String => Boolean) = ((cell: String) => cell.toBoolean)

  def DateConverter(dateFormat: DateFormat = DateFormat.getInstance()): (String => Date) = ((cell: String) => dateFormat.parse(cell))

  def NumberConverter(): (String => BigDecimal) = ((cell: String) => BigDecimal.apply(cell))

  def StringConverter(): (String => String) = ((cell: String) => cell)
}
