package datasetdiff

import org.specs.Specification
import org.specs.runner.JUnit4
import java.lang.String

/**
 * Tests for ConvertingColumnComparator.
 *
 * @author agustafson
 */
class ConvertingColumnComparatorTest extends JUnit4(ConvertingColumnComparatorSpecification)

object ConvertingColumnComparatorSpecification extends Specification {
  "ConvertingColumnComparator" should {
    val string2IntConverter: (String) => Int = (cell: String) => (Integer.parseInt(cell))
    val columnComparator: ConvertingColumnComparator[Int, String, String] =
      new ConvertingColumnComparator[Int, String, String](string2IntConverter, string2IntConverter)

    "compare 2 same values" in {
      columnComparator.compareColumn(Some("3"), Some("3")) must_== MatchedComparisonResult
      columnComparator.compareColumn(None, None) must_== MatchedComparisonResult
    }

    /*
    // TODO: uncomment test: compare 2 different values
    "compare 2 different values" in {
      columnComparator.compareColumn(Some("3"), Some("2")) mustBe (
        UnmatchedComparisonResult(Some(SuccessfulConversionResult("3")), Some(SuccessfulConversionResult("2")))
      )
    }
    */

    /*
    // TODO: uncomment test: compare a value with nothing
    "compare a value with nothing" in {
      columnComparator.compareColumn(Some("3"), None) mustBe (
        UnmatchedComparisonResult(Some(SuccessfulConversionResult("3")), None)
      )
    }
    */
  }
}
