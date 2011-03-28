package org.datasetdiff

import org.specs.Specification
import org.apache.poi.hssf.usermodel.{HSSFCell, HSSFRow, HSSFSheet, HSSFWorkbook}
import java.io.{ByteArrayInputStream, ByteArrayOutputStream}

/**
 * @author agustafson
 */
object ExcelInputDatasetSpecification extends Specification {
  "ExcelInputDataset" should {
    "import a dataset" in {
      val workbook: HSSFWorkbook = new HSSFWorkbook()
      val worksheet: HSSFSheet = workbook.createSheet()

      val row0: HSSFRow = worksheet.createRow(0)
      val row0column0: HSSFCell = row0.createCell(0)
      row0column0.setCellValue("a")

      val byteArrayOutputStream: ByteArrayOutputStream = new ByteArrayOutputStream()
      workbook.write(byteArrayOutputStream)
      val inputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray)

      val inputDataset = ExcelInputDataset(inputStream, 0)
      val rows: Iterator[Seq[HSSFCell]] = inputDataset.extractDataRows
      ExcelColumnConverter.String(rows.next()(0)) must_== "a"
    }
  }
}