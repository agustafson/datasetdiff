package datasetdiff

import math.BigDecimal

/**
 * @author agustafson
 */
class JdbcColumnConverter[+T] extends ColumnConverter[AnyRef, T] {
  def apply(input: AnyRef): T = input.asInstanceOf[T]
}

object JdbcColumnConverters {
  def NumberConverter(): (String => BigDecimal) = ((input: AnyRef) => BigDecimal(input.toString))
}
