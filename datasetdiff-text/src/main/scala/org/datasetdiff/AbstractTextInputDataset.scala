package org.datasetdiff

import scala.io.Source
import scala.collection.mutable.ListBuffer

import java.lang.String
import java.io.InputStream

/**
 * @author agustafson
 */
abstract class AbstractTextInputDataset(private val inputStream: InputStream)
  extends InputDataset[String]
{
  lazy val extractDataRows: List[Seq[String]] = {
    val source = Source.fromInputStream(inputStream)

    val buffer = ListBuffer[Seq[String]]()
    for (line <- source.getLines) {
      buffer += splitLine(line)
    }
    buffer.toList
  }

  protected def splitLine(line: String): List[String]
}