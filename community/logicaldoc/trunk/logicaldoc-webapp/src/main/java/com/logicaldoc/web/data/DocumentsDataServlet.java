package com.logicaldoc.web.data;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

import com.logicaldoc.core.document.dao.DocumentDAO;
import com.logicaldoc.util.Context;
import com.logicaldoc.web.SessionBean;

public class DocumentsDataServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		/*
		 * Validate the session 
		 */
		String sid = (String) request.getParameter("sid");
		SessionBean.validateSession(sid);

		
		/*
		 * Load some filters from the current request
		 */
		int max = Integer.parseInt(request.getParameter("max"));
		Long folderId = null;
		if (StringUtils.isNotEmpty(request.getParameter("folderId")))
			folderId = new Long(request.getParameter("folderId"));
		String filename=null;
		if (StringUtils.isNotEmpty(request.getParameter("filename")))		
		 filename = request.getParameter("filename");
		Boolean indexable = null;
		if (StringUtils.isNotEmpty(request.getParameter("indexable")))
			indexable = new Boolean(indexable);


		response.setContentType("text/xml");

		// Headers required by Internet Explorer
		response.setHeader("Pragma", "public");
		response.setHeader("Cache-Control", "must-revalidate, post-check=0,pre-check=0");
		response.setHeader("Expires", "0");

		PrintWriter writer = response.getWriter();
		writer.write("<list>");

		/*
		 * Execute the Query 
		 */
		Context context = Context.getInstance();
		DocumentDAO dao = (DocumentDAO)context.getBean(DocumentDAO.class);
		StringBuffer query=new StringBuffer("select A.id from Document A where 1=1 ");
		if(folderId!=null)
			query.append(" and A.folder.id="+folderId);
		if(indexable!=null)
			if(indexable==true)
			   query.append(" and not(A.indexed=2) ");
			else
			   query.append(" and (A.indexed=2) ");
		if(filename!=null)
			 query.append(" and lower(A.fileName) like '%"+filename.toLowerCase()+"%' ");
		List<Object> records=(List<Object>) dao.findByQuery(query.toString(), null, max);
		
		/*
		 * Iterqte over records composing the response XML document
		 */
		for (Object record : records) {
			Object[] cols=(Object[])record;
			writer.print("<document>");
			writer.print("<id>" + cols[0] + "</id>");
			
			//TODO Completare l'implementazione
			
//			writer.print("<customId>" + Long.toString(folderId + 1000 + i) + "</customId>");
//			writer.print("<docref></docref>");
//			writer.print("<icon>word</icon>");
//			writer.print("<title>Title " + Long.toString(folderId + 1000 + i) + "</title>");
//			writer.print("<version>1.0</version>");
//			writer.print("<lastModified>2010-10-26T11:32:23</lastModified>");
//			writer.print("<published>2010-02-12T11:32:23</published>");
//			writer.print("<publisher>Marco Meschieri</publisher>");
//			writer.print("<created>2010-02-12T11:32:23</created>");
//			writer.print("<creator>Admin Admin</creator>");
//			writer.print("<size>1234556</size>");
//			writer.print("<immutable>blank</immutable>");
//			if (indexable)
//				writer.print("<indexed>blank</indexed>");
//			else
//				writer.print("<indexed>indexed</indexed>");
//			writer.print("<locked>blank</locked>");
//			if (StringUtils.isEmpty(filename))
//				writer.print("<filename>Title " + Long.toString(folderId + 1000 + i) + ".doc</filename>");
//			else
//				writer.print("<filename>Title " + filename + Long.toString(folderId + 1000 + i) + ".doc</filename>");
//			writer.print("<status>0</status>");
			writer.print("</document>");
		}
		
		writer.write("</list>");
	}
}
