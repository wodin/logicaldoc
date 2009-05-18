package com.logicaldoc.core.spring;

import java.io.IOException;
import java.util.Properties;

import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;

public class PropertiesPlaceHolder extends PropertyPlaceholderConfigurer{
	
	@Override
	public Properties mergeProperties() throws IOException {
		return super.mergeProperties();
	}
}
