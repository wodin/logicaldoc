/*
 * Entry.java
 *
 * Created on 23. Juli 2003, 22:09
 */

package com.logicaldoc.core.text.analyzer;

/**
 * This class encapsulates an entry consists of a top word and the quantity of this top word in the text.
 * @author   Michael Scholz
 * @version  1.0
 */
public class Entry
{
    private String word = "";
    private String originWord = "";

    private int number = 0;

    /**
     * Creates a new instance of Entry.
     */
    public Entry()
    {
    } // end ctor Entry

    /**
     * Creates a new instance of Entry.
     * @param wd - Found top word.
     * @param nb - Quantity of the top word in the text.
     */
    public Entry(
        String wd,
        int    nb)
    {
        word = wd;
        number = nb;
    } // end ctor Entry

    /**
     * Returns the top word.
     */
    public String getWord()
    {
        return word;
    } // end method getWord

    /**
     * Returns the quantity of the top word in the text.
     */
    public int getNumber()
    {
        return number;
    } // end method getNumber

    /**
     * Sets the top word.
     */
    public void setWord(String wd)
    {
        word = wd;
    } // end method setWord

    /**
     * Sets the quantity of the top word.
     */
    public void setNumber(int nb)
    {
        number = nb;
    } // end method setNumber

    /**
     * @return  Returns the originWord.
     */
    public String getOriginWord()
    {
        return originWord;
    } // end method getOriginWord

    /**
     * @param originWord  The originWord to set.
     */
    public void setOriginWord(String originWord)
    {
        this.originWord = originWord;
    } // end method setOriginWord
} // end class Entry
