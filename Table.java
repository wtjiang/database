package db61b;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import static db61b.Utils.*;

/** A single table in a database.
 *  @author P. N. Hilfinger, Winston Jiang
 */
class Table {
    /** A new Table whose columns are given by COLUMNTITLES, which may
     *  not contain duplicate names. */
    Table(String[] columnTitles) {
        if (columnTitles.length == 0) {
            throw error("table must have at least one column");
        }
        _size = 0;
        _rowSize = columnTitles.length;

        for (int i = columnTitles.length - 1; i >= 1; i -= 1) {
            for (int j = i - 1; j >= 0; j -= 1) {
                if (columnTitles[i].equals(columnTitles[j])) {
                    throw error("duplicate column name: %s",
                                columnTitles[i]);
                }
            }
        }
        _titles = columnTitles;
        _columns = new ValueList[_rowSize];
        for (int i = 0; i < _columns.length; i++) {
            _columns[i] = new ValueList();
        }
    }

    /** A new Table whose columns are give by COLUMNTITLES. */
    Table(List<String> columnTitles) {
        this(columnTitles.toArray(new String[columnTitles.size()]));
    }

    /** Return the number of columns in this table. */
    public int columns() {
        return _columns.length;
    }

    /** Return the title of the Kth column.  Requires 0 <= K < columns(). */
    public String getTitle(int k) {
        return _titles[k];
    }

    /** Return the number of the column whose title is TITLE, or -1 if
     *  there isn't one. */
    public int findColumn(String title) {
        for (int i = 0; i < _titles.length; i++) {
            if (_titles[i].equals(title)) {
                return i;
            }
        }
        return -1;

    }

    /** Return the number of rows in this table. */
    public int size() {
        return _size;
    }

    /** Return the value of column number COL (0 <= COL < columns())
     *  of record number ROW (0 <= ROW < size()). */
    public String get(int row, int col) {
        try {
            return _columns[col].get(row);
        } catch (IndexOutOfBoundsException excp) {
            throw error("invalid row or column");
        }
    }

    /** Add a new row whose column values are VALUES to me if no equal
     *  row already exists.  Return true if anything was added,
     *  false otherwise. */
    public boolean add(String[] values) {
        if (_columns.length != values.length) {
            return false;
        }

        int addable = 0;
        for (int r = 0; r < size(); r++) {
            for (int c = 0; c < columns(); c++) {
                if (values[c].equals(get(r, c))) {
                    addable += 1;
                }
            }
            if (addable == columns()) {
                return false;
            } else {
                addable = 0;
            }
        }
        _size += 1;

        for (int i = 0; i < columns(); i++) {
            _columns[i].add(values[i]);
        }

        int count = size() - 1;
        for (int i = 0; i < size() - 1; i++) {
            int val = compareRows(size() - 1, _index.get(i));
            if (val < 0) {
                count = i;
                _index.add(count, size() - 1);
                return true;
            }
        }
        _index.add(count, size() - 1);
        return true;
    }


    /** Add a new row whose column values are extracted by COLUMNS from
     *  the rows indexed by ROWS, if no equal row already exists.
     *  Return true if anything was added, false otherwise. See
     *  Column.getFrom(Integer...) for a description of how Columns
     *  extract values. */
    public boolean add(List<Column> columns, Integer... rows) {
        String[] rowVals = new String[columns.size()];
        for (int row = 0, i = 0; row < rows.length; row++, i = 0) {
            for (int col = 0; col < columns.size(); col++, i++) {
                Column column = columns.get(col);
                String val = column.getFrom(rows);
                rowVals[i] = val;
            }
        }
        return this.add(rowVals);
    }


