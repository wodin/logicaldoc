<% // generate a redirect as ICEFaces would do
 String redirectedErrorLocation = null;
 if((redirectedErrorLocation = (String)request.getAttribute("javax.servlet.error.request_uri")) != null){
 
  if(redirectedErrorLocation.indexOf("/webdav") >= 0)      {
     response.sendError(HttpServletResponse.SC_NOT_FOUND);
     return;
  }
 }
	
 response.setContentType("text/xml;charset=UTF-8");
 response.setStatus(200);
 
 // copy error objects to the session if you want to see details on the error page
 session.setAttribute("_error_message",
    request.getAttribute("javax.servlet.error.message"));
 session.setAttribute("_error_exception_type",
    request.getAttribute("javax.servlet.error.exception_type"));
 session.setAttribute("_error_exception",
    request.getAttribute("javax.servlet.error.exception"));
 session.setAttribute("_error_status_code",
    request.getAttribute("javax.servlet.error.status_code"));
 session.setAttribute("_error_request_uri",
    request.getAttribute("javax.servlet.error.request_uri"));
 session.setAttribute("_error_servlet_name",
    request.getAttribute("javax.servlet.error.servlet_name"));
//response.sendRedirect(request.getContextPath() + "/error.jspx");
 %>
 <htm>
 <head></head>
 <body>
 <jsp:forward page="error.iface"></jsp:forward>
 </body>
 </htm>
 