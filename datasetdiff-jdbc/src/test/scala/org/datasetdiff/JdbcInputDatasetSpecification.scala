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
  val databaseName: String = "jdbcTest";
  val baseConnectionUrl: String = "jdbc:derby:" + databaseName
  val jdbcExecutor = new JdbcExecutor(baseConnectionUrl + ";create=true")

  doBeforeSpec {
    jdbcExecutor.execute("CREATE TABLE TEST (ID INT, DESCRIPTION VARCHAR(255))")
  }

  doAfterSpec {
    jdbcExecutor.ignore {
      () => jdbcExecutor.execute("DROP TABLE TEST")
    }
    jdbcExecutor.close
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
      jdbcExecutor.execute("INSERT INTO TEST (ID, DESCRIPTION) VALUES (1,'a')")
      jdbcExecutor.execute("INSERT INTO TEST (ID, DESCRIPTION) VALUES (2,'b')")
      jdbcExecutor.withStatement((statement: Statement) => {
        val resultSet = statement.executeQuery("SELECT * FROM TEST")

        val jdbcInputDataset: JdbcInputDataset = new JdbcInputDataset(resultSet)
        val results = jdbcInputDataset.extractDataRows()

        val expectedResults = List(Seq(1, "a"), Seq(2, "b"))
        results.toList must haveTheSameElementsAs(expectedResults)
      })
    }
  }
}