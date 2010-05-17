package com.logicaldoc.gui.common.server.mock;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MockLogDownload extends HttpServlet {

	private static final long serialVersionUID = 1L;

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String sid = request.getParameter("sid");
		if (sid == null)
			throw new IOException("Invalid session");
		try {
			InputStream is = this.getClass().getResourceAsStream("/dms.log.html");
			byte[] buf = new byte[1024];
			int read = 1;
			while (read > 0) {
				read = is.read(buf, 0, buf.length);
				if (read > 0)
					response.getOutputStream().write(buf, 0, read);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
}