    /** Read the contents of the file NAME.db, and return as a Table.
     *  Format errors in the .db file cause a DBException. */
    static Table readTable(String name) {
        BufferedReader input;
        Table table;
        input = null;
        table = null;
        try {
            input = new BufferedReader(new FileReader(name + ".db"));
            String header = input.readLine();
            if (header == null) {
                throw error("missing header in DB file");
            }
            String[] columnNames = header.split(",");
            table = new Table(columnNames);

            String values;
            while ((values = input.readLine()) != null) {
                String[] vals = values.split(",");

                if (vals.length != columnNames.length) {
                    throw error(
                            "column lengths incompatible in DB file.");
                }
                table.add(vals);
            }

        } catch (FileNotFoundException e) {
            throw error("could not find %s.db", name);
        } catch (IOException e) {
            throw error("problem reading from %s.db", name);
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    /* Ignore IOException */
                }
            }
        }
        return table;
    }

    /** Write the contents of TABLE into the file NAME.db. Any I/O errors
     *  cause a DBException. */
    void writeTable(String name) {
        PrintStream output;
        output = null;
        try {
            String sep;
            sep = "";
            output = new PrintStream(name + ".db");
            for (int i = 0; i < _titles.length; i++) {
                if (i == _titles.length - 1) {
                    output.print(_titles[i]);
                } else {
                    output.print(_titles[i] + ",");
                }
            }
            for (int k: _index) {
                output.println(sep);
                for (int i = 0; i < columns(); i++) {
                    if (i == columns() - 1) {
                        output.print(_columns[i].get(k));
                    } else {
                        output.print(_columns[i].get(k) + ",");
                    }
                }
            }
        } catch (IOException e) {
            throw error("trouble writing to %s.db", name);
        } finally {
            if (output != null) {
                output.close();
            }
        }
    }



    /** Print my contents on the standard output, separated by spaces
     *  and indented by two spaces. */
    void print() {
        for (int row = 0; row < _size; row++) {
            System.out.print("  ");
            for (int col = 0; col < _rowSize; col++) {
                if (col == _rowSize - 1) {
                    System.out.print(get(_index.get(row), col));
                } else {
                    System.out.print(get(_index.get(row), col) + " ");
                }
            }
            System.out.println();
        }
    }

    /** Return a new Table whose columns are COLUMNNAMES, selected from
     *  rows of this table that satisfy CONDITIONS. */
    Table select(List<String> columnNames, List<Condition> conditions) {
        Table result = new Table(columnNames);
        if (conditions.isEmpty()) {
            for (int row = 0, k = 0; row < this.size(); row++, k = 0) {
                String[] tempRow = new String[columnNames.size()];
                for (String col: columnNames) {
                    tempRow[k] = this.get(row, this.findColumn(col));
                    k++;
                }
                result.add(tempRow);
            }
            return result;
        } else {
            for (int row = 0, k = 0; row < this.size(); row++, k = 0) {
                if (Condition.test(conditions, row)) {
                    String[] tempRow = new String[columnNames.size()];
                    for (String col: columnNames) {
                        tempRow[k] = this.get(row, this.findColumn(col));
                        k++;
                    }
                    result.add(tempRow);
                } else {
                    continue;
                }
            }
        }
        return result;
    }

    /** Return a new Table whose columns are COLUMNNAMES, selected
     *  from pairs of rows from this table and from TABLE2 that match
     *  on all columns with identical names and satisfy CONDITIONS. */
    Table select(Table table2, List<String> columnNames,
                 List<Condition> conditions) {
        Table result = new Table(columnNames);
        ArrayList<Column> columnsList = new ArrayList<>();
        ArrayList<Column> columnsCommon1 = new ArrayList<Column>();
        ArrayList<Column> columnsCommon2 = new ArrayList<Column>();

        for (int i = 0; i < columnNames.size(); i++) {
            columnsList.add(new Column(columnNames.get(i), this, table2));
        }
        for (int col1 = 0; col1 < this.columns(); col1++) {
            for (int col2 = 0; col2 < table2.columns(); col2++) {
                if (this.getTitle(col1).equals(table2.getTitle(col2))) {
                    columnsCommon1.add(new Column(this.getTitle(col1), this));
                    columnsCommon2.add(
                            new Column(table2.getTitle(col2), table2));
                }
            }
        }

        for (int row = 0; row < this.size(); row++) {
            for (int row2 = 0; row2 < table2.size(); row2++) {
                if ((equijoin(columnsCommon1, columnsCommon2, row, row2))) {
                    if (Condition.test(conditions, row, row2)) {
                        result.add(columnsList, row, row2);
                    }
                }
            }
        }
        return result;
    }


        /** Return <0, 0, or >0 depending on whether the row formed from
         *  the elements _columns[0].get(K0), _columns[1].get(K0), ...
         *  is less than, equal to, or greater than that formed from elememts
         *  _columns[0].get(K1), _columns[1].get(K1), ....  This method ignores
         *  the _index. */
    private int compareRows(int k0, int k1) {
        for (int i = 0; i < _columns.length; i += 1) {
            int c = _columns[i].get(k0).compareTo(_columns[i].get(k1));
            if (c != 0) {
                return c;
            }
        }
        return 0;
    }

    /** Return true if the columns COMMON1 from ROW1 and COMMON2 from
     *  ROW2 all have identical values.  Assumes that COMMON1 and
     *  COMMON2 have the same number of elements and the same names,
     *  that the columns in COMMON1 apply to this table, those in
     *  COMMON2 to another, and that ROW1 and ROW2 are indices, respectively,
     *  into those tables. */
    private static boolean equijoin(List<Column> common1, List<Column> common2,
                                    int row1, int row2) {
        if (common1.size() == 0 && common2.size() == 0) {
            return true;
        }
        for (Column c1 : common1) {
            for (Column c2 : common2) {
                if (c1.getName().equals(c2.getName())) {
                    String s1 = c1.getFrom(row1);
                    String s2 = c2.getFrom(row2);
                    if (!s1.equals(s2)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /** A class that is essentially ArrayList<String>.  For technical reasons,
     *  we need to encapsulate ArrayList<String> like this because the
     *  underlying design of Java does not properly distinguish between
     *  different kinds of ArrayList at runtime (e.g., if you have a
     *  variable of type Object that was created from an ArrayList, there is
     *  no way to determine in general whether it is an ArrayList<String>,
     *  ArrayList<Integer>, or ArrayList<Object>).  This leads to annoying
     *  compiler warnings.  The trick of defining a new type avoids this
     *  issue. */
    private static class ValueList extends ArrayList<String> {
    }

    /** My column titles. */
    private final String[] _titles;
    /** My columns. Row i consists of _columns[k].get(i) for all k. */
    private final ValueList[] _columns;

    /** Rows in the database are supposed to be sorted. To do so, we
     *  have a list whose kth element is the index in each column
     *  of the value of that column for the kth row in lexicographic order.
     *  That is, the first row (smallest in lexicographic order)
     *  is at position _index.get(0) in _columns[0], _columns[1], ...
     *  and the kth row in lexicographic order is at position _index.get(k).
     *  When a new row is inserted, insert its index at the appropriate
     *  place in this list.
     *  (Alternatively, we could simply keep each column in the proper order
     *  so that we would not need _index.  But that would mean that inserting
     *  a new row would require rearranging _rowSize lists (each list in
     *  _columns) rather than just one. */
    private final ArrayList<Integer> _index = new ArrayList<>();

    /** My number of rows (redundant, but convenient). */
    private int _size;
    /** My number of columns (redundant, but convenient). */
    private final int _rowSize;
}
