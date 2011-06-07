package datasetdiff

import java.io.ByteArrayInputStream
import java.lang.String

import org.specs.runner.JUnit4
import org.specs.Specification

/**
 * @author agustafson
 */
class TabDelimitedInputDatasetTest extends JUnit4(TabDelimitedInputDatasetSpecification)

object TabDelimitedInputDatasetSpecification extends Specification {
  "A tab delimited parser" should {
    "import a dataset" in {
      val input: String = "a\tb\tc\n" + "d\te\tf"
      val inputStream: ByteArrayInputStream = new ByteArrayInputStream(input.getBytes())
      val inputDataset: TabDelimitedInputDataset = new TabDelimitedInputDataset(inputStream)
      val rows: Iterator[Seq[String]] = inputDataset.extractDataRows
      val expectedRows = List[Seq[String]](
        Seq("a","b","c"),
        Seq("d","e","f")
      )
      rows.toList must haveSameElementsAs(expectedRows)
    }
  }
}
