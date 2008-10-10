/*
 * GroupRules.java
 *
 * Created on 18. November 2003, 19:44
 */

package com.logicaldoc.core.security;

/**
 *
 * @author Michael Scholz
 */
public class GroupRule
{
    private String groupName;

    private boolean read;

    private boolean write;

    /**
     * true, if this rule can be changed in a dialog
     */
    private boolean enabled;

    /** Creates a new instance of GroupRules */
    public GroupRule()
    {
        groupName = "";
        read = false;
        write = false;
        enabled = false;
    } // end ctor GroupRule

    public String getGroupName()
    {
        return groupName;
    } // end method getGroupName

    public boolean getRead()
    {
        return read;
    } // end method getRead

    public boolean getWrite()
    {
        return write;
    } // end method getWrite

    /**
     * @see GroupRule#enabled
     */
    public boolean getEnabled()
    {
        return enabled;
    } // end method getEnabled

    public void setGroupName(String gname)
    {
        groupName = gname;
    } // end method setGroupName

    public void setRead(boolean r)
    {
        read = r;
    } // end method setRead

    public void setWrite(boolean w)
    {
        write = w;
    } // end method setWrite

    /**
     * @see GroupRule#enabled
     */
    public void setEnabled(boolean e)
    {
        enabled = e;
    } // end method setEnabled
} // end class GroupRule