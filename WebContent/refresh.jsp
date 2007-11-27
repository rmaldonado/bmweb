<!-- JSP multiuso para refrescar listados, etc -->
<html>
<body>
<form name="formulario" method="post" action="<%= request.getAttribute("url") %>">
</form>


<script languaje="javascript">
<% if (request.getAttribute("mensaje") != null ){ %>
alert('<%= request.getAttribute("mensaje") %>');
<% } %>
document.formulario.submit();
</script>

</body>
</html>
