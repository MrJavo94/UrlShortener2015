$(document).ready(function(){
	$("#alert_changer").hide();
	
	$.ajax({
		type:"GET",
		url:"/checkAuth?url=" + window.location.pathname,
		success : function(result) {
			if (result=="true") {
				console.log('Authenticated user is owner. Showing options for changing alert.');
				setAlertChangerVisibility();
			} else {
				console.log('Authenticated user is not owner.');
			}
		},
		error : function() {
		    console.log('Error in owner-checker');
		}
	});
});

function setAlertChangerVisibility() {
	console.log('It works!!! kio');
	$("#alert_changer").show();
}
