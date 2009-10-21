package com.logicaldoc.workflow;

import java.io.IOException;
import java.util.Properties;

import org.hibernate.cfg.Configuration;
import org.jbpm.persistence.db.DbPersistenceServiceFactory;

import com.logicaldoc.core.spring.PropertiesPlaceHolder;
import com.logicaldoc.util.Context;

/**
 * Extension of the standard jBPM persistence service, that gets the DB
 * connection parameters from LogicalDOC configuration.
 * 
 * @author Sebastian Wenzky
 * @since 5.0
 */
public class PersistenceFactory extends DbPersistenceServiceFactory {

	private static final long serialVersionUID = 4845270442669524870L;

	public PersistenceFactory() {
		setTransactionEnabled(false);
	}

	/**
	 * 
	 * Setting up following properties:
	 * 
	 * <property name="hibernate.dialect">org.hibernate.dialect.MySQLDialect</property>
	 * <property name="hibernate.connection.driver_class">com.mysql.jdbc.Driver</property>
	 * <property
	 * name="hibernate.connection.url">jdbc:mysql://localhost:3306/jbpm_test</property>
	 * <property name="hibernate.connection.username"></property> <property
	 * name="hibernate.connection.password"></property>
	 * 
	 */
	@Override
	public synchronized Configuration getConfiguration() {
		Context ctx = Context.getInstance();
		PropertiesPlaceHolder cfgPlaceHolder = (PropertiesPlaceHolder) ctx.getBean("PropertyPlaceholderConfigurer");
		Properties properties = null;
		try {
			properties = cfgPlaceHolder.mergeProperties();
		} catch (IOException e) {
			e.printStackTrace();
		}

		Configuration configuration = super.getConfiguration();
		configuration.setProperty("hibernate.dialect", properties.getProperty("hibernate.dialect"));
		configuration.setProperty("hibernate.connection.driver_class", properties.getProperty("jdbc.driver"));
		configuration.setProperty("hibernate.connection.url", properties.getProperty("jdbc.url"));
		configuration.setProperty("hibernate.connection.username", properties.getProperty("jdbc.username"));
		configuration.setProperty("hibernate.connection.password", properties.getProperty("jdbc.password"));

		return configuration;
	}
}