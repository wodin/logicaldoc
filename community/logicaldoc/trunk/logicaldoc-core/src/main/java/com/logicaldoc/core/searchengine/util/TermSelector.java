package com.logicaldoc.core.searchengine.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;

import com.logicaldoc.core.document.Term;
import com.logicaldoc.core.document.dao.TermDAO;
import com.logicaldoc.core.searchengine.Result;
import com.logicaldoc.util.Context;


/**
 * Created on 15.11.2004
 */
public class TermSelector
{
    private Hashtable<String, TermEntry> terms;

    /**
     * Creates a new TermSelector.
     */
    public TermSelector()
    {
        terms = new Hashtable<String, TermEntry>();
    } // end ctor TermSelector

    /**
     * Selects n terms the mostly occurred in all document being in a given search result.
     * @param searchResult
     * @param count Count of terms to be returned.
     * @return
     */
    public Collection getTopTerms(
        Collection searchResult,
        int        count)
    {
        Iterator iter = searchResult.iterator();
        TermDAO termDao = (TermDAO)Context.getInstance().getBean(TermDAO.class);

        while (iter.hasNext()) {
            Result rs = (Result) iter.next();
            Collection termcoll =
                termDao.findByDocId(rs.getDocId());
            Iterator termiter = termcoll.iterator();

            while (termiter.hasNext()) {
                Term term = (Term) termiter.next();

                if (terms.containsKey(term.getStem())) {
                    TermEntry entry = (TermEntry) terms.get(term.getStem());

                    entry.setValue(entry.getValue() + term.getValue());

                    if (term.getOriginWord().length()
                            < entry.getOriginWord().length()) {
                        entry.setOriginWord(term.getOriginWord());
                    }

                    double value = term.getValue();
                    Edge edge = new Edge();

                    if (value > 30.0) {
                        edge.setThickness(3);
                    } else if (value > 10.0) {
                        edge.setThickness(2);
                    } else {
                        edge.setThickness(1);
                    }

                    edge.setId(term.getDocId());
                    entry.addDocument(edge);
                } else {
                    TermEntry entry = new TermEntry();
                    entry.setName(term.getStem());

                    //entry.setWordCount(term.getWordCount());
                    //entry.setValue(term.getValue() * term.getWordCount());
                    entry.setValue(term.getValue());
                    entry.setOriginWord(term.getOriginWord());

                    double value = term.getValue();
                    Edge edge = new Edge();

                    if (value > 30.0) {
                        edge.setThickness(3);
                    } else if (value > 10.0) {
                        edge.setThickness(2);
                    } else {
                        edge.setThickness(1);
                    }

                    edge.setId(term.getDocId());
                    entry.addDocument(edge);
                    terms.put(term.getStem(), entry);
                }
            }
        }

        Collection<TermEntry> coll = new ArrayList<TermEntry>(count);

        if (terms.size() > 0) {
            for (int i = 0; i < count; i++) {
                TermEntry e = getTopWord();
                coll.add(e);
            }
        }

        return coll;
    } // end method getTopTerms

    protected TermEntry getTopWord()
    {
        TermEntry entry = new TermEntry();
        Enumeration enum1 = terms.keys();
        String topterm = "";
        double topvalue = 0d;

        while (enum1.hasMoreElements()) {
            String term = (String) enum1.nextElement();
            TermEntry te = (TermEntry) terms.get(term);

            //double val = (double)te.getWordCount() / te.getValue();
            double val = te.getValue();

            if (val > topvalue) {
                topvalue = val;
                topterm = term;
                entry = te;
            }
        }

        terms.remove(topterm);
        return entry;
    } // end method getTopWord
} // end class TermSelector
