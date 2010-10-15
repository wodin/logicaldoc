package com.logicaldoc.plugin.language.fa;
/* From http://java.sun.com/docs/books/tutorial/index.html */
/*
 * Copyright (c) 1995-1998 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * Permission to use, copy, modify, and distribute this software and its
 * documentation for NON-COMMERCIAL purposes and without fee is hereby granted
 * provided that this copyright notice appears in all copies. Please refer to
 * the file "copyright.html" for further important copyright and licensing
 * information.
 * 
 * SUN MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE
 * SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE, OR
 * NON-INFRINGEMENT. SUN SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY
 * LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS
 * DERIVATIVES.
 */

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

public class DateFormatDemo {

  static public void displayDate(Locale currentLocale) {

    Date today;
    String dateOut;
    DateFormat dateFormatter;

    dateFormatter = DateFormat.getDateInstance(DateFormat.DEFAULT,
        currentLocale);
    today = new Date();
    dateOut = dateFormatter.format(today);

    System.out.println(dateOut + "   " + currentLocale.toString());
  }

  static public void showBothStyles(Locale currentLocale) {

    Date today;
    String result;
    DateFormat formatter;

    int[] styles = { DateFormat.DEFAULT, DateFormat.SHORT,
        DateFormat.MEDIUM, DateFormat.LONG, DateFormat.FULL };

    System.out.println();
    System.out.println("Locale: " + currentLocale.toString());
    System.out.println();

    today = new Date();

    for (int k = 0; k < styles.length; k++) {
      formatter = DateFormat.getDateTimeInstance(styles[k], styles[k],
          currentLocale);
      result = formatter.format(today);
      System.out.println(result);
    }
  }

  static public void showDateStyles(Locale currentLocale) {

    Date today = new Date();
    String result;
    DateFormat formatter;

    int[] styles = { DateFormat.DEFAULT, DateFormat.SHORT,
        DateFormat.MEDIUM, DateFormat.LONG, DateFormat.FULL };

    System.out.println();
    System.out.println("Locale: " + currentLocale.toString());
    System.out.println();

    for (int k = 0; k < styles.length; k++) {
      formatter = DateFormat.getDateInstance(styles[k], currentLocale);
      result = formatter.format(today);
      System.out.println(result);
    }
  }

  static public void showTimeStyles(Locale currentLocale) {

    Date today = new Date();
    String result;
    DateFormat formatter;

    int[] styles = { DateFormat.DEFAULT, DateFormat.SHORT,
        DateFormat.MEDIUM, DateFormat.LONG, DateFormat.FULL };

    System.out.println();
    System.out.println("Locale: " + currentLocale.toString());
    System.out.println();

    for (int k = 0; k < styles.length; k++) {
      formatter = DateFormat.getTimeInstance(styles[k], currentLocale);
      result = formatter.format(today);
      System.out.println(result);
    }
  }

  static public void main(String[] args) {

//    Locale[] locales = { new Locale("fr", "FR"), new Locale("de", "DE"),
//        new Locale("en", "US") };
    
    Locale[] locales = { new Locale("en", "US"), new Locale("fa")};

    for (int i = 0; i < locales.length; i++) {
      displayDate(locales[i]);
    }

    showDateStyles(new Locale("en", "US"));
    //showDateStyles(new Locale("fr", "FR"));
    showDateStyles(new Locale("fa"));

    showTimeStyles(new Locale("en", "US"));
    //showTimeStyles(new Locale("de", "DE"));
    showTimeStyles(new Locale("fa"));

    showBothStyles(new Locale("en", "US"));
    showBothStyles(new Locale("fa"));

  }
}
