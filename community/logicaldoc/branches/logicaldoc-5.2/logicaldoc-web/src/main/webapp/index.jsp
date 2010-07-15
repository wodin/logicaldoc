<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"
	language="java" buffer="32768kb"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<html>
	<head>
		<title></title>
	</head>
	<body>
		<c:if test="${!empty authUser}">
			<jsp:forward page="main.iface" />
		</c:if>
		<c:if test="${empty authUser}">
			<jsp:forward page="login.iface" />
		</c:if>
	</body>
</html>
