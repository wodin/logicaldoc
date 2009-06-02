package com.logicaldoc.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.logicaldoc.core.document.History;
import com.logicaldoc.core.document.dao.HistoryDAO;
import com.logicaldoc.core.security.authentication.AuthenticationChain;
import com.logicaldoc.util.Context;

public class DocumentRSS extends HttpServlet {

	private static final long serialVersionUID = 1L;

	protected static Log log = LogFactory.getLog(DocumentRSS.class);

	public void init(ServletConfig config) throws ServletException {
		super.init(config);
	}

	public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

		// Get Authorization header
		String auth = req.getHeader("Authorization");
		System.err.println("auth: " + auth + ".");
		String docId = req.getParameter("docId");
		System.err.println("docId: " + docId);

		// Do we allow that user?
		if (!allowUser(auth)) {
			res.setContentType("text/plain");

			// Not allowed, so report he's unauthorized
			res.setHeader("WWW-Authenticate", "BASIC realm=\"LogicalDOC DMS\"");
			res.sendError(HttpServletResponse.SC_UNAUTHORIZED);
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

			List<History> histories = new ArrayList<History>();
			try {
				HistoryDAO historyDAO = (HistoryDAO) Context.getInstance().getBean(HistoryDAO.class);
				histories = historyDAO.findByDocId(Long.parseLong(docId));
				Collections.sort(histories, new Comparator<History>() {
					@Override
					public int compare(History o1, History o2) {
						return o2.getDate().compareTo(o1.getDate());
					}
				});
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}

			// Sun, 19 May 2002 15:21:36 GMT
			// Mon, 01 Jun 2009 12:06:06 CEST
			SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.ENGLISH);
			for (Iterator iter = histories.iterator(); iter.hasNext();) {
				History element = (History) iter.next();

				// print an item
				out.println("<item>");
				out.println("<title>title</title>");
				out.println("<link>http://demo.logicaldoc.com/action.xxx?ld_path_info=history&amp;docId=80</link>");
				out.println("<description>" + element.getEvent() + "</description>");
				out.println("<pubDate>" + sdf.format(element.getDate()) + "</pubDate>");
				out.println("<author>" + element.getUserName() + "</author>");
				out.println("</item>");
			}

			// end of the output
			out.println("</channel>");
			out.println("</rss>");
		}
	}

	// This method checks the user information sent in the Authorization
	// header against the database of users maintained in the users Hashtable.
	protected boolean allowUser(String auth) throws IOException {

		if (auth == null)
			return false; // no auth

		if (!auth.toUpperCase().startsWith("BASIC "))
			return false; // we only do BASIC

		// Get encoded user and password, comes after "BASIC "
		System.err.println("auth.length(): " + auth.length());
		String userpassEncoded = auth.substring(6);

		// Decode it, using any base 64 decoder (we use com.oreilly.servlet)
		byte[] decodedBytes = Base64.decodeBase64(userpassEncoded.getBytes());
		String upD = new String(decodedBytes);
		System.err.println("upD: " + upD);

		// Check our user list to see if that user and password are "allowed"
		return isAllowedUser(upD);
	}

	private boolean isAllowedUser(String upD) {

		String user = upD.substring(0, upD.indexOf(":"));
		String pass = upD.substring(upD.indexOf(":") + 1);
		AuthenticationChain authenticationChain = (AuthenticationChain) Context.getInstance().getBean(
				AuthenticationChain.class);
		if (authenticationChain.authenticate(user, pass)) {
			return true;
		}

		return false;
	}
}
