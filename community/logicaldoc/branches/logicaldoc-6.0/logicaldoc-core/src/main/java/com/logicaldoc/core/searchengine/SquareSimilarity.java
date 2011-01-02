package com.logicaldoc.core.searchengine;

import org.apache.lucene.search.Similarity;

/**
 * Similarity class for lucene searcher classes.
 * Created on 14.01.2005
 * 
 * @author Michael Scholz
 */
public class SquareSimilarity extends Similarity {
    
    private static final long serialVersionUID = 1L;

    public SquareSimilarity() {
    } 

    public float lengthNorm(String fieldName, int numTerms) {
        return (float) (1.0D / Math.sqrt(Math.sqrt(numTerms)));
    } 

    public float queryNorm(float sumOfSquaredWeights) {
        return (float) (1.0D / Math.sqrt(sumOfSquaredWeights));
    } 

    public float tf(float freq) {
        return (float) (Math.sqrt(freq));
    } 

    public float sloppyFreq(int distance) {
        return 1.0F / (float) (distance + 1);
    } 

    public float idf(int docFreq, int numDocs) {
        return (float) (Math.sqrt(Math.log((double) numDocs / (double) (docFreq + 1)) + 1.0D));
    } 

    public float coord(int overlap, int maxOverlap) {
        return (float) overlap / (float) maxOverlap;
    } 
} 
