package ngordnet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.TreeMap;

/**
 * Records the data of words for that year
 * 
 * @author Yilun Chen
 */
public class YearlyRecord {
    private HashMap<String, Number> recordSI; // String to Count, hashmap to
                                              // improve speed slightly
    private TreeMap<Number, ArrayList<String>> recordIS; // Count to String,
                                                         // keeps order
    private HashMap<String, Number> rankings; // for use in rank, hashmap to
                                              // make things faster
    private boolean needSort = false; // default don't need to sort

    /** Creates a new empty YearlyRecord. */
    public YearlyRecord() {
        recordSI = new HashMap<String, Number>();
        recordIS = new TreeMap<Number, ArrayList<String>>();
        rankings = new HashMap<String, Number>();
    }

    /** Creates a YearlyRecord using the given data. */
    public YearlyRecord(HashMap<String, Integer> otherCountMap) {
        recordSI = new HashMap<String, Number>();
        recordIS = new TreeMap<Number, ArrayList<String>>();
        rankings = new HashMap<String, Number>();

        for (String word : otherCountMap.keySet()) {
            this.put(word, otherCountMap.get(word));
        }
    }

    /** Returns the number of times WORD appeared in this year. */
    public int count(String word) {
        if (recordSI.containsKey(word)) {
            return recordSI.get(word).intValue();
        } else {
            return 0;
        }
    }

    /** Records that WORD occurred COUNT times in this year. */
    public void put(String word, int count) {
        // if it already contains the word, then remove it
        if (recordSI.containsKey(word)) {
            // for performance, save the value
            Number recordSIVal = recordSI.get(word);

            recordIS.get(recordSIVal).remove(word);
            // remove the mapping if there is no value
            if (recordIS.get(recordSIVal).size() == 0) {
                recordIS.remove(recordSIVal);
            }
        }

        // add to existing arraylist if the count is a duplicate or make a new
        // arrlist, make sure to check if null
        if (recordIS.keySet() != null && recordIS.keySet().contains(count)) {
            recordIS.get(count).add(word);
        } else {
            ArrayList<String> toPut = new ArrayList<String>();
            toPut.add(word);
            recordIS.put(count, toPut);
        }
        needSort = true;

        recordSI.put(word, count);
    }

    /** Returns the number of words recorded this year. */
    public int size() {
        return recordSI.size();
    }

    /** Returns all words in ascending order of count. */
    public Collection<String> words() {
        // add the words into a set in sorted order thanks to recordIS
        ArrayList<String> toReturn = new ArrayList<String>();
        for (Number key : recordIS.keySet()) {
            for (String word : recordIS.get(key)) {
                toReturn.add(word);
            }
        }
        return toReturn;
    }

    /** Returns all counts in ascending order of count. */
    public Collection<Number> counts() {
        return recordIS.keySet();
    }

    /**
     * Returns rank of WORD. Most common word is rank 1. If two words have the
     * same rank, break ties arbitrarily. No two words should have the same
     * rank.
     */
    public int rank(String word) {
        // if the word isnt in the dataset, return a nonsense value
        if (!recordSI.containsKey(word)) {
            return -1;
        }

        // sort if needed
        if (needSort) {
            int rank = 1;
            rankings.clear();

            // sort
            for (Number key : recordIS.descendingKeySet()) {
                for (String value : recordIS.get(key)) {
                    rankings.put(value, rank);
                    rank += 1;
                }
            }

            needSort = false; // no longer need to sort
        }
        return rankings.get(word).intValue();
    }
}
