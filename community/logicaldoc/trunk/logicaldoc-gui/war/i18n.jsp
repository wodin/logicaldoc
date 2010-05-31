<%@ page language="java" contentType="text/plain; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@page import="java.util.ResourceBundle"%>
<%@page import="com.logicaldoc.gui.common.server.Browser"%>
<%@page import="java.util.Locale"%>
<%@page import="java.util.StringTokenizer"%>
<%@page import="java.util.Iterator"%>
<%!
   public Locale toLocale(String str) {
		if (str==null || str.isEmpty())
			return Locale.ENGLISH;

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
%>
<%
    //Detect the bundle to use
	String bundle = request.getParameter("bundle");
	if (bundle == null || "".equals(bundle))
		bundle = "messages";

	//Detect the locale
	Browser browser = new Browser(request, session);
	String locale = request.getParameter("locale");
	if (locale == null || "".equals(locale))
		locale = browser.getLanguage();
		
	//Prepare the i18n variable
	StringBuffer sb = new StringBuffer("var "+bundle+"_i18n = {\n");
	ResourceBundle rb = ResourceBundle.getBundle("i18n." + bundle, toLocale(locale));
	Iterator iter=rb.keySet().iterator();
	while (iter.hasNext()) {
		String key = ((String) iter.next());
		sb.append(key.replaceAll("\\.", "_"));
		sb.append(" : \"");
		sb.append(rb.getString(key));
		sb.append("\",\n");
	}
	sb.append("xx: \"xx\"\n};");
%>
<%=sb.toString()%>