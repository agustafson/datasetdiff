Background
=============
I was looking for a java library (or scala) which can find the difference between 2 datasets, whether the data is in
excel, a delimited text file or an SQL ResultSet. Finding nothing, I decided to write a library myself.

Usage
=================
* Create 2 InputDatasets from an excel file, delimited text file or SQL ResultSet
* Create ColumnComparators to compare columns from each InputDataset.
  Using a ConvertingColumnComparator (eg ExcelColumnConverter.Number) allows the ability to convert
  from the input column (eg: an excel cell)
  to a value for comparison (eg a BigDecimal)
* Create a new DatasetDiff, passing the ColumnComparators as a constructor param
* Execute the compareDatasets method on the DatasetDiff which will return a List of DiffResults
  - a DiffResult contains the elementNumber, side (LEFT/RIGHT) and value

Example
-------
See DatasetDiffSystemTest (scala) or DatasetDiffJavaTest (java) for more detail.

