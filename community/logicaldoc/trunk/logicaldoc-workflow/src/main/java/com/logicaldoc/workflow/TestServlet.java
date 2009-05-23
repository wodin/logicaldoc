package com.logicaldoc.workflow;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jbpm.JbpmConfiguration;
import org.jbpm.JbpmContext;
import org.jbpm.db.GraphSession;
import org.jbpm.graph.exe.ProcessInstance;
import org.jbpm.svc.ServiceFactory;

import com.logicaldoc.util.Context;

@SuppressWarnings({ "unused", "serial" })
public class TestServlet extends HttpServlet{
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		String s ="";     
		JbpmConfiguration config = (JbpmConfiguration)Context.getInstance().getBean("jbpmConfiguration");
		JbpmContext jbpmContext = config.createJbpmContext();
		GraphSession graphSession = jbpmContext.getGraphSession();
		
		super.doGet(req, resp);
	}
}
