package com.logicaldoc.core.communication;

import java.util.Date;

/**
 * This is the parent class for email and systemmessage.
 * 
 * @author Michael Scholz
 * @author Marco Meschieri
 * @version 1.0
 */
public abstract class Message {

    private int messageId = 0;

    private String messageText = "";

    private String author = "";

    private String subject = "";

    private String sentDate = "";

    private int read = 0; // 0 - false; 1 - true

    public int getMessageId() {
        return messageId;
    }

    public String getMessageText() {
        return messageText;
    }

    public String getAuthor() {
        return author;
    }

    public String getSubject() {
        return subject;
    }

    public String getSentDate() {
        return sentDate;
    }

    public int getRead() {
        return read;
    }

    public void setMessageId(int id) {
        messageId = id;
    }

    public void setMessageText(String mess) {
        messageText = mess;
    }

    public void setAuthor(String auth) {
        author = auth;
    }

    public void setSubject(String subj) {
        subject = subj;
    }

    public void setSentDate(String dat) {
        sentDate = dat;
    }

    public void setRead(int rd) {
        read = rd;
    }

    public Date getSentDateAsDate() {
        Date date = new Date();
        try {
            date.setTime(Long.parseLong(getSentDate()));
        } catch (Exception e) {
        }
        return date;

    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Message))
            return false;

        Message other = (Message) obj;
        return other.getMessageId() == this.getMessageId();
    }

    @Override
    public int hashCode() {
        return new Integer(messageId).hashCode();
    }

    @Override
    public String toString() {
        return messageText;
    }
}
