package com.logicaldoc.gui.frontend.client.services;

import java.util.Date;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.logicaldoc.gui.common.client.InvalidSessionException;
import com.logicaldoc.gui.common.client.beans.GUICalendarEvent;

/**
 * The client side stub for the Calendar Service. This service allows the
 * handling of calendar events.
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.7
 */
@RemoteServiceRelativePath("calendar")
public interface CalendarService extends RemoteService {

	/**
	 * Saves an event
	 */
	public void saveEvent(String sid, GUICalendarEvent event) throws InvalidSessionException;

	/**
	 * Gets an event
	 */
	public GUICalendarEvent getEvent(String sid, long eventId) throws InvalidSessionException;

	/**
	 * Deletes an event. If the event is a master, in any case all the
	 * occurrences will be deleted too.
	 */
	public void deleteEvent(String sid, long eventId) throws InvalidSessionException;

	/**
	 * Counts the number of events that start from now until a given date.
	 * 
	 * @param sid The session identifier
	 * @param end The and date
	 * @return The number of found events
	 * 
	 * @throws InvalidSessionException
	 */
	public int countUserEvents(String sid, Date end) throws InvalidSessionException;
}
