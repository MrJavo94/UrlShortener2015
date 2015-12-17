$(document).ready(
    function() {
    
    $(function () {
        $('.button-checkbox').each(
            function () {

            // Settings
            var $widget = $(this),
                $button = $widget.find('button'),
                $checkbox = $widget.find('input:checkbox'),
                color = $button.data('color'),
                settings = {
                    on: {
                        icon: 'glyphicon glyphicon-check'
                    },
                    off: {
                        icon: 'glyphicon glyphicon-unchecked'
                    }
                };

            // Event Handlers
            $button.on('click', function () {
                $checkbox.prop('checked', !$checkbox.is(':checked'));
                $checkbox.triggerHandler('change');
                updateDisplay();
            });
            $checkbox.on('change', function () {
                updateDisplay();
            });

            // Actions
            function updateDisplay() {
                var isChecked = $checkbox.is(':checked');

                // Set the button's state
                $button.data('state', (isChecked) ? "on" : "off");

                // Set the button's icon
                $button.find('.state-icon')
                    .removeClass()
                    .addClass('state-icon ' + settings[$button.data('state')].icon);

                // Update the button's color
                if (isChecked) {
                    $button
                        .removeClass('btn-default')
                        .addClass('btn-' + color + ' active');
                }
                else {
                    $button
                        .removeClass('btn-' + color + ' active')
                        .addClass('btn-default');
                }
            }

            // Initialization
            function init() {

                updateDisplay();

                // Inject the icon if applicable
                if ($button.find('.state-icon').length == 0) {
                    $button.prepend('<i class="state-icon ' + settings[$button.data('state')].icon + '"></i>');
                }
            }
            init();
        });
});
      $("#personal").on('keyup change',
                function(event) {
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
	                    $("#anunc").html( "<h3>URL ya ocupada. Sugerencias :<h3>");
	          			var botones = "";
	           			for(var i = 0; i<msg.responseJSON.length; i++)
						{
				              var seg = msg.responseJSON[i];
				              botones += "<button id='" + seg + "' onclick='refrescarSugerencia(this.id)' type='button' class='btn btn-link'>" + seg + " </button>";
				        }
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

        $("#shortener").submit(
            function(event) {
                event.preventDefault();
                $.ajax({
                    type : "POST",
                    url : "/link",
                    data : $(this).serialize(),
                    success : function(msg) {
                        var custom = document.getElementsByName("custom");
                        if(msg.owner!=null){
                            $("#result").html(
                                "<h3>Aquí tiene su enlace acortado</h3>"
                                + "<div class='alert alert-success lead'><a target='_blank' href='"
                                + msg.uri
                                + "'>"
                                + msg.uri
                                + "</a></div></br><h3>Token: <h3>"
                                + " <div class='alert alert-success lead'>?token="
                                + msg.owner
                                + "</div>");
                        }
                        else{
                            $("#result").html(
                                "<h3>Aquí tiene su enlace acortado</h3>"
                                + "<div class='alert alert-success lead'><a target='_blank' href='"
                                + msg.uri
                                + "'>"
                                + msg.uri
                                + "</a></div>");
                        }
                        
                    },
                    error : function() {
                        $("#result").html(
                                "<div class='alert alert-danger lead'>ERROR</div>");
                    }
                });
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

