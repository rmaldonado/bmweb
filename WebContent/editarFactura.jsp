<%@ page import="java.util.*" %>
<%@ page import="bmweb.dto.*" %>

<%

  FacturaDTO factura = null;
  if (request.getAttribute("factura") != null){
  	factura = (FacturaDTO) request.getAttribute("factura");
  }

  String accion = request.getParameter("accion");
  
  boolean estaModificando = false;
  if ("modificar".equals( accion )){ estaModificando = true; }
  
%>
<jsp:include page="cabecera.jsp" flush="true"/>

<div>

    <h1>Mantenedor de Facturas</h1>
    	
	<form name="formulario" method="post" action="Factura">
	<input type="hidden" name="accion" value="<%= accion %>">
	
	<table id="listado">	

		<tr class="encabezados-tabla">
			<td colspan="2">Creando Nueva Factura</td>
		</tr>
		<tr class="fila-detalle-impar">
			<td>N&uacute;mero factura:</td>
			<td id="celda-codigo" style="text-align:left"><%= (factura == null)? "": factura.getNumero().toString() %></td>		
		</tr>

		<tr class="fila-detalle-par">
			<td>Observaciones:</td>
			<td style="text-align:left"><%= (factura == null)? "": factura.getObservaciones() %></td>
		</tr>

	</table>
	
	<h1>Bonos de la Factura</h1>
	
	<table id="listado">	

		<tr class="encabezados-tabla">
<%
  if (estaModificando) {
%>
			<td>Eliminar</td>
<% } %>
			<td>Bono</td>
		</tr>
		
		<tr class="fila-detalle-impar">	
			<td style="text-align:left">
<%
	//for (int i=0; factura.getDetalle() != null && factura.getDetalle().size(); i++){
	Iterator i = factura.getDetalle().iterator();
	
	while (i.hasNext()){
		FacturaItemDTO item = (FacturaItemDTO) i.next();
%>
		<tr class="fila-detalle-impar">	

<%
  if (estaModificando) {
%>
			<td style="text-align:left"><input type="checkbox" name="eliminar.<%= item.getBonoSerial() %>"></td>
<% } %>
			<td style="text-align:left">
				<%= item.getBonoSerial() %>
			</td>
		</tr>
<% } %>


<%
  if (estaModificando) {
%>
		<tr class="fila-detalle-impar">
			<td colspan="2">
				<input type="button" class="button" onClick="document.formulario.accion.value='';document.formulario.submit()" value="Volver al listado">
				<input type="reset"  class="button" value="Restaurar valores originales">
				<input name="btnsubmit" type="button" onClick="validar()" value="<%= (estaModificando)?"Modificar":"Guardar datos" %>" class="submit">
			</td>
		</tr>
<% } %>
		
	</table>
	</form>
	
	<script language="javascript">
	  function validar(){
	    if (document.formulario.factura){
	      if (!CampoEsNumeroEnRango(document.formulario.factura, 1, 999999999)){
	        return false;
	      }
	    }
	    
	    if (
	    	CampoEsNoNulo(document.formulario.factura) 
	    	) {
	      document.formulario.submit();
	    }    
	    
	  }
	</script>

	</div>

	</body>
</html>