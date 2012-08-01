<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%! static String MODULE="frontend"; %>
<%@ include file="header.jsp" %>

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
	document.getElementById('loadingTitle').innerHTML = 'Loading';
</script>
		<script type="text/javascript">
	document.getElementById('loadingMsg').innerHTML = 'Loading Core API...';
</script>

		<!--include the SC Core API-->
		<script src="frontend/sc/modules/ISC_Core.js?isc_version=7.1.js"></script>

		<!--include SmartClient -->
		<script type="text/javascript">
	document.getElementById('loadingMsg').innerHTML = 'Loading UI Components...';
</script>
		<script src='frontend/sc/modules/ISC_Foundation.js'></script>
		<script src='frontend/sc/modules/ISC_Containers.js'></script>
		<script src='frontend/sc/modules/ISC_Grids.js'></script>
		<script src='frontend/sc/modules/ISC_Forms.js'></script>
		<script src='frontend/sc/modules/ISC_RichTextEditor.js'></script>
		<script src='frontend/sc/modules/ISC_Calendar.js'></script>
		<script type="text/javascript">
	document.getElementById('loadingMsg').innerHTML = 'Loading Data API...';
</script>
		<script src='frontend/sc/modules/ISC_DataBinding.js'></script>

<!--load skin-->
<script type="text/javascript">document.getElementById('loadingMsg').innerHTML = 'Loading skin...';</script>
<script type="text/javascript">
    document.write("<"+"script src=frontend/sc/skins/" + currentSkin + "/load_skin.js?isc_version=7.1.js><"+"/script>");
</script>

		<!--load localizations-->
		<script type="text/javascript">
	document.getElementById('loadingMsg').innerHTML = 'Loading messages...';
</script>
		
		<div id="DropSpot" style="width:0px; height:0px; z-index:-1000"></div>

<%@ include file="footer.jsp" %>
