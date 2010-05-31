<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>
<html>
	<head>
		<link type="text/css" href="http://ajax.googleapis.com/ajax/libs/jqueryui/1.8.1/themes/smoothness/jquery-ui.css" rel="Stylesheet" />			
		<script src="http://ajax.googleapis.com/ajax/libs/jquery/1.4.2/jquery.min.js"></script> 
		<script src="http://ajax.googleapis.com/ajax/libs/jqueryui/1.8.1/jquery-ui.min.js"></script>
		<script src="/js/jquery-ui-timepicker-addon-0.4.min.js"></script>
		<script>
			$(document).ready(function () {
				$('#datepicker').timepicker();
				$('#add').click(function() {
					$('#dialog').dialog();
				});	
				$('#submit').click(function() {
					$.post('/app/alarm/create', $('#alarm-form').serialize());					
					});	
			});
		
		</script>
	</head>	
	<body>
		<a id="add" href="#">Add new Alarm</a>
		<div style="display:none" id="dialog" title="Add new Alarm">
			<form action="${ctx}/alarm/create" id="alarm-form">
				<input id="datepicker" name="time">
				<br>
				<input type="button" id="submit" value="Add new alarm">
			</form>
		</div>		
	</body>