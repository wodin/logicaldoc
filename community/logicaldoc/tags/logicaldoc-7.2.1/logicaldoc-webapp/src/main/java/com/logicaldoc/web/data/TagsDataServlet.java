package com.logicaldoc.web.data;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.logicaldoc.core.document.dao.DocumentDAO;
import com.logicaldoc.core.generic.Generic;
import com.logicaldoc.core.generic.GenericDAO;
import com.logicaldoc.core.security.UserSession;
import com.logicaldoc.util.Context;
import com.logicaldoc.util.config.ContextProperties;
import com.logicaldoc.web.util.ServiceUtil;

/**
 * This servlet is responsible for document tags data.
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 6.0
 */
public class TagsDataServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private static Logger log = LoggerFactory.getLogger(TagsDataServlet.class);

	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		try {
			UserSession session = ServiceUtil.validateSession(request);

			response.setContentType("text/xml");
			response.setCharacterEncoding("UTF-8");

			// Headers required by Internet Explorer
			response.setHeader("Pragma", "public");
			response.setHeader("Cache-Control", "must-revalidate, post-check=0,pre-check=0");
			response.setHeader("Expires", "0");

			DocumentDAO docDao = (DocumentDAO) Context.getInstance().getBean(DocumentDAO.class);
			ContextProperties config = (ContextProperties) Context.getInstance().getBean(ContextProperties.class);
			String mode = config.getProperty(session.getTenantName() + ".tag.mode");

			String firstLetter = request.getParameter("firstLetter");
			String editing = request.getParameter("editing");

			HashMap<String, Integer> tgs = new HashMap<String, Integer>();

			if (("preset".equals(firstLetter) ||"preset".equals(mode)) && "true".equals(editing)) {
				// We have to return the preset only, since the user is editing a document
				GenericDAO gDao = (GenericDAO) Context.getInstance().getBean(GenericDAO.class);
				List<Generic> buf = gDao.findByTypeAndSubtype("tag", null, null, session.getTenantId());
				for (Generic generic : buf)
					tgs.put(generic.getSubtype(), 0);
			} else if (org.apache.commons.lang.StringUtils.isNotEmpty(firstLetter)) {
				tgs = (HashMap<String, Integer>) docDao.findTags(firstLetter, session.getTenantId());
			} else {
				List<String> buf = docDao.findAllTags(null, session.getTenantId());
				for (String tag : buf) {
					tgs.put(tag, 0);
				}
			}

			PrintWriter writer = response.getWriter();
			writer.write("<list>");
			int i = 0;

			List<String> words = new ArrayList<String>(tgs.keySet());
			Collections.sort(words);

			for (String tag : words) {
				writer.print("<tag>");
				writer.print("<index>" + i++ + "</index>");
				writer.print("<word><![CDATA[" + tag + "]]></word>");
				writer.print("<count>" + tgs.get(tag) + "</count>");
				writer.print("</tag>");
			}
			writer.write("</list>");
		} catch (Throwable e) {
			log.error(e.getMessage(), e);
			if (e instanceof ServletException)
				throw (ServletException) e;
			else if (e instanceof IOException)
				throw (IOException) e;
			else
				throw new ServletException(e.getMessage(), e);
		}
	}
}