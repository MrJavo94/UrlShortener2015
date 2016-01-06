<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml" xmlns:th="http://www.thymeleaf.org"
     xmlns:sec="http://www.thymeleaf.org/thymeleaf-extras-springsecurity3">
    <head>
        <title>Spring Security Example </title>
        <link rel="stylesheet" type="text/css" href="webjars/bootstrap/3.3.5/css/bootstrap.css" />
        <link rel="stylesheet" type="text/css" href="/css/font-awesome.css" />
        <link rel="stylesheet" type="text/css" href="/css/estilos.css" />
        <link rel="stylesheet" type="text/css" href="/css/bootstrap-social.css" />
        <script type="text/javascript" src="js/redirect.js"></script>
    </head>
    <body>
    <div id="hash" style="display:none;">${hash}</div>
                <!-- ZONA DE JORGE   -->
            <div class=".col-lg-2">
                <div id="sign-in-buttons" class=".col-lg-2"></div>
                <h2 id="sign-in-message">Please Sign In</h2>
                <br>
                <div id="gl_div" class="input-group">
                    <form id="gl_signin" action="/connect/google" method="POST">
                        <button class="social-button" type="submit">
                            <a class="btn btn-block btn-social btn-google">
                                <span class="fa fa-google"></span> Sign in with Google
                            </a>
                        </button>
                    </form>
                </div>
                <br>
                <div id="fb_div" class="input-group">
                    <form id="fb_signin" action="/connect/facebook" method="POST">
                        <input type="hidden" name="scope" value="email"/>
                        <button class="social-button" type="submit">
                            <a class="btn btn-block btn-social btn-facebook">
                                <span class="fa fa-facebook"></span> Sign in with Facebook
                            </a>
                        </button>
                    </form>
                </div>
                <br>
                <div id="tw_div" class="input-group">
                    <form id="tw_signin" action="/connect/twitter" method="POST">
                        <button class="social-button" type="submit">
                            <a class="btn btn-block btn-social btn-twitter">
                                <span class="fa fa-twitter"></span> Sign in with Twitter
                            </a>
                        </button>
                    </form>
                </div>
            </div>
            </br>
        <form th:action="@{/login}" method="post">
            <div><label> User Name : <input type="text" name="username"/> </label></div>
            <div><label> Password: <input type="password" name="password"/> </label></div>
            <div><input type="submit" value="Sign In"/></div>
        </form>
        <div class="row">
            <div class="col-lg-offset-2 col-lg-8 text-center">
                <br />
                <div id="result"></div>
            </div>
        </div>
    </body>
</html>