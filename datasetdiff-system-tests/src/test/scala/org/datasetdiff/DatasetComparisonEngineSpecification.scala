package org.datasetdiff

import org.specs.Specification
import org.specs.runner.JUnit4
import java.text.SimpleDateFormat
import java.util.Date

class DatasetComparisonEngineSpecification extends JUnit4(DatasetComparisonEngineSpecification)

/**
 * @author: agustafson
 */
object DatasetComparisonEngineSpecification extends Specification {
  "datacomp" should {
    "compare 2 simple text files successfully" in {
      val classLoader: ClassLoader = this.getClass.getClassLoader

      val fileName1 = "simple1.txt"
      val fileName2 = "simple2.txt"
      val textFile1 = new TabDelimitedInputDataset(classLoader.getResourceAsStream(fileName1))
      val textFile2 = new TabDelimitedInputDataset(classLoader.getResourceAsStream(fileName2))

      val columnTypes: Map[Int, ClassManifest[_]] = Map(
        1 -> classManifest[String],
        2 -> classManifest[BigDecimal]
      )
      val stringConverter: (String) => String = TextColumnConverters.StringConverter
      val numberConverter: (String) => BigDecimal = TextColumnConverters.NumberConverter

      val stringColumnComparator = ConvertingColumnComparator[String, String, String](stringConverter, stringConverter)
      val numberColumnComparator = ConvertingColumnComparator[BigDecimal, String, String](numberConverter, numberConverter)
      
      val dateValueComparator = (left: Date, right: Date) => {
          val dateFormat = new SimpleDateFormat("yyyyMMdd")
          dateFormat.format(left) == dateFormat.format(right)
      }
      val dateColumnComparator   = ConvertingColumnComparator[Date, String, String](
        TextColumnConverters.DateConverter(new SimpleDateFormat("dd/MM/yyyy hh:mm")),
        TextColumnConverters.DateConverter(new SimpleDateFormat("yyyyMMdd")),
        dateValueComparator)

      val columnComparators = Map(
        0 -> stringColumnComparator,
        1 -> numberColumnComparator,
        2 -> dateColumnComparator
      )

      val engine = new DatasetComparisonEngine[String,String](columnComparators)

      val comparisons: List[Array[ComparisonResult]] = engine.compareDatasets(textFile1, textFile2)
      comparisons.length mustBe 3
      for (rowComparison <- comparisons; cellComparison <- rowComparison) {
        println(cellComparison)
        cellComparison.isMatched mustBe true
      }
    }
  }
  
}