package ngordnet;

import java.util.Collection;
import java.util.TreeMap;

import edu.princeton.cs.introcs.In;

/**
 * Provides a way to store data on large sets of words using Yearly Records and
 * Time Series
 * 
 * @author Yilun Chen
 */
public class NGramMap {
    private TreeMap<Integer, YearlyRecord> wordYTYR;
    private TreeMap<String, TimeSeries<Integer>> wordTS;
    private TimeSeries<Long> yearData;

    /** Constructs an NGramMap from WORDSFILENAME and COUNTSFILENAME. */
    public NGramMap(String wordsFilename, String countsFilename) {
        wordYTYR = new TreeMap<Integer, YearlyRecord>();
        wordTS = new TreeMap<String, TimeSeries<Integer>>();
        yearData = new TimeSeries<Long>();

        In wordsIn = new In(wordsFilename);
        In countsIn = new In(countsFilename);

        YearlyRecord tempUtilYR;
        TimeSeries<Integer> tempUtilTS;

        String[] tempUtilArr;

        int year;

        // loop to add maps to year and word
        while (wordsIn.hasNextLine()) {
            tempUtilArr = wordsIn.readLine().split("\\t");

            year = Integer.parseInt(tempUtilArr[1]);

            // add to YR
            if (wordYTYR.containsKey(year)) {
                wordYTYR.get(year).put(tempUtilArr[0], Integer.parseInt(tempUtilArr[2]));
            } else {
                tempUtilYR = new YearlyRecord();
                tempUtilYR.put(tempUtilArr[0], Integer.parseInt(tempUtilArr[2]));
                wordYTYR.put(year, tempUtilYR);
            }

            // add to TS
            if (wordTS.containsKey(tempUtilArr[0])) {
                wordTS.get(tempUtilArr[0]).put(year, Integer.parseInt(tempUtilArr[2]));
            } else {
                tempUtilTS = new TimeSeries<Integer>();
                tempUtilTS.put(year, Integer.parseInt(tempUtilArr[2]));
                wordTS.put(tempUtilArr[0], tempUtilTS);
            }
        }

        // loop to record yearData
        while (countsIn.hasNextLine()) {
            tempUtilArr = countsIn.readLine().split(",");

            yearData.put(Integer.parseInt(tempUtilArr[0]), Long.parseLong(tempUtilArr[1]));
        }
    }

    /**
     * Returns the absolute count of WORD in the given YEAR. If the word did not
     * appear in the given year, return 0.
     */
    public int countInYear(String word, int year) {
        if (wordYTYR.containsKey(year) && wordYTYR.get(year).words().contains(word)) {
            return wordYTYR.get(year).count(word);
        } else {
            return 0;
        }
    }

    /** Returns a defensive copy of the YearlyRecord of YEAR. */
    public YearlyRecord getRecord(int year) {
        YearlyRecord toReturn = new YearlyRecord();

        // add each data point into the new YR
        for (String word : wordYTYR.get(year).words()) {
            toReturn.put(word, wordTS.get(word).get(year));
        }

        return toReturn;
    }

    /** Returns the total number of words recorded in all volumes. */
    public TimeSeries<Long> totalCountHistory() {
        return yearData;
    }

    /** Provides the history of WORD between STARTYEAR and ENDYEAR. */
    public TimeSeries<Integer> countHistory(String word, int startYear, int endYear) {
        return new TimeSeries<Integer>(wordTS.get(word), startYear, endYear);
    }

    /** Provides a defensive copy of the history of WORD. */
    public TimeSeries<Integer> countHistory(String word) {
        return new TimeSeries<Integer>(wordTS.get(word));
    }

    /** Provides the relative frequency of WORD between STARTYEAR and ENDYEAR. */
    public TimeSeries<Double> weightHistory(String word, int startYear, int endYear) {
        return countHistory(word, startYear, endYear).dividedBy(yearData);
    }

    /** Provides the relative frequency of WORD. */
    public TimeSeries<Double> weightHistory(String word) {
        return countHistory(word).dividedBy(yearData);
    }

    /**
     * Provides the summed relative frequency of all WORDS between STARTYEAR and
     * ENDYEAR. If a word does not exist, ignore it rather than throwing an
     * exception.
     */
    public TimeSeries<Double> summedWeightHistory(Collection<String> words, int startYear,
            int endYear) {
        // create a util time series
        TimeSeries<Double> toReturn = new TimeSeries<Double>();

        for (String word : words) {
            if (wordTS.containsKey(word)) {
                toReturn = toReturn.plus(weightHistory(word, startYear, endYear));
            }
        }
        return toReturn;
    }

    /** Returns the summed relative frequency of all WORDS. */
    public TimeSeries<Double> summedWeightHistory(Collection<String> words) {
        return summedWeightHistory(words, yearData.firstKey(), yearData.lastKey());
    }

    /**
     * Provides processed history of all words between STARTYEAR and ENDYEAR as
     * processed by YRP.
     */
    public TimeSeries<Double> processedHistory(int startYear, int endYear,
            YearlyRecordProcessor yrp) {
        // create a util time series
        TimeSeries<Double> toReturn = new TimeSeries<Double>();

        for (Integer year : wordYTYR.keySet()) {
            if (year >= startYear && year <= endYear) {
                toReturn.put(year, yrp.process(wordYTYR.get(year)));
            }
        }
        return toReturn;
    }

    /** Provides processed history of all words ever as processed by YRP. */
    public TimeSeries<Double> processedHistory(YearlyRecordProcessor yrp) {
        return processedHistory(yearData.firstKey(), yearData.lastKey(), yrp);
    }
}
