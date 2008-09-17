package com.logicaldoc.util.config;


/**
 * @author Michael Scholz
 * @author Marco Meschieri
 */
public class BackupConfig extends ContextConfigurator {
	
    private boolean enabled = false;

    private String location = "";

    private BackupConfig() {
        super();
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getLocation() {
    	return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public boolean write() {
        setProperty("BackupConfig", "location", location);
        setProperty("BackupConfig", "enabled", Boolean.toString(enabled));
        return super.write();
    }
}