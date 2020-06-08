package io.jdbib.deduplication;

import org.apache.commons.text.similarity.LevenshteinDistance;

public final class Similarity {
    private static final LevenshteinDistance LEVENSHTEIN_DISTANCE = LevenshteinDistance.getDefaultInstance();

    private Similarity() {
    }

    public static double compareWordByWord(String s1, String s2) {
        s1 = s1.toLowerCase();
        s2 = s2.toLowerCase();
        String[] w1 = s1.split("\\s");
        String[] w2 = s2.split("\\s");
        int minSize = Math.min(w1.length, w2.length);

        int misses = 0;
        for (int i = 0; i < minSize; i++) {
            double corr = calculateSimilarity(w1[i], w2[i]);
            if (corr < 0.75) {
                misses++;
            }
        }
        return 1. - (double) misses / (double) minSize;
    }

    private static double calculateSimilarity(String str1, String str2) {
        String shorter, longer;
        if (str1.length() < str2.length()) {
            shorter = str1;
            longer = str2;
        } else {
            shorter = str2;
            longer = str1;
        }
        int longerLength = longer.length();

        if (longerLength == 0) {
            return 1.;
        }
        int distance = LEVENSHTEIN_DISTANCE.apply(shorter, longer);
        return 1. - (double) distance / (double) longerLength;
    }
}