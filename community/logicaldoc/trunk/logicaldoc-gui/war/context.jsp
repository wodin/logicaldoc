<%@ page language="java" contentType="text/plain; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@page import="java.util.Properties"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.io.FileInputStream"%>

<%
	//Prepare the context variable
	StringBuffer sb = new StringBuffer("var context = {\n");
	Properties pp = new Properties();
	FileInputStream is=new FileInputStream(getServletContext().getRealPath("WEB-INF/classes/context.properties"));
	pp.load(is);
	Iterator iter=pp.keySet().iterator();
	while (iter.hasNext()) {
		String k = ((String) iter.next());
		if (k.startsWith("jdbc"))
			continue;
		sb.append(k.replaceAll("\\.", "_"));
		sb.append(" : \"");
		sb.append(pp.get(k).toString().replaceAll("\\\\","\\\\\\\\"));
		sb.append("\",\n");
	}
	sb.append("xx: \"xx\"\n};");
%>
<%=sb.toString()%>

var defaultFolder = '<%=System.getProperty("user.home").replaceAll("\\\\","/")+"/logicaldoc"%>';