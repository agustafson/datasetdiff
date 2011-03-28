package org.datasetdiff

/**
 * Input data set, representing a list of rows.
 *
 * @author agustafson
 */
trait InputDataset[+C]
{
  def extractDataRows(): Iterator[Seq[C]]
}
