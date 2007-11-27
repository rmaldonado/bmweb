<%@ page import="bmweb.util.*" %>


<jsp:include page="cabecera.jsp" flush="true"/>

				<div>
								
				<h3>Ha salido de la aplicaci&oacute;n.</h3>
				
				<script language="javascript">
				
				  // Si viene un mensaje para el usuario, lo borro en esta pantalla
				  if (GetCookie('mensaje')){ DeleteCookie('mensaje'); }
				</script>

				</div>

<jsp:include page="pie.jsp" flush="true"/>

<!-- abrir pagina de cierre de sesion de dipreca -->
<script language="javascript">
//top.document.location = "http://www.dipreca.cl";
document.location = "https://www.dipreca.cl/inicio/perfiles/vs/salir.asp?permisos=MZEHSUSAUA";
</script>
