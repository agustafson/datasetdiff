package datasetdiff

/**
 * Converts a column to a value.
 *
 * @author agustafson
 */
trait ColumnConverter[-C, +O] extends (C => O)
