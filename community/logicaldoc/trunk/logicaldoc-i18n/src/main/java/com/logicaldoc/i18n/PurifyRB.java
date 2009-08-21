package com.logicaldoc.i18n;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Set;

public class PurifyRB {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		ResourceBundle master = ResourceBundle.getBundle("i18n/application", Locale.ENGLISH);
		
		Locale[] locales = new Locale[]{Locale.GERMAN, Locale.FRENCH, Locale.ITALIAN, new Locale("es"), new Locale("pt"), new Locale("nl"), new Locale("pt", "BR"), Locale.CHINA, new Locale("ro"),new Locale("el")};
		for (int i = 0; i < locales.length; i++) {
			
			try {
				System.out.println("\nAnalyzing language: "+ locales[i]);
				writePurifiedRB(master, locales[i]);
			} catch (Exception e) {
                System.out.println("Locale: "+ locales[i] + ", NOT FOUND");
                e.printStackTrace();
			}
		}
		
		
		System.out.println("Finished");
	}

	private static void writePurifiedRB(ResourceBundle master, Locale locale) {
		
		ResourceBundle translated = ResourceBundle.getBundle("i18n/application", locale);
		
		if (locale != translated.getLocale()) {
			System.out.println("Translation for locale: "+ locale + ", not Found; Skipped");
			return;
		}
		
		
		Properties destprop = new Properties();
	
        // 1: controllare che nel bundle dest i valori delle chiavi siano diversi da quelli nel bundle source

		int counter = 0;
		int mre = 0;
		
		Set<String> keySet = translated.keySet();
		for (String key : keySet) {
	         String value = translated.getString(key);
	         try {
				String masterValue = master.getString(key);
				
				 if (masterValue != null) {
					 // la chiave esiste anche nel master
					 if (!value.equals(masterValue)) {
						 // il valore è diverso da quello iniziale
						 // copio l'accoppiata chiave valore nel properties di destinazione
						 destprop.put(key, value);
						 //System.out.println("trans <> master");
						 counter++;
					 }
				 }
			} catch (MissingResourceException e) {
				mre++;
			}
		}
	
		System.out.println("trans props n.: " +translated.keySet().size());
		System.out.println("written props n.: " +counter);
		System.out.println("exceding keywords n.: " +mre);
		
		try {
			// scrivo il prop destinazione su FileSystem
			System.out.println(locale);
			File file = new File("src/main/resources/i18n/application_" +locale +".properties");
			FileOutputStream out = new FileOutputStream(file);
			destprop.store(out, "comments");
			out.flush();
			out.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
