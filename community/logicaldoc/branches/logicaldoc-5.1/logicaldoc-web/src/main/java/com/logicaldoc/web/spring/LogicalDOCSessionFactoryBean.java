package com.logicaldoc.web.spring;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.springframework.core.io.Resource;

import com.logicaldoc.util.Context;
import com.logicaldoc.util.PluginRegistry;

/**
 * Looking for extensions that wants to add mappings 
 * to the current session
 * 
 * @author Sebastian Wenzky
 * 
 */
public class LogicalDOCSessionFactoryBean extends
		org.springframework.orm.hibernate3.LocalSessionFactoryBean {

	@Override
	public void setMappingLocations(Resource[] mappingLocations) {

		List<Resource> mappingList = new LinkedList<Resource>(Arrays.asList(mappingLocations));

		// retrieve all additionally mappings
		String[] mappings = PluginRegistry.getInstance().getMappings();

		// iterate over all mappings and generate dynamically an
		// resource-implementations on using springs Resource-Framwork
		for (String mapping : mappings){
			Resource[] resources = Context.getInstance().getResources(mapping);
			
			for (Resource resource : resources) 
				mappingList.add( resource );
			
			
		}

		//fire and forget
		super.setMappingLocations(mappingList.toArray(new Resource[]{}));
	}
}
