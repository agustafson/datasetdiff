package datasetdiff

import scala.io.Source
import java.lang.String
import java.io.InputStream
import org.apache.commons.io.LineIterator

/**
 * @author agustafson
 */
abstract class AbstractTextInputDataset(private val inputStream: InputStream)
  extends InputDataset[String]
{
  lazy val extractDataRows: Seq[Seq[String]] = {
    val lineIterator: LineIterator = new LineIterator(Source.fromInputStream(inputStream).bufferedReader())

    new Iterator[Seq[String]] {
      def next(): Seq[String] = splitLine(lineIterator.next)

      def hasNext: Boolean = lineIterator.hasNext
    }.toSeq
  }

  protected def splitLine(line: String): List[String]
}