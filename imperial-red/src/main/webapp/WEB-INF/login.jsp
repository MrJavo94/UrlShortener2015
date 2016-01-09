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
				<div class="col-lg-5 col-md-5 col-sm-12 col-xs-12">
					<form role="form" method="POST" action="/userlogin">
						<h2>Please Sign In</h2>
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
				<div class="col-lg-5 col-md-5 col-sm-12 col-xs-12">
					<form role="form" action="/users" method="POST">
						<h2>
							Please Register <small>It's free and always will be.</small>
						</h2>
						<hr class="colorgraph">
						<div class="form-group">
							<input type="email" name="mail" id="mail"
								class="form-control input-lg" placeholder="Email" tabindex="4">
						</div>
						<div class="row">
							<div class="col-xs-12 col-sm-6 col-md-12">
								<div class="form-group">
									<input type="password" name="password" id="passwordRegister"
										class="form-control input-lg" placeholder="Password"
										tabindex="5">
								</div>
							</div>
							<div class="col-xs-12 col-sm-6 col-md-12">
								<div class="form-group">
									<input type="password" name="repassword" id="repasswordRegister"
										class="form-control input-lg"
										placeholder="Confirm your password" tabindex="6">
								</div>
							</div>
						</div>
						<div class="row">
							<div class="col-xs-12 col-sm-12 col-md-12">
								<span class="button-checkbox">
									<button id="terms" type="button" class="btn" data-color="info"
										tabindex="7">I Agree</button> <input type="checkbox"
									name="t_and_c" id="t_and_c" class="hidden" value="1">
								</span>
							</div>
							<div class="col-xs-12 col-sm-12 col-md-12">
								By clicking <strong class="label label-primary">Register</strong>,
								you agree to the <a href="#" data-toggle="modal"
									data-target="#t_and_c_m">Terms and Conditions</a> set out by
								this site, including our Cookie Use.
							</div>
						</div>
						<hr class="colorgraph">
						<div class="row">
							<div class="col-xs-12 col-md-12">
								<button type="submit" id="submit"
									class="btn btn-lg btn-primary btn-block btn-lg">Register</button>
							</div>
						</div>
					</form>
				</div>
			</div>
			<!-- Modal -->
			<div class="modal fade" id="t_and_c_m" tabindex="-1" role="dialog"
				aria-labelledby="myModalLabel" aria-hidden="true">
				<div class="modal-dialog modal-lg">
					<div class="modal-content">
						<div class="modal-header">
							<h4 class="modal-title" id="myModalLabel">Terms & Conditions</h4>
						</div>
						<div class="modal-body">
							<p>Donde vas pequeña Ines con esa cara de angel.</p>
							<p>Lorem ipsum dolor sit amet, consectetur adipisicing elit.
								Similique, itaque, modi, aliquam nostrum at sapiente consequuntur
								natus odio reiciendis perferendis rem nisi tempore possimus ipsa
								porro delectus quidem dolorem ad.</p>
							<p>Lorem ipsum dolor sit amet, consectetur adipisicing elit.
								Similique, itaque, modi, aliquam nostrum at sapiente consequuntur
								natus odio reiciendis perferendis rem nisi tempore possimus ipsa
								porro delectus quidem dolorem ad.</p>
							<p>Lorem ipsum dolor sit amet, consectetur adipisicing elit.
								Similique, itaque, modi, aliquam nostrum at sapiente consequuntur
								natus odio reiciendis perferendis rem nisi tempore possimus ipsa
								porro delectus quidem dolorem ad.</p>
							<p>Lorem ipsum dolor sit amet, consectetur adipisicing elit.
								Similique, itaque, modi, aliquam nostrum at sapiente consequuntur
								natus odio reiciendis perferendis rem nisi tempore possimus ipsa
								porro delectus quidem dolorem ad.</p>
							<p>Lorem ipsum dolor sit amet, consectetur adipisicing elit.
								Similique, itaque, modi, aliquam nostrum at sapiente consequuntur
								natus odio reiciendis perferendis rem nisi tempore possimus ipsa
								porro delectus quidem dolorem ad.</p>
							<p>Lorem ipsum dolor sit amet, consectetur adipisicing elit.
								Similique, itaque, modi, aliquam nostrum at sapiente consequuntur
								natus odio reiciendis perferendis rem nisi tempore possimus ipsa
								porro delectus quidem dolorem ad.</p>
						</div>
						<div class="modal-footer">
							<button type="button" class="btn btn-primary" data-dismiss="modal">Close</button>
						</div>
					</div>
					<!-- /.modal-content -->
				</div>
				<!-- /.modal-dialog -->
			</div>
			<!-- /.modal -->
		</div>
	</body>
</html>