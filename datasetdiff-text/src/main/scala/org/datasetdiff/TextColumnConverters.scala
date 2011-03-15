package org.datasetdiff

import java.util.Date
import java.text.SimpleDateFormat

/**
 * Text columns converters.
 *
 * @author: agustafson
 */
object TextColumnConverters {
  def BooleanConverter(): (String => Boolean) = ((cell: String) => cell.toBoolean)

  def DateConverter(datePattern: String): (String => Date) = ((cell: String) => new SimpleDateFormat(datePattern).parse(cell))

  def NumberConverter(): (String => BigDecimal) = ((cell: String) => BigDecimal.apply(cell))

  def StringConverter(): (String => String) = ((cell: String) => cell)
}
