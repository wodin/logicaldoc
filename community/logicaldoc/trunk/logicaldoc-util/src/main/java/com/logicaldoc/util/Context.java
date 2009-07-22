package com.logicaldoc.util;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.ContextStartedEvent;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.core.io.Resource;

import com.logicaldoc.util.event.SystemEvent;
import com.logicaldoc.util.event.SystemEventStatus;

/**
 * Utility class collecting static methods related to spring's context.
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 3.0
 */
public class Context implements ApplicationContextAware, ApplicationListener {

	private static HashMap<SystemEventStatus, LinkedList<SystemEvent>> systemEvents = new HashMap<SystemEventStatus, LinkedList<SystemEvent>>();

	// Singleton instance
	private static Context instance;

	// The Spring's application context
	private static ApplicationContext applicationContext;

	private Context() {
		Context.instance = this;
	}

	public static Context getInstance() {
		return instance;
	}

	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		Context.applicationContext = applicationContext;
	}

	/**
	 * Retrieves a bean registered in the Spring context.
	 * 
	 * @param id The bean identifier
	 * @return The bean instance
	 */
	public Object getBean(String id) {
		return applicationContext.getBean(id);
	}

	/**
	 * Retrieves a bean registered in the Spring context using a class name. At
	 * first the fully qualified class name is checked, then if nothing was
	 * found the simple class name is used as bean id.
	 * 
	 * @param clazz The bean identifier as class name
	 * @return The bean instance
	 */
	public Object getBean(Class clazz) {
		String id = clazz.getName();

		if (!applicationContext.containsBean(id))
			id = id.substring(id.lastIndexOf('.') + 1);

		return getBean(id);
	}

	/**
	 * Reloads the Spring application context.
	 * <p>
	 * NOTE: use carefully, invoke only during setup when the config.xml is
	 * changed
	 */
	public static void refresh() {
		if (applicationContext != null) {
			try {
				((AbstractApplicationContext) applicationContext).stop();
				((AbstractApplicationContext) applicationContext).start();
				((AbstractApplicationContext) applicationContext).refresh();
			} catch (Throwable e) {
//				e.printStackTrace();
			}
		}
	}

	/**
	 * Adds an Listener to a particular Event given from
	 * 
	 * @see {@link SystemEvent#getSystemStatus()}
	 * @param evt
	 */
	public static void addListener(SystemEvent evt) {

		synchronized (systemEvents) {
			LinkedList<SystemEvent> evts = Context.systemEvents.get(evt.getSystemStatus());

			// firstly we have to initialize every systemstatus
			if (evts == null) {
				evts = new LinkedList<SystemEvent>();
				Context.systemEvents.put(evt.getSystemStatus(), evts);
			}

			evts.add(evt);
		}
	}

	/**
	 * Removes a particular Listener from the list
	 * 
	 * @param evt
	 */
	public static void removeListener(SystemEvent evt) {
		synchronized (systemEvents) {
			LinkedList<SystemEvent> evts = Context.systemEvents.get(evt.getSystemStatus());
			if (evts == null || evts.size() == 0)
				return;

			evts.remove(evt);
		}
	}

	public Resource[] getResources(String resourcePattern) {
		try {
			return this.applicationContext.getResources(resourcePattern);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Returns a Spring-Resource
	 * 
	 * @return
	 */
	public Resource getResource(String resourceLocation) {
		return this.applicationContext.getResource(resourceLocation);
	}

	/**
	 * Processes a newly incoming event on appropriated events that registered
	 * itself on it
	 */
	@Override
	public synchronized void onApplicationEvent(ApplicationEvent event) {
		if ((event instanceof ContextStartedEvent) || (event instanceof ContextRefreshedEvent)) {
			processEvents(SystemEventStatus.BEANS_AVAILABLE);
		} else if (event instanceof ContextClosedEvent) {
			processEvents(SystemEventStatus.SYSTEM_DESTORY);
		}

	}

	private void processEvents(SystemEventStatus st) {
		LinkedList<SystemEvent> evts = Context.systemEvents.get(st);
		
		// surely its possible that no one event have been
		// registered to the current spring event
		if (evts == null || evts.size() == 0)
			return;

		for (SystemEvent evt : evts) {
			evt.processEvent();
		}
	}
}
