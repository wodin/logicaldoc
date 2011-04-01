package com.logicaldoc.core;

import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.logicaldoc.core.task.TaskManager;
import com.logicaldoc.util.config.ContextProperties;
import com.logicaldoc.util.plugin.LogicalDOCPlugin;

/**
 * Plugin class for the Core plugin
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 3.5.0
 */
public class CorePlugin extends LogicalDOCPlugin {

	protected static Log log = LogFactory.getLog(CorePlugin.class);

	@Override
	protected void doStart() throws Exception {
		ContextProperties pbean = new ContextProperties();
		if (StringUtils.isEmpty(pbean.getProperty("id"))){
			pbean.setProperty("id", UUID.randomUUID().toString());
			pbean.write();
		}
		
		// Register scheduled tasks
		TaskManager manager = new TaskManager();
		manager.registerTasks();
	}
}
