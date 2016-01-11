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
				$("#rules").show();
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

function deleteRule(boton){
	var idRule=$(boton).attr('id');
	event.preventDefault();
    var urlActual= document.URL.split("/");
	var idActual=urlActual[3].substring(0, urlActual[3].length-1);
	$.ajax({
	      type : "POST",
	      url : "/deleteRule",
	      data : {url : idActual, rule : idRule},
	      success : function(links) {
	    	  $.ajax({
			      type : "GET",
			      url : "/getRules",
			      data : {url : idActual},
			      success : function(links) {
			    	  console.log('2');
			    	  console.log(links);
			    	  var content = "</br>";
			    	  var urls = links.split(" ");
			    	  for (var i=0; i<links.length; i++) {
			    		  if (urls[i] != undefined && urls[i] != "") {
				    		content += "<div >"+urls[i]+" <button id=\""+i+"\" type=\"button\" class=\"btn btn-default\" onclick='deleteRule(this)' value='0'>Delete rule</button>"
		    			  + "<button  type=\"button\" class=\"btn btn-default\" onclick='showRule(\"_"+i+"\")' value='0'>Edit rule</button></div><br>"
		    			  + "<div id=\"_"+i+"\" style=\"display:none;\"><textarea id=\"_t"+i+"\">"+urls[i]+"</textarea></br>"
		    			  + "<div id=\"result_"+i+"\"></div></br>"
		    			  + "<button  type=\"button\" class=\"btn btn-default\" onclick='editRule("+i+")' value='0'>OK</button></br></br></div>";
			    		  }
			    	  }
			          $("#rules_result").html(content);
			      }
			    });
	      }
	});
}
function showRule(aux){
	if(document.getElementById(aux).value==1){
		document.getElementById(aux).value=0;
		$("#"+aux).hide();
	}
	else{
		document.getElementById(aux).value=1;
		$("#"+aux).show();
	}
	
}

function editRule(i){
	event.preventDefault();
    var urlActual= document.URL.split("/");
	var idActual=urlActual[3].substring(0, urlActual[3].length-1);
	var editRule=document.getElementById("_t"+i).value;
	$.ajax({
		type : "POST",
		url : "/editRule",
		data : {url : idActual, rule : i, edit: editRule},
		success : function(links) {
			$.ajax({
		      type : "GET",
		      url : "/getRules",
		      data : {url : idActual},
		      success : function(links) {
		    	  console.log('2');
		    	  console.log(links);
		    	  var content = "</br>";
		    	  var urls = links.split(" ");
		    	  for (var i=0; i<links.length; i++) {
		    		  if (urls[i] != undefined && urls[i] != "") {
		    			  content += "<div >"+urls[i]+" <button id=\""+i+"\" type=\"button\" class=\"btn btn-default\" onclick='deleteRule(this)' value='0'>Delete rule</button>"
		    			  + "<button  type=\"button\" class=\"btn btn-default\" onclick='showRule(\"_"+i+"\")' value='0'>Edit rule</button></div><br>"
		    			  + "<div id=\"_"+i+"\" style=\"display:none;\"><textarea id=\"_t"+i+"\">"+urls[i]+"</textarea></br>"
		    			  + "<div id=\"result_"+i+"\"></div></br>"
		    			  + "<button  type=\"button\" class=\"btn btn-default\" onclick='editRule("+i+")' value='0'>OK</button></br></br></div>";
		    		  }
		    	  }
		          $("#rules_result").html(content);
		      }
		    });
		},
		error : function() {
		    $("#result_"+i).html("<div class='alert alert-danger lead'>ERROR</div>");
		}
	});
}
$(document).ready(function() {
	$("#rules_btn").click(function() {
		//if(document.getElementById("rules_boton").value==0){
			document.getElementById("rules_boton").value=1;
			$("#rules_result").show();
			event.preventDefault();
		    var urlActual= document.URL.split("/");
			var idActual=urlActual[3].substring(0, urlActual[3].length-1);
			console.log('1');
		    $.ajax({
		      type : "GET",
		      url : "/getRules",
		      data : {url : idActual},
		      success : function(links) {
		    	  console.log('2');
		    	  console.log(links);
		    	  var content = "</br>";
		    	  var urls = links.split(" ");
		    	  for (var i=0; i<links.length; i++) {
		    		  if (urls[i] != undefined && urls[i] != "") {
		    			  content += "<div >"+urls[i]+" <button id=\""+i+"\" type=\"button\" class=\"btn btn-default\" onclick='deleteRule(this)' value='0'>Delete rule</button>"
		    			  + "<button  type=\"button\" class=\"btn btn-default\" onclick='showRule(\"_"+i+"\")' value='0'>Edit rule</button></div><br>"
		    			  + "<div id=\"_"+i+"\" style=\"display:none;\"><textarea id=\"_t"+i+"\">"+urls[i]+"</textarea></br>"
		    			  + "<div id=\"result_"+i+"\"></div></br>"
		    			  + "<button  type=\"button\" class=\"btn btn-default\" onclick='editRule("+i+")' value='0'>OK</button></br></br></div>";
		    		  }
		    	  }
		          $("#rules_result").html(content);
		      }
		    });
		/*}
		else{
			document.getElementById("rules_boton").value=0;
			$("#rules_result").hide();
		}*/
   });
});



