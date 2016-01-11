<!doctype html>
<html>
    <head>
        <title>Reg&iacute;strate</title>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <link rel="stylesheet" type="text/css"
            href="webjars/bootstrap/3.3.5/css/bootstrap.min.css" />
        <link rel="stylesheet" type="text/css" href="/css/estilos.css" />
        <link rel="stylesheet" type="text/css"
            href="webjars/bootstrap/3.3.5/css/bootstrap.css" />
        <link rel="stylesheet" type="text/css" href="/css/font-awesome.css" />
        <link rel="stylesheet" type="text/css" href="/css/estilos.css" />
        <link rel="stylesheet" type="text/css" href="/css/bootstrap-social.css" />
        <script type="text/javascript" src="webjars/jquery/2.1.4/jquery.min.js"></script>
        <script type="text/javascript"
            src="webjars/bootstrap/3.3.5/js/bootstrap.min.js"></script>
        <script type="text/javascript" src="js/signUp.js"></script>
    </head>
    <body>
        <div class="container">
            <div class="row">
                <div class="col-lg-offset-3 col-lg-5 col-md-5 col-sm-12 col-xs-12">
                    <form role="form" method="POST" action="/userlogin">
                        <h2>The link you requested is private</h2>
                        <h3>Please Sign In</h3>
                        <hr class="colorgraph">
                        <div class="form-group">
                            <input type="email" name="email" id="email"
                                class="form-control input-lg" placeholder="Email Address">
                        </div>
                        <div class="form-group">
                            <input type="password" name="password" id="password"
                                class="form-control input-lg" placeholder="Password">
                        </div>
                        <!--<span class="button-checkbox">
                            <button type="button" class="btn" data-color="info">Remember Me</button>
                            <input type="checkbox" name="remember_me" id="remember_me" checked="checked" class="hidden">
                            <a href="" class="btn btn-link pull-right">Forgot Password?</a>
                        </span>-->
                        <hr class="colorgraph">
                        <div class="row">
                            <div class="col-xs-12 col-sm-12 col-md-12">
                                <input type="submit" class="btn btn-lg btn-success btn-block"
                                    value="Sign In">
                            </div>
                        </div>
                    </form>
                    <br>
                    <br>
                    <div class="row" style="text-align: center">
                        <div class="col-lg-6 col-md-6 col-sm-12 col-xs-12 pull-left">
                            <form id="gl_signin" action="/connect/google" method="POST">
                                <button class="social-button" type="submit">
                                    <a class="btn btn-block btn-social btn-google"> <span
                                        class="fa fa-google"></span> Sign in with Google
                                    </a>
                                </button>
                            </form>
                        </div>
                        <div class="col-lg-6 col-md-6 col-sm-12 col-xs-12 pull-right">
                            <form id="fb_signin" action="/connect/facebook" method="POST">
                                <input type="hidden" name="scope" value="email" />
                                <button class="social-button" type="submit">
                                    <a class="btn btn-block btn-social btn-facebook"> <span
                                        class="fa fa-facebook"></span> Sign in with Facebook
                                    </a>
                                </button>
                            </form>
                        </div>
                    </div>
                </div>
    
                <div class="col-lg-2 col-md-2"></div>
        </div>
    </body>
</html>