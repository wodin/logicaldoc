<html> 
<head>
<script language="javascript">
var sessionId=null;
var exitMessage="You are trying to leave the application without closing the current session. Please use the 'exit' menu.";
var isIE = navigator.userAgent.indexOf("MSIE")!=-1;
var isFF = navigator.userAgent.indexOf("Firefox")!=-1;

function unloadTriggerIE(e){
  if(sessionId!=null){
    e = e || window.event;
    e.returnValue = exitMessage;
  }
}

function unloadTriggerChrome(){
  if(sessionId!=null){
	return exitMessage;
  }
}

if(isIE || isFF)
   window.onbeforeunload  = unloadTriggerIE;
else
   window.onbeforeunload = unloadTriggerChrome;
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
  <iframe id="frame" src="./frontend.jsp?<%=request.getQueryString()%>" width="100%" height="100%" scrolling="auto" frameborder="0" />
</body>
</html>