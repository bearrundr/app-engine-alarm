<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>
<html>
	<head>
		<style type="text/css">
			.counter {
			width: 130px;
			}
		</style>
		<link type="text/css" href="http://ajax.googleapis.com/ajax/libs/jqueryui/1.8.1/themes/smoothness/jquery-ui.css" rel="Stylesheet" />			
		<link type="text/css" href="/css/jquery.countdown.css" rel="Stylesheet" />
		<script src="http://ajax.googleapis.com/ajax/libs/jquery/1.4.2/jquery.min.js"></script> 
		<script src="http://ajax.googleapis.com/ajax/libs/jqueryui/1.8.1/jquery-ui.min.js"></script>
		<script src="/js/jquery-ui-timepicker-addon-0.4.min.js"></script>
		<script src="/js/jquery.countdown.js"></script>
		<script>
			$(document).ready(function () {
				$('#dialog').dialog({ autoOpen: false });
				$('#datepicker').timepicker();
				$('#add').click(function() {
					$('#dialog').dialog('open');
				});	
				$('#submit').click(function() {
					$.post('/app/alarm/create', $('#alarm-form').serialize());
					$('#dialog').dialog('close');	
					 location.reload();				
					});
				
			});
		
		</script>
	</head>	
	<body>	
		<h3>Your Alarms</h3>	
		<c:forEach items="${alarms}" var="alarm" varStatus="status">		 	
			<div id="counter${status.index}" class="counter"></div>
				<script type="text/javascript">
					$(function () {
						$('#counter${status.index}').countdown({until: +${alarm.secondsForNextAlarm}, format:'HMS'});			
					});		
		</script>			
		<br><br>	
		</c:forEach>
	
		<div>
			<a id="add" href="#">Add new Alarm</a>
		</div>
		<div style="display:none" id="dialog" title="Add new Alarm">
			<b>Enter Time in UTC time zone</b>
			<form action="${ctx}/alarm/create" id="alarm-form">
				<input id="datepicker" name="time">
				<br>
				<input type="button" id="submit" value="Add new alarm">
			</form>
		</div>			
	</body>