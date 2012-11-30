package datasetdiff

import resource._
import java.sql.{Connection, PreparedStatement, ResultSet}

/**
 * @author agustafson
 */
object JdbcExecutor {
  private def setArguments(preparedStatement: PreparedStatement, arguments: Any*) {
    var argumentIndex = 1
    for (argument <- arguments) {
      preparedStatement.setObject(argumentIndex, argument)
      argumentIndex += 1
    }
  }

  def executeUpdate(preparedStatement: PreparedStatement, arguments: Any*): Int = {
    setArguments(preparedStatement, arguments: _*)
    preparedStatement.executeUpdate()
  }

  def executeUpdate(sql: String, arguments: Any*)(implicit connection: Connection): Int = {
    managed(connection.prepareStatement(sql)) acquireAndGet {
      preparedStatement => {
        executeUpdate(preparedStatement, arguments: _*)
      }
    }
  }

  def executeQuery[T](preparedStatement: PreparedStatement, arguments: Any*)(resultSetHandler: ResultSet => T): T = {
    setArguments(preparedStatement, arguments: _*)
    managed(preparedStatement.executeQuery()) acquireAndGet {
      resultSet => {
        resultSetHandler(resultSet)
      }
    }
  }

  def executeQuery[T](sql: String, arguments: Any*)(resultSetHandler: ResultSet => T)(implicit connection: Connection): T = {
    managed(connection.prepareStatement(sql)) acquireAndGet {
      preparedStatement => {
        executeQuery(preparedStatement, arguments: _*)(resultSetHandler)
      }
    }
  }

  def ignore[O](f: () => O): Option[O] = {
    try {
      Some(f())
    }
    catch {
      case _: Throwable => None
    }
  }
}
