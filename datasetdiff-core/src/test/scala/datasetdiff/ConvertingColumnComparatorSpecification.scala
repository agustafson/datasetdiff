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
    "compare 2 vals" in {
      val string2IntConverter: (String) => Int = (cell: String) => (Integer.parseInt(cell))

      val columnComparator: ConvertingColumnComparator[Int, String, String] =
        new ConvertingColumnComparator[Int, String, String](string2IntConverter, string2IntConverter)

      columnComparator.compareColumn(Some("3"), Some("3")) must haveClass[MatchedComparisonResult]
      columnComparator.compareColumn(None, None) must haveClass[MatchedComparisonResult]
      columnComparator.compareColumn(Some("3"), Some("2")) must haveClass[UnmatchedComparisonResult[_]]
      columnComparator.compareColumn(Some("3"), None) must haveClass[UnmatchedComparisonResult[_]]
    }
  }
}
