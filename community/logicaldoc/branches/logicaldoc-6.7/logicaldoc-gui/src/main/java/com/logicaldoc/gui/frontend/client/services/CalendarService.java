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
	 * Searches for events.
	 * 
	 * @param startDate Start date (optional)
	 * @param endDate End date (optional)
	 * @param expireFrom (optional)
	 * @param expireTo (optional)
	 * @param frequency The frequency of the event (1,15, 30 ... optional)
	 * @param title The title (used with like operator, optional)
	 * @param title Maximum number of records (optional)
	 * @param status The title (used with like operator, optional)
	 * 
	 * @return The list of events ordered by ascending date
	 */
	public GUICalendarEvent[] find(String sid, Date startDate, Date endDate, Date expireFrom, Date expireTo,
			Integer frequency, String title, Integer status, Integer maxRecords) throws InvalidSessionException;

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
