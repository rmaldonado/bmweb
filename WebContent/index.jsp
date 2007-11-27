<%@ page import="bmweb.util.*" %>
<jsp:include page="cabecera.jsp" flush="true"/>


				<div>
								
						<h3>Bienvenido al M&oacute;dulo web de Bonos Electr&oacute;nicos Dipreca</h3>

						<div>

						Este m&oacute;dulo le permite a todos los beneficiarios inscritos en el sistema:

							<ul>
							<!-- li>Consultar el listado de Proveedores de Salud y las Prestaciones que ofrecen</li -->
							<li>
								Obtener Bonos M&eacute;dicos Abiertos para los prestadores que atienden en la ciudad
								en que se encuentra cada beneficiario, y recuperar los bonos como un archivo
								en formato Adobe PDF, y que se puede enviar por correo electr&oacute;nico.
							</li>
							<li>
								Consultar y modificar los usuarios habilitados del sistema de beneficios m&eacute;dicos.
							</li>
							</ul>
						</div>

						Para ingresar a la aplicaci&oacute;n, debe utilizar su nombre de usuario y contrase&ntilde;a:<br />

						<form name="formulario" method="post" action="Login">
							<div class="destacado">
								Nombre de Usuario:
								<input name="usuario" value="" size="20" type="text" class="input">
								&nbsp;&nbsp;&nbsp;&nbsp;Contrase&ntilde;a:
								<input name="password" value="" size="20" type="password" class="input">
								&nbsp;<input name="ingresar" value="Ingresar" type="button" onClick="validar()">
							</div>
						</form>

								<div>
									
								</div>

						</div>

	<script language="javascript">
	
		// Selecciono el foco al cambio "usuario" del formulario
		document.formulario.usuario.focus();
	
		function validar(){

			// validar un usuario y contrasena no nulos
			if (!document.formulario.usuario.value || !document.formulario.password.value){
				alert("Debe ingresar un usuario y contrasena validos para utilizar el sistema.");
				return;
			}

			document.formulario.submit();

		}
	</script>
	
<jsp:include page="pie.jsp" flush="true"/>

<%
  // Veo si la sesion de usuario contiene una instancia de UsuarioWeb, voy a "Inicio"
  if (request.getSession().getAttribute(UsuarioWeb.ATRIBUTO_USUARIO_WEB) != null) {
%>
<form name="irInicio" action="Inicio" method="post"></form>
<script language="javascript">
document.irInicio.submit();
</script> 
<%
  }
%>
