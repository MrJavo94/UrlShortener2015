$(document).ready(function(){
  $('.button-checkbox').each(function(){
      // Settings
      var widget = $(this),
          button = widget.find('button'),
          checkbox = widget.find('input:checkbox'),
          color = button.data('color'),
          settings = {
              on: {
                  icon: 'glyphicon glyphicon-check'
              },
              off: {
                  icon: 'glyphicon glyphicon-unchecked'
              }
          };

      // Event Handlers
      button.on('click', function () {
          checkbox.prop('checked', !checkbox.is(':checked'));
          checkbox.triggerHandler('change');
          updateDisplay();
      });
      checkbox.on('change', function () {
          updateDisplay();
      });

      // Actions
      function updateDisplay() {
          var isChecked = checkbox.is(':checked');

          // Set the button's state
          button.data('state', (isChecked) ? "on" : "off");

          // Set the button's icon
          button.find('.state-icon')
              .removeClass()
              .addClass('state-icon ' + settings[button.data('state')].icon);

          // Update the button's color
          if (isChecked) {
              button
                  .removeClass('btn-default')
                  .addClass('btn-' + color + ' active');
          }
          else {
              button
                  .removeClass('btn-' + color + ' active')
                  .addClass('btn-default');
          }
      }

      // Initialization
      function init() {

          updateDisplay();

          // Inject the icon if applicable
          if (button.find('.state-icon').length == 0) {
              button.prepend('<i class="state-icon ' + settings[button.data('state')].icon + '"></i>');
          }
      }
      init();
  });

  $("#personal").on('keyup change',function(event) {
    $("#recom").hide();
    $("#anunc").hide();
    $("#no").hide();
    $("#yes").hide();
    $("#loading").show();
    $.ajax({
      type:"GET",
      url:"/rec/rec",
      data:$("#shortener").serialize(),
      success : function() {
        $("#loading").hide();
        $("#no").hide();
        $("#yes").show();
        $("#validation").removeClass("has-error").addClass("has-success");
      },
      error : function(msg) {
        $("#loading").hide();
        $("#yes").hide();
        $("#no").show();
        $("#validation").removeClass("has-success").addClass("has-error");
        if(msg.responseJSON.length>0){
          $("#anunc").show();
          $("#anunc").html( "<h3>URL ya ocupada. Sugerencias :<h3>");
			    var botones = "";
 			    for(var i = 0; i<msg.responseJSON.length; i++){
            var seg = msg.responseJSON[i];
            botones += "<button id='" + seg + "' onclick='refrescarSugerencia(this.id)' type='button' class='btn btn-link'>" + seg + " </button>";
          }
	        $("#recom").show();
	        $("#recom").html(botones);
	        $("#recom").addClass("alert alert-success lead");
       }
     }
    });
  });

  $('#login').click(
      function(event){
      if($('#nick').val().length < 1){
          alert("Nick cannot be blank");
          // Prevent form submission
          event.preventDefault();
      }
  });

  $("#shortener").submit(function(event) {
    event.preventDefault();
    $.ajax({
      type : "POST",
      url : "/link",
      data : $(this).serialize(),
      success : function(msg) {
          var custom = document.getElementsByName("custom");
          if(msg.token!=null){
              $("#result").html(
                  "<h3>Aquí tiene su enlace acortado</h3>"
                  + "<div class='alert alert-success lead'><a target='_blank' href='"
                  + msg.uri
                  + "'>"
                  + msg.uri
                  + "</a></div></br><h3>Token: <h3>"
                  + " <div class='alert alert-success lead'>?token="
                  + msg.token
                  + "</div>"
                  + "<h3>Código QR:<h3><img src=\"https://api.qrserver.com/v1/create-qr-code/?size=150x150&data="+createQRData(msg.uri)+"?token="+msg.token+"\">");
          }
          else{
              $("#result").html(
                  "<h3>Aquí tiene su enlace acortado</h3>"
                  + "<div class='alert alert-success lead'><a target='_blank' href='"
                  + msg.uri
                  + "'>"
                  + msg.uri
                  + "</a></div>"
                  + "<h3>Código QR:</h3><img src=\"https://api.qrserver.com/v1/create-qr-code/?size=150x150&data="+createQRData(msg.uri)+"\">");
          }

      },
      error : function() {
          $("#result").html("<div class='alert alert-danger lead'>ERROR</div>");
      }
    });
  });
  $(".vc").click(function() {
  	if ($(this).attr("value")=="option1") {
  		$(".vcard").hide();
  	}
  	if ($(this).attr("value")=="option2") {
  		$(".vcard").show();
  	}
  });

  // Function for adding email input
  $(document).on('click', '.btn-add', function(e){
    e.preventDefault();
    console.log('Entered');
    var controlForm = $('.list_emails'),
        currentEntry = $(this).parents('.entry:first'),
        newEntry = $(currentEntry.clone()).appendTo(controlForm);

    newEntry.find('input').val('');
    controlForm.find('.entry:not(:last) .btn-add')
        .removeClass('btn-add').addClass('btn-remove')
        .removeClass('btn-success').addClass('btn-danger')
        .html('<span class="glyphicon glyphicon-minus"></span>');
  }).on('click', '.btn-remove', function(e){
    $(this).parents('.entry:first').remove();
    e.preventDefault();
  });


  $.ajax({
    type : "GET",
    url : "/connect/twitter/check",
    success : function(msg) {
    	console.log(msg);
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
              window.location = window.location;
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
        		"<img id=\"fb-img\" src=\"" + msg + "\" alt=\"Facebook image\">"+
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
              location.reload();
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
              location.reload();
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


function refrescarSugerencia(id) {
	$("#personal").val(id);
	 $("#personal").trigger("keyup");
}

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
//Creates the data inside the QR
function createQRData(uri) {
	var optionQR = document.getElementsByName("optionQR");
	if (optionQR[0].checked) {
		//If 1st option checked, only uri in QR
		return encodeURIComponent(uri);
	} else if (optionQR[1].checked) {
		//If 2nd option checked, creates vcard in QR
		var name = document.getElementsByName("name")[0].value;
		var phone = document.getElementsByName("phone")[0].value;
		var data = "BEGIN:VCARD\nVERSION:2.1\nFN:" + name + "\nN:;" + name + "\nTEL;HOME;VOICE:"
			+ phone + "\nURL:" + uri + "\nEND:VCARD\n";
		return encodeURIComponent(data);
	} else {
		console.log('Entered unexpected state in QR option function');
	}
}
