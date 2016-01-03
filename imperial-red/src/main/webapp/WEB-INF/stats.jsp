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
                	console.log('message arrived');
                	var clicks=JSON.parse(newdata.body).clicks;
                	var clicksBy=JSON.parse(newdata.body).clicksByCountry;
                    showStats(clicks);
                    drawRegionsMap2(clicksBy);
                });
            });
            console.log('Ok');
            
            setFromToVisibility();
		}
		
		function setFromToVisibility() {
			var from = document.getElementById("show_from");
			var to = document.getElementById("show_to");
			if (from.innerHTML.length <= 5) {
				from.style.display = 'none';
			}
			if (to.innerHTML.length <= 3) {
				to.style.display = 'none';
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
              <div id="clicks">Clicks ${clicks}</div>
			  <div id="show_from">From ${from}</div>
			  <div id="show_to">To ${to}</div>
            </div>
          </div>

      </div>
			<div class="col-sm-4">
				<div id="geo_chart" style="width: 900px; height: 500px;"></div>
			</div>
			<form>
				<div class="form-group">
					<label for="from">From...</label>
					<input type="date" class="form-control" name="from">
				</div>
				<div class="form-group">
					<label for="to">To...</label>
					<input type="date" class="form-control" name="to">
				</div>
				<button type="submit" class="btn btn-default">Update</button>
			</form>
		</div>
		<div class="col-sm-4">
			<div id="geo_chart" style="width: 900px; height: 500px;"></div>
		</div>
	</div>
</body>
</html>
