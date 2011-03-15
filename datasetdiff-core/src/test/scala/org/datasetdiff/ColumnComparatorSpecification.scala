package org.datasetdiff

import org.specs.{ScalaCheck, Specification}
import org.specs.runner.JUnit4

/**
 * Tests for ColumnComparator.
 *
 * @author: agustafson
 */
class ColumnComparatorTest extends JUnit4(ColumnComparatorSpecification)

object ColumnComparatorSpecification extends Specification with ScalaCheck {
  "ColumnComparator" should {
    "compare strings" in {
      ColumnComparator.defaultComparator[String, Char].compareColumn(Some("a"), Some('a')).isMatched mustBe true
      ColumnComparator.defaultComparator[String, String].compareColumn(Some("a"), Some("A")).isMatched mustBe false
      ColumnComparator.defaultComparator[Char, Char].compareColumn(Some('a'), Some('b')).isMatched mustBe false
    }

    "compare numbers" in {
      ColumnComparator.defaultComparator[Long, Int].compareColumn(Some(3), Some(3)).isMatched mustBe true
      ColumnComparator.defaultComparator[String, Int].compareColumn(Some("2"), Some(2)).isMatched mustBe true
      ColumnComparator.defaultComparator[Long, BigDecimal].compareColumn(Some(5), Some(BigDecimal(5))).isMatched mustBe true
      ColumnComparator.defaultComparator[Long, Int].compareColumn(Some(2), Some(3)).isMatched mustBe false
    }
  }
}