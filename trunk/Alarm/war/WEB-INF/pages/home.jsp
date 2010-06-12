<%@ include file="/WEB-INF/pages/common/taglibs.jsp"%>
<html>
	<head>
		<style type="text/css">
			.counter {
			width: 180px;
			display: block;
			margin-bottom: 40px;
			}
		</style>
		<link type="text/css" href="http://ajax.googleapis.com/ajax/libs/jqueryui/1.8.1/themes/smoothness/jquery-ui.css" rel="Stylesheet" />			
		<link type="text/css" href="/css/jquery.countdown.css" rel="Stylesheet" />
		<script src="http://ajax.googleapis.com/ajax/libs/jquery/1.4.2/jquery.min.js"></script> 
		<script src="http://ajax.googleapis.com/ajax/libs/jqueryui/1.8.1/jquery-ui.min.js"></script>
		<script src="/js/jquery-ui-timepicker-addon-0.4.min.js"></script>
		<script src="/js/jquery.countdown.js"></script>
		<script>

			var alarms = {};
			
			alarms.deleteAlarm = function(event) {				
				var id = $(this).attr('id');						
				$.post('/app/alarm/delete', 
					{ alarmId: id },
					 alarms.deleteSuccess,
					 "json"	
				);
				$("#"+id).fadeOut();
				$("#counter"+id).countdown('destroy');		
				
						
			};
			
			alarms.addAlarmSuccess = function(data) {
				var counterId = "counter"+data.id; 
				$("#alarms").append("<a id=\""+data.id+"\" href=\"#\" class=\"delete\">Delete this alarm</a>");
				$("#alarms").append("<div id=\""+counterId+"\" class=\"counter\">New counter</div>");
				$("#"+counterId).countdown({until: +data.time, format:'DHMS'});	
				$("#"+data.id).click(alarms.deleteAlarm);							
			};

			alarms.deleteSuccess = function(data) {
				//alert(data.status);
			}

			
			$(document).ready(function () {
				$(".delete").click(alarms.deleteAlarm);
				$('#dialog').dialog({ autoOpen: false });
				$('#datepicker').datetimepicker();
				$('#add').click(function() {
					$('#dialog').dialog('open');
				});	
				$('#submit').click(function() {
					$.post('/app/alarm/create', 
							$('#alarm-form').serialize(),
							alarms.addAlarmSuccess,
							"json"	
						);
					$('#dialog').dialog('close');	
					});
				
			});
		
		</script>
	</head>	
	<body>	
		<h3>Your Alarms</h3>
		<div id=alarms>	
			<c:forEach items="${alarms}" var="alarm" varStatus="status">				
					<a id="${alarm.id}" href="#" class="delete">Delete this alarm</a>		 	
					<div id="counter${alarm.id}" class="counter"></div>				
					<script type="text/javascript">
						$(function () {
							$('#counter${alarm.id}').countdown({until: +${alarm.secondsForNextAlarm}, format:'DHMS'});			
						});						
			</script>						
			</c:forEach>
		</div>
		<br><br>	
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