package com.logicaldoc.core.searchengine.util;

import java.util.ArrayList;
import java.util.Collection;


/**
 * Created on 15.11.2004
 */
public class TermEntry
{
    private String name;
    private double value;
    private String originWord;
    private Collection<Edge> documents;

    /**
     *
     */
    public TermEntry()
    {
        name = "";
        value = 0d;
        originWord = "";
        documents = new ArrayList<Edge>();
    } // end ctor TermEntry

    /**
     * @return  Returns the name.
     */
    public String getName()
    {
        return name;
    } // end method getName

    /**
     * @param name  The name to set.
     */
    public void setName(String name)
    {
        this.name = name;
    } // end method setName

    /**
     * @return  Returns the documents.
     */
    public Collection getDocuments()
    {
        return documents;
    } // end method getDocuments

    /**
     * @param documents  The documents to set.
     */
    public void setDocuments(Collection<Edge> documents)
    {
        this.documents = documents;
    } // end method setDocuments

    /**
     * Adds the id of an document to the document list.
     * @param id
     */
    public void addDocument(Edge edge)
    {
        documents.add(edge);
    } // end method addDocument

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
    public double getValue()
    {
        return value;
    } // end method getValue

    /**
     * @param value  The value to set.
     */
    public void setValue(double value)
    {
        this.value = value;
    } // end method setValue
} // end class TermEntry