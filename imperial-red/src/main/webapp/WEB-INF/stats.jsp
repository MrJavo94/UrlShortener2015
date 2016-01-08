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
    <script type='text/javascript' src="https://www.gstatic.com/charts/loader.js"></script>
    <script type="text/javascript" src="js/stats_functions.js"></script>
	
	<script type="text/javascript">
		var stompClient = null;
		google.load("visualization", "1", {packages:["map"]});
		google.setOnLoadCallback(drawRegionsMap);
		google.setOnLoadCallback(drawMarkersMap);

		<!-- marks-->
		function drawMarkersMap() {
			console.log(${clicksByCity});
	      var data = google.visualization.arrayToDataTable(${clicksByCity});
	      
	      var options = {
	      	showTip: true,
	      	zoomLevel: '2',
	        displayMode: 'markers',
	        colorAxis: {colors: ['green', 'blue', 'red']}
	      };

	      var chart = new google.visualization.Map(document.getElementById('chart_div'));
	      chart.draw(data, options);
	    };

	    function drawMarkersMap2(map) {
	    	//var array=JSON.parse("["+ map.substring(1, map.length-1) +"];");
	    	var array = (new Function("return [" + map.substring(1, map.length-1)+ "];")());
	    	console.log(array);
	    	console.log(array.length);
			var data = google.visualization.arrayToDataTable(array);

			var options = {
				showTip: true,
	      		zoomLevel: '2',
				displayMode: 'markers',
				colorAxis: {colors: ['green', 'blue', 'red']}
			};

			var chart = new google.visualization.Map(document.getElementById('chart_div'));
			chart.draw(data, options);
		};
	    <!-- marks-->
		function drawRegionsMap() {
			console.log(${clicksByCountry});
			var data = google.visualization.arrayToDataTable(${clicksByCountry});
			var options = {};
			var chart = new google.visualization.GeoChart(document.getElementById('geo_chart'));
			chart.draw(data, options);
		}
		
		function drawRegionsMap2(map) {
			var array = (new Function("return [" + map.substring(1, map.length-1)+ "];")());
			var data = google.visualization.arrayToDataTable(array);
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
		                	var clicksByCountry=JSON.parse(newdata.body).clicksByCountry;
		                	var clicksByCity=JSON.parse(newdata.body).clicksByCity;
		                	console.log(clicksByCity);
		                    showStats(clicks);
		                    setFromToVisibility();
		                    drawMarkersMap2(clicksByCity);
		                    drawRegionsMap2(clicksByCountry);

		                    
	                	}
	                	else{
	                		filterStats(idActual);
	                	}
	                }
	                else{
	                	console.log('message arrived, filter');
		                	var clicks=JSON.parse(newdata.body).clicks;
		                	var clicksByCountry=JSON.parse(newdata.body).clicksByCountry;
		                	var clicksByCity=JSON.parse(newdata.body).clicksByCity;
		                    showStats(clicks);
		                    setFromToVisibility();
		                    drawMarkersMap2(clicksByCity);
		                    drawRegionsMap2(clicksByCountry);
		                    
	                }	
                });
            });
            console.log('Ok');
            $("#chart_div").hide();
			$("#mnlg").hide();
			$("#mxlg").hide();
			$("#mnlt").hide();
			$("#mxlt").hide();
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
			to: document.getElementById("to").value,
			min_latitude: document.getElementById("min_latitude").value,
			max_longitude: document.getElementById("max_longitude").value,
			max_latitude: document.getElementById("max_latitude").value,
			min_longitude: document.getElementById("min_longitude").value } )
		}

		function changeMap(){
			if(document.getElementById("button_change").value==0){
				//ocultar pais
				document.getElementById("button_change").value=1;
				$("#chart_div").show();
				$("#geo_chart").hide();
				filterStats();
				$("#mnlg").show();
				$("#mxlg").show();
				$("#mnlt").show();
				$("#mxlt").show();
			}
			else{
				//ocultar ciudades
				document.getElementById("button_change").value=0;
				$("#chart_div").hide();
				$("#geo_chart").show();
				document.getElementById("min_latitude").value="";
				document.getElementById("max_longitude").value="";
				document.getElementById("max_latitude").value="";
				document.getElementById("min_longitude").value="";
				filterStats();
				$("#mnlg").hide();
				$("#mxlg").hide();
				$("#mnlt").hide();
				$("#mxlt").hide();
				
			}
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
				<div class="col-lg-12">
					<div class="form-group">
						<label for="from">From...</label>
						<input type="date" class="form-control" name="from" id="from">
					</div>
					<div class="form-group">
						<label for="to">To...</label>
						<input type="date" class="form-control" name="to" id="to">
					</div>
				</div>
				<div class="col-lg-6">
					<div class="form-group" id="mxlg">
						<label for="max_longitude">Max Longitude</label>
						<input type="number" class="form-control" id="max_longitude">
					</div>
					<div class="form-group" id="mnlg">
						<label for="min_longitude">Min Longitude</label>
						<input type="number" class="form-control" id="min_longitude">
					</div>
				</div>
				<div class="col-lg-6">
					<div class="form-group" id="mxlt">
						<label for="max_latitude">Max Latitude</label>
						<input type="number" class="form-control"  id="max_latitude">
					</div>

					<div class="form-group" id="mnlt">
						<label for="min_latitude">Min Latitude</label>
						<input type="number" class="form-control"  id="min_latitude">
					</div>
				</div>
				<button type="button" class="btn btn-default" onclick='filterStats()'>Update</button>
			</form>
			</br>
			<div id="alert_changer"></div>
		</div>
		<div class="col-sm-8">
			<button type="button" id="button_change" class="btn btn-default" value='0' onclick='changeMap()'>Change View</button>
			</br></br>
			<div id="geo_chart" style="width: 900px; height: 500px;"></div>
			<div id="chart_div" style="width: 900px; height: 500px;"></div>
		</div>
	</div>
	<div class="row">
			
		</div>
</body>
</html>
