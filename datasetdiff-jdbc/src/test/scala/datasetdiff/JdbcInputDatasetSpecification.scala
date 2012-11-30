package datasetdiff

import org.specs.Specification
import org.specs.runner.JUnit4
import java.lang.String
import java.sql._
import resource._
import JdbcExecutor._

/**
 * @author agustafson
 */
class JdbcInputDatasetTest extends JUnit4(JdbcInputDatasetSpecification)

object JdbcInputDatasetSpecification extends Specification {
  val databaseName: String = "jdbcTest";
  val baseConnectionUrl: String = "jdbc:derby:" + databaseName
  val connectionUrl = baseConnectionUrl + ";create=true"
  implicit val connection = DriverManager.getConnection(connectionUrl)

  doBeforeSpec {
    executeUpdate("CREATE TABLE TEST (ID INT, DESCRIPTION VARCHAR(255))")
  }

  doAfterSpec {
    ignore {
      () => executeUpdate("DROP TABLE TEST")
    }
    connection.close()
    // shutdown
    var gotSqlException = false;
    try {
      DriverManager.getConnection("jdbc:derby:;shutdown=true");
    } catch {
      case se: SQLException =>
        if (se.getSQLState.equals("XJ015")) {
          gotSqlException = true;
        }
    }
    if (!gotSqlException) {
      println("Database did not shut down normally");
    }
  }

  "JdbcInputDataset" should {
    "extract a simple dataset" in {
      val insertSql = "INSERT INTO TEST (ID, DESCRIPTION) VALUES (?,?)"
      managed(connection.prepareStatement(insertSql)) acquireAndGet {
        preparedStatement: PreparedStatement => {
          executeUpdate(preparedStatement, 1, "a")
          executeUpdate(preparedStatement, 2, "b")
        }
      }
      executeQuery("SELECT * FROM TEST") {
        resultSet => {
          val jdbcInputDataset: JdbcInputDataset = new JdbcInputDataset(resultSet)
          val results = jdbcInputDataset.extractDataRows()

          val expectedResults = List(Seq(1, "a"), Seq(2, "b"))
          results.toList must haveTheSameElementsAs(expectedResults)
        }
      }
    }
  }
}
