package com.logicaldoc.web.data;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.logicaldoc.core.task.Task;
import com.logicaldoc.core.task.TaskManager;
import com.logicaldoc.core.task.TaskTrigger;
import com.logicaldoc.i18n.I18N;
import com.logicaldoc.util.Context;
import com.logicaldoc.web.util.SessionUtil;

/**
 * This servlet is responsible for tasks data.
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 6.0
 */
public class TasksDataServlet extends HttpServlet {

	private static Log log = LogFactory.getLog(TasksDataServlet.class);

	private static final long serialVersionUID = 1L;

	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		SessionUtil.validateSession(request);

		try {
			String locale = request.getParameter("locale");

			response.setContentType("text/xml");

			// Headers required by Internet Explorer
			response.setHeader("Pragma", "public");
			response.setHeader("Cache-Control", "must-revalidate, post-check=0,pre-check=0");
			response.setHeader("Expires", "0");

			TaskManager manager = (TaskManager) Context.getInstance().getBean(TaskManager.class);
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
			df.setTimeZone(TimeZone.getTimeZone("UTC"));

			PrintWriter writer = response.getWriter();
			writer.write("<list>");

			for (Task task : manager.getTasks()) {
				writer.print("<task>");
				writer.print("<name><![CDATA[" + task.getName() + "]]></name>");
				writer.print("<label><![CDATA[" + I18N.message("task.name." + task.getName(), locale) + "]]></label>");
				if (task.getScheduling().isEnabled()) {
					writer.print("<eenabled>true</eenabled>");
					writer.print("<enabledIcon>bullet_green</enabledIcon>");
				} else {
					writer.print("<eenabled>false</eenabled>");
					writer.print("<enabledIcon>bullet_red</enabledIcon>");
				}
				writer.print("<status>" + task.getStatus() + "</status>");
				if (task.getScheduling().getMode().equals(TaskTrigger.MODE_CRON))
					writer.print("<scheduling>" + task.getScheduling().getCronExpression() + "</scheduling>");
				else if (task.getScheduling().getMode().equals(TaskTrigger.MODE_SIMPLE))
					writer.print("<scheduling>" + I18N.message("each", locale) + " "
							+ task.getScheduling().getIntervalSeconds() + " "
							+ I18N.message("seconds", locale).toLowerCase() + "</scheduling>");
				writer.print("<progress>" + task.getCompletionPercentage() + "</progress>");

				if (task.getScheduling().getPreviousFireTime() != null) {
					writer.print("<lastStart>" + df.format(task.getScheduling().getPreviousFireTime()) + "</lastStart>");
				}

				if (task.getScheduling().getNextFireTime() != null) {
					writer.print("<nextStart>" + df.format(task.getScheduling().getNextFireTime()) + "</nextStart>");
				}
				writer.print("<indeterminate>" + "" + task.isIndeterminate() + "</indeterminate>");

				writer.print("</task>");
			}
			writer.write("</list>");
		} catch (Throwable t) {
			log.error(t.getMessage(), t);
		}
	}
}