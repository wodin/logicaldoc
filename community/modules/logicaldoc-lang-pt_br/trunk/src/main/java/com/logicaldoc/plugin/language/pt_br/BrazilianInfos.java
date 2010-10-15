package com.logicaldoc.plugin.language.pt_br;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.Locale;

public class BrazilianInfos {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		// Print infromation about language
        Locale loc = new Locale("pt", "BR");
        System.out.println(loc);
        System.out.println(loc.getDisplayCountry());
        System.out.println(loc.getDisplayLanguage());
        
        DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT, loc);
        String xxx = df.format(new java.util.Date());
        System.out.println(xxx);
        
        df = DateFormat.getTimeInstance(DateFormat.MEDIUM, loc);
        xxx = df.format(new java.util.Date());
        System.out.println(xxx);
        
        NumberFormat nf = NumberFormat.getInstance(loc);
        Integer intobj = new Integer(1500);
        String xxx3 = nf.format(intobj);
        System.out.println(xxx3);
        
        Double doubleobj = new Double(1250.35);
        xxx3 = nf.format(doubleobj);
        System.out.println(xxx3);
	}

}
