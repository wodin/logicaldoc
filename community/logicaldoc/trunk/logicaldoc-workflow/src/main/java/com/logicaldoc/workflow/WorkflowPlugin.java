package com.logicaldoc.workflow;


import java.io.IOException;
import java.util.Properties;

import org.hibernate.MappingNotFoundException;
import org.hibernate.cfg.Configuration;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.java.plugin.PluginManager;

import com.logicaldoc.core.spring.PropertiesPlaceHolder;
import com.logicaldoc.util.Context;
import com.logicaldoc.util.event.SystemEvent;
import com.logicaldoc.util.event.SystemEventStatus;
import com.logicaldoc.util.plugin.LogicalDOCPlugin;

@SuppressWarnings("unused")
public class WorkflowPlugin extends LogicalDOCPlugin{

	@SuppressWarnings("serial")
	public class ClassLoaderConfiguration extends Configuration {
	
		@Override
		public Configuration addResource(String resourceName){
			Configuration configuration = null;
			try {
				configuration = super.addResource(resourceName);
			}catch(MappingNotFoundException e){System.out.println("WARNING: " + resourceName + " can not be found normally");}
			
			if(configuration == null){
				
				try {
					//configuration =  addInputStream( null );
					String s = "";
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			
			}
			return configuration;
		}
	}
	
	private class Event extends SystemEvent {
		
		private PluginManager manager;
		
		public Event(PluginManager manager){
			this();
			this.manager = manager;
		}
		
		public Event() {
			super(SystemEventStatus.BEANS_AVAILABLE);
		}

		@Override
		public void processEvent() {
			Context ctx = Context.getInstance();
			Configuration config = new Configuration();
			PropertiesPlaceHolder cfgPlaceHolder = (PropertiesPlaceHolder)ctx.getBean("PropertyPlaceholderConfigurer");
			Properties properties = null;
			try {
				properties = cfgPlaceHolder.mergeProperties();
			} catch (IOException e) {
				e.printStackTrace();
			}
			

			/*
			  		Setting up following properties:
			 
			      <property name="hibernate.dialect">org.hibernate.dialect.MySQLDialect</property>
				  <property name="hibernate.connection.driver_class">com.mysql.jdbc.Driver</property>
				  <property name="hibernate.connection.url">jdbc:mysql://localhost:3306/jbpm_test</property>
				  <property name="hibernate.connection.username"></property>
				  <property name="hibernate.connection.password"></property>
			 */
			
			Configuration configuration = new Configuration();
			configuration.configure(this.getClass().getResource("/workflow-hibernate.cfg.xml"));
			configuration.setProperty("hibernate.dialect", properties.getProperty("hibernate.dialect"));
			configuration.setProperty("hibernate.connection.driver_class", properties.getProperty("jdbc.driver"));
			configuration.setProperty("hibernate.connection.url", properties.getProperty("jdbc.url"));
			configuration.setProperty("hibernate.connection.username", properties.getProperty("jdbc.username"));
			configuration.setProperty("hibernate.connection.password", properties.getProperty("jdbc.password"));
			configuration.setProperty("hibernate.hbm2ddl.auto", "create");
			

			SchemaExport schema = new SchemaExport(configuration);
			
			schema.drop(true, true);
			schema.create(true, true);
			
			if(properties == null)
				throw new RuntimeException("Properties file must not be null");
			
		}
		
	}
	
	@Override
	protected void start() throws Exception {
		Context.addListener(new Event(getManager()));
		
	}
	
	protected void install() throws Exception {
		
		Configuration installConfiguration = new Configuration();
		Context.addListener(new Event());
		String s = "";
	}
}
