<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ page import="java.text.*" %>
<%@ page import="java.util.*" %>
<%@ page import="bmweb.dto.*" %>
<%@ page import="bmweb.util.*" %>
<%

  SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

  Map estadosConvenio = new HashMap();
  estadosConvenio.put(new Integer(1), "Convenio nuevo o mantiene valor");
  estadosConvenio.put(new Integer(2), "Valor modificado");
  estadosConvenio.put(new Integer(3), "Eliminado del convenio");

  ConvenioDTO convenio = new ConvenioDTO();
  if (request.getAttribute("convenio") != null){
	convenio = (ConvenioDTO) request.getAttribute("convenio");
  }

  List lista = new ArrayList();
  if (request.getAttribute("convenios") != null){
	  lista = (List) request.getAttribute("convenios");
  }

  HashMap mapaCiudades = new HashMap();
  if (request.getAttribute("ciudades") != null){
  	mapaCiudades = (HashMap) request.getAttribute("ciudades");
  }
  
  List listaCiudades = new ArrayList();
  if (request.getAttribute("listaCiudades") != null){
  	listaCiudades = (List) request.getAttribute("listaCiudades");
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

  if (request.getAttribute("pagSiguiente") != null){
    pagSiguiente = true;
  }
  
  UsuarioWeb usuarioWeb = (UsuarioWeb) request.getSession().getAttribute(UsuarioWeb.ATRIBUTO_USUARIO_WEB);
  
  // coloco el titulo de la pagina
  //request.setAttribute("titulo", "Detalle de Convenio");

%>
<jsp:include page="cabecera.jsp" flush="true"/>

<script language="javascript">
  function procesarArchivo(){
    if ((document.getElementById('archivo').value) != "") {
      document.formularioArchivo.submit();
    } else {
      alert("Debe seleccionar un archivo para procesar.");
    }
  }
</script>

<div>

	<h1>Procesar un listado de precios</h1>
	
	Para procesar un listado de convenios, utilice el siguiente formulario para
	cargar un archivo CVS con datos separados por comas.
	
	<form name="formularioArchivo" method="post" action="Convenios" 
	  enctype="multipart/form-data">
		<input type="file" name="archivo" id="archivo">
		<input type="hidden" name="accion" value="procesarArchivo">
		<input type="button" value="Procesar archivo" onClick="procesarArchivo()">
	</form>

<% if (lista.size() > 0) { %>	
	<h1>Detalle del Convenio Cargado</h1>
	<form name="formulario" method="post" action="Convenios">
		<input type="hidden" name="inicio" value="<%= inicio %>">
		<input type="hidden" name="dpp" value="<%= dpp %>">
		<input type="hidden" name="id" value="<%= convenio.getCodigo() %>">
		<input type="hidden" name="accion" value="detalle">

	<table id="listado" style="height:500px;overflow:auto;">
		<tr class="encabezados-tabla">
			<td>C&oacute;digo Prestación</td>
			<td>Valor Convenido</td>
			<td>Valor Lista</td>
			<td>Estado</td>
		</tr>
	
<%
		int numFila = 1;
		for (Iterator it = lista.iterator(); it.hasNext();) {
			ValconDTO valcon = (ValconDTO) it.next();

		    // Determino un string que se alterna para cambiar la grafica de las filas
		    String clase= (numFila%2 == 0)? "fila-detalle-par":"fila-detalle-impar";
		    numFila++;
		    
		    
%>

		<tr class="<%=clase%>">
			<td><%= valcon.getCodigoPrestacion() %></td>
			<td><%= valcon.getValorCovenido() %></td>
			<td><%= valcon.getValorLista() %></td>
			<td><%= estadosConvenio.get(new Integer(valcon.getEstado())) %></td>
		</tr>
		
<%
		} // for
			
%>
	</table>

	<!-- paginador-->
	<table id="listado">
		<tr class="encabezados-tabla">
			<td style="text-align:right">
<%
		if ( inicio > 0 ){
%>			
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
	
<%
	} // if lista.size() > 0
%>		
</div>

<jsp:include page="pie.jsp" flush="true"/>
