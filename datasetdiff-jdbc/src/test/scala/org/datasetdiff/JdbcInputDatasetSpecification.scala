package org.datasetdiff

import org.specs.Specification
import org.specs.runner.JUnit4
import java.lang.String
import java.sql._

/**
 * @author agustafson
 */
class JdbcInputDatasetTest extends JUnit4(JdbcInputDatasetSpecification)

object JdbcInputDatasetSpecification extends Specification {
  val driver: String = "org.apache.derby.jdbc.EmbeddedDriver";
  val databaseName: String = "jdbcTest";
  val baseConnectionUrl: String = "jdbc:derby:" + databaseName
  var connection = DriverManager.getConnection(baseConnectionUrl + ";create=true")

  doBeforeSpec {
    ignore {
      () => execute("DROP TABLE TEST")
    }
    execute("CREATE TABLE TEST (ID INT, DESCRIPTION VARCHAR(255))")
  }

  doAfterSpec {
    ignore {
      connection.close
    }
    // shutdown
    var gotSQLExc = false;
    try {
       DriverManager.getConnection("jdbc:derby:;shutdown=true");
    } catch {
      case se: SQLException =>
        if (se.getSQLState().equals("XJ015")) {
          gotSQLExc = true;
        }
    }
    if (!gotSQLExc) {
       System.out.println("Database did not shut down normally");
    }
  }

  "JdbcInputDataset" should {
    "extract a simple dataset" in {
      execute("INSERT INTO TEST (ID, DESCRIPTION) VALUES (1,'a')")
      execute("INSERT INTO TEST (ID, DESCRIPTION) VALUES (2,'b')")
      withStatement((statement: Statement) => {
        val resultSet = statement.executeQuery("SELECT * FROM TEST")

        val jdbcInputDataset: JdbcInputDataset = new JdbcInputDataset(resultSet)
        val results = jdbcInputDataset.extractDataRows()

        val expectedResults = List(Seq(1, "a"), Seq(2, "b"))
        results.toList must haveTheSameElementsAs(expectedResults)
      })
    }
  }

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
}