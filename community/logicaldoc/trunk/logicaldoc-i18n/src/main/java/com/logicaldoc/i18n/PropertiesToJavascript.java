package com.logicaldoc.i18n;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.StringTokenizer;

public class PropertiesToJavascript {

	public static void main(String[] args) throws IOException {

		List<Locale> locales = new ArrayList<Locale>();
		Properties loc = new Properties();
		loc.load(PropertiesToJavascript.class.getResourceAsStream("/i18n/i18n.properties"));
		StringTokenizer st = new StringTokenizer(loc.getProperty("locales"), ",", false);
		while (st.hasMoreElements()) {
			String elem = (String) st.nextElement();
			locales.add(toLocale(elem));
		}
		if (!locales.contains(Locale.ENGLISH))
			locales.add(Locale.ENGLISH);
		
		List<String> bundles = new ArrayList<String>();
		st = new StringTokenizer(loc.getProperty("bundles"), ",", false);
		while (st.hasMoreElements()) {
			String elem = (String) st.nextElement();
			bundles.add(elem);
		}

		for (String bundle : bundles) {
			System.out.println("\nInspecting bundle " + bundle);
			for (Locale locale : locales) {
				try {
					System.out.println("\nAnalyzing language: " + locale);
					writeJavascript(bundle, locale);
				} catch (Exception e) {
					System.out.println("Locale: " + locale + ", NOT FOUND");
					e.printStackTrace();
				}
			}
		}

		System.out.println("Finished");
	}

	private static void writeJavascript(String bundle, Locale locale) throws IOException {
		Properties prop = new Properties();
		prop.load(PropertiesToJavascript.class.getResourceAsStream("/i18n/" + bundle + "_" + locale + ".properties"));

		File dir = new File("target/js/i18n");
		dir.mkdir();
		dir.mkdirs();

		File file = new File(dir, bundle + "_" + locale + ".js");
		FileOutputStream out = new FileOutputStream(file);
		OutputStreamWriter ow = new OutputStreamWriter(out, "UTF-8");
		ow.write("var " + bundle + "_" + locale + " = {");
		boolean first = true;
		for (Object key : prop.keySet()) {
			if (!first)
				ow.write(",");
			String jsKey = key.toString().replaceAll("-", "").replace('.', '_');
			String jsValue = prop.getProperty((String) key);
			jsValue = jsValue.replace("\"", "\\" + "u0022");
			jsValue = jsValue.replaceAll("\n", "\\" + "n");
			jsValue = jsValue.replaceAll("\r", "\\" + "r");
			jsValue = jsValue.replace("\u007B", "\\" + "u007B");
			jsValue = jsValue.replace("\u007D", "\\" + "u007D");
			jsValue = jsValue.replace("\u003A", "\\" + "u003A");
			jsValue = jsValue.replace("\u002C", "\\" + "u002C");
//			int car = ',';
//			System.out.println(Integer.toString(car, 16));
			ow.write("\n" + jsKey + ": " + "\"" + jsValue + "\"");
			first = false;
		}
		ow.write("\n};");
		ow.flush();
		out.flush();
		out.close();
		ow.close();
	}

	public static Locale toLocale(String str) {
		String lang = "";
		String country = "";
		String variant = "";
		StringTokenizer st = new StringTokenizer(str, "_", false);
		lang = st.nextToken();
		if (st.hasMoreTokens())
			country = st.nextToken();
		if (st.hasMoreTokens())
			variant = st.nextToken();
		return new Locale(lang, country, variant);
	}
}
