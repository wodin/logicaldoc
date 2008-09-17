package com.logicaldoc.core.text.parser;

import java.io.File;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.logicaldoc.core.ZipBean;
import com.logicaldoc.util.Context;
import com.logicaldoc.util.config.SettingsConfig;

/**
 * Created on 10.12.2004
 */
public class ZABWParser extends AbstractParser {
	protected static Log logger = LogFactory.getLog(ZABWParser.class);

	public void parse(File file) {
		try {
			String filename = file.getName();
			SettingsConfig conf = (SettingsConfig) Context.getInstance().getBean(SettingsConfig.class);
			ZipBean.unzip(file.getAbsolutePath(), conf.getValue("userdir") + "unjar/", filename);

			File xmlfile = new File(conf.getValue("userdir") + "unjar/" + filename);
			XMLParser parser = new XMLParser();
			parser.parse(xmlfile);
			content = parser.getContent().toString();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}
}