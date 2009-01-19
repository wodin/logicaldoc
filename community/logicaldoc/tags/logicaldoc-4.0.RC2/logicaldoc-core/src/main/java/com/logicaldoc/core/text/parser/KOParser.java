package com.logicaldoc.core.text.parser;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.logicaldoc.util.Context;
import com.logicaldoc.util.config.SettingsConfig;
import com.logicaldoc.util.io.JarUtil;

/**
 * @author Michael Scholz
 */
public class KOParser extends AbstractParser {
	protected static Log logger = LogFactory.getLog(KOParser.class);

	public void parse(File file) {
		StringBuffer buffer = new StringBuffer();
		try {
			SettingsConfig conf = (SettingsConfig) Context.getInstance().getBean(SettingsConfig.class);
			JarUtil.unjar(file.getAbsolutePath(), conf.getValue("userdir") + "unjar/", conf.getValue("kocontent"));

			File xmlfile = new File(conf.getValue("userdir") + "unjar/" + conf.getValue("kocontent"));
			InputStream in = new FileInputStream(xmlfile);
			BufferedInputStream reader = new BufferedInputStream(in);
			int ichar = 0;
			boolean istag = false;
			boolean isspec = false;

			while ((ichar = reader.read()) != -1) {
				if (ichar == 60) {
					buffer.append((char) 32);
					istag = true;
				}

				if (!istag) {
					if (ichar == 195) {
						isspec = true;
					} else {
						if (isspec) {
							switch (ichar) {
							case 132: {
								buffer.append('Ä');
								break;
							}

							case 164: {
								buffer.append('ä');
								break;
							}

							case 150: {
								buffer.append('Ö');
								break;
							}

							case 182: {
								buffer.append('ö');
								break;
							}

							case 156: {
								buffer.append('Ü');
								break;
							}

							case 188: {
								buffer.append('ü');
								break;
							}

							case 159: {
								buffer.append('ß');
								break;
							}
							}

							isspec = false;
						} else {
							buffer.append((char) ichar);
						}
					}
				}

				if (ichar == 62) {
					istag = false;
				}
			}

			in.close();
			reader.close();
		} catch (Exception ex) {
			logger.error(ex.getMessage());
		}
		content = buffer.toString();
	}
}