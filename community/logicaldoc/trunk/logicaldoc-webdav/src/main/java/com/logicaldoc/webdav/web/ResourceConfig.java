package com.logicaldoc.webdav.web;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jackrabbit.server.io.MimeResolver;
import org.apache.jackrabbit.server.io.PropertyManager;
import org.apache.jackrabbit.server.io.PropertyManagerImpl;
import org.apache.jackrabbit.webdav.simple.DefaultItemFilter;
import org.apache.jackrabbit.webdav.simple.ItemFilter;

import com.logicaldoc.webdav.io.manager.IOManager;

/**
 * For more informations, please visit
 * {@link org.apache.jackrabbit.webdav.simple.ResourceConfig}
 * 
 * @author Sebastian Wenzky
 * 
 */
public class ResourceConfig {

	protected static Log log = LogFactory.getLog(ResourceConfig.class);

	private ItemFilter itemFilter;
	private IOManager ioManager;
	private PropertyManager propManager;
	private MimeResolver mimeResolver;

	public void setIOManager(IOManager ioManager) {
		this.ioManager = ioManager;
	}

	public IOManager getIOManager() {
		return ioManager;
	}

	public PropertyManager getPropertyManager() {
		if (propManager == null) {
			log.debug("ResourceConfig: missing property-manager > "
					+ "building default.");
			propManager = PropertyManagerImpl.getDefaultManager();
		}
		return propManager;
	}

	public ItemFilter getItemFilter() {
		if (itemFilter == null) {
			log.debug("ResourceConfig: missing resource filter > "
					+ "building DefaultItemFilter ");
			itemFilter = new DefaultItemFilter();
		}
		return itemFilter;
	}

	public MimeResolver getMimeResolver() {
		return mimeResolver;
	}
}
