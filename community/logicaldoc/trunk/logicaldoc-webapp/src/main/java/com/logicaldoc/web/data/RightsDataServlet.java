package com.logicaldoc.web.data;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

import com.logicaldoc.core.security.dao.MenuDAO;
import com.logicaldoc.util.Context;
import com.logicaldoc.web.util.SessionUtil;

public class RightsDataServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		SessionUtil.validateSession(request);

		Long folderId = null;
		if (StringUtils.isNotEmpty(request.getParameter("folderId")))
			folderId = new Long(request.getParameter("folderId"));

		response.setContentType("text/xml");

		// Headers required by Internet Explorer
		response.setHeader("Pragma", "public");
		response.setHeader("Cache-Control", "must-revalidate, post-check=0,pre-check=0");
		response.setHeader("Expires", "0");

		PrintWriter writer = response.getWriter();
		writer.write("<list>");

		MenuDAO dao = (MenuDAO) Context.getInstance().getBean(MenuDAO.class);
		StringBuffer query = new StringBuffer(
				"select A.groupId, C.name, A.write, A.addChild, A.manageSecurity, A.manageImmutability, A.delete, A.rename, "
						+ "A.bulkImport, A.bulkExport, A.sign, A.archive, A.workflow "
						+ "from MenuGroup A, Menu B, Group C where A.menuId = B.id and A.groupId = C.id ");
		if (folderId != null)
			query.append(" and B.id=" + folderId);

		List<Object> records = (List<Object>) dao.findByQuery(query.toString(), null, null);

		/*
		 * Iterate over records composing the response XML document
		 */
		for (Object record : records) {
			Object[] cols = (Object[]) record;

			writer.print("<right>");
			writer.print("<entityId>" + cols[0] + "</entityId>");
			writer.print("<entity><![CDATA[" + cols[1] + "]]></entity>");
			writer.print("<read>true</read>");
			writer.print("<write>" + trueOfFalse((Integer) cols[2]) + "</write>");
			writer.print("<add>" + trueOfFalse((Integer) cols[3]) + "</add>");
			writer.print("<security>" + trueOfFalse((Integer) cols[4]) + "</security>");
			writer.print("<immutable>" + trueOfFalse((Integer) cols[5]) + "</immutable>");
			writer.print("<delete>" + trueOfFalse((Integer) cols[6]) + "</delete>");
			writer.print("<rename>" + trueOfFalse((Integer) cols[7]) + "</rename>");
			writer.print("<import>" + trueOfFalse((Integer) cols[8]) + "</import>");
			writer.print("<export>" + trueOfFalse((Integer) cols[9]) + "</export>");
			writer.print("<sign>" + trueOfFalse((Integer) cols[10]) + "</sign>");
			writer.print("<archive>" + trueOfFalse((Integer) cols[11]) + "</archive>");
			writer.print("<workflow>" + trueOfFalse((Integer) cols[12]) + "</workflow>");
			writer.print("</right>");
		}
		writer.write("</list>");
	}

	private String trueOfFalse(int value) {
		if (value == 1)
			return "true";
		else
			return "false";
	}
}
