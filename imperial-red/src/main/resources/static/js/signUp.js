$(document).ready(
    function() {
        $('#submit').click(function(event){
           	$.ajax({
				type : "GET",
				data : $("#mail")
				.serialize(),
				async: false,
				url : "/users/checkmail",
				success : function() {
					return true;
				},
				error : function(msg) {
					alert("Mail already registered");
					event.preventDefault();
					}
				});
        	
        	
            data = $('#passwordRegister').val();
            
            console.log(data);
            var len = data.length;
            console.log(len);
            
            if(len < 1) {
                alert("Password cannot be blank");
                // Prevent form submission
                event.preventDefault();
            }
             
            if($('#passwordRegister').val() != $('#repasswordRegister').val()) {
                alert("Password and Confirm Password don't match");
                // Prevent form submission
                event.preventDefault();
            }
             
            if($('#t_and_c').is(":not(:checked)")){
                alert("You have to accept Terms and Conditions");
                // Prevent form submission
                event.preventDefault();
            }
        });
    }
);
$(document).ready(
    function () {
    $('.button-checkbox').each(function () {

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
                $button.val("1");
            }
            else {
                $button
                    .removeClass('btn-' + color + ' active')
                    .addClass('btn-default');
                $button.val("0");
            }
        }

        // Initialization
        function init() {

            updateDisplay();

            // Inject the icon if applicable
            if ($button.find('.state-icon').length == 0) {
                $button.prepend('<i class="state-icon ' + settings[$button.data('state')].icon + '"></i> ');
            }
        }
        init();
    });
});

