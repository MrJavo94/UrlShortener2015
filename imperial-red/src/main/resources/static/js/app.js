$(document).ready(
    function() {
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