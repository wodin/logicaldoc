<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%
    String locale = request.getParameter("locale");
	if (locale == null || "".equals(locale))
		locale = "";
%>
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<title></title>
		<!--CSS for loading message at application Startup-->
		<style type="text/css">
body {
	overflow: hidden
}

#loading {
	border: 1px solid #ccc;
	position: absolute;
	left: 45%;
	top: 40%;
	padding: 2px;
	z-index: 20001;
	height: auto;
}

#loading a {
	color: #225588;
}

#loading .loadingIndicator {
	background: white;
	font: bold 13px tahoma, arial, helvetica;
	padding: 10px;
	margin: 0;
	height: auto;
	color: #444;
}

#loadingMsg {
	font: normal 10px arial, tahoma, sans-serif;
}
</style>
        <link REL="STYLESHEET" HREF="./skin/style.css" TYPE="text/css" />
		<!--load context-->
		<script src="./context.jsp"></script>
		<link rel='image/x-icon' href='./skin/images/favicon.ico' />
		<script type="text/javascript">
		   // Determine what skin file to load
           var currentSkin = "Enterprise";
           var isomorphicDir = "setup/sc/";
        </script>
	</head>
	<body>
		<!--add loading indicator while the app is being loaded-->
		<div id="loadingWrapper">
			<div id="loading">
				<div class="loadingIndicator">
					<img src="./skin/images/loading.gif" width="16"
						height="16"
						style="margin-right: 8px; float: left; vertical-align: top;" />
					<span id="loadingTitle"></span>
					<br />
					<span id="loadingMsg">Loading styles and images...</span>
				</div>
			</div>
		</div>

		<script type="text/javascript">
	document.getElementById('loadingTitle').innerHTML = context.product_name
			+ ' ' + context.product_release;
</script>
		<script type="text/javascript">
	document.getElementById('loadingMsg').innerHTML = 'Loading Core API...';
</script>

		<!--include the SC Core API-->
		<script src="setup/sc/modules/ISC_Core.js?isc_version=7.1.js"></script>

		<!--include SmartClient -->
		<script type="text/javascript">
	document.getElementById('loadingMsg').innerHTML = 'Loading UI Components...';
</script>
		<script src='setup/sc/modules/ISC_Foundation.js'></script>
		<script src='setup/sc/modules/ISC_Containers.js'></script>
		<script src='setup/sc/modules/ISC_Grids.js'></script>
		<script src='setup/sc/modules/ISC_Forms.js'></script>
		<script src='setup/sc/modules/ISC_RichTextEditor.js'></script>
		<script src='setup/sc/modules/ISC_Calendar.js'></script>
		<script type="text/javascript">
	document.getElementById('loadingMsg').innerHTML = 'Loading Data API...';
</script>
		<script src='setup/sc/modules/ISC_DataBinding.js'></script>

<!--load skin-->
<script type="text/javascript">document.getElementById('loadingMsg').innerHTML = 'Loading skin...';</script>
<script type="text/javascript">
    document.write("<"+"script src=setup/sc/skins/" + currentSkin + "/load_skin.js?isc_version=7.1.js><"+"/script>");
</script>

		<!--load localizations-->
		<script type="text/javascript">
	document.getElementById('loadingMsg').innerHTML = 'Loading messages...';
</script>

		<!--include the nocache JS-->
		<script type="text/javascript" src="setup/setup.nocache.js"></script>
		
		<script>
		  var defaultFolder = '<%=System.getProperty("user.home").replaceAll("\\\\","/")+"/logicaldoc"%>';	
        </script>
	</body>
</html>
