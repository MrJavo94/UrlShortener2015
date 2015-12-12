<!doctype html>
<html>
<head>
<title>Statistics</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<link rel="stylesheet" type="text/css"
	href="webjars/bootstrap/3.3.5/css/bootstrap.min.css" />
	<script type="text/javascript" src="https://www.google.com/jsapi"></script>
	<script type="text/javascript">
	      google.load("visualization", "1", {packages:["geochart"]});
	      google.setOnLoadCallback(drawRegionsMap);

	      function drawRegionsMap() {
			var data = google.visualization.arrayToDataTable(${clicksByCountry});
	        var options = {};
	        var chart = new google.visualization.GeoChart(document.getElementById('geo_chart'));
	        chart.draw(data, options);
	      }
	</script>
</head>
<body>
	<div class="container-full">
			<h1>Stastistics</h1>
			<p class="lead">Statistic from the URL Shortener</p>
			<br>
		<div class="row">
			<div class="col-sm-4 text-center">
          <div class="panel panel-default">
            <div class="panel-heading">
              <h3 class="panel-title">Statistics</h3>
            </div>
            <div class="panel-body">
              <p>Target ${target}</p>
              <p>Created date ${date}</p>
              <p>Clicks ${clicks}</p>
            </div>
          </div>

      </div>
			<div class="col-sm-4">
				<div id="geo_chart" style="width: 900px; height: 500px;"></div>
			</div>
		</div>

</body>
</html>
