package com.logicaldoc.workflow.data;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.logicaldoc.util.Context;
import com.logicaldoc.web.util.SessionUtil;
import com.logicaldoc.workflow.editor.WorkflowPersistenceTemplate;
import com.logicaldoc.workflow.editor.WorkflowPersistenceTemplateDAO;

/**
 * This servlet is responsible for workflows data.
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 6.0
 */
public class WorkflowsDataServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		SessionUtil.validateSession(request);

		response.setContentType("text/xml");

		// Headers required by Internet Explorer
		response.setHeader("Pragma", "public");
		response.setHeader("Cache-Control", "must-revalidate, post-check=0,pre-check=0");
		response.setHeader("Expires", "0");

		Context context = Context.getInstance();
		WorkflowPersistenceTemplateDAO dao = (WorkflowPersistenceTemplateDAO) context
				.getBean(WorkflowPersistenceTemplateDAO.class);

		List<WorkflowPersistenceTemplate> records = dao.findAll();

		PrintWriter writer = response.getWriter();
		writer.write("<list>");

		/*
		 * Iterate over records composing the response XML document
		 */
		for (WorkflowPersistenceTemplate record : records) {
			writer.print("<workflow>");
			writer.print("<id>" + record.getId() + "</id>");
			writer.print("<name><![CDATA[" + record.getName() + "]]></name>");
			writer.print("</workflow>");
		}
		writer.write("</list>");
	}
}
