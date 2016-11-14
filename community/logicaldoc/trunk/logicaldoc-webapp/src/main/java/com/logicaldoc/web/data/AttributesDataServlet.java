package com.logicaldoc.web.data;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.logicaldoc.core.metadata.Attribute;
import com.logicaldoc.core.metadata.AttributeSetDAO;
import com.logicaldoc.core.security.Session;
import com.logicaldoc.i18n.I18N;
import com.logicaldoc.util.Context;
import com.logicaldoc.util.LocaleUtil;
import com.logicaldoc.web.util.ServiceUtil;

/**
 * This servlet is responsible for attribute sets data.
 * 
 * @author Marco Meschieri - LogicalDOC
 * @since 7.5.4
 */
public class AttributesDataServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private static Logger log = LoggerFactory.getLogger(AttributesDataServlet.class);

	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		try {
			Session session = ServiceUtil.validateSession(request);

			response.setContentType("text/xml");
			response.setCharacterEncoding("UTF-8");

			// Avoid resource caching
			response.setHeader("Pragma", "no-cache");
			response.setHeader("Cache-Control", "no-store");
			response.setDateHeader("Expires", 0);

			Locale locale = Locale.ENGLISH;
			if (StringUtils.isNotEmpty(request.getParameter("locale")))
				locale = LocaleUtil.toLocale(request.getParameter("locale"));

			PrintWriter writer = response.getWriter();
			writer.write("<list>");

			AttributeSetDAO dao = (AttributeSetDAO) Context.get().getBean(AttributeSetDAO.class);

			/*
			 * Put the standard attributes
			 */
			writer.print("<attribute>");
			writer.print("<name>filename</name>");
			writer.print("<label><![CDATA[" + I18N.message("filename", locale) + "]]></label>");
			writer.print("<type>" + Attribute.TYPE_STRING + "</type>");
			writer.print("</attribute>");

			writer.print("<attribute>");
			writer.print("<name>title</name>");
			writer.print("<label><![CDATA[" + I18N.message("title", locale) + "]]></label>");
			writer.print("<type>" + Attribute.TYPE_STRING + "</type>");
			writer.print("</attribute>");

			writer.print("<attribute>");
			writer.print("<name>customId</name>");
			writer.print("<label><![CDATA[" + I18N.message("customid", locale) + "]]></label>");
			writer.print("<type>" + Attribute.TYPE_STRING + "</type>");
			writer.print("</attribute>");

			writer.print("<attribute>");
			writer.print("<name>lastModified</name>");
			writer.print("<label><![CDATA[" + I18N.message("lastmodified", locale) + "]]></label>");
			writer.print("<type>" + Attribute.TYPE_DATE + "</type>");
			writer.print("</attribute>");

			writer.print("<attribute>");
			writer.print("<name>created</name>");
			writer.print("<label><![CDATA[" + I18N.message("createdon", locale) + "]]></label>");
			writer.print("<type>" + Attribute.TYPE_DATE + "</type>");
			writer.print("</attribute>");

			writer.print("<attribute>");
			writer.print("<name>published</name>");
			writer.print("<label><![CDATA[" + I18N.message("publishedon", locale) + "]]></label>");
			writer.print("<type>" + Attribute.TYPE_DATE + "</type>");
			writer.print("</attribute>");

			writer.print("<attribute>");
			writer.print("<name>fileVersion</name>");
			writer.print("<label><![CDATA[" + I18N.message("fileversion", locale) + "]]></label>");
			writer.print("<type>" + Attribute.TYPE_STRING + "</type>");
			writer.print("</attribute>");
			
			writer.print("<attribute>");
			writer.print("<name>version</name>");
			writer.print("<label><![CDATA[" + I18N.message("version", locale) + "]]></label>");
			writer.print("<type>" + Attribute.TYPE_STRING + "</type>");
			writer.print("</attribute>");

			writer.print("<attribute>");
			writer.print("<name>type</name>");
			writer.print("<label><![CDATA[" + I18N.message("type", locale) + "]]></label>");
			writer.print("<type>" + Attribute.TYPE_STRING + "</type>");
			writer.print("</attribute>");

			writer.print("<attribute>");
			writer.print("<name>size</name>");
			writer.print("<label><![CDATA[" + I18N.message("size", locale) + "]]></label>");
			writer.print("<type>" + Attribute.TYPE_INT + "</type>");
			writer.print("</attribute>");

			writer.print("<attribute>");
			writer.print("<name>creator</name>");
			writer.print("<label><![CDATA[" + I18N.message("creator", locale) + "]]></label>");
			writer.print("<type>" + Attribute.TYPE_STRING + "</type>");
			writer.print("</attribute>");

			writer.print("<attribute>");
			writer.print("<name>publisher</name>");
			writer.print("<label><![CDATA[" + I18N.message("publisher", locale) + "]]></label>");
			writer.print("<type>" + Attribute.TYPE_STRING + "</type>");
			writer.print("</attribute>");

			writer.print("<attribute>");
			writer.print("<name>comment</name>");
			writer.print("<label><![CDATA[" + I18N.message("comment", locale) + "]]></label>");
			writer.print("<type>" + Attribute.TYPE_STRING + "</type>");
			writer.print("</attribute>");
			
			writer.print("<attribute>");
			writer.print("<name>template</name>");
			writer.print("<label><![CDATA[" + I18N.message("template", locale) + "]]></label>");
			writer.print("<type>" + Attribute.TYPE_STRING + "</type>");
			writer.print("</attribute>");

			writer.print("<attribute>");
			writer.print("<name>workflowStatus</name>");
			writer.print("<label><![CDATA[" + I18N.message("workflowstatus", locale) + "]]></label>");
			writer.print("<type>" + Attribute.TYPE_STRING + "</type>");
			writer.print("</attribute>");
			
			writer.print("<attribute>");
			writer.print("<name>startPublishing</name>");
			writer.print("<label><![CDATA[" + I18N.message("startpublishing", locale) + "]]></label>");
			writer.print("<type>" + Attribute.TYPE_DATE + "</type>");
			writer.print("</attribute>");
			
			writer.print("<attribute>");
			writer.print("<name>stopPublishing</name>");
			writer.print("<label><![CDATA[" + I18N.message("stoppublishing", locale) + "]]></label>");
			writer.print("<type>" + Attribute.TYPE_DATE + "</type>");
			writer.print("</attribute>");
			
			writer.print("<attribute>");
			writer.print("<name>folder</name>");
			writer.print("<label><![CDATA[" + I18N.message("folder", locale) + "]]></label>");
			writer.print("<type>" + Attribute.TYPE_STRING + "</type>");
			writer.print("</attribute>");
			
			writer.print("<attribute>");
			writer.print("<name>score</name>");
			writer.print("<label><![CDATA[" + I18N.message("score", locale) + "]]></label>");
			writer.print("<type>" + Attribute.TYPE_INT + "</type>");
			writer.print("</attribute>");
			
			writer.print("<attribute>");
			writer.print("<name>rating</name>");
			writer.print("<label><![CDATA[" + I18N.message("rating", locale) + "]]></label>");
			writer.print("<type>" + Attribute.TYPE_INT + "</type>");
			writer.print("</attribute>");
			
			/*
			 * Iterate over the collection of extended attributes
			 */
			Map<String, Attribute> attributes = dao.findAttributes(session.getTenantId(), null);
			for (String name : attributes.keySet()) {
				writer.print("<attribute>");
				writer.print("<name><![CDATA[ext_" + name + "]]></name>");
				writer.print("<label><![CDATA[" + name + "]]></label>");
				writer.print("<type>" + attributes.get(name).getType() + "</type>");
				writer.print("</attribute>");
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