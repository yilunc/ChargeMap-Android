package ngordnet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.TreeMap;

/**
 * Stores the data for a single word in all years
 * 
 * @author Yilun Chen
 */
public class TimeSeries<T extends Number> extends TreeMap<Integer, T> {

    /** Constructs a new empty TimeSeries. */
    public TimeSeries() {
        super();
    }

    /** Creates a copy of TS. */
    public TimeSeries(TimeSeries<T> ts) {
        super();

        // copy ts into this
        for (Integer key : ts.keySet()) {
            this.put(key, ts.get(key));
        }
    }

    /**
     * Creates a copy of TS, but only between STARTYEAR and ENDYEAR. inclusive
     * of both end points.
     */
    public TimeSeries(TimeSeries<T> ts, int startYear, int endYear) {
        super();

        // copy ts into this under the conditions
        for (Integer key : ts.keySet()) {
            if (key >= startYear && key <= endYear) {
                this.put(key, ts.get(key));
            }
        }
    }

    /** Returns all years for this time series (in any order). */
    public Collection<Number> years() {
        ArrayList<Number> totalYears = new ArrayList<Number>();

        // add years to a treeset and return
        for (Integer year : this.keySet()) {
            totalYears.add(year);
        }

        return totalYears;
    }

    /**
     * Returns all data for this time series. Must be in the same order as
     * years().
     */
    public Collection<Number> data() {
        ArrayList<Number> totalData = new ArrayList<Number>();

        // add data to a treeset and return
        for (Integer year : this.keySet()) {
            totalData.add(this.get(year));
        }

        return totalData;
    }

    /**
     * Returns the quotient of this time series divided by the relevant value in
     * ts. If ts is missing a key in this time series, return an
     * IllegalArgumentException.
     */
    public TimeSeries<Double> dividedBy(TimeSeries<? extends java.lang.Number> ts) {
        TimeSeries<Double> divided = new TimeSeries<Double>();
        boolean hasFound = false;

        // divide if both have the key, error otehrwise
        for (Integer year : this.keySet()) {
            if (ts.containsKey(year)) {
                divided.put(year, this.get(year).doubleValue() / ts.get(year).doubleValue());
                hasFound = true;
            }
            if (!hasFound) {
                throw new IllegalArgumentException("Key missing from ts");
            } else {
                hasFound = false;
            }
        }

        return divided;
    }

    /**
     * Returns the sum of this time series with the given ts. The result is a a
     * Double time series (for simplicity).
     */
    public TimeSeries<Double> plus(TimeSeries<? extends java.lang.Number> ts) {
        TimeSeries<Double> added = new TimeSeries<Double>();

        // find same key in this and add
        for (Integer year : this.keySet()) {
            if (ts.containsKey(year)) {
                added.put(year, this.get(year).doubleValue() + ts.get(year).doubleValue());
            } else {
                added.put(year, this.get(year).doubleValue());
            }
        }

        // find any other entries in ts
        for (Integer year : ts.keySet()) {
            if (!this.containsKey(year)) {
                added.put(year, ts.get(year).doubleValue());
            }
        }

        return added;
    }
}
