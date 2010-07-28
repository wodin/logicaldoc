package com.logicaldoc.workflow.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.java.plugin.registry.Extension;

import com.logicaldoc.util.plugin.PluginRegistry;
import com.logicaldoc.workflow.transform.TransformModel;

/**
 * Configuration issues about the Workflows models that are the nodes usable in
 * a BPM like start, task, end.
 * <p>
 * The configuration is taken from the WorkflowModel extension point
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 5.0
 */
public class ModelConfiguration {
	protected static Log log = LogFactory.getLog(ModelConfiguration.class);

	private Map<String, TransformModel> transformers = new HashMap<String, TransformModel>();

	public Map<String, TransformModel> getTransformers() {
		if (transformers.isEmpty())
			init();
		return transformers;
	}

	private void init() {
		PluginRegistry registry = PluginRegistry.getInstance();
		Collection<Extension> exts = registry.getExtensions("logicaldoc-workflow", "WorkflowModel");

		for (Extension ext : exts) {
			String name = ext.getParameter("name").valueAsString();

			String transformerClass = ext.getParameter("transformer").valueAsString();
			try {
				if (StringUtils.isNotEmpty(transformerClass)) {
					TransformModel transformer = (TransformModel) Class.forName(transformerClass).newInstance();
					transformers.put(name, transformer);
				}
			} catch (Throwable t) {
				log.error("Class " + transformerClass + " not found or not a transformer");
			}
		}
	}
}