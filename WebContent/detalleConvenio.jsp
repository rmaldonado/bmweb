<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ page import="java.text.*" %>
<%@ page import="java.util.*" %>
<%@ page import="bmweb.dto.*" %>
<%@ page import="bmweb.util.*" %>
<%

  SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

  ConvenioDTO convenio = new ConvenioDTO();
  if (request.getAttribute("convenio") != null){
	convenio = (ConvenioDTO) request.getAttribute("convenio");
  }

  List lista = new ArrayList();
  if (request.getAttribute("valcon") != null){
	  lista = (List) request.getAttribute("valcon");
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

  String fechaInicio = "";
  if (null != convenio.getFechaInicio()) {
	  fechaInicio = sdf.format(convenio.getFechaInicio());
  } else {
	  fechaInicio = "Convenio nuevo";
  }
  
  String fechaTermino = "";
  if (null != convenio.getFechaTermino()) {
	  fechaTermino = sdf.format(convenio.getFechaTermino());
  } else {
	  fechaTermino = "Convenio nuevo";
  }
  
  Map estadosConvenio = new HashMap();
  estadosConvenio.put(new Integer(0), "Convenio vigente");
  estadosConvenio.put(new Integer(ConvenioDTO.CONVENIO_NUEVO), "Nuevo convenio");
  estadosConvenio.put(new Integer(ConvenioDTO.CONVENIO_MODIFICADO), "Convenio Modificado");
  estadosConvenio.put(new Integer(ConvenioDTO.CONVENIO_ELIMINADO), "Convenio Eliminado");
  
%>
<jsp:include page="cabecera.jsp" flush="true"/>

<div>

	<h1>Información del Convenio</h1>
	
	<table id="listado">
		<tr class="encabezados-tabla">
			<td colspan="4">
			Convenio #<%= convenio.getCodigo() %>: "<%= convenio.getGlosa() %>",
			Estado <%= estadosConvenio.get(new Integer(convenio.getEstadoConvenio())) %>
			</td>
		</tr>		
		<tr class="fila-detalle-impar">
			<td><strong>Código Prestador</strong></td><td><%= convenio.getCodigoPrestador() %></td>
			<td><strong>Código Arancel Fonasa</strong></td><td><%= convenio.getCodigoArancelFonasa() %></td>
		</tr>	
		<tr class="fila-detalle-par">
			<td><strong>Fecha Inicio Convenio</strong></td><td><%= fechaInicio %></td>
			<td><strong>Fecha Término Convenio</strong></td><td><%= fechaTermino %></td>
		</tr>
		<tr class="fila-detalle-impar">
			<td><strong>Tipo de Convenio</strong></td><td><%= convenio.getTipoConvenio() %></td>
			<td><strong>Resolución de Concurrencia</strong></td><td><%= "" + convenio.getCodigoConcurrencia() %></td>
		</tr>	
		<tr class="fila-detalle-par">
			<td><strong>Moneda</strong></td><td><%= convenio.getMoneda() %></td>
			<td><strong>Convenio hace referencia a FONASA?</strong></td><td><%= "" + convenio.getReferenciaFonasa() %></td>
		</tr>
		<tr class="fila-detalle-impar">
			<td><strong>Nivel de Referencia FONASA</strong></td><td><%= convenio.getNivelReferenciaFonasa() %></td>
			<td><strong>Factor de Referencia FONASA</strong></td><td><%= convenio.getFactorRefFonasa() %></td>
		</tr>	
		<tr class="encabezados-tabla">
			<td colspan="4" style="text-align:right">
			<form>
				<input type="hidden" name="id" value="<%= convenio.getCodigo() %>">
				<input type="hidden" name="accion" value="detalleExcel">
				<input type="submit" value="Exportar listado en formato Excel">
			</form>
			</td>
		</tr>		
			
	</table>

	<h1>Detalle del Convenio</h1>
	<form name="formulario" method="post" action="Convenios">
		<input type="hidden" name="inicio" value="<%= inicio %>">
		<input type="hidden" name="dpp" value="<%= dpp %>">
		<input type="hidden" name="id" value="<%= convenio.getCodigo() %>">
		<input type="hidden" name="accion" value="detalle">

	<table id="listado">
		<tr class="encabezados-tabla">
			<td>C&oacute;digo Prestación</td>
			<td>Valor Convenido</td>
			<td>Valor Lista</td>
			<td>Estado</td>
			<td colspan="2"></td>
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
			<td>.</td>
			<td>.</td>
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
</div>

<jsp:include page="pie.jsp" flush="true"/>
