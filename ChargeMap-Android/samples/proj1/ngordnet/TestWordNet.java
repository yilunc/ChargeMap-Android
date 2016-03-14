package ngordnet;

import static org.junit.Assert.*;

import java.util.HashSet;

import org.junit.Test;

/**
 * Perform tests of the WordNet class
 */

public class TestWordNet {

    /** Tests basic operations of WordNet */
    @Test
    public void testBasic() {
        WordNet wn = new WordNet("./wordnet/synsets11.txt", "./wordnet/hyponyms11.txt");

        assertEquals(true, wn.isNoun("jump"));
        assertEquals(true, wn.isNoun("leap"));
        assertEquals(true, wn.isNoun("demotion"));
        assertEquals(true, wn.isNoun("change"));
        assertEquals(true, wn.isNoun("nasal_decongestant"));
        assertEquals(false, wn.isNoun("oi oi oi"));
        assertEquals(false, wn.isNoun("nasal"));
        assertEquals(false, wn.isNoun("jump1"));
    }

    /** Tests nouns() method of WordNet */
    @Test
    public void testNouns() {
        WordNet wn = new WordNet("./wordnet/synsets11.txt", "./wordnet/hyponyms11.txt");

        HashSet<String> expected = new HashSet<String>();
        expected.add("augmentation");
        expected.add("nasal_decongestant");
        expected.add("change");
        expected.add("action");
        expected.add("actifed");
        expected.add("antihistamine");
        expected.add("increase");
        expected.add("descent");
        expected.add("parachuting");
        expected.add("leap");
        expected.add("demotion");
        expected.add("jump");
        assertEquals(expected, wn.nouns());
        assertEquals(true, expected.equals(wn.nouns()));
    }
    
    /** Tests nouns() method of WordNet for a wrong result*/
    @Test
    public void testNounsWrong() {
        WordNet wn = new WordNet("./wordnet/synsets11.txt", "./wordnet/hyponyms11.txt");

        HashSet<String> expected = new HashSet<String>();
        expected.add("augmentation");
        expected.add("nasal_decongestant");
        expected.add("change");
        expected.add("action");
        expected.add("actifed");
        expected.add("antihistamine");
        expected.add("increase");
        expected.add("descent");
        expected.add("parachuting");
        expected.add("leap");
        expected.add("demotion");
        expected.add("jump");
        expected.add("hello");
        assertEquals(false, expected.equals(wn.nouns()));
    }

    /** Tests hyponyms() method of WordNet */
    @Test
    public void testHypo() {
        WordNet wn = new WordNet("./wordnet/synsets11.txt", "./wordnet/hyponyms11.txt");

        HashSet<String> expected = new HashSet<String>();
        expected.add("augmentation");
        expected.add("increase");
        expected.add("leap");
        expected.add("jump");
        assertEquals(expected, wn.hyponyms("increase"));
        assertEquals(true, expected.equals(wn.hyponyms("increase")));
    }

    /** Tests hyponyms() method of WordNet */
    @Test
    public void testHypoBroken() {
        WordNet wn = new WordNet("./wordnet/synsets11.txt", "./wordnet/hyponyms11.txt");

        HashSet<String> expected = new HashSet<String>();
        expected.add("augmentation");
        expected.add("increase");
        expected.add("leap");
        expected.add("jump");
        expected.add("saltation");
        assertEquals(false, expected.equals(wn.hyponyms("increase")));
    }

    public static void main(String[] args) {
        jh61b.junit.textui.runClasses(TestWordNet.class);
    }
}
