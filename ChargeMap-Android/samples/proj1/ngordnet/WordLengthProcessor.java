package ngordnet;

public class WordLengthProcessor implements YearlyRecordProcessor {

    /*
     * Calculates the overage word length for the YearlyRecord YEARLYRECORD in
     * terms of a double.
     */
    @Override
    public double process(YearlyRecord yearlyRecord) {
        double totalCount = 0d;
        double numWords = 0d;
        for (String word : yearlyRecord.words()) {
            totalCount += word.length() * yearlyRecord.count(word);
            numWords += yearlyRecord.count(word);
        }
        if (numWords == 0d) {
            return 0;
        } else {
            return totalCount / numWords;
        }
    }

}
