package com.logicaldoc.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Hashtable;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;

public class DocumentRSS extends HttpServlet {

	private static final long serialVersionUID = 1L;

	Hashtable users = new Hashtable();

	public void init(ServletConfig config) throws ServletException {

		super.init(config);

		// Names and passwords are case sensitive!
		users.put("Wallace:cheese", "allowed");
		users.put("Gromit:sheepnapper", "allowed");
		users.put("Penguin:evil", "allowed");
	}

	public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

		// Get Authorization header
		String auth = req.getHeader("Authorization");
		String docId = req.getParameter("docId");
		System.err.println("docId: " + docId);

		// Do we allow that user?
		if (!allowUser(auth)) {
			res.setContentType("text/plain");

			// Not allowed, so report he's unauthorized
			res.setHeader("WWW-Authenticate", "BASIC realm=\"LogicalDOC DMS\"");
			res.sendError(res.SC_UNAUTHORIZED);
		} else {
			res.setContentType("text/xml");
			PrintWriter out = res.getWriter();

			// Allowed, so show him the secret stuff
			out.println("<rss version=\"2.0\">");
			out.println("<channel>");
			out.println("<title>LogicalDOC RSS</title>");
			out.println("<copyright>(c) 2009 Logical Objects</copyright>");
			out.println("<link>http://demo.logicaldoc.com/</link>");
			out.println("<description>LOGICALDOC-RSS</description>");

			// printy an item
			out.println("<item>");
			out.println("<title>test</title>");
			out.println("<link>http://demo.logicaldoc.com/action.xxx?ld_path_info=history&amp;docId=80</link>");
			out.println("<description>");
			out.println("My first item");
			out.println("</description>");
			out.println("</item>");
			// end of the item

			// end of the output
			out.println("</channel>");
			out.println("</rss>");
		}
	}

	private boolean isUserPassEmpty(String auth) {
		if (auth != null && auth.toUpperCase().startsWith("BASIC ") && auth.length() == 10)
			return true;
		return false;
	}

	// This method checks the user information sent in the Authorization
	// header against the database of users maintained in the users Hashtable.
	protected boolean allowUser(String auth) throws IOException {

		if (auth == null)
			return false; // no auth

		if (!auth.toUpperCase().startsWith("BASIC "))
			return false; // we only do BASIC

		// Get encoded user and password, comes after "BASIC "
		String userpassEncoded = auth.substring(6);

		// Decode it, using any base 64 decoder (we use com.oreilly.servlet)
		byte[] decodedBytes = Base64.decodeBase64(userpassEncoded.getBytes());
		String upD2 = new String(decodedBytes);
		System.err.println("upD2: " + upD2);

		// Check our user list to see if that user and password are "allowed"
		if ("allowed".equals(users.get(upD2)))
			return true;
		else
			return false;
	}
}
