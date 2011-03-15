package org.datasetdiff

import org.datasetdiff.TextColumnConverters._
import org.specs.runner.JUnit4
import org.specs.{ScalaCheck, Specification}
import java.util.Date
import java.text.SimpleDateFormat

/**
 * @author: agustafson
 */
class TextColumnConvertersTest extends JUnit4(TextColumnConvertersSpecification)

object TextColumnConvertersSpecification extends Specification with ScalaCheck {
  "StringConverter" verifies {
    (s: String) => StringConverter().apply(s) == s
  }

  "NumberConverter for Number" verifies {
    (n: Number) => NumberConverter().apply(n.toString) == n
  }
  "NumberConverter for Double" verifies {
    (n: Double) => NumberConverter().apply(n.toString) == n
  }

  "BooleanConverter" verifies {
    (b: Boolean) => {
      val booleanConverter = BooleanConverter()
      val booleanString = b.toString
      booleanConverter(booleanString.toLowerCase) == b &&
      booleanConverter(booleanString.toUpperCase) == b
    }
  }

  "DateConverter" verifies {
    (date: Date) => {
      if (date.getYear < 99999) {
        val datePattern = "dd/MM/yyyy kk:mm:ss.SSS"
        val dateString = new SimpleDateFormat(datePattern).format(date)
        val dateConverter = DateConverter(datePattern)
        val convertedDate = dateConverter(dateString)
        new DateMatcher(datePattern, convertedDate).matches(date)
      } else {
        true
      }
    }
  }
}
