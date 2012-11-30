package datasetdiff

import java.io.InputStream
import org.apache.poi.hssf.usermodel.{HSSFSheet, HSSFCell, HSSFWorkbook}
import scala.collection.JavaConversions._

/**
 * @author agustafson
 */
protected abstract class ExcelInputDataset(private val inputStream: InputStream)
  extends InputDataset[HSSFCell]
{
  private lazy val workbook: HSSFWorkbook = new HSSFWorkbook(inputStream)
  private lazy val worksheet = sheet(workbook)

  def extractDataRows(): Seq[Seq[HSSFCell]] =
    for {
      rowIndex <- firstRowNumber to lastRowNum
      excelRow = worksheet.getRow(rowIndex)
    } yield excelRow.map(_.asInstanceOf[HSSFCell]).toSeq

  protected def sheet(workbook: HSSFWorkbook): HSSFSheet

  protected def firstRowNumber: Int = {
    worksheet.getFirstRowNum
  }

  protected def lastRowNum: Int = {
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
