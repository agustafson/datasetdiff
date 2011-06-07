package datasetdiff

/**
 * ColumnConverter which converts text to a type.
 *
 * @author agustafson
 */
trait TextColumnConverter[+O] extends ColumnConverter[String, O]
