<%@ page import="java.text.*" %>
<%@ page import="java.util.*" %>
<%@ page import="bmweb.util.*" %>
<%@ page import="bmweb.dto.*" %>

<%

  DocumentoPagoDTO factura = null;
  if (request.getAttribute("factura") != null){
  	factura = (DocumentoPagoDTO) request.getAttribute("factura");
  }

  List listaBonosSinFacturar = null;
  if (request.getAttribute("bonos") != null){
  	listaBonosSinFacturar = (List) request.getAttribute("bonos");
  }
  
  HashMap mapaBonos = new HashMap();
  if (request.getAttribute("mapaBonos") != null){
  	mapaBonos = (HashMap) request.getAttribute("mapaBonos");
  }
  
  boolean facturaLiquidada = false;
  if (factura != null && factura.getFechaLiquidacion() != null){
  	facturaLiquidada = true;
  }
  
  // Parche - Si indico que quiero imprimir la factura, quito
  // los botones como si fuera una factura liquidada
  
  boolean imprimir = false;
  if ( request.getParameter("imprimir") != null ){
  	facturaLiquidada = true;
    imprimir = true;
  }
 
  // si la factura esta cerrada, quito los botones agregar/quitar bonos
  if ( request.getAttribute("cerrada") != null){
  	facturaLiquidada = true;
  }

  SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
  int valorTotalFactura = 0;
  
  HttpSession sesion = request.getSession();
  UsuarioWeb usuarioWeb = (UsuarioWeb) sesion.getAttribute(UsuarioWeb.ATRIBUTO_USUARIO_WEB);
  
  int rutSinDV = Integer.parseInt( usuarioWeb.getRutEmisor() );
  String rutFormateado = TextUtil.getRutFormateado( rutSinDV );
  
%>
<jsp:include page="cabecera.jsp" flush="true"/>

<div>

<% if (imprimir){ %>

    <h1>Certificado de Facturas: <%= usuarioWeb.getNombreCompleto() %> (<%= rutFormateado  %>)</h1>

    <p>
    Certifico que los bonos asociados a la factura #<%= factura.getNumeroFactura() %>
    con fecha <%=  sdf.format(new Date()) %>
    tienen un valor cobrado que corresponde a las atenciones realizadas.
    </p>

<% } else { %>

    <h1>Mantenedor de Facturas: <%= usuarioWeb.getNombreCompleto() %> (<%= rutFormateado  %>)</h1>

<% } %>
    	
	<form name="formulario" method="post" action="Factura">
