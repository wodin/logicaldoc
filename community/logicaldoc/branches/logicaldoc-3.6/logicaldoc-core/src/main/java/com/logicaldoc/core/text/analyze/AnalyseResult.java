/*
 * Created on 21.03.2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.logicaldoc.core.text.analyze;

import java.util.Hashtable;


/**
 * @author Michael Scholz
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class AnalyseResult
{
    private long wordCount = 0;

    private Hashtable<String, WordEntry> wordTable =
        new Hashtable<String, WordEntry>();

    public AnalyseResult()
    {
    } // end ctor AnalyseResult

    public long getWordCount()
    {
        return wordCount;
    } // end method getWordCount

    public Hashtable<String, WordEntry> getWordTable()
    {
        return wordTable;
    } // end method getWordTable

    public void setWordCount(long l)
    {
        wordCount = l;
    } // end method setWordCount

    public void setWordTable(Hashtable<String, WordEntry> hashtable)
    {
        wordTable = hashtable;
    } // end method setWordTable

} // end class AnalyseResult