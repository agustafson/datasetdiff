package org.datasetdiff

import org.specs.Specification
import org.specs.runner.JUnit4
import java.util.Date
import org.apache.poi.hssf.usermodel.HSSFCell

class DatasetDiffTest extends JUnit4(DatasetDiffTest)

/**
 * @author: agustafson
 */
object DatasetDiffTest extends Specification {
  "DatasetDiff" should {
    "compare 2 simple text files successfully" in {
      val classLoader: ClassLoader = this.getClass.getClassLoader

      val fileName1 = "simple1.txt"
      val fileName2 = "simple2.txt"
      val textFile1 = new TabDelimitedInputDataset(classLoader.getResourceAsStream(fileName1))
      val textFile2 = new TabDelimitedInputDataset(classLoader.getResourceAsStream(fileName2))

      val stringConverter: (String) => String = TextColumnConverters.StringConverter()
      val numberConverter: (String) => BigDecimal = TextColumnConverters.NumberConverter()

      val stringColumnComparator = new ConvertingColumnComparator[String, String, String](stringConverter, stringConverter)
      val numberColumnComparator = new ConvertingColumnComparator[BigDecimal, String, String](numberConverter, numberConverter)
      
      val dateValueComparator = (left: Date, right: Date) => new DateMatcher("yyyyMMdd", left).matches(right)
      val dateColumnComparator   = new ConvertingColumnComparator[Date, String, String](
        TextColumnConverters.DateConverter("dd/MM/yyyy hh:mm"),
        TextColumnConverters.DateConverter("yyyyMMdd"),
        dateValueComparator)

      val columnComparators = Map(
        0 -> stringColumnComparator,
        1 -> numberColumnComparator,
        2 -> dateColumnComparator
      )

      val datasetDiff = new DatasetDiff[String,String](columnComparators)

      val comparisons: List[Array[ComparisonResult]] = datasetDiff.compareDatasets(textFile1, textFile2)
      comparisons.length mustBe 3
      for (rowComparison <- comparisons; cellComparison <- rowComparison) {
        cellComparison must(haveClass[MatchedComparisonResult])
      }
    }

    "compare a simple text and excel file successfully" in {
      val classLoader: ClassLoader = this.getClass.getClassLoader

      val textFileName = "simple1.txt"
      val excelFileName = "simple1.xls"
      val textFile = new TabDelimitedInputDataset(classLoader.getResourceAsStream(textFileName))
      val excelFile = ExcelInputDataset(classLoader.getResourceAsStream(excelFileName), 0)

      val stringTextConverter: (String) => String = TextColumnConverters.StringConverter()
      val numberTextConverter: (String) => BigDecimal = TextColumnConverters.NumberConverter()
      val stringExcelConverter: (HSSFCell) => String = ExcelColumnConverter.String
      val numberExcelConverter: (HSSFCell) => BigDecimal = ExcelColumnConverter.Number

      val stringColumnComparator = new ConvertingColumnComparator[String, String, HSSFCell](stringTextConverter, stringExcelConverter)
      val numberColumnComparator = new ConvertingColumnComparator[BigDecimal, String, HSSFCell](numberTextConverter, numberExcelConverter)

      val dateValueComparator = (left: Date, right: Date) => new DateMatcher("yyyyMMdd", left).matches(right)
      val dateColumnComparator = new ConvertingColumnComparator[Date, String, HSSFCell](
        TextColumnConverters.DateConverter("dd/MM/yyyy hh:mm"),
        ExcelColumnConverter.Date,
        dateValueComparator)

      val columnComparators = Map(
        0 -> stringColumnComparator,
        1 -> numberColumnComparator,
        2 -> dateColumnComparator
      )

      val datasetDiff = new DatasetDiff[String,HSSFCell](columnComparators)

      val comparisons: List[Array[ComparisonResult]] = datasetDiff.compareDatasets(textFile, excelFile)
      comparisons.length mustBe 3
      for (rowComparison <- comparisons; cellComparison <- rowComparison) {
        cellComparison must(haveClass[MatchedComparisonResult])
      }
    }
  }
}