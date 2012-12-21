package com.logicaldoc.webservice;

import java.text.SimpleDateFormat;

import com.ibm.icu.util.Calendar;
import com.ibm.icu.util.GregorianCalendar;
import com.logicaldoc.core.searchengine.SearchOptions;
import com.logicaldoc.webservice.auth.AuthClient;
import com.logicaldoc.webservice.document.DocumentClient;
import com.logicaldoc.webservice.document.WSDocument;
import com.logicaldoc.webservice.folder.FolderClient;
import com.logicaldoc.webservice.search.SearchClient;
import com.logicaldoc.webservice.search.WSSearchOptions;
import com.logicaldoc.webservice.search.WSSearchResult;
import com.logicaldoc.webservice.search.WSTagCloud;
import com.logicaldoc.webservice.security.SecurityClient;
import com.logicaldoc.webservice.system.SystemClient;

public class Main {
	public static void main(String[] args) throws Exception {
	   GregorianCalendar gc=new GregorianCalendar();
	   gc.set(2009, 6, 19);
	   gc.add(Calendar.MONTH, 42);
	   
	   SimpleDateFormat df=new SimpleDateFormat("dd/MM/yyyy");
	   
	   System.out.println("end: "+df.format(gc.getTime()));
	}
}
