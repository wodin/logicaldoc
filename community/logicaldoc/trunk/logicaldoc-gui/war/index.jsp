<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%if(request.getQueryString()!=null && !"".equals(request.getQueryString())){%>
<c:redirect url="${request.contextPath}/login.jsp?${pageContext.request.queryString}"/>
<%}else{%>
<c:redirect url="${request.contextPath}/login.jsp"/>
<%}%>