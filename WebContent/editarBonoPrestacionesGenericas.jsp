<%@ page import="java.util.*" %>
<%@ page import="bmweb.util.UsuarioWeb" %>
<%@ page import="bmweb.dto.*" %>

<%-- 
Recibo un BonoDTO en el request para desplegar los campos
y una lista de PrestacionesGenericas asociadas al bono.

Solo en el último momento al grabar se graba todo el conjunto
de una sola vez en el Servlet.
--%>

<%

  ArrayList listaCiudades = new ArrayList();
  
  if (request.getAttribute("ciudades") != null){
  	listaCiudades = (ArrayList) request.getAttribute("ciudades");
  }

  // "prestaciones" es el conjunto generico de prestaciones
  List prestaciones = new ArrayList();
  if (request.getAttribute("prestaciones") != null){
  	prestaciones = (List) request.getAttribute("prestaciones");
  }

  // "detalle" es el conjunto de prestaciones asociadas a un sólo bono
  List detalle = new ArrayList();
  
  if (request.getAttribute("detalle") != null){
  	detalle = (List) request.getAttribute("detalle");
  }


  String ts = "" + (new Date()).getTime();

  BonoDTO b = null;
  if (request.getAttribute("bono") != null){
  	b = (BonoDTO) request.getAttribute("bono");

	UsuarioWeb usuario = (UsuarioWeb) request.getSession().getAttribute(UsuarioWeb.ATRIBUTO_USUARIO_WEB);
	
	String nombreCiudadBono = "";
	for (int i=0; i<listaCiudades.size(); i++){
		CiudadDTO c = (CiudadDTO) listaCiudades.get(i);
		String nombreCiudad = c.getNombre();
		String codigoCiudad = "" + c.getCodigo();
		if (codigoCiudad != null && b.getIdCiudad() != null && codigoCiudad.equals(b.getIdCiudad().toString())){
			nombreCiudadBono = nombreCiudad;
	    }
	}

  PrestadorDTO prestador = null;
  if (request.getAttribute("prestador") != null){
  	prestador = (PrestadorDTO) request.getAttribute("prestador");
  }

%>
<jsp:include page="cabecera.jsp" flush="true"/>

<div>

    <h1>Consulta de Bonos</h1>
    	
	<form name="formulario" method="post" action="Bonos">
	<input type="hidden" name="ts" value="<%= ts %>">
	<input type="hidden" name="accion" value="modificarPrestaciones">
    <input type="hidden" name="folio" value="<%= b.getFolio() %>">
    <input type="hidden" name="opDetalle" value="">
    
    <input type="hidden" name="idPrestador" value="<%= prestador.getRutAcreedor() %>">

	
	<table id="listado">	

		<tr class="encabezados-tabla">
			<td colspan="2">Editando Bono #<%= b.getFolio() %></td>
		</tr>

		<tr class="fila-detalle-par">
			<td style="text-align:right">Ciudad en que se realizar&aacute; la atenci&oacute;n</td>
			<td style="text-align:left" width="50%"><%= nombreCiudadBono %></td>
		</tr>

		<tr class="fila-detalle-impar">
			<td style="text-align:right">Prestador</td>
			<td style="text-align:left" width="50%"><%= prestador.getRazonSocial() %></td>
		</tr>

		<tr class="fila-detalle-par">
			<td style="text-align:right">Carnet de Medicina Curativa</td>
			<td style="text-align:left" width="50%"><%= (b.getCarneBeneficiario() == null)? "": b.getCarneBeneficiario() %></td>		
		</tr>

	</table>


	<h1>Detalle de Prestaciones</h1>

<%-- Itero sobre las filas del detalle de las prestaciones --%>

	<table id="listado">	

<%
  if (detalle != null && detalle.size() > 0) {
%>
		<tr class="encabezados-tabla">
			<td>Borrar?</td>
			<td>Prestaciones Gen&eacute;ricas del Bono</td>
		</tr>

<%
} else {
%>
		<tr class="fila-detalle-impar">
			<td colspan="2">
			Este bono no posee prestaciones asociadas.<br>
			Para agregar prestaciones a este bono, use el bot&oacute;n
			"Agregar otra prestaci&oacute;n".
			</td>
		</tr>
<%
}

	for (int i=0; detalle != null && i<detalle.size(); i++){
	  PrestacionGenericaDTO p = (PrestacionGenericaDTO) detalle.get(i);
	  String estilo = (i%2==0)?"fila-detalle-par":"fila-detalle-impar";
%>
		<tr class="<%= estilo %>">
			<td>
				<input type="checkbox" class="button" name="borrar<%= i %>" value="borrar">
			</td>
			<td>
			<select name="prestacion<%= i %>">
			<%-- La lista de prestaciones, con la prestación seleccionada --%>
			<%
			for (int j=0; prestaciones != null && j<prestaciones.size(); j++){
				PrestacionGenericaDTO g = (PrestacionGenericaDTO) prestaciones.get(j);
				String selected = (g.getNombre().equals(p.getNombre()))?"selected":"";
			%>
			<option value="<%= g.getCodigo() %>" <%= selected %>><%= g.getNombre() %></option>
			<%
			}
			%>
			</select>
			</td>
		</tr>

<%
	}
%>

		<tr class="fila-detalle-impar">
			<td colspan="2">
				<input type="button" class="button" onClick="agregarFila()" value="Agregar otra prestaci&oacute;n">
				<input type="button" class="button" onClick="borrarFilas()" value="Borrar las prestaciones seleccionadas">
				<input name="btnsubmit" type="button" onClick="guardar()" value="Guardar Bono" class="submit">
			</td>
		</tr>
		
	</table>
	</form>
	
	<script language="javascript">
	  function guardar(){
	    if (confirm('Confirme que desea guardar definitivamente este bono')){
		    document.formulario.accion.value = "guardarDetalle";
		    document.formulario.submit();
	    }
	  }
	  
	  function agregarFila(){
	    document.formulario.opDetalle.value = "agregarFila";
	    document.formulario.submit();
	  }
	  
	  function borrarFilas(){
	    document.formulario.opDetalle.value = "borrarFilas";
	    document.formulario.submit();
	  }
	  
	</script>
		
<%
		} else {
%>
		<script language="javascript">
		alert("Error:\n\nNo se encontró el dato que desea editar.");
		history.back();
		</script>
<%
		}
%>

</div>

	</body>
</html>



<!-- Datos del Bono: Emisor, Prestador, Ciudad, etc. -->

<!-- 
Filas con 
(checkbox) Numero de Fila - Prestación Generica (Combo)
-->

<!-- (Agregar Fila) (Borrar filas seleccionadas) (Grabar) -->