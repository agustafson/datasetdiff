package org.datasetdiff

object Predefs {
  trait DateOrdering extends Ordering[java.util.Date] {
    def compare(x: java.util.Date, y: java.util.Date) = x.compareTo(y)
  }
  implicit object Date extends DateOrdering
}
