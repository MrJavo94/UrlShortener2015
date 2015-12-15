<!doctype html>
<html>
<head>
<title>Reg&iacute;strate</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<link rel="stylesheet" type="text/css"
	href="webjars/bootstrap/3.3.5/css/bootstrap.min.css" />
	<link rel="stylesheet" type="text/css"
	href="/css/estilos.css" />


</head>
<body>
<div class="container">

<div class="row">
    <div class="col-xs-12 col-sm-8 col-md-6 col-sm-offset-2 col-md-offset-3">
		<form role="form" action="/users" method="POST">
			<h2>Reg&iacute;strate <small>Es gratis y siempre lo ser&aacute;.</small></h2>
			<hr class="colorgraph">
			<div class="row">
				<div class="col-xs-12 col-sm-6 col-md-6">
					<div class="form-group">
                        <input type="text" name="first_name" id="first_name" class="form-control input-lg" placeholder="Nombre" tabindex="1">
					</div>
				</div>
				<div class="col-xs-12 col-sm-6 col-md-6">
					<div class="form-group">
						<input type="text" name="last_name" id="last_name" class="form-control input-lg" placeholder="Primer apellido" tabindex="2">
					</div>
				</div>
			</div>
			<div class="form-group">
				<input type="text" name="nick" id="nick" class="form-control input-lg" placeholder="Nombre de usuario" tabindex="3">
			</div>
			<div class="form-group">
				<input type="email" name="mail" id="mail" class="form-control input-lg" placeholder="Correo electr&oacute;nico" tabindex="4">
			</div>
			<div class="row">
				<div class="col-xs-12 col-sm-6 col-md-6">
					<div class="form-group">
						<input type="password" name="password" id="password" class="form-control input-lg" placeholder="Contrase&ntilde;a" tabindex="5">
					</div>
				</div>
				<div class="col-xs-12 col-sm-6 col-md-6">
					<div class="form-group">
						<input type="password" name="password_confirmation" id="password_confirmation" class="form-control input-lg" placeholder="Confirma tu contrase&ntilde;a" tabindex="6">
					</div>
				</div>
			</div>
			<div class="row">
				<div class="col-xs-4 col-sm-3 col-md-2">
					<span class="button-checkbox input-group-addon">
					<label>
					
								<input type="checkbox" name="okTerms" value="true">
								Guay!
							</label>
					
					</span>
				</div>
				<div class="col-xs-8 col-sm-9 col-md-9">
					 Pulsando <strong class="label label-primary">Reg&iacute;strate</strong>,aceptas los T&eacute;rminos y condiciones de este sitio, incluyendo el uso de Cookies..
				</div>
			</div>
			
			<hr class="colorgraph">
			<div class="row">
                <div class="col-xs-12 col-md-3"></div>
				<div class="col-xs-12 col-md-6">
				<button type="submit" name="/users" class="btn btn-lg btn-primary btn-block btn-lg">Reg&iacute;strate 

							</button>
				</div>
				
			</div>
		</form>
	</div>
</div>
</div>
</html>