<!-- JSP multiuso para refrescar listados, etc -->

<html>
<body>
<form name="formulario" method="post" action="Login">
<input type="hidden" name="usuario" value="<%= request.getParameter("usuario") %>">
</form>

<script languaje="javascript">
document.formulario.submit();
</script>

</body>
</html>