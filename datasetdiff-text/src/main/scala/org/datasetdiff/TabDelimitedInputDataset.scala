package org.datasetdiff

import scala.collection.immutable.List

import java.lang.String
import java.io.InputStream

/**
 * @author agustafson
 */
class TabDelimitedInputDataset(inputStream: InputStream) extends AbstractTextInputDataset(inputStream) {
  override protected def splitLine(line: String): List[String] = {
    line.split("\t").toList
  }
}