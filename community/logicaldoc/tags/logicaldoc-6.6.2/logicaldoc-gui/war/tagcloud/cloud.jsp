<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@page import="java.io.*"%>
<%@page import="java.util.*"%>
<%@page import="com.logicaldoc.web.service.*"%>
<%@page import="com.logicaldoc.gui.common.client.beans.*"%>

<html>

<head>

<script src="./cloud.js"></script>

<script>

function init() {

    var w = document.body.clientWidth, h = document.body.clientHeight;
    var clouder = document.getElementById('clouder');
    
    clouder.style.width = w;
    clouder.style.height = h;
    clouder.style.position = "absolute";
    clouder.style.left = 0;
    clouder.style.top = 0;
    clouder.style.border = "0px solid white";
    
    window.clouder = new Clouder({
        container: clouder,
        callback: parent.searchTag,
        tags: createTags(),
        interval: 2,
        stepAngle: 0.18
    });
    
} // init

function createTags() {
    var elems = [];
    
    <%

        TagServiceImpl service=new TagServiceImpl();
        GUITag[] tags=service.getTagCloud();
        for(int i=0; i<tags.length; i++){
    %>
         elems.push({text: "<%=tags[i].getTag()%>", id: "<%=tags[i].getTag()%>", weight: <%=""+((float)tags[i].getScale())/10%>});
    <%}%>
    

    return elems;
} // createTags

</script>

</head>

<body onLoad="init();">
<div id="clouder"/>
</body>

</html>

