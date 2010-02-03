package com.logicaldoc.core.document;

import java.lang.reflect.InvocationTargetException;

import org.apache.commons.beanutils.BeanUtils;

/**
 * Basic concrete implementation of <code>AbstractDocument</code>
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 1.0
 */
public class Document extends AbstractDocument {
	public Document() {
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		Document cloned = new Document();
		try {
			BeanUtils.copyProperties(cloned, this);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return cloned;
	}
}