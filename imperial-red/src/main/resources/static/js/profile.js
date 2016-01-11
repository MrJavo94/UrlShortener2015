/* AJAX function retrieving links from user */
$(document).ready(
	function() {
		console.log('1');
		$.ajax({
			type : "GET",
			url : "/userlinks",
			data : $(this).serialize(),
			success : function(links) {
				console.log('2');
				console.log(links);
				var content = "<table class=\"table\"><tr><th>Shortened URL</th>" +
						"<th>Target</th><th>Statistics</th></tr>";
				var linksJSON = $.parseJSON(links);
				for (var i = 0; i < linksJSON.length; i++) {
					var uri = linksJSON[i].uri;
					var target = linksJSON[i].target;
					content += "<tr><td><a href=\"" + uri + "\">" + uri + "</a></td>"
							+ "<td><a href=\"" + target + "\">" + target + "</a></td>"
							+ "<td><a href=\"" + uri + "+\">" + uri + "+</a></td>";
				}
				content += "</table>";
				$("#links_list").html(content);
			}
		});
	});

/* 
 * When submitting password_changer form, a POST is created that returns
 * the status of the password modification to the user, using AJAX.
 */
$(document).ready(
	function(event) {
		$("#password_changer").submit(
			function(event) {
				event.preventDefault();
				$.ajax({
					type : "POST",
					url : "/changePassword",
					data : $(this).serialize(),
					success : function(code) {
						console.log('Changed password');
						$("#result").html(
								"<div class='alert alert-success lead'>Password changed successfully</div>"
						);
					},
					error : function(code) {
						if (code.responseText=="1") {
							$("#result").html(
									"<div class='alert alert-danger lead'>Old password incorrectly entered</div>"
							);
						}
						if (code.responseText=="2") {
							$("#result").html(
									"<div class='alert alert-danger lead'>New passwords don't match</div>"
							);
						}
						
					}
				});
			});
	
	});
		