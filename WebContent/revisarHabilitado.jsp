<!-- Reviso si el request indica que había un codigo duplicado -->
<%
if ("1".equals((String)request.getAttribute("existeCodigo")) ) {
%>

<script language="javascript">
//alert(unescape("Ya existe un registro para ese c%F3digo.\nNo se permitir%E1 grabar hasta que corrija ese valor."));
window.top.document.getElementById("duplicado").style.display = "";
window.top.document.formulario.btnsubmit.disabled = true;
window.top.document.formulario.codigo.style.background = "red";
</script>
<%
} else {
%>
<script language="javascript">
window.top.document.getElementById("duplicado").style.display = "none";
window.top.document.formulario.btnsubmit.disabled = false;
window.top.document.formulario.codigo.style.background = "white";
</script>
<%
}
%>