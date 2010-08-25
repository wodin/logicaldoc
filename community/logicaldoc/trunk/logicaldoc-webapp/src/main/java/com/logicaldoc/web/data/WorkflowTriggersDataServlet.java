package com.logicaldoc.web.data;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

import com.logicaldoc.core.generic.Generic;
import com.logicaldoc.core.generic.dao.GenericDAO;
import com.logicaldoc.util.Context;
import com.logicaldoc.web.util.SessionUtil;

public class WorkflowTriggersDataServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		SessionUtil.validateSession(request);

		Long folderId = null;
		if (StringUtils.isNotEmpty(request.getParameter("folderId")))
			folderId = new Long(request.getParameter("folderId"));

		System.out.println("WorkflowTriggersDataServlet folderId: " + folderId);

		response.setContentType("text/xml");

		// Headers required by Internet Explorer
		response.setHeader("Pragma", "public");
		response.setHeader("Cache-Control", "must-revalidate, post-check=0,pre-check=0");
		response.setHeader("Expires", "0");

		PrintWriter writer = response.getWriter();
		writer.write("<list>");

		Context context = Context.getInstance();
		GenericDAO dao = (GenericDAO) context.getBean(GenericDAO.class);

		List<Generic> triggerGenerics = dao.findByTypeAndSubtype("wf-trigger", folderId + "-%");

		System.out.println("WorkflowTriggersDataServlet triggerGenerics: " + triggerGenerics.size());

		/*
		 * Iterate over records composing the response XML document
		 */
		for (Generic generic : triggerGenerics) {
			System.out.println("WorkflowTriggersDataServlet generic: " + generic.getId());
			writer.print("<workflowtrigger>");
			writer.print("<id>" + generic.getId() + "</id>");
			writer.print("<workflowId>" + generic.getDouble2().toString() + "</workflowId>");
			writer.print("<templateId>" + generic.getSubtype().substring(generic.getSubtype().indexOf("-") + 1)
					+ "</templateId>");
			writer.print("<workflow><![CDATA[" + generic.getString1() + "]]></workflow>");
			writer.print("<template><![CDATA[" + generic.getString2() + "]]></template>");
			writer.print("</workflowtrigger>");
		}
		writer.write("</list>");
	}
}