package ngordnet;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collection;

import org.junit.Test;

/**
 * Perform tests of the TimeSeries class
 */

public class TestTimeSeries {

    /** Tests basic operations of TimeSeries */
    @Test
    public void testBasic() {
        TimeSeries<Double> ts = new TimeSeries<Double>();

        ts.put(1992, 3.6);
        ts.put(1993, 9.2);
        ts.put(1994, 15.2);
        ts.put(1995, 16.1);
        ts.put(1996, -15.7);

        assertEquals(3.6, ts.get(1992), 1e-5);
        assertEquals(-15.7, ts.get(1996), 1e-5);
        assertEquals(true, ts.containsValue(16.1));
        assertEquals(false, ts.containsValue(16.2));
    }

    /** Tests year() and data() methods of TimeSeries */
    @Test
    public void testYearData() {
        TimeSeries<Double> ts = new TimeSeries<Double>();

        ts.put(1992, 3.6);
        ts.put(1993, 9.2);
        ts.put(1994, 15.2);
        ts.put(1995, 16.1);
        ts.put(1996, -15.7);

        Collection<Number> years = ts.years();
        Collection<Number> data = ts.data();

        ArrayList<Number> yearCompare = new ArrayList<Number>();
        yearCompare.add(1992);
        yearCompare.add(1993);
        yearCompare.add(1994);
        yearCompare.add(1995);
        yearCompare.add(1996);

        ArrayList<Number> dataCompare = new ArrayList<Number>();
        dataCompare.add(3.6);
        dataCompare.add(9.2);
        dataCompare.add(15.2);
        dataCompare.add(16.1);
        dataCompare.add(-15.7);

        assertEquals(true, years.equals(yearCompare));
        assertEquals(true, data.equals(dataCompare));

        yearCompare.add(1997);
        dataCompare.add(12312312);

        assertEquals(false, years.equals(yearCompare));
        assertEquals(false, data.equals(dataCompare));
    }

    /** Tests plus operation of TimeSeries */
    @Test
    public void testPlus() {
        TimeSeries<Double> ts1 = new TimeSeries<Double>();
        TimeSeries<Double> ts2 = new TimeSeries<Double>();
        TimeSeries<Double> ts3 = new TimeSeries<Double>();
        TimeSeries<Double> empty = new TimeSeries<Double>();
        TimeSeries<Double> ts5 = new TimeSeries<Double>();
        TimeSeries<Double> ts6 = new TimeSeries<Double>();
        TimeSeries<Double> ts7 = new TimeSeries<Double>();
        TimeSeries<Double> ts8 = new TimeSeries<Double>();

        ts1.put(1992, 3.6);
        ts1.put(1993, 9.2);
        ts1.put(1994, 15.2);
        ts1.put(1995, 16.1);
        ts1.put(1996, -15.7);

        ts2.put(1992, 3.6);
        ts2.put(1993, 0.8);
        ts2.put(1994, 2.0);
        ts2.put(1995, 16.9);
        ts2.put(1996, 15.7);

        ts3.put(1992, 7.2);
        ts3.put(1993, 10.0);
        ts3.put(1994, 17.2);
        ts3.put(1995, 33.0);
        ts3.put(1996, 0.0);

        ts5.put(1992, 5.0);

        ts6.put(1992, 8.6);
        ts6.put(1993, 9.2);
        ts6.put(1994, 15.2);
        ts6.put(1995, 16.1);
        ts6.put(1996, -15.7);

        ts7.put(1990, 5.0);

        ts8.put(1990, 5.0);
        ts8.put(1992, 3.6);
        ts8.put(1993, 9.2);
        ts8.put(1994, 15.2);
        ts8.put(1995, 16.1);
        ts8.put(1996, -15.7);

        assertEquals(ts1.plus(ts2), ts3);
        assertEquals(empty.plus(ts2), ts2);
        assertEquals(ts1.plus(ts5), ts6);
        assertEquals(ts1.plus(ts7), ts8);
        assertEquals(ts2.plus(empty), ts2);
    }

    /** Tests divide method of TimeSeries */
    @Test
    public void testDivide() {
        TimeSeries<Double> ts1 = new TimeSeries<Double>();
        TimeSeries<Double> ts2 = new TimeSeries<Double>();
        TimeSeries<Double> ts3 = new TimeSeries<Double>();

        ts1.put(1992, 6.0);
        ts1.put(1993, 9.0);
        ts1.put(1994, 75.0);
        ts1.put(1995, 16.0);
        ts1.put(1996, -15.0);

        ts2.put(1992, 3.0);
        ts2.put(1993, 9.0);
        ts2.put(1994, 15.0);
        ts2.put(1995, 4.0);
        ts2.put(1996, -15.0);
        ts2.put(2003, 12.0);

        ts3.put(1992, 2.0);
        ts3.put(1993, 1.0);
        ts3.put(1994, 5.0);
        ts3.put(1995, 4.0);
        ts3.put(1996, 1.0);

        assertEquals(ts3, ts1.dividedBy(ts2));
    }

    public static void main(String[] args) {
        jh61b.junit.textui.runClasses(TestTimeSeries.class);
    }
}
