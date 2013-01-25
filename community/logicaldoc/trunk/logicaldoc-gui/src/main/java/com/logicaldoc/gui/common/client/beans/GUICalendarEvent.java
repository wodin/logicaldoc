package com.logicaldoc.gui.common.client.beans;

import java.io.Serializable;
import java.util.Date;

import com.logicaldoc.gui.common.client.Constants;

/**
 * This class represents an event in a calendar. An event is always associated
 * to a selection of documents and users.
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.7
 */
public class GUICalendarEvent implements Serializable {

	private static final long serialVersionUID = 1L;

	private long id = 0;

	private Long parentId = null;

	private String title = "";

	private String description = "";

	private Date startDate = new Date();

	private Date expirationDate = null;

	private GUIValuePair[] participants = new GUIValuePair[0];

	private GUIDocument[] documents = new GUIDocument[0];

	/**
	 * The recurrency of this event, expressed in days
	 */
	private int recurrency = 0;

	private int remindTime = 1;

	private String remindUnit = Constants.TIME_HOUR;
	
	private long creatorId;
	
	private String creator;

	public GUICalendarEvent() {
	}

	@Override
	public String toString() {
		return getId() + "-" + getTitle();
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Long getParentId() {
		return parentId;
	}

	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getExpirationDate() {
		return expirationDate;
	}

	public void setExpirationDate(Date expirationDate) {
		this.expirationDate = expirationDate;
	}

	public GUIDocument[] getDocuments() {
		return documents;
	}

	public void setDocuments(GUIDocument[] documents) {
		this.documents = documents;
	}

	public int getRecurrency() {
		return recurrency;
	}

	public void setRecurrency(int recurrency) {
		this.recurrency = recurrency;
	}


	public GUIValuePair[] getParticipants() {
		return participants;
	}

	public void setParticipants(GUIValuePair[] participants) {
		this.participants = participants;
	}
	
	public void addParticipant(GUIValuePair newPart){
		GUIValuePair[] newParts=new GUIValuePair[participants.length+1];
		for (int i = 0; i < participants.length; i++) {
			newParts[i]=participants[i];
		}
		newParts[participants.length]=newPart;
		participants=newParts;
	}
	
	public void removeParticipant(String code){
		GUIValuePair[] newParts=new GUIValuePair[participants.length-1];
		int j=0;
		for (int i = 0; i < participants.length; i++) {
			if(code.equals(participants[i].getCode()))
				continue;
			newParts[j++]=participants[i];
		}
		participants=newParts;
	}
	
	public void removeDocument(long docId){
		GUIDocument[] newDocs=new GUIDocument[documents.length-1];
		int j=0;
		for (int i = 0; i < documents.length; i++) {
			if(docId==documents[i].getId())
				continue;
			newDocs[j++]=documents[i];
		}
		documents = newDocs;
	}

	public int getRemindTime() {
		return remindTime;
	}

	public void setRemindTime(int remindTime) {
		this.remindTime = remindTime;
	}

	public String getRemindUnit() {
		return remindUnit;
	}

	public void setRemindUnit(String remindUnit) {
		this.remindUnit = remindUnit;
	}

	public long getCreatorId() {
		return creatorId;
	}

	public void setCreatorId(long creatorId) {
		this.creatorId = creatorId;
	}

	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}
}