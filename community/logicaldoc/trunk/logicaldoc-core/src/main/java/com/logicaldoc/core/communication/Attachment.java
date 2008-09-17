package com.logicaldoc.core.communication;

/**
 * @author Michael Scholz
 * @author Alessandro Gasparini
 */
public class Attachment {

    private String icon = "";

    private String filename = "";

    private String mimeType = "";

    public Attachment() {
    }

    public String getFilename() {
        return filename;
    }

    public String getIcon() {
        return icon;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String string) {
        mimeType = string;
    }
}
