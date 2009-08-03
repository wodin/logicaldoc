/*
 * Created on 16.11.2004
 */
package com.logicaldoc.core.text.analyzer;

/**
 * @author  Administrator
 */
public class WordEntry
{
    private int value;
    private String originWord;

    /**
     *
     */
    public WordEntry()
    {
        value = 0;
        originWord = "";
    } // end ctor WordEntry

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

    /**
     * @return  Returns the value.
     */
    public int getValue()
    {
        return value;
    } // end method getValue

    /**
     * @param value  The value to set.
     */
    public void setValue(int value)
    {
        this.value = value;
    } // end method setValue

    /**
     * Increments the value of this entry.
     *
     */
    public void incValue()
    {
        value++;
    } // end method incValue

    /**
     * Decrements the value of this entry.
     *
     */
    public void decValue()
    {
        value--;
    } // end method decValue
} // end class WordEntry
