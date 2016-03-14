package ngordnet;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * Perform tests of the NGramMap class only tests CountInYear and constructors
 * since other methods will be tested in plotting graphs
 */

public class TestNGramMap {
    NGramMap ngm;

    /** Tests constructor timing and CountInYear methods of NGram */
    @Test
    public void testConstructorCount() {
        final long startTime = System.nanoTime();
        ngm = new NGramMap("./ngrams/all_words.csv", "./ngrams/total_counts.csv");
        final long duration = System.nanoTime() - startTime;
        System.out.println("time: " + duration / 1000000000.0);
        
        assertEquals(0, ngm.countInYear("sadsadasd", 1999));
        assertEquals(173294, ngm.countInYear("airport", 2008));
        assertEquals(0, ngm.countInYear("airport", 100));
    }
    
    public static void main(String[] args) {
        jh61b.junit.textui.runClasses(TestNGramMap.class);
    }
}
