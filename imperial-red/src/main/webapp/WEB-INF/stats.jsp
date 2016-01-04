<!doctype html>
<html>
<head>
	<title>Statistics</title>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<meta name="viewport" content="width=device-width, initial-scale=1.0">
	<link rel="stylesheet" type="text/css"
		href="webjars/bootstrap/3.3.5/css/bootstrap.min.css" />
	<script type="text/javascript" src="https://www.google.com/jsapi"></script>
	<script type="text/javascript" src="js/sockjs-0.3.4.js"></script>
    <script type="text/javascript" src="js/stomp.js"></script>
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js"></script>
	<script type="text/javascript">
		var stompClient = null;
		google.load("visualization", "1", {packages:["geochart"]});
		google.setOnLoadCallback(drawRegionsMap);

		function drawRegionsMap() {
			console.log(${clicksByCountry});
			var data = google.visualization.arrayToDataTable(${clicksByCountry});
			var options = {};
			var chart = new google.visualization.GeoChart(document.getElementById('geo_chart'));
			chart.draw(data, options);
		}
		
		function drawRegionsMap2(jander) {
			console.log('Updating in client');
			console.log(jander);
			var data = google.visualization.arrayToDataTable(jander);
			var options = {};
			var chart = new google.visualization.GeoChart(document.getElementById('geo_chart'));
			chart.draw(data, options);
		}
		function showStats(message) {
            var clicks=document.createTextNode("Clicks " + message);
            var old=document.getElementById('clicks');
            old.replaceChild(clicks, old.childNodes[0]);
        }

		function init() {
			var socket = new SockJS('/stats');
            stompClient = Stomp.over(socket);
            stompClient.connect({}, function(frame) {
            	var urlActual= document.URL.split("/");
			    var idActual=urlActual[3].substring(0, urlActual[3].length-1);
			    var subscripcion='/topic/'+idActual;
			    console.log(idActual);
                console.log('Connected to: ' + frame);
                stompClient.subscribe(subscripcion, function(newdata){
	                console.log(JSON.parse(newdata.body).filter);
	                if(JSON.parse(newdata.body).filter==false){
	                	if(document.getElementById("from").value.localeCompare('')==0 &&
	                			document.getElementById("to").value.localeCompare('')==0){
	                		console.log('message arrived, no filter');
		                	var clicks=JSON.parse(newdata.body).clicks;
		                	var clicksBy=JSON.parse(newdata.body).clicksByCountry;
		                    showStats(clicks);
		                    setFromToVisibility();
		                    drawRegionsMap2(clicksBy);
	                	}
	                	else{
	                		filterStats(idActual);
	                	}
	                }
	                else{
	                	console.log('message arrived, filter');
		                	var clicks=JSON.parse(newdata.body).clicks;
		                	var clicksBy=JSON.parse(newdata.body).clicksByCountry;
		                    showStats(clicks);
		                    setFromToVisibility();
		                    drawRegionsMap2(clicksBy);
	                }	
                });
            });
            console.log('Ok');
            
            setFromToVisibility();
		}

		function filterStats(){
			var urlActual= document.URL.split("/");
			var idActual=urlActual[3].substring(0, urlActual[3].length-1);
			console.log("peticion filter");
    		console.log(idActual+".."+document.getElementById("from").value+".."+document.getElementById("to").value)
    		$.get( "/stats/filter/",
			{ id: idActual,
			from: document.getElementById("from").value,
			to: document.getElementById("to").value } )
		    /*.done(function(data) {
				console.log("ajax bien");
				console.log(data.clicks);
				showStats(data.clicks);
				setFromToVisibility();
            	drawRegionsMap2(data.clicksByCountry);
            	
		    })
		    	.fail(function(data) {
			    console.log("ajax mal");

		    });*/
			
		}
		
		function setFromToVisibility() {
			var from = document.getElementById("from");
			var to = document.getElementById("to");
			if (document.getElementById("from").value.localeCompare('')==0) {
				$("#show_from").hide();
			}
			else{
				$("#show_from").text("From " + document.getElementById("from").value);
				$("#show_from").show();
			}
			if (document.getElementById("to").value.localeCompare('')==0) {
				$("#show_to").hide();
			}
			else{
				$("#show_to").text("To " + document.getElementById("to").value);
				$("#show_to").show();
			}
		}
	</script>
</head>
<body onload=init()>
	<div class="container-full">
	<h1>Statistics</h1>
	<p class="lead">Statistics from the URL Shortener</p>
	<br>
	<div class="row">
		<div class="col-sm-4 text-center">
			<div class="panel panel-default">
				<div class="panel-heading">
					<h3 class="panel-title">Statistics</h3>
				</div>
				<div class="panel-body">
					<div id="target">Target ${target}</div>
					<div id="date">Created date ${date}</div>
					<div id ="clicks">Clicks ${clicks}</div>
					<div><p id="show_from">From ${from}</p></div>
					<div><p id="show_to">To ${to}</p></div>
				</div>
			</div>
			<form>
				<div class="form-group">
					<label for="from">From...</label>
					<input type="date" class="form-control" name="from" id="from">
				</div>
				<div class="form-group">
					<label for="to">To...</label>
					<input type="date" class="form-control" name="to" id="to">
				</div>
				<button type="button" class="btn btn-default" onclick='filterStats()'>Update</button>
			</form>
		</div>
		<div class="col-sm-4">
			<div id="geo_chart" style="width: 900px; height: 500px;"></div>
		</div>
	</div>
</body>
</html>
