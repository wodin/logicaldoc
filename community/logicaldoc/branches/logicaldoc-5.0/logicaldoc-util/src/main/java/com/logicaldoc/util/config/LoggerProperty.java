/*
 * Logger.java
 *
 * Created on 7. Februar 2004, 21:11
 */

package com.logicaldoc.util.config;

/**
 *
 * @author  Michael Scholz
 */
public class LoggerProperty
{
    private String appender = "";

    private String file = "";

    /** Creates a new instance of Logger */
    public LoggerProperty()
    {
    } // end ctor LoggerProperty

    public String getAppender()
    {
        return appender;
    } // end method getAppender

    public String getFile()
    {
        return file;
    } // end method getFile

    public void setAppender(String app)
    {
        appender = app;
    } // end method setAppender

    public void setFile(String fle)
    {
        file = fle;
    } // end method setFile

} // end class LoggerProperty
