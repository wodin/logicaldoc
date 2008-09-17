/*
 * StopTable.java
 *
 * Created on 23. Juli 2003, 19:20
 */

package com.logicaldoc.core.text.analyze;

import java.util.Hashtable;


/**
 * This class creates a hashtable filled with stop words.
 * @author  Michael Scholz
 * @version 1.0
 */
public class StopTable
{
    /**
     * This method transforms the array of stop words into a hashtable of stop words.
     * @param stopwords - Array of stop words.
     */
    public final static Hashtable setStopWords(String[] stopwords)
    {
        Hashtable<String, String> stoptable =
            new Hashtable<String, String>(stopwords.length);

        for (int i = 0; i < stopwords.length; i++) {
            stoptable.put(stopwords[i], stopwords[i]);
        }

        return stoptable;
    } // end method setStopWords

} // end class StopTable
