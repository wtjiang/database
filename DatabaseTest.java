package db61b;

import org.junit.Test;

import static org.junit.Assert.*;

/** Testing methods for the Database class.
 *  @author Winston Jiang*/
public class DatabaseTest {


    @Test
    public void testPutAndGet() {
        Database db = new Database();
        String[] tableTitles = new String[]
        {"Name", "Age", "Sex", "Height", "Weight"};
        Table seinfeldM = new Table(tableTitles);
        Table seinfeldF = new Table(tableTitles);

        String[] r1 = new String[] {"Jerry", "33", "Male", "71", "165"};
        String[] r2 = new String[] {"George", "32", "Male", "66", "150"};
        String[] r3 = new String[] {"Elaine", "31", "Female", "64", "120"};
        String[] r4 = new String[] {"Kramer", "35", "Male", "75", "175"};
        String[] r5 = new String[] {"Newman", "34", "Male", "65", "155"};

        seinfeldM.add(r1);
        seinfeldM.add(r2);
        seinfeldF.add(r3);
        seinfeldM.add(r4);
        seinfeldM.add(r5);

        db.put("seinfeld male", seinfeldM);
        db.put("seinfeld female", seinfeldF);
        assertEquals(seinfeldM, db.get("seinfeld male"));
        assertEquals(seinfeldF, db.get("seinfeld female"));
    }

}
