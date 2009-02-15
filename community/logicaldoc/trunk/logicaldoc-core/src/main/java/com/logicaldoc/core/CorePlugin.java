package com.logicaldoc.core;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.java.plugin.registry.Extension;
import org.java.plugin.registry.ExtensionPoint;

import com.logicaldoc.core.security.authentication.ForeignSystemComponent;
import com.logicaldoc.core.task.TaskManager;
import com.logicaldoc.util.config.ContextConfigurator;
import com.logicaldoc.util.plugin.LogicalDOCPlugin;

/**
 * Plugin class for the Core plugin
 * 
 * @author Marco Meschieri - Logical Objects
 * @version $Id:$
 * @since 3.5.0
 */
public class CorePlugin extends LogicalDOCPlugin {
	protected static Log log = LogFactory.getLog(CorePlugin.class);

	@Override
	protected void doStart() throws Exception {
		
		//Register scheduled tasks
		TaskManager manager=new TaskManager();
		manager.registerTasks();
		

		
		ContextConfigurator cfg = new ContextConfigurator();
		
		
		//authenticationChain
		
		List<String> authenticationComponents = new LinkedList<String>();
		ExtensionPoint toolExtPoint = getManager().getRegistry()
				.getExtensionPoint(getDescriptor().getId(),
						"AuthenticationComponents");
		for (Iterator<Extension> it = toolExtPoint.getConnectedExtensions()
				.iterator(); it.hasNext();) {
			Extension ext = it.next();
			authenticationComponents.add(ext.getParameter("beanId").valueAsString());
		}
		cfg.clearPropertyValue("authenticationChain", "authenticationComponents");
		cfg.addPropertyBeanRefList("authenticationChain", "authenticationComponents", authenticationComponents);
		
		cfg.write();
	}
}
