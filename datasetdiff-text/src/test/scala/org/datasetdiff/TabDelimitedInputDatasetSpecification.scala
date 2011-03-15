package org.datasetdiff

import scala.collection.immutable.List

import java.io.ByteArrayInputStream
import java.lang.String

import org.specs.runner.JUnit4
import org.specs.Specification

/**
 * @author: agustafson
 */
class TabDelimitedInputDatasetTest extends JUnit4(TabDelimitedInputDatasetSpecification)

object TabDelimitedInputDatasetSpecification extends Specification {
  "A tab delimited parser" should {
    "import a dataset" in {
      val input: String = "a\tb\tc\n" + "d\te\tf"
      val inputStream: ByteArrayInputStream = new ByteArrayInputStream(input.getBytes())
      val inputDataset: TabDelimitedInputDataset = new TabDelimitedInputDataset(inputStream)
      val rows: List[Seq[String]] = inputDataset.extractDataRows
      rows(0) must_== List("a","b","c")
      rows(1) must_== List("d","e","f")
    }
  }
}
