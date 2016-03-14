package ngordnet;

import edu.princeton.cs.introcs.StdIn;
import edu.princeton.cs.introcs.In;

/**
 * Provides a simple user interface for exploring WordNet and NGram data.
 * 
 * @author Yilun Chen
 */
public class NgordnetUI {

    /*
     * Main method handling the UI of Ngordnet. Returns when the "quit" command
     * is invoked.
     */
    public static void main(String[] args) {
        In in = new In("./ngordnet/ngordnetui.config");
        System.out.println("Reading ngordnetui.config...");
        String wordFile = in.readString();
        String countFile = in.readString();
        String synsetFile = in.readString();
        String hyponymFile = in.readString();
        System.out.println("\nBased on ngordnetui.config, using the following: " + wordFile + ", "
                + countFile + ", " + synsetFile + ", and " + hyponymFile + ".");
        System.out.println("\nLoading files...");
        long startTime = System.nanoTime();
        YearlyRecordProcessor yrp = new WordLengthProcessor();
        System.out.println("\nDone loading WordLengthProcessor in "
                + (System.nanoTime() - startTime) / 1000000000.0 + " seconds.\n ");
        startTime = System.nanoTime();
        WordNet wNet = new WordNet(synsetFile, hyponymFile);
        System.out.println("Done loading WordNet in " + (System.nanoTime() - startTime)
                / 1000000000.0 + " seconds.\n ");
        startTime = System.nanoTime();
        NGramMap nGram = new NGramMap(wordFile, countFile);
        System.out.println("Done loading nGramMap in " + (System.nanoTime() - startTime)
                / 1000000000.0 + " seconds.\n ");

        In inUtil = new In();
        String utilStr = "";
        int utilInt = 0;
        int startDate = nGram.totalCountHistory().firstKey();
        int endDate = nGram.totalCountHistory().lastKey();
        while (true) {
            try {
                System.out.print("> ");
                String[] rawTokens = StdIn.readLine().split(" ");
                String command = rawTokens[0];
                String[] tokens = new String[rawTokens.length - 1];
                System.arraycopy(rawTokens, 1, tokens, 0, rawTokens.length - 1);
                switch (command) {
                case "quit":
                    return;
                case "help":
                    help(tokens, inUtil, utilStr);
                    break;
                case "range":
                    if (tokens.length > 2) {
                        throw new ArrayIndexOutOfBoundsException();
                    }
                    if (Integer.parseInt(tokens[0]) >= 0 && Integer.parseInt(tokens[1]) >= 0) {
                        startDate = Integer.parseInt(tokens[0]);
                        endDate = Integer.parseInt(tokens[1]);
                    } else {
                        throw new ArrayIndexOutOfBoundsException();
                    }
                    break;
                case "count":
                    count(tokens, utilStr, utilInt, nGram);
                    break;
                case "hyponyms":
                    hyponyms(tokens, utilStr, wNet);
                    break;
                case "history":
                    Plotter.plotAllWords(nGram, tokens, startDate, endDate);
                    break;
                case "hypohist":
                    Plotter.plotCategoryWeights(nGram, wNet, tokens, startDate, endDate);
                    break;
                case "wordlength":
                    wordlength(tokens, nGram, startDate, endDate, yrp);
                    break;
                case "zipf":
                    zipf(tokens, nGram);
                    break;
                default:
                    System.out.println("Invalid command.");
                    break;
                }
            } catch (NullPointerException e) {
                System.out.println("Error: data does not exist in dataset.");
            } catch (IndexOutOfBoundsException e) {
                System.out.println("Error: wrong number of arguments.");
            } catch (IllegalArgumentException e) {
                System.out.println("Error: " + e + ".");
            }
        }
    }

    /*
     * Reads in the help.txt and displays the contents on screen. Takes in
     * string array TOKENS, file reader INUTIL and string UTILSTR. Returns void.
     */
    private static void help(String[] tokens, In inUtil, String utilStr) {
        if (tokens.length > 0) {
            throw new ArrayIndexOutOfBoundsException();
        }
        inUtil = new In("ngrams/help.txt");
        utilStr = inUtil.readAll();
        System.out.println(utilStr);
    }

    /*
     * Prints the number of times a word appeared in a year. Takes in string
     * array TOKENS, string UTILSTR, integer UTILINT, and NGramMap object NGRAM.
     * Returns void.
     */
    private static void count(String[] tokens, String utilStr, int utilInt, NGramMap nGram) {
        if (tokens.length > 2) {
            throw new ArrayIndexOutOfBoundsException();
        }
        utilStr = tokens[0];
        utilInt = Integer.parseInt(tokens[1]);
        System.out.println(nGram.countInYear(utilStr, utilInt));
    }

    /*
     * Prints out the hyponyms of a word. Takes in string array TOKENS, string
     * UTILSTR, and WordNet object WNET. Returns void.
     */
    private static void hyponyms(String[] tokens, String utilStr, WordNet wNet) {
        if (tokens.length > 1) {
            throw new ArrayIndexOutOfBoundsException();
        }
        utilStr = tokens[0];
        System.out.println(wNet.hyponyms(utilStr));
    }

    /*
     * Plots the average wordlength of all words from STARTDATE to ENDDATE.
     * Takes in string array TOKENS, NGramMap object NGRAM, and integers
     * STARTDATE and ENDDATE. Returns void.
     */
    private static void wordlength(String[] tokens, NGramMap nGram, int startDate, int endDate,
            YearlyRecordProcessor yrp) {
        if (tokens.length > 0) {
            throw new ArrayIndexOutOfBoundsException();
        }
        Plotter.plotTS(nGram.processedHistory(startDate, endDate, yrp),
                "Avgerage Word Length by Year", "Avg. Word Length", "Years", "Avg. Word Length");
    }

    /*
     * Plots the zipf law plot for all words in a certain year. Takes in string
     * array TOKENS and NGramMap NGRAM. Returns void.
     */
    private static void zipf(String[] tokens, NGramMap nGram) {
        if (tokens.length > 1) {
            throw new ArrayIndexOutOfBoundsException();
        }
        Plotter.plotZipfsLaw(nGram, Integer.parseInt(tokens[0]));
    }
}
