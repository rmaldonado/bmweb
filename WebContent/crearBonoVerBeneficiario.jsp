<%@ page import="java.util.*" %>
<%@ page import="bmweb.util.UsuarioWeb" %>
<%@ page import="bmweb.dto.BeneficiarioDTO" %>
<html>
<head>
		<script language="javascript" src="jslib.js"></script>
</head>

<body>


<!--
JSP utilizado para validar en pantalla el CMC de un beneficiario
-->

<!-- jsp:include page="cabecera.jsp" flush="true"/ -->
<%
// Recupero el DTO del Beneficiario y coloco el nombre en la otra pantalla
BeneficiarioDTO ben = (BeneficiarioDTO) request.getAttribute("beneficiario");

if (ben != null) {
  String nombreBeneficiario = "";
  nombreBeneficiario = ben.getNombre() + " " + ben.getPat() + " " + ben.getMat();
%>

<script language="javascript">
//window.top.document.getElementById("nombreBeneficiario").innerHTML = "<%= nombreBeneficiario %>";
//window.top.document.formulario.btnsubmit.disabled = false;
parent.document.getElementById("nombreBeneficiario").innerHTML = "<%= nombreBeneficiario %>";
parent.document.formulario.btnsubmit.disabled = false;
</script>
<%
  } else {
%>

<script language="javascript">
parent.document.getElementById("nombreBeneficiario").innerHTML = "";
parent.document.formulario.btnsubmit.disabled = true;
</script>
<%
 }
%>

	<script language="javascript">
	  // Si viene un mensaje para el usuario, lo despliego (version 2)
	  if (GetCookie('mensaje')){ alert( GetCookie('mensaje') ); DeleteCookie('mensaje'); }
	</script>

	</body>
</html>