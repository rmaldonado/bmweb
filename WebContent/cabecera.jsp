<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page import="java.util.*" %>
<%@ page import="bmweb.util.*" %>
<%
  String titulo = "Dipreca Beneficios M&eacute;dicos";
  
  if (request.getAttribute("titulo") != null) {
    titulo = (String) request.getAttribute("titulo") + " - " + titulo;
  }

  HttpSession sesion = request.getSession();
  UsuarioWeb usuarioWeb = null;

  if (sesion != null) {
  	try {  usuarioWeb = (UsuarioWeb) sesion.getAttribute(UsuarioWeb.ATRIBUTO_USUARIO_WEB); }
  	catch (Exception ex) { }
  }

%>
<html>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<% if (usuarioWeb != null) { %>
<meta http-equiv="REFRESH" content="1800; URL=Logout?timeout=<%= (new Date()).getTime() %>">
<!-- Logout luego de N segundos de inactividad -->
<% } %>
	<head>
		<title><%= titulo %></title>
		<style type="text/css">@import url(calendar.css);</style>
		<link rel="stylesheet" type="text/css" href="estilo.css">
		<script language="javascript" src="jslib.js"></script>
		<script language="javascript" src="calendar.js"></script>
		<script language="javascript" src="calendar-es.js"></script>
		<script language="javascript" src="calendar-setup.js"></script>
	</head> 
	
	<body>

<% if (usuarioWeb != null) { %>
<!-- logout inteligente v 2.0 -->
<script language="javascript">
var segundos = 300; // 5 minutos por pantalla al inicio
setInterval("logout()", 1000); // cada segundo reviso cuanto tiempo queda

function logout(){
  segundos = segundos -1;
  
  // se acabo el tiempo - fuera
  if (segundos < 0){
    document.location = "Logout?timeout=<%= (new Date()).getTime() %>";
  }
  
  // mensaje de fuera en 2 minutos
  if (segundos < 120) {
    document.getElementById("logoutmsg").style.display = "";
  }

}

function agregar(num){
  segundos += num;
  document.getElementById("logoutmsg").style.display = "none";
}

</script>

<div id="logoutmsg" class="logout" style="display:none">
Ud. saldr&aacute; del sistema en 2 minutos m&aacute;s por inactividad.
<a href="#" onclick="agregar(300)">Haga click aqu&iacute;</a> para evitarlo.
</div>
<% } %>

			<div class="encabezado">
<%
	if (usuarioWeb != null ){
%>
	<%-- = usuarioWeb.getNombreUsuario() --%> Usuario: <b><%= usuarioWeb.getNombreCompleto() %></b> <br>
				<a href="Inicio" title="Volver al inicio de la aplicaci&oacute;n">Inicio</a> |
<%
	} else {
%>
				<!-- Inicio | -->
<%
	}
%>
				<!-- a href="javascript:alert('En Construccion')" title="Abrir la ventana de Ayuda de la aplicaci&oacute;n">Ayuda</a> | -->
<%
	if (usuarioWeb != null){
%>
				<a href="Logout" title="Salir de la aplicaci&oacute;n">Salir</a>
<%
	} else {
%>
				<!-- Salir -->
<%
	}
%>

			</div>

<!-- fin cabecera -->