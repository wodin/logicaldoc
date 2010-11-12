<html> 
<head>
<script language="javascript">
var sessionId=null;
var exitMessage="You are trying to leave the application without closing the current session. Please use the 'exit' menu.";
 
function unloadTrigger(e){
  if(sessionId!=null){
    e = e || window.event;
    e.returnValue = exitMessage;
  }
}

window.onbeforeunload  = unloadTrigger;
</script>

<style type="text/css">
<!--
#frame {
    width: 100%;
    height: 100%;
}

body {
	margin-top: 0px;
	margin-bottom: 0px;
	margin-left: 0px;
	margin-right: 0px;
}
-->
</style>
<link rel="shortcut icon" type="image/x-icon" href='./skin/brand/favicon.ico' />
</head>
<body onunload="unloadTrigger(event);">
  <iframe id="frame" src="./frontend.jsp" width="100%" height="100%" scrolling="auto" frameborder="0" />
</body>
</html>