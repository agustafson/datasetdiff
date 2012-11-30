package datasetdiff

import scala.collection.mutable.ListBuffer

import java.io.InputStream
import org.apache.poi.hssf.usermodel.{HSSFSheet, HSSFCell, HSSFWorkbook}
import collection.JavaConversions.JIteratorWrapper

/**
 * @author agustafson
 */
protected abstract class ExcelInputDataset(private val inputStream: InputStream)
  extends InputDataset[HSSFCell]
{
  private lazy val workbook: HSSFWorkbook = new HSSFWorkbook(inputStream)
  private lazy val worksheet = sheet(workbook)

  def extractDataRows(): Seq[Seq[HSSFCell]] = {
    val rowBuffer = new ListBuffer[Seq[HSSFCell]]()

    for (val rowIndex <- getFirstRowNumber() to getLastRowNum();
         val excelRow = worksheet.getRow(rowIndex)
    ) {
      val row = for (cell <- new JIteratorWrapper(excelRow.cellIterator))
        yield cell.asInstanceOf[HSSFCell]
      rowBuffer += row.toSeq
    }
    rowBuffer.toSeq
  }

  protected def sheet(workbook: HSSFWorkbook): HSSFSheet

  protected def getFirstRowNumber: Int = {
    worksheet.getFirstRowNum
  }

  protected def getLastRowNum: Int = {
    worksheet.getLastRowNum
  }
}

object ExcelInputDataset {
  def apply(inputStream: InputStream, sheetName: String) = new ExcelInputDataset(inputStream) {
    protected def sheet(workbook: HSSFWorkbook): HSSFSheet = workbook.getSheet(sheetName)
  }
  
  def apply(inputStream: InputStream, sheetIndex: Int) = new ExcelInputDataset(inputStream) {
    protected def sheet(workbook: HSSFWorkbook): HSSFSheet = workbook.getSheetAt(sheetIndex)
  }
}
