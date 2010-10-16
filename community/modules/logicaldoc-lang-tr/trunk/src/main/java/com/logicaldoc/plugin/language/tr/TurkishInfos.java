package com.logicaldoc.plugin.language.tr;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.Locale;

public class TurkishInfos {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		// Print ifnromation about language
        Locale tr = new Locale("tr", "TR");
        System.out.println(tr);
        System.out.println(tr.getDisplayCountry());
        System.out.println(tr.getDisplayLanguage());
        
        DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT, tr);
        String xxx = df.format(new java.util.Date());
        System.out.println(xxx);
        
        df = DateFormat.getTimeInstance(DateFormat.MEDIUM, tr);
        xxx = df.format(new java.util.Date());
        System.out.println(xxx);
        
        NumberFormat nf = NumberFormat.getInstance(tr);
        Integer intobj = new Integer(1500);
        String xxx3 = nf.format(intobj);
        System.out.println(xxx3);
        
        Double doubleobj = new Double(1250.35);
        xxx3 = nf.format(doubleobj);
        System.out.println(xxx3);
	}

}
