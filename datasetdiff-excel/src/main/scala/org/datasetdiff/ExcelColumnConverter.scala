package org.datasetdiff

import java.util.Date
import org.apache.poi.hssf.usermodel.HSSFCell

/**
 * {@linkplain ColumnConverter} for excel files.
 *
 * @author agustafson
 */
trait ExcelColumnConverter[O] extends ColumnConverter[HSSFCell, O]

object ExcelColumnConverter {
  val Boolean: (HSSFCell => Boolean) = ((cell: HSSFCell) => cell.getBooleanCellValue)
  
  val Date: (HSSFCell => Date) = ((cell: HSSFCell) => cell.getDateCellValue)

  val Number: (HSSFCell => BigDecimal) = ((cell: HSSFCell) => BigDecimal(cell.getNumericCellValue))

  val String: (HSSFCell => String) = ((cell: HSSFCell) => cell.getStringCellValue)
}
