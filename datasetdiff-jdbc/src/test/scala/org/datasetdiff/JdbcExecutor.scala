package org.datasetdiff

import java.sql.{DriverManager, Connection, ResultSet, Statement}

/**
 * @author agustafson
 */
class JdbcExecutor(connectionUrl: String) {
  val connection = DriverManager.getConnection(connectionUrl)

  def execute(sql: String): Boolean = {
    withStatement((statement: Statement) => {
      statement.execute(sql)
    })
  }

  def executeQuery(sql: String): ResultSet = {
    withStatement((statement: Statement) => {
      statement.executeQuery(sql)
    })
  }

  def withStatement[O](f: Statement => O): O = {
    val statement: Statement = connection.createStatement
    try {
      f(statement)
    }
    finally {
      statement.close
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

  def close() {
    ignore {
      connection.close
    }
  }

}