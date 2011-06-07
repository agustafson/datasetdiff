Reasons
=============
I was looking for a java library (or scala) which could compare 2 datasets, whether the data is in
excel or a delimited text file. Finding nothing, I decided to write a library myself.
SQL ResultSet support as an InputDataset has also been added.

Usage
=================
* Create 2 InputDatasets from an excel file, delimited text file or SQL ResultSet</li>
* Create ColumnComparators to compare columns from each InputDataset.
  Using a ConvertingColumnComparator (eg ExcelColumnConverter.Number) allows the ability to convert
  from the input column (eg: an excel cell)
  to a value for comparison (eg a BigDecimal)
* Create a new DatasetDiff, passing the ColumnComparators as a constructor param</li>
* Execute the compareDatasets method on the DatasetDiff which will return a List of Arrays of ComparisonResults
  - the list represents the rows and the arrays represent the columns in those rows

Example
-------
See DatasetDiffSystemTest (scala) or DatasetDiffJavaTest (java) for more detail.

Java builder
------------
There is a DatasetDiffBuilder for use with java

    List<ComparisonResult[]> comparisons = new DatasetDiffBuilder<String, String>()
                .withColumnComparator(0, stringColumnComparator)
                .withColumnComparator(1, numberColumnComparator)
                .withColumnComparator(2, dateColumnComparator)
                .compare(textInputDataset1, textInputDataset2);

