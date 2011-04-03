package org.datasetdiff

import org.specs.Specification
import org.specs.runner.JUnit4
import java.util.Date
import org.apache.poi.hssf.usermodel.HSSFCell
import java.text.SimpleDateFormat
import java.sql.{Timestamp, ResultSet, SQLException, DriverManager}

class DatasetDiffSystemTest extends JUnit4(DatasetDiffSystemTest)

/**
 * @author agustafson
 */
object DatasetDiffSystemTest extends Specification {
  val classLoader: ClassLoader = this.getClass.getClassLoader
  val databaseName: String = "jdbcTest";
  val baseConnectionUrl: String = "jdbc:derby:" + databaseName
  val jdbcExecutor = new JdbcExecutor(baseConnectionUrl + ";create=true")

  doBeforeSpec {
    jdbcExecutor.executeUpdate("CREATE TABLE SIMPLE (COL_VARCHAR VARCHAR(1), COL_NUMBER REAL, COL_DATE TIMESTAMP)")
  }

  doAfterSpec {
    jdbcExecutor.ignore {
      () => jdbcExecutor.executeUpdate("DROP TABLE SIMPLE")
    }
    jdbcExecutor.close
    // shutdown
    var gotSQLExc = false;
    try {
      DriverManager.getConnection("jdbc:derby:;shutdown=true");
    } catch {
      case se: SQLException =>
        if (se.getSQLState().equals("XJ015")) {
          gotSQLExc = true;
        }
    }
    if (!gotSQLExc) {
      System.out.println("Database did not shut down normally");
    }
  }

  "DatasetDiff" should {
    "compare 2 simple text files successfully" in {
      val fileName1 = "simple1.txt"
      val fileName2 = "simple2.txt"
      val textFile1 = new TabDelimitedInputDataset(classLoader.getResourceAsStream(fileName1))
      val textFile2 = new TabDelimitedInputDataset(classLoader.getResourceAsStream(fileName2))

      val stringConverter: (String) => String = TextColumnConverters.StringConverter()
      val numberConverter: (String) => BigDecimal = TextColumnConverters.NumberConverter()

      val stringColumnComparator = new ConvertingColumnComparator[String, String, String](stringConverter, stringConverter)
      val numberColumnComparator = new ConvertingColumnComparator[BigDecimal, String, String](numberConverter, numberConverter)

      val dateValueComparator = (left: Date, right: Date) => new DateMatcher("yyyyMMdd", left).matches(right)
      val dateColumnComparator = new ConvertingColumnComparator[Date, String, String](
        TextColumnConverters.DateConverter("dd/MM/yyyy hh:mm"),
        TextColumnConverters.DateConverter("yyyyMMdd"),
        dateValueComparator)

      val columnComparators = Map(
        0 -> stringColumnComparator,
        1 -> numberColumnComparator,
        2 -> dateColumnComparator
      )

      val datasetDiff = new DatasetDiff[String, String](columnComparators)

      val comparisons: List[Array[ComparisonResult]] = datasetDiff.compareDatasets(textFile1, textFile2)
      comparisons.length mustBe 3
      for (rowComparison <- comparisons; cellComparison <- rowComparison) {
        cellComparison must(haveClass[MatchedComparisonResult])
      }
    }

    "compare a simple text and excel file successfully" in {
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

      val datasetDiff = new DatasetDiff[String, HSSFCell](columnComparators)

      val comparisons: List[Array[ComparisonResult]] = datasetDiff.compareDatasets(textFile, excelFile)
      comparisons.length mustBe 3
      for (rowComparison <- comparisons; cellComparison <- rowComparison) {
        cellComparison must(haveClass[MatchedComparisonResult])
      }
    }

    "compare a simple text file and database table" in {
      val textFileName = "simple1.txt"
      val textFileDataset = new TabDelimitedInputDataset(classLoader.getResourceAsStream(textFileName))

      val dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm")
      val insertSql = "INSERT INTO SIMPLE (COL_VARCHAR, COL_NUMBER, COL_DATE) VALUES (?,?,?)"
      jdbcExecutor.executeUpdate(insertSql, "a", 1, new Timestamp(dateFormat.parse("24/12/2009 13:50").getTime))
      jdbcExecutor.executeUpdate(insertSql, "b", 2.1, null)
      jdbcExecutor.executeUpdate(insertSql, "c", 3.0, new Timestamp(dateFormat.parse("30/11/2008 02:32").getTime))
      jdbcExecutor.executeQuery("SELECT * FROM SIMPLE") {
        (resultSet: ResultSet) => {
          val simpleDbTableDataset = new JdbcInputDataset(resultSet)

          val stringTextConverter: (String) => String = TextColumnConverters.StringConverter()
          val numberTextConverter: (String) => BigDecimal = TextColumnConverters.NumberConverter()
          val stringDbConverter: (AnyRef) => String = (input: AnyRef) => input.toString
          val numberDbConverter: (AnyRef) => BigDecimal = (input: AnyRef) => BigDecimal(input.toString)

          val stringColumnComparator = new ConvertingColumnComparator[String, String, AnyRef](stringTextConverter, stringDbConverter)
          val numberColumnComparator = new ConvertingColumnComparator[BigDecimal, String, AnyRef](numberTextConverter, numberDbConverter)

          val dateValueComparator = (left: Date, right: Date) => new DateMatcher("yyyyMMdd", left).matches(right)
          val dateColumnComparator = new ConvertingColumnComparator[Date, String, AnyRef](
            TextColumnConverters.DateConverter("dd/MM/yyyy hh:mm"),
            new JdbcColumnConverter[Date],
            dateValueComparator)

          val columnComparators = Map(
            0 -> stringColumnComparator,
            1 -> numberColumnComparator,
            2 -> dateColumnComparator
          )

          val datasetDiff = new DatasetDiff[String, AnyRef](columnComparators)

          val comparisons: List[Array[ComparisonResult]] = datasetDiff.compareDatasets(textFileDataset, simpleDbTableDataset)
          comparisons.length mustBe 3
          for (rowComparison <- comparisons; cellComparison <- rowComparison) {
            cellComparison must(haveClass[MatchedComparisonResult])
          }
        }
      }
    }
  }
}