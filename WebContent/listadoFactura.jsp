<%@ page import="java.util.*" %>
<%@ page import="bmweb.util.*" %>
<%@ page import="bmweb.dto.*" %>


<%
  List lista = new ArrayList();

  if (request.getAttribute("lista") != null){
  	lista = (List) request.getAttribute("lista");
  }

  // paginacion
  int dpp = Constantes.DATOS_POR_PAGINA;
  int inicio = 0;
  boolean pagSiguiente = false;

  if (request.getAttribute("dpp") != null){
    try { dpp = Integer.parseInt( (String)request.getAttribute("dpp") ); } catch (Exception ex) {}
  }

  if (request.getAttribute("inicio") != null){
    try { inicio = Integer.parseInt( request.getParameter("inicio") ); } catch (Exception ex) {}
  }

  if ( lista.size() > dpp){
    pagSiguiente = true;
  }

  // deteccion de parametros para filtros
  boolean mostrarFiltros = false;
  if ("1".equals(request.getParameter("mostrarFiltros"))) { mostrarFiltros = true; }

  String opcodigo = "";  
  if (request.getParameter("opcodigo") != null) { opcodigo = request.getParameter("opcodigo"); }

  String codigo = "";
  if (request.getParameter("codigo") != null) { codigo = request.getParameter("codigo"); }
  
  String opnombre = "";
  if (request.getParameter("opnombre") != null) { opnombre = request.getParameter("opnombre"); }

  String nombre = "";
  if (request.getParameter("nombre") != null) { nombre = request.getParameter("nombre"); }

  String opubicacion = "";
  if (request.getParameter("opubicacion") != null) { opubicacion = request.getParameter("opubicacion"); }

  String ubicacion = "";
  if (request.getParameter("ubicacion") != null) { ubicacion = request.getParameter("ubicacion"); }

  String ciudad = "";
  if (request.getParameter("dom_ciudad") != null) { ciudad = request.getParameter("dom_ciudad"); }

  String opdireccion = "";
  if (request.getParameter("opdireccion") != null) { opdireccion = request.getParameter("opdireccion"); }

  String direccion = "";
  if (request.getParameter("direccion") != null) { direccion = request.getParameter("direccion"); }

  String opresponsable = "";
  if (request.getParameter("opresponsable") != null) { opresponsable = request.getParameter("opresponsable"); }

  String responsable = "";
  if (request.getParameter("responsable") != null) { responsable = request.getParameter("responsable"); }

  String opactivo = "";
  if (request.getParameter("opactivo") != null) { opactivo = request.getParameter("opactivo"); }
  

  // coloco el titulo de la pagina
  request.setAttribute("titulo", "Administracion de Facturas");
%>
<jsp:include page="cabecera.jsp" flush="true"/>

<div>

	<h1>Administraci&oacute;n de Facturas</h1>

	<form name="formulario" method="post" action="Factura">
	<input type="hidden" name="inicio" value="<%= inicio %>">
	<input type="hidden" name="dpp" value="<%= dpp %>">

	<table id="listado">
		<tr class="encabezados-tabla">
			<td>N&uacute;mero Factura</td>
			<td>Rut Acreedor</td>
			<td colspan="2"></td>
		</tr>
	
<%
		int numFila = 1;
		for (Iterator it = lista.iterator(); it.hasNext();) {
		    DocumentoPagoDTO item = (DocumentoPagoDTO) it.next();

		    // Determino un string que se alterna para cambiar la grafica de las filas
		    String clase= (numFila%2 == 0)? "fila-detalle-par":"fila-detalle-impar";
		    numFila++;
%>
		<tr class="<%=clase%>">
			<td><%= item.getNumeroFactura() %></td>
			<td><%= item.getRutAcreedor() %></td>
			<td><a href="Factura?accion=detalle&factura=<%= item.getNumeroFactura() %>" title="Editar Registro">Ver detalles</a>
			<td><a href="javascript:eliminarFactura(<%= item.getId() %>,<%= item.getNumeroFactura() %>)" title="Editar Registro">Borrar Factura</a>
		</tr>
		
<%
		}
%>
	</table>

	<!-- paginador-->
	<table id="listado">
		<tr class="encabezados-tabla">
			<td style="text-align:right">
<%
		if ( inicio > 0 ){
%>			
			<!-- a href="Factura?inicio=<%= (inicio-dpp) %>&dpp=<%= dpp %>" title="Ir la la p&aacute;gina anterior">&lt;&lt;</a -->
			<input type="button" class="button" value="&lt; p&aacute;gina anterior"
			onclick="document.formulario.inicio.value=<%= (inicio-dpp) %>;document.formulario.dpp.value=<%= dpp %>;document.formulario.submit()">
<%
		}
%>			

<%
		if ( inicio > 0  && pagSiguiente){
%>			
			|
<%
		}
%>			

<%
		if ( pagSiguiente ){
%>			
			<input type="button" class="button" value="p&aacute;gina siguiente &gt;"
			onclick="document.formulario.inicio.value=<%= (inicio+dpp) %>;document.formulario.dpp.value=<%= dpp %>;document.formulario.submit()">
<%
		}
%>			
			
			</td>
		</tr>
	
	</form>	
	</table>

	<p>
	<form method="post" action="Factura">
	<input type="hidden" name="accion" value="crear">
	<input type="submit" value="Ingresar N&uacute;mero de Factura" class="submit">
	</form>
	</p>

	</div>
	

	<script language="javascript">
	  function eliminarFactura(codigo, nombre){

	    if (confirm("Confirme que desea eliminar la factura #" + nombre + " ")){
	       document.location = "Factura?accion=eliminar&codigo=" + codigo;
	    }
	  }
	  
	  function mostrar_filtros(){
	  
	  	if (document.formulario.mostrarFiltros){
		  	document.formulario.mostrarFiltros.value = "1";
		}
	  
	    if (document.getElementById("filtro-min")){
	    	document.getElementById("filtro-min").style.display = "none";
	    }
	  
	    if (document.getElementById("filtro-max")){
	    	document.getElementById("filtro-max").style.display = "";
	    }
	  
	  }
	  
	  function ocultar_filtros(){

	  	if (document.formulario.mostrarFiltros){
		  	document.formulario.mostrarFiltros.value = "0";
		}
	  
	    if (document.getElementById("filtro-max")){
	    	document.getElementById("filtro-max").style.display = "none";
	    }
	  
	    if (document.getElementById("filtro-min")){
	    	document.getElementById("filtro-min").style.display = "";
	    }
	  
	  }
	  
	  function limpiar_filtros(){
	  
	  	// TODO: Hacer validaciones con if (document.formulario.campo) ...
	  	
	  	document.formulario.opcodigo.selectedIndex = 0;
	  	document.formulario.codigo.value = "";
	  	document.formulario.opnombre.selectedIndex = 0;
	  	document.formulario.nombre.value = "";
	  	document.formulario.opubicacion.selectedIndex = 0;
	  	document.formulario.ubicacion.value = "";
	  	
	  	document.formulario.dom_ciudad.selectedIndex = 0;

	  	document.formulario.direccion.value = "";
	  	document.formulario.opdireccion.selectedIndex = 0;

	  	document.formulario.responsable.value = "";
	  	document.formulario.opresponsable.selectedIndex = 0;
	  	
	  }

	  // En esta página, si viene la cookie "update", simplemente se consume la cookie
	  // Si no se encuentra la cookie "update", se fuerza un refresco de la pagina
	  if (!GetCookie('update')){ document.formulario.submit(); } else { DeleteCookie('update'); }

	</script>

<jsp:include page="pie.jsp" flush="true"/>

