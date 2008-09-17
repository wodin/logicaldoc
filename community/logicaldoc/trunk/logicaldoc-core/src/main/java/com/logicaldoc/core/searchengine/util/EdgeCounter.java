package com.logicaldoc.core.searchengine.util;

import java.util.Collection;
import java.util.Iterator;


/*
 * Created on 15.11.2004
 */
public class EdgeCounter
{
    /**
     *
     */
    public static int count(Collection keywords)
    {
        Iterator iter = keywords.iterator();
        int count = 0;

        while (iter.hasNext()) {
            try {
                TermEntry entry = (TermEntry) iter.next();
                count += entry.getDocuments()
                        .size();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return count;
    } // end method count

} // end class EdgeCounter
