<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@page import="java.io.*"%>
<%@page import="java.util.*"%>
<%@page import="com.logicaldoc.web.service.*"%>
<%@page import="com.logicaldoc.gui.common.client.beans.*"%>

<html>

<head>
 <script src="./tagcanvas.min.js" type="text/javascript"></script>
 <script type="text/javascript">
  window.onload = function() {
    var w = document.body.clientWidth-20, h = document.body.clientHeight;
    var clouder = document.getElementById('ldTagCloud');
    
    clouder.style.width = w;
    clouder.style.height = h;
    clouder.style.position = "absolute";
    clouder.style.left = 0;
    clouder.style.top = 0;
    clouder.style.border = "0px solid white";
  
    try {
      TagCanvas.textColour = '#0033CC';
      TagCanvas.outlineColour = '#6699FF'; 
      //TagCanvas.fadeIn = 2000;
      TagCanvas.weight = true;
      TagCanvas.weightFrom = 'alt';
      TagCanvas.Start('ldTagCloud','ldTags');
    } catch(e) {
      // something went wrong, hide the canvas container
      document.getElementById('ldTagCloudContainer').style.display = 'none';
    }
  };
 </script>
</head>

<body>
<div id="ldTagCloudContainer">
 <canvas width="300" height="300" id="ldTagCloud">
  <p>In Internet Explorer versions up to 8, things inside the canvas are inaccessible!</p>
 </canvas>
</div>
<div id="ldTags" style="display: none">
 <ul>
    <%

        TagServiceImpl service=new TagServiceImpl();
        GUITag[] tags=service.getTagCloud();
        for(int i=0; i<tags.length; i++){
    %>
         <li><a href="javascript:parent.searchTag('<%=tags[i].getTag()%>');" alt='<%=""+(tags[i].getScale()*4)%>'><%=tags[i].getTag()%></a></li>
    <%}%>
 </ul>
</div>

</body>

</html>

