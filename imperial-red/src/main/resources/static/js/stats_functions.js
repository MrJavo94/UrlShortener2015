$(document).ready(function(){	
	$.ajax({
		type:"GET",
		url:"/checkAuth?url=" + window.location.pathname,
		success : function(result) {
			if (result != "") {
				console.log('Authenticated user is owner. Showing options for changing alert.');
				var parts = result.split("##");
				var expireDate = parts[0];
				var alertDate = parts[1];
				console.log('Metiendo html');
				$("#alert_changer").html(
						"<p>Expire Date: " + expireDate + "</p>" +
						"<p>Alert Date: " + alertDate + "</p>" + 
						"<label>Change expire date...</label>" +
						"<input type=\"date\" class=\"form-control\" name=\"expire\" id=\"expire\">" +
						"<label>Change previous days alert...</label>" +
						"<select class=\"form-control\" name=\"days\" id=\"days\">" + 
							"<option>1</option><option>7</option><option>15</option><option>30</option>" +
						"</select>" +
						"<button type=\"submit\" class=\"btn btn-default\" onClick=\"changeExpire()\">Update</button>"
				);
				//$("#rules").show();
				console.log('Metido html');
			} else {
				console.log('Authenticated user is not owner.');
			}
		},
		error : function() {
		    console.log('Error in owner-checker');
		}
	});

  $("#rules").submit(function(event) {
    event.preventDefault();
    var urlActual= document.URL.split("/");
	var idActual=urlActual[3].substring(0, urlActual[3].length-1);
    document.getElementById("url").value=idActual;
    console.log($(this).serialize());
    $.ajax({
      type : "POST",
      url : "/setRules",
      data : $(this).serialize(),
      success : function(msg) {
          $("#result").html("<div class='alert alert-success lead'>OK</div>");

      },
      error : function() {
          $("#result").html("<div class='alert alert-danger lead'>ERROR</div>");
      }
    });
  });
});

function changeExpire() {
	var expire = document.getElementById("expire").value;
	var selectDays = document.getElementById("days");
	var days = selectDays.options[selectDays.selectedIndex].text;
	console.log(expire + "##" + days);
	
	$.ajax({
		type:"GET",
		url:"/changeExpire?url=" + window.location.pathname + "&expire=" + expire + "&days=" + days,
		success : function(result) {
			console.log('#' + window.location.pathname + '#');
			console.log('Ended!');
		},
		error : function() {
		    console.log('Error!');
		}
	});
}


