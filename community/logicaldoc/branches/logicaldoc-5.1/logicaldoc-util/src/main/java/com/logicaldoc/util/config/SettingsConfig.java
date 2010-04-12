package com.logicaldoc.util.config;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.jdom.Element;

/**
 * Various directorySettings
 * 
 * @author Michael Scholz
 * @author Marco Meschieri
 * @version 1.0
 */
public class SettingsConfig extends ContextConfigurator {
    
    private Properties settings = new Properties();

    public SettingsConfig() {
        super();
    }

    public Properties getSettings() {
        return settings;
    }

    public void setSettings(Properties settings) {
        this.settings = settings;
    }

    public String getValue(String name) {
    	return settings.getProperty(name, "");
    }

    /**
     * This method sets a setting by a given name.
     * 
     * @param name Name of the setting
     */
    public void setValue(String name, String value) {
        settings.setProperty(name, value);
    }

    /**
     * Returns all elements of the same category
     * 
     * @param category The wanted catecory
     * @return The list of elements
     */
    public List<Element> getSettings(String category) {
        List<Element> list = new ArrayList<Element>();
        for (Iterator iter = settings.keySet().iterator(); iter.hasNext();) {
            String key = (String) iter.next();
            if (key.endsWith(".category") && category.equals(settings.getProperty(key))) {
                Element element = getPropElement("SettingsConfig", "settings", key.substring(0, key.lastIndexOf(".")));
                list.add(element);
            }
        }
        return list;
    }

    public boolean write() {
        for (Iterator iter = settings.keySet().iterator(); iter.hasNext();) {
            String key = (String) iter.next();
            setPropValue("SettingsConfig", "settings", key, settings.getProperty(key));
        }
        return super.write();
    }
}