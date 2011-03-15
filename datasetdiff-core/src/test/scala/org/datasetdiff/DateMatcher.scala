package org.datasetdiff

import java.util.Date
import org.hamcrest.{Description, TypeSafeMatcher}
import java.text.SimpleDateFormat

/**
 * Hamcrest matcher for {@linkplain Date}s.
 *
 * @author: agustafson
 */
class DateMatcher(datePattern: String, date: Date) extends TypeSafeMatcher[Date] {
  def this(date: Date) = {
    this("yyyyMMdd kk:mm:ss.SSS", date)
  }

  def matchesSafely(item: Date): Boolean = {
    val dateFormat = new SimpleDateFormat(datePattern)
    val formatLeft = dateFormat.format(date)
    val formatRight = dateFormat.format(item)
    formatLeft == formatRight
  }

  def describeTo(description: Description): Unit = {
    description.appendValue(date)
  }
}