package datasetdiff

import org.specs.Specification
import org.specs.runner.JUnit4

/**
 * @author agustafson
 */
class JdbcColumnConverterTest extends JUnit4(JdbcColumnConverterSpecification)

object JdbcColumnConverterSpecification extends Specification {
  "JdbcColumnConverter" should {
    "convert Long to BigDecimal" in {
      val converter = new JdbcColumnConverter[Int]
      converter(1.asInstanceOf[AnyRef]) mustEqual BigDecimal("1")
    }
  }
}