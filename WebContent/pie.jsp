	<div class="pie-de-pagina">
	
	BMWeb versi&oacute;n 1.0.1 - Ultima Modificaci&oacute;n: 19 de Julio 2006
	</div>
	<span style="text-align:right"><a href="creditos/" style="color:#fff;font-size:8px">creditos</a>

	<%--
	<% if (request.getAttribute("mensaje") != null ){ %>
	<!-- Viene un mensaje para el usuario -->
	<script languaje="javascript">
	alert('<%= request.getAttribute("mensaje") %>');
	</script>
	<% } %>
	--%>

	<script language="javascript">
	  // Si viene un mensaje para el usuario, lo despliego (version 2)
	  if (GetCookie('mensaje')){ alert( GetCookie('mensaje') ); DeleteCookie('mensaje'); }
	</script>

	</body>
</html>
