<%-- 
   Page Displayed when a release change is detected
--%>
<%! static String MODULE="releasecheck"; %>
<%@ include file="header.jsp" %>
    <p>
       <img src="./skin/brand/logo.png" boder="0"/>
    </p>
    <p>
       <%=message("newrelseasewarning",request)%>
    </p>
    <p>
       <%=message("performfollowingsteps",request)%>:
       <ol>
         <li><%=message("deletecache",request)%> <a href="http://help.logicaldoc.com/clearcache.html" target="_blank"><%=message("clickforhowto",request)%></a></li>
         <li><%=message("loginfromhere",request)%>: <a href="<%=request.getContextPath()%>/index.jsp?skipreleasecheck=true"><%=message("login",request)%></a></li>
       </ol>
    </p>
    <hr/>
    
    <p>
      <p style="font-size: small">
        client version: <b><%= getClientVersion(request) %></b><br/>
        server version: <b><%= getServerVersion() %></b>
      </p>
    </p>
       
<%@ include file="footer.jsp" %>