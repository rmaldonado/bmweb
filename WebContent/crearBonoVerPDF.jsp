<%
String folio = (String) request.getAttribute("folio");
%>
<script language="javascript">
  document.location = "Bonos?accion=detalle&folio=" + <%= folio %>;
</script>
