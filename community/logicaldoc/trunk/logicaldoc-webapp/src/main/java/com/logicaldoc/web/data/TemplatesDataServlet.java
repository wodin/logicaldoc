package com.logicaldoc.web.data;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.logicaldoc.core.document.DocumentTemplate;
import com.logicaldoc.core.document.dao.DocumentTemplateDAO;
import com.logicaldoc.core.generic.Generic;
import com.logicaldoc.core.generic.dao.GenericDAO;
import com.logicaldoc.i18n.I18N;
import com.logicaldoc.util.Context;

/**
 * This servlet is responsible for document templates data.
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class TemplatesDataServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private static Log log = LogFactory.getLog(TemplatesDataServlet.class);

	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		try {
			String folderId = request.getParameter("folderId");

			int type = 0;
			if (request.getParameter("templateType") != null)
				type = new Integer(request.getParameter("templateType"));

			List<Long> templateIds = new ArrayList<Long>();
			if (StringUtils.isNotEmpty(folderId)) {
				GenericDAO genericDao = (GenericDAO) Context.getInstance().getBean(GenericDAO.class);
				// Get all the 'wf-trigger' generics on this folder
				List<Generic> triggerGenerics = genericDao.findByTypeAndSubtype("wf-trigger", folderId + "-%");
				// Retrieve all the ids of the templates associated to a
				// workflow
				// already associated on the given folder
				for (Generic generic : triggerGenerics) {
					String templateId = generic.getSubtype().substring(generic.getSubtype().indexOf("-") + 1);
					if (StringUtils.isNotEmpty(templateId))
						templateIds.add(Long.parseLong(templateId));
				}
			}

			response.setContentType("text/xml");
			response.setCharacterEncoding("UTF-8");

			// Headers required by Internet Explorer
			response.setHeader("Pragma", "public");
			response.setHeader("Cache-Control", "must-revalidate, post-check=0,pre-check=0");
			response.setHeader("Expires", "0");

			PrintWriter writer = response.getWriter();
			writer.write("<list>");

			if ("true".equals(request.getParameter("withempty"))) {
				writer.print("<template>");
				writer.print("<id></id>");
				writer.print("<name> </name>");
				writer.print("<documents>0</documents>");
				writer.print("<readonly>false</readonly>");
				writer.print("</template>");
			}

			DocumentTemplateDAO dao = (DocumentTemplateDAO) Context.getInstance().getBean(DocumentTemplateDAO.class);

			/*
			 * Iterate over the collection of templates
			 */
			for (DocumentTemplate template : dao.findByType(type)) {
				if (templateIds.contains(template.getId()))
					continue;

				writer.print("<template>");
				writer.print("<id>" + template.getId() + "</id>");
				writer.print("<name><![CDATA[" + template.getName() + "]]></name>");
				writer.print("<documents>" + dao.countDocs(template.getId()) + "</documents>");
				writer.print("<description><![CDATA[" + template.getDescription() + "]]></description>");
				writer.print("<readonly>" + Boolean.toString(template.getReadonly() == 1) + "</readonly>");
				writer.print("<type>" + template.getType() + "</type>");
				writer.print("<category><![CDATA[" + I18N.message("sostdoctype." + template.getCategory())
						+ "]]></category>");
				writer.print("<signrequired>" + template.getType() + "</signrequired>");
				writer.print("</template>");
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