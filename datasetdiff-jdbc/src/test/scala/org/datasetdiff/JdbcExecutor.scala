package org.datasetdiff

import java.sql.{DriverManager, PreparedStatement, ResultSet}

/**
 * @author agustafson
 */
class JdbcExecutor(connectionUrl: String) {
  val connection = DriverManager.getConnection(connectionUrl)

  def executeUpdate(sql: String, arguments: Any*): Int = {
    val preparedStatement: PreparedStatement = prepareStatement(sql, arguments.toArray)
    withStatement(preparedStatement, (statement: PreparedStatement) => {
      statement.executeUpdate()
    })
  }

  def executeQuery(sql: String, arguments: Any*)(resultSetHandler: ResultSet => Unit) {
    val preparedStatement: PreparedStatement = prepareStatement(sql, arguments.toArray)
    withStatement(preparedStatement, (statement: PreparedStatement) => {
      val resultSet = statement.executeQuery()
      resultSetHandler(resultSet)
    })
  }

  def close() {
    ignore {
      connection.close
    }
  }

  def ignore[O](f: () => O): Option[O] = {
    try {
      Some(f())
    }
    catch {
      case _ => None
    }
  }

  private def withStatement[O](statement: PreparedStatement, f: (PreparedStatement) => O): O = {
    try {
      f(statement)
    }
    finally {
      statement.close
    }
  }

  private def prepareStatement(sql: String, arguments: Array[Any]): PreparedStatement = {
    val preparedStatement = connection.prepareStatement(sql)
    var argumentIndex = 1
    for (argument <- arguments) {
      preparedStatement.setObject(argumentIndex, argument)
      argumentIndex += 1
    }
    preparedStatement
  }

}