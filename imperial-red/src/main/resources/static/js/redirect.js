$(document).ready(function(){

  $.ajax({
    type : "GET",
    url : "/connect/twitter/check",
    success : function(msg) {
      var custom = document.getElementsByName("custom");
      if(msg.profileImageUrl!=null){
        $("#sign-in-message").hide();
        $("#gl_div").hide();
        $("#fb_div").hide();
        $("#tw_div").hide();
        $("#sign-in-buttons").html(
            "<img src=\"" + msg.profileImageUrl + "\" alt=\"Image of Twitter\">"+
            "<button id=\"tw\" class=\"disconnect-button\">"+
              "<a class=\"btn btn-block btn-social \">"+
                "<span class=\"fa\"></span> Disconnect"+
              "</a>"+
            "</button>"
        );
        $("#tw").click(function(){
          $.ajax({
            type : "DELETE",
            url : "/connect/twitter/remove",
            success : function(msg) {
              console.log("success");
              $("#sign-in-buttons").html("");
              $("#sign-in-message").show();
              $("#gl_div").show();
              $("#fb_div").show();
              $("#tw_div").show();
            },
            error : function(err) {
              console.log("err");
              $("#result").html("<div class='alert alert-danger lead'>ERROR CON SOCIAL KIO</div>");
            }
          });
        });
      }
      else{

      }

    },
    error : function() {
      $("#result").html(
          "<div class='alert alert-danger lead'>ERROR CON SOCIAL KIO</div>");
    }
  });

  $.ajax({
    type : "GET",
    url : "/connect/facebook/check",
    success : function(msg) {
      var custom = document.getElementsByName("custom");
      if(msg!=null){
        $("#sign-in-message").hide();
        $("#gl_div").hide();
        $("#fb_div").hide();
        $("#tw_div").hide();
        $("#sign-in-buttons").html(
            "<img src=\"data:image/png;base64," + msg + "\" alt=\"Facebook image\">"+
            "<button id=\"fb\" class=\"disconnect-button\">"+
              "<a class=\"btn btn-block btn-social \">"+
                "<span class=\"fa\"></span> Disconnect"+
              "</a>"+
            "</button>"
        );
        $("#fb").click(function(){
          $.ajax({
            type : "DELETE",
            url : "/connect/facebook/remove",
            success : function(msg) {
              console.log("success");
              $("#sign-in-buttons").html("");
              $("#sign-in-message").show();
              $("#gl_div").show();
              $("#fb_div").show();
              $("#tw_div").show();
            },
            error : function(err) {
              console.log("err");
              $("#result").html("<div class='alert alert-danger lead'>ERROR CON SOCIAL KIO</div>");
            }
          });
        });

      }
      else{

      }
    },
    error : function() {
      $("#result").html(
          "<div class='alert alert-danger lead'>ERROR CON SOCIAL KIO</div>");
    }
  });

  $.ajax({
    type : "GET",
    url : "/connect/google/check",
    success : function(msg) {
      var custom = document.getElementsByName("custom");
      if(msg!=null){
        $("#sign-in-message").hide();
        $("#gl_div").hide();
        $("#fb_div").hide();
        $("#tw_div").hide();
        $("#sign-in-buttons").html(
            "<img id=\"gl-img\" src=\"" + msg + "\" alt=\"Image of Google\">"+
              "<button id=\"gl\" class=\"disconnect-button\">"+
                "<a class=\"btn btn-block btn-social \">"+
                  "<span class=\"fa\"></span> Disconnect"+
                "</a>"+
              "</button>"
        );

        $("#gl").click(function(){
          $.ajax({
            type : "DELETE",
            url : "/connect/google/remove",
            success : function(msg) {
              console.log("success");
              $("#sign-in-buttons").html("");
              $("#sign-in-message").show();
              $("#gl_div").show();
              $("#fb_div").show();
              $("#tw_div").show();
            },
            error : function(err) {
              console.log("err");
              $("#result").html("<div class='alert alert-danger lead'>ERROR CON SOCIAL KIO</div>");
            }
          });
        });
      }
    },
    error : function() {
      $("#result").html(
          "<div class='alert alert-danger lead'>ERROR CON SOCIAL KIO</div>");
    }
  });
});




function onSignIn(googleUser) {
	var profile = googleUser.getBasicProfile();
	console.log('ID: ' + profile.getId());
	console.log('Name: ' + profile.getName());
	console.log('Image URL: ' + profile.getImageUrl());
	console.log('Email: ' + profile.getEmail());

	// The ID token you need to pass to your backend:
	var id_token = googleUser.getAuthResponse().id_token;
	console.log("ID Token: " + id_token);

	var xhr = new XMLHttpRequest();
	console.log('PROTOCOL: ' + window.location.protocol);
	console.log('HOSTNAME: ' + window.location.hostname);
	console.log('PORT: ' + window.location.port);
	console.log('HREF: ' + window.location.href);
	xhr.open('POST', window.location.href + 'google-login');
	xhr.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
	xhr.onload = function() {
	  console.log('Signed in as: ' + xhr.responseText);
	};
	xhr.send('idtoken=' + id_token);
}

