package com.logicaldoc.core.document;

/**
 * Possible events in the document's history
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.6
 */
public enum DocumentEvent {
	STORED("event.stored"),
    CHANGED("event.changed"),
	CHECKEDIN("event.checkedin"),
	CHECKEDOUT("event.checkedout"),
	IMMUTABLE("event.makeimmutable"),
	RENAMED("event.renamed"),
	DOWNLOADED("event.downloaded"),
	MOVED("event.moved"),
	LOCKED("event.locked"),
	UNLOCKED("event.unlocked"),
	ARCHIVED("event.archived"),
	DELETED("event.deleted"),
	SENT("event.sent"),
	BARCODED("event.barcoded"),
	WORKFLOWSTATUS("event.workflowstatus"),
	SHORTCUT_STORED("event.shortcut.stored"),
	SHORTCUT_MOVED("event.shortcut.moved"),
	SHORTCUT_DELETED("event.shortcut.deleted"),
	VIEWED("event.viewed"),
	RESTORED("event.restored"),
	NEW_NOTE("event.newnote"),
	SIGNED("event.signed"),
	EXPORTPDF("event.exportpdf"),
	EXPORTED("event.exported"),
	ADDED_TO_CALEVENT("event.caladd"),
	REMOVED_FROM_CALEVENT("event.caldel"),
	SUBSCRIBED("event.subscribed"),
	STAMP_APPLIED("event.stampapplied");

    private String event;

	DocumentEvent(String event) {
	    this.event = event;
	}

	public String toString() {
	    return this.event;
	}

	public static DocumentEvent fromString(String event) {
	    if (event != null) {
	      for (DocumentEvent b : DocumentEvent.values()) {
	        if (event.equalsIgnoreCase(b.event)) {
	          return b;
	        }
	      }
	    }
 	    return null;
    }
}