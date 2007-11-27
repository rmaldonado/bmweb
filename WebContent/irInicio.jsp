<html>

  <body>
    <form name="formulario" action="Inicio" method="post"></form>

	<script languaje="javascript">
	<% if (request.getAttribute("mensaje") != null ){ %>
	<!-- Viene un mensaje para el usuario -->
	alert('<%= request.getAttribute("mensaje") %>');
	<% } %>
    document.formulario.submit();
	</script>

  </body>
</html>