package com.logicaldoc.core.script;

/**
 * Utility functions for manipulating classes and resources.
 * 
 * @author Marco Meschieri - LogicalDOC
 * @since 7.5.1
 */
public class ClassTool {
	public Object newInstance(String classname) throws InstantiationException, IllegalAccessException,
			ClassNotFoundException {
		return Class.forName(classname).newInstance();
	}
}