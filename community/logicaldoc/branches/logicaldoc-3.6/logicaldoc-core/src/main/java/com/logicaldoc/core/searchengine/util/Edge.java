package com.logicaldoc.core.searchengine.util;

/**
 * Created on 15.11.2004
 */
public class Edge
{
    private int thickness;
    private int id;

    /**
     *
     */
    public Edge()
    {
    } // end ctor Edge

    public Edge(
        int thick,
        int i)
    {
        thickness = thick;
        id = i;
    } // end ctor Edge

    /**
     * @return  Returns the id.
     */
    public int getId()
    {
        return id;
    } // end method getId

    /**
     * @param id  The id to set.
     */
    public void setId(int id)
    {
        this.id = id;
    } // end method setId

    /**
     * @return  Returns the thickness.
     */
    public int getThickness()
    {
        return thickness;
    } // end method getThickness

    /**
     * @param thickness  The thickness to set.
     */
    public void setThickness(int thickness)
    {
        this.thickness = thickness;
    } // end method setThickness
} // end class Edge
