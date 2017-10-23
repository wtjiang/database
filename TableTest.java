package db61b;

import org.junit.Test;

import static org.junit.Assert.*;
import java.util.ArrayList;

/** Testing methods for the Table class.
 *  @author Winston Jiang
 */
public class TableTest {

    /** Tests the number of columns method in Table. */
    @Test
    public void testColumns() {
        String[] tableTitles = {"Name", "Name Length"};
        Table names = new Table(tableTitles);
        assertEquals(2, names.columns());
    }

    /** Tests the getTitle method in Table. */
    @Test
    public void testGetTitle() {
        String[] tableTitles = {"Name", "Name Length"};
        Table names = new Table(tableTitles);
        assertEquals("Name", names.getTitle(0));
        assertEquals("Name Length", names.getTitle(1));
    }

    /** Tests the find column number method in Table. */
    @Test
    public void testFindColumn() {
        String[] tableTitles = {"Name", "Name Length"};
        Table names = new Table(tableTitles);
        assertEquals(0, names.findColumn("Name"));
        assertEquals(1, names.findColumn("Name Length"));
        assertEquals(-1, names.findColumn("Not a Column"));
    }

    /** Tests the number of rows method in Table. */
    @Test
    public void testSize() {
        String[] tableTitles = {"Name", "Name Length"};
        Table names = new Table(tableTitles);
        String[] win = {"winston", "7"};
        String[] jess = {"jessica", "7"};
        names.add(win);
        assertEquals(1, names.size());
        names.add(jess);
        assertEquals(2, names.size());
    }

    /** Tests the get(row, col) method in Table. */
    @Test
    public void testGet() {
        String[] tableTitles = {"Name", "Name Length"};
        Table names = new Table(tableTitles);
        String[] win = {"winston", "7"};
        String[] jess = {"jessica", "7"};
        names.add(win);
        names.add(jess);
        assertEquals("winston", names.get(0, 0));
        assertEquals("7", names.get(1, 1));
    }

    /** Tests the row add method in Table. */
    @Test
    public void testAdd() {
        String[] tableTitles = {"Name", "Name Length"};
        Table names = new Table(tableTitles);
        String[] win = {"winston", "7"};
        String[] jess = {"jessica", "7"};
        String[] winBad = {"winston", "7", "1"};
        String[] winBad1 = {"winston"};
        String[] jessBad = {"jessica", "7"};
        assertEquals(true, names.add(win));
        assertEquals(true, names.add(jess));
        assertEquals(false, names.add(winBad));
        assertEquals(false, names.add(winBad1));
        assertEquals(false, names.add(jessBad));
    }

    /** Tests the table reading method in Table. */
    @Test
    public void testReadTable() {
        Table seinfeld = Table.readTable("seinfeld1");
        assertEquals(3, seinfeld.size());
        assertEquals(4, seinfeld.columns());
        assertEquals("32", seinfeld.get(1, 1));
        assertEquals("Female", seinfeld.get(2, 2));
    }

    /** Tests the table writing method in Table. */
    @Test
    public void testWriteTable() {
        String[] tableTitles = new String[] { "Name", "Age", "Sex", "Height" };
        Table seinfeld = new Table(tableTitles);
        String[] r1 = new String[] {"Jerry", "33", "Male", "71"};
        String[] r2 = new String[] {"George", "32", "Male", "66"};
        String[] r3 = new String[] {"Elaine", "31", "Female", "64"};
        seinfeld.add(r1);
        seinfeld.add(r2);
        seinfeld.add(r3);
        seinfeld.writeTable("seinfeld1");

        String[] tableTitles2 = new String[] {"Name", "Height", "Weight"};
        Table seinfeld2 = new Table(tableTitles2);
        String[] r11 = new String[] {"Jerry", "71", "165"};
        String[] r21 = new String[] {"George", "66", "150"};
        String[] r31 = new String[] {"Elaine", "64", "120"};
        seinfeld2.add(r11);
        seinfeld2.add(r21);
        seinfeld2.add(r31);
        seinfeld2.writeTable("seinfeld2");
    }

    /** Tests the table printing method in Table. */
    @Test
    public void testPrint() {
        String[] tableTitles = new String[] {"Initial", "+1", "+2", "+3", "+4"};
        Table numbers = new Table(tableTitles);
        String[] r1 = new String[] {"0", "1", "2", "3", "4"};
        String[] r2 = new String[] {"1", "2", "3", "4", "5"};
        numbers.add(r1);
        numbers.add(r2);
        numbers.print();
    }

    /** Tests the single table select method in Table. */
    @Test
    public void testSelect() {
        String[] tableTitles = new String[] {"Name", "Age", "Sex", "Height" };
        Table seinfeld = new Table(tableTitles);
        String[] r1 = new String[] {"Jerry", "33", "Male", "71"};
        String[] r2 = new String[] {"George", "32", "Male", "66"};
        String[] r3 = new String[] {"Elaine", "31", "Female", "64"};
        seinfeld.add(r1);
        seinfeld.add(r2);
        seinfeld.add(r3);

        Column col2 = new Column("Height", seinfeld);
        ArrayList<Condition> conds1 = new ArrayList<>();
        Condition cond1 = new Condition(col2, ">", "65");
        conds1.add(cond1);
        ArrayList<String> cols = new ArrayList<>();
        cols.add("Height");
        Table selected = seinfeld.select(cols, conds1);
        assertEquals(0, selected.findColumn("Height"));
        assertEquals(2, selected.size());
        assertEquals("71", selected.get(0, 0));
    }

    /** Tests the double table column select method in Table. */
    @Test
    public void testDoubleSelect() {
        String[] tableTitles = new String[] { "Name", "Age", "Sex", "Height" };
        Table seinfeld = new Table(tableTitles);
        String[] r1 = new String[] {"Jerry", "33", "Male", "71"};
        String[] r2 = new String[] {"George", "32", "Male", "66"};
        String[] r3 = new String[] {"Elaine", "31", "Female", "64"};
        seinfeld.add(r1);
        seinfeld.add(r2);
        seinfeld.add(r3);

        String[] tableTitles2 = new String[] {"Name", "Height", "Weight"};
        Table seinfeld2 = new Table(tableTitles2);
        String[] r11 = new String[] {"Jerry", "71", "165"};
        String[] r21 = new String[] {"George", "66", "150"};
        String[] r31 = new String[] {"Elaine", "64", "120"};
        seinfeld2.add(r11);
        seinfeld2.add(r21);
        seinfeld2.add(r31);

        Column col1 = new Column("Height", seinfeld, seinfeld2);
        ArrayList<Condition> conds1 = new ArrayList<>();
        Condition cond1 = new Condition(col1, ">", "65");
        conds1.add(cond1);
        ArrayList<String> cols = new ArrayList<>();
        cols.add("Name");
        cols.add("Height");
        Table selected = seinfeld.select(seinfeld, cols, conds1);
        assertEquals(1, selected.findColumn("Height"));
        assertEquals(0, selected.findColumn("Name"));
        assertEquals(2, selected.size());
        assertEquals(2, selected.columns());
        assertEquals("Jerry", selected.get(0, 0));
        assertEquals("66", selected.get(1, 1));
    }

}