<% if (factura == null) { %>
	<input type="hidden" name="accion" value="insertar">
<% } else { %>
	<input type="hidden" name="accion" value="modificar">
<% } %>	
	<table id="listado">	

		<tr class="encabezados-tabla">
			<td colspan="2">
			<% if (factura == null) { %>Creando Nueva Factura<% } else { %>Detalle de la Factura #<%= factura.getNumeroFactura() %><% } %>
			</td>
		</tr>

<% if (factura == null) { %>
		<tr class="fila-detalle-impar">
			<td>N&uacute;mero factura:</td>
			<td id="celda-codigo" style="text-align:left">
				<input type="text" name="factura" value="">
			</td>		
		</tr>
<% } else { %>
		<input type="hidden" name="factura" value="<%= factura.getNumeroFactura() %>">
<% } %>
	</table>

<%
// Si hay factura puedo ver el detalle
  if (factura != null) {
%>

	<h1>Detalle de Bonos de la Factura</h1>

	<%
	// Si hay factura puedo ver el detalle
	  if (factura.getDetalle().size() > 0) {
	%>
	<!-- listado con detalle de bonos -->
	<table id="listado">
		<tr class="encabezados-tabla">
			<td>Folio</td>
			<td>Valor del Bono</td>
			<td>Fecha</td>
			<td>Beneficiario</td>
			<td></td>
		</tr>
	<%
	// foreach FactItem en factura.getDetalle()
	Iterator i = factura.getDetalle().iterator();
	while ( i.hasNext() ) {
	  String[] fila = (String[]) i.next();
	  String folioBono = "?";
	  
	  String serial = fila[0];
	  String folio  = fila[1];
	  String valorTotal  = fila[2];
	  
	  String fecha = fila[3];
	  String nombreBeneficiario = fila[4];
	  
	  valorTotalFactura += (new Integer(valorTotal)).intValue();

	%>	
		<tr class="fila-detalle-impar">
			<td><%= folio %></td>
			<td>$<%= valorTotal %></td>
			<td><%= fecha %></td>
			<td><%= nombreBeneficiario %></td>
			<% if (!facturaLiquidada) { %>
			<td><input type="button" class="submit" onClick="quitarBono(<%= serial %>, <%= folio %>)" value="Quitar bono de la factura"></td>
			<% } %>
		</tr>
	<%
	  }
	%>


	<% if (facturaLiquidada) { %>
		<tr class="fila-detalle-impar">
			<td></td>
		    <td><b>Valor Total: $<%= valorTotalFactura %></b></td>
		</tr>
	<% } %>

		
	</table>
<%
  }
%>

	<script language="javascript">
	function agregarBono(){
		document.formulario.accion.value = "agregarBono";
		document.formulario.submit();
	}
	
	function quitarBono(serial, bono){
	 	if (confirm('Confirme que desea quitar el bono web #' + bono + " de esta factura")){
			document.formQuitar.folioBono.value = bono;
			document.formQuitar.submit();
	 	}
	}
	
	function verAgregarBono(){
		document.getElementById("btnNuevoBono").style.display = "none";
		document.getElementById("nuevoBono").style.display = "";
	}

	function ocultarAgregarBono(){
		document.getElementById("btnNuevoBono").style.display = "";
		document.getElementById("nuevoBono").style.display = "none";
	}
	</script>


	<!-- minitabla para ingresar un nuevo bono -->
	<table id="listado">
		<tr class="fila-detalle-impar">
			<td>
			
			
			<% if (!facturaLiquidada) { %>

			<input type="button" class="submit" value="Imprimir Comprobante" onClick="imprimir()"><br><br>
			
			<div id="btnNuevoBono">
			<input type="button" class="submit" value="Agregar Bono a la Factura" onClick="verAgregarBono()">
			</div>


			<div id="nuevoBono" style="display:none">
			Folio de Bono Web: 

			<!-- input type="text" size="8" name="folioBono" -->
			<select name="folioBono" multiple size="8">
			<% for (int i=0; listaBonosSinFacturar != null && i < listaBonosSinFacturar.size(); i++) { 
				String[] fila = (String[]) listaBonosSinFacturar.get(i);
			%>
			<option value="<%= fila[0] %>"><%= fila[0] %></option>
			<% } %>
			</select>

			<input type="button" class="submit" value="Guardar" onClick="agregarBono()">
			<input type="button" class="submit" value="Cancelar" onClick="ocultarAgregarBono()">
			</div>
			
			<% } %>

			</td>
		</tr>
	</table>


<%
  }
%>

<%
// Si hay factura puedo ver el detalle
  if (factura == null) {
%>

	<table id="listado">
		<tr class="fila-detalle-impar">
			<td colspan="2">
				<!--
				<input type="button" class="button" onClick="document.formulario.accion.value='';document.formulario.submit()" value="Volver al listado">
				<input type="reset"  class="button" value="Restaurar valores originales">
				-->
				<input name="btnsubmit" type="button" onClick="validar()" value="Guardar datos" class="submit">
			</td>
		</tr>
<%
  }
%>
		
	</table>
	</form>

	<form name="formImprimir" target="_blank" method="post" action="Factura">
	  <input type="hidden" name="factura" value="<%= request.getParameter("factura") %>">
	  <input type="hidden" name="accion" value="detalle">
	  <input type="hidden" name="imprimir" value="imprimir">
	</form>

	<form name="formQuitar" method="post" action="Factura">
	  <input type="hidden" name="factura" value="<%= request.getParameter("factura") %>">
	  <input type="hidden" name="accion" value="quitarBono">
	  <input type="hidden" name="folioBono" value="">
	</form>

	
	<script language="javascript">
	  function validar(){
	    if (document.formulario.factura){
	      if (!CampoEsNumeroEnRango(document.formulario.factura, 1, 999999999)){
	        return false;
	      } else {
		      document.formulario.submit();
	      }
	    } else {
		      document.formulario.submit();
	    }

	  }
	  
	  function imprimir(){
	    document.formImprimir.submit();
	  }
	</script>

	</div>
	
<jsp:include page="pie.jsp" flush="true"/>

<%
  if (imprimir) {
%>
<script>
window.print();
alert("Luego de imprimir este comprobante, cierre esta ventana.");
</script>
<%
  }
%>