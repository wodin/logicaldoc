package com.logicaldoc.bm;

/**
 * Helper class to catch non-critical issues.
 */
public class LoaderClientException extends Exception
{
	private static final long serialVersionUID = 1711402412409691110L;

	public LoaderClientException(String msg)
    {
        super(msg);
    }
}
