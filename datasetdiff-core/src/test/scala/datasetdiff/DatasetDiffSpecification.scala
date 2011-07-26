package datasetdiff

import org.specs.Specification
import org.specs.mock.Mockito
import org.specs.runner.JUnit4

/**
 * Specification for DatasetDiff.
 *
 * @author agustafson
 */
class DatasetDiffTest extends JUnit4(DatasetDiffSpecification)

object DatasetDiffSpecification extends Specification with Mockito {
  "DatasetDiff" should {
    "compare 2 datasets" in {
      val intConverter = (input: String) => Integer.parseInt(input)
      val stringConverter = (input: String) => input
      val ignoreCaseComparator = (left: String, right: String) => left.equalsIgnoreCase(right)

      val intColumnComparator = new ConvertingColumnComparator[Int, String, String](intConverter, intConverter)
      val stringColumnComparator = new ConvertingColumnComparator[String, String, String](stringConverter, stringConverter, ignoreCaseComparator)

      val columnComparators: Map[Int, ColumnComparator[String, String]] = Map(
        0 -> intColumnComparator,
        1 -> stringColumnComparator
      )

      val leftDataset = mock[InputDataset[String]]
      leftDataset.extractDataRows returns Seq(
        Seq("1", "a"),
        Seq("2", "b"),
        Seq("3", "c")
      )
      val rightDataset = mock[InputDataset[String]]
      rightDataset.extractDataRows returns Seq(
        Seq("1", "A"),
        Seq("2", "B"),
        Seq("3", "C")
      )

      val datasetDiff = new DatasetDiff[String, String](columnComparators)

      val comparisonResults = datasetDiff.compareDatasets(leftDataset, rightDataset)
      comparisonResults must beEmpty
    }

    "compare 2 datasets with expected failure" in {
      val intConverter = (input: String) => Integer.parseInt(input)
      val stringConverter = (input: String) => input
      val ignoreCaseComparator = (left: String, right: String) => left.equalsIgnoreCase(right)

      val intColumnComparator = new ConvertingColumnComparator[Int, String, String](intConverter, intConverter)
      val stringColumnComparator = new ConvertingColumnComparator[String, String, String](stringConverter, stringConverter, ignoreCaseComparator)

      val columnComparators: Map[Int, ColumnComparator[String, String]] = Map(
        0 -> intColumnComparator,
        1 -> stringColumnComparator
      )

      val leftDataset = mock[InputDataset[String]]
      leftDataset.extractDataRows returns Seq(
        Seq("1", "a"),
        Seq("2", "b"),
        Seq("4", "d"),
        Seq("5", "e")
      )
      val rightDataset = mock[InputDataset[String]]
      rightDataset.extractDataRows returns Seq(
        Seq("1", "A"),
        Seq("2", "B"),
        Seq("3", "C"),
        Seq("4", "D")
      )

      val datasetDiff = new DatasetDiff[String, String](columnComparators)

      val comparisonResults = datasetDiff.compareDatasets(leftDataset, rightDataset)
      val expectedResults = Seq[DiffResult](
        new DiffResult(2, DifferenceSide.RIGHT, Some(Seq("3", "C"))),
        new DiffResult(3, DifferenceSide.LEFT, Some(Seq("5", "e")))
      )
      comparisonResults must haveTheSameElementsAs(expectedResults)
    }
  }
}