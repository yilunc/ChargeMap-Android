package ngordnet;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.junit.Test;

import edu.princeton.cs.introcs.StdRandom;
import edu.princeton.cs.introcs.Stopwatch;

/**
 * Perform tests of the YearlyRecord Class
 */

public class TestYearlyRecord {
    
    /** Tests constructors of TimeSeries */
    @Test
    public void testConstructor() {
        YearlyRecord yr = new YearlyRecord();
        HashMap<String, Integer> yr1 = new HashMap<String, Integer>();

        yr.put("quayside", 95);
        yr.put("surrogate", 340);
        yr.put("merchantman", 181);
        
        yr1.put("quayside", 95);
        yr1.put("surrogate", 340);
        yr1.put("merchantman", 181);
        
        YearlyRecord yr2 = new YearlyRecord(yr1);
        
        assertEquals(true, yr.counts().equals(yr2.counts()));
        assertEquals(true, yr.words().equals(yr2.words()));
        assertEquals(true, yr.size() == (yr2.size()));
    }

    /** Tests put, words and rank operations of TimeSeries */
    @Test
    public void testMisc() {
        YearlyRecord yr = new YearlyRecord();
        ArrayList<String> expected = new ArrayList<String>();
        Set<Integer> counts = new TreeSet<Integer>();

        yr.put("quayside", 95);
        yr.put("surrogate", 336);
        yr.put("surrogate", 337);
        yr.put("surrogate", 338);
        yr.put("surrogate", 339);
        yr.put("surrogate", 328);
        yr.put("surrogate", 340);
        yr.put("merchantman", 324);
        yr.put("merchantman", 181);
        
        expected.add("quayside");
        expected.add("merchantman");
        expected.add("surrogate");
        expected.add("Hi");
        
        counts.add(95);
        counts.add(181);
        counts.add(340);
        counts.add(500);
        
        assertEquals(1, yr.rank("surrogate"));
        assertEquals(2, yr.rank("merchantman"));
        yr.put("Hi", 500);
        assertEquals(4, yr.rank("quayside"));
        assertEquals(-1, yr.rank("oioioi"));
        assertEquals(expected, yr.words());
        assertEquals(4, yr.size());
        assertEquals(counts, yr.counts());
    }
    
    /** Tests count operations of TimeSeries */
    @Test
    public void testCount() {
        YearlyRecord yr = new YearlyRecord();

        yr.put("quayside", 95);
        yr.put("surrogate", 336);
        yr.put("surrogate", 337);
        yr.put("surrogate", 338);
        yr.put("surrogate", 339);
        yr.put("surrogate", 328);
        yr.put("surrogate", 340);
        yr.put("merchantman", 181);
        
        assertEquals(340, yr.count("surrogate"));
        assertEquals(181, yr.count("merchantman"));
        assertEquals(95, yr.count("quayside"));
        assertEquals(0, yr.count("hello"));
    }

    public static String randomString() {
        int leftLimit = 97; // letter 'a'
        int rightLimit = 123; // letter 'z' + 1
        int targetStringLength = 10;
        StringBuilder buffer = new StringBuilder(targetStringLength);
        for (int i = 0; i < targetStringLength; i++) {
            int randomLimitedInt = StdRandom.uniform(leftLimit, rightLimit);
            buffer.append((char) randomLimitedInt);
        }
        return buffer.toString();
    }

    private int countRankCalls(int N, int maxTime) {
        YearlyRecord yr = new YearlyRecord();

        List<String> words = new ArrayList<String>();
        for (int i = 0; i < N; i += 1) {
            String rs = randomString();
            yr.put(rs, i);
            words.add(rs);
        }

        int rankCount = 0;
        Stopwatch sw = new Stopwatch();
        while (sw.elapsedTime() < maxTime) {
            String retrievalString = words.get(StdRandom.uniform(0, N));
            yr.rank(retrievalString);
            rankCount += 1;
        }

        System.out.println("Completed " + rankCount + " rank() ops in " + maxTime
                + " seconds on YearlyRecord with " + N + " entries.");
        return rankCount;
    }

    /*
     * Tests to see how many rank calls can be made in 2 seconds for very large
     * number of words
     */
    @Test(timeout = 10000)
    public void testRankCalls() {
        int maxTimeInSeconds = 2;
        int numWordsToPut = 1;
        countRankCalls(numWordsToPut, maxTimeInSeconds);

        numWordsToPut = 100;
        countRankCalls(numWordsToPut, maxTimeInSeconds);

        numWordsToPut = 1000;
        countRankCalls(numWordsToPut, maxTimeInSeconds);

        numWordsToPut = 10000;
        countRankCalls(numWordsToPut, maxTimeInSeconds);
    }

    public static void main(String[] args) {
        jh61b.junit.textui.runClasses(TestYearlyRecord.class);
    }
}
