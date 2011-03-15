package org.datasetdiff

import org.datasetdiff.TextColumnConverters._
import org.specs.runner.JUnit4
import org.specs.{ScalaCheck, Specification}
import java.text.SimpleDateFormat
import java.util.Date

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
        val dateFormat = new SimpleDateFormat("dd/MM/yyyy kk:mm:ss.SSS")
        val dateConverter = DateConverter(dateFormat)
        val dateString = dateFormat.format(date)
        val convertedDate = dateConverter(dateString)
        (convertedDate compareTo date) == 0
      } else {
        true
      }
    }
  }
}
