<%@ page import="java.text.*" %>
<%@ page import="java.util.*" %>
<%@ page import="bmweb.util.*" %>
<%@ page import="bmweb.dto.*" %>


<%
  List lista = new ArrayList();
  if (request.getAttribute("listaConvenios") != null){
	  	lista = (List) request.getAttribute("listaConvenios");
  }

  HashMap mapaCiudades = new HashMap();
  if (request.getAttribute("ciudades") != null){
  	mapaCiudades = (HashMap) request.getAttribute("ciudades");
  }

  List listaCiudades = new ArrayList();
  if (request.getAttribute("listaCiudades") != null){
  	listaCiudades = (List) request.getAttribute("listaCiudades");
  }
  
  SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

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

  // deteccion de parametros para filtros
  boolean mostrarFiltros = false;
  if ("1".equals(request.getParameter("mostrarFiltros"))) { mostrarFiltros = true; }

  String opcodigo = "";  
  if (request.getParameter("opcodigo") != null) { opcodigo = request.getParameter("opcodigo"); }

  String ide = "";  
  if (request.getParameter("id") != null) { ide = request.getParameter("id"); }

  String tipoConvenios = "";  
  if (request.getParameter("tipoConvenios") != null) { tipoConvenios = request.getParameter("tipoConvenios"); }

  // coloco el titulo de la pagina
  request.setAttribute("titulo", "Administración de Convenios");
%>
<jsp:include page="cabecera.jsp" flush="true"/>

<div>

	<h1>Administración de Convenios</h1>

	<table class="tabla-borde-delgado" id="filtro-min" style="<%= (mostrarFiltros)? "display:none":"" %>">
		<tr class="encabezados-tabla">
			<td style="text-align:right">
				<a href="javascript:mostrar_filtros()" title="Mostrar filtros para refinar el listado de Habilitados">
				Mostrar filtros</a>
			</td>
		</tr>
	</table>

	<table class="tabla-borde-delgado" id="filtro-max" style="<%= (mostrarFiltros)? "":"display:none" %>">
	<form name="formulario" method="post" action="Convenios">
		<input type="hidden" name="mostrarFiltros" value="<%= (mostrarFiltros)? "1":"0" %>">
		<input type="hidden" name="xaccion" value="listado">
		<input type="hidden" name="inicio" value="">
		<input type="hidden" name="dpp" value="">

		<input type="hidden" name="sala" value="">
		
		<tr class="encabezados-tabla">
			<td colspan="2">
				
			</td>
			<td>
				<span style="width:100%;text-align:right">
				<a href="javascript:ocultar_filtros()" title="Ocultar">Ocultar opciones de b&uacute;squeda</a>
				</span>
			</td>
		</tr>


		<tr class="fila-detalle-par">
			<td>Codigo de Prestador</td>
			<td style="text-align:left">
			<%
			String estiloDivId = "display:none";
			if ("si".equals(ide)) estiloDivId = "";
			%>
				<select name="ide" onChange="mostrarId()">
				<option value="no" <%= "no".equals(ide)?"selected":"" %>>No filtrar</option>
				<option value="si" <%= "si".equals(ide)?"selected":"" %>>Filtrar por este codigo de prestador</option>
				</select>
				
				<br>

				<!-- filtro rut prestador -->
				<span id="div-Id" style="<%= estiloDivId %>">
				<input type="text" name="id" size="12" value="<%= ide %>" onBlur="if(!CampoEsNumeroEnRango(this, 1, 9999999)){document.formulario.ide.selectedIndex=0;mostrarId();}">				
				</span>
			</td>
			
			<td rowspan="2">
			  <input type="submit" class="submit" value="Buscar">
			</td>
		</tr>

		<tr class="fila-detalle-par">
			<td>Tipo de Convenios</td>
			<td style="text-align:left">
				<select name="tipoConvenios" onChange="mostrarId()">
				<option value="vigentes" <%= "vigentes".equals(tipoConvenios)?"selected":"" %>>Sólo Vigentes</option>
				<option value="nuevos" <%= "vigentes".equals(tipoConvenios)?"selected":"" %>>Sólo Nuevos</option>
				<option value="modificados" <%= "vigentes".equals(tipoConvenios)?"selected":"" %>>Sólo Modificados</option>
				<option value="eliminados" <%= "vigentes".equals(tipoConvenios)?"selected":"" %>>Sólo Eliminados</option>
				<option value="todos" <%= "todos".equals(tipoConvenios)?"selected":"" %>>Mostrar todos</option>
				</select>
			</td>
			
		</tr>
		
		
	</table>

	
	<table id="listado">
		<tr class="encabezados-tabla">
			<td>C&oacute;digo</td>
			<td>C&oacute;digo Prestador</td>
			<td>Nombre del Convenio</td>
			<td>Inicio</td>
			<td>Término</td>
			<td colspan="2"></td>
		</tr>
	
<%
		int numFila = 1;
		for (Iterator it = lista.iterator(); it.hasNext();) {
			ConvenioDTO c = (ConvenioDTO) it.next();

		    // Determino un string que se alterna para cambiar la grafica de las filas
		    String clase= (numFila%2 == 0)? "fila-detalle-par":"fila-detalle-impar";
		    numFila++;
		    
		    String fechaInicio = "";
		    if (null == c.getFechaInicio()){ fechaInicio = "Sin fecha de inicio"; }
		    else { fechaInicio = sdf.format(c.getFechaInicio()); }
		    
		    String fechaTermino = "";
		    if (null == c.getFechaTermino()){ fechaTermino = "Sin fecha de termino"; }
		    else { fechaTermino = sdf.format(c.getFechaTermino()); }
		    
%>

		<tr class="<%=clase%>">
			<td><a href="Convenios?accion=detalle&id=<%= c.getCodigo() %>"><%= c.getCodigo() %></a></td>
			<td><%= c.getCodigoPrestador() %></td>
			<td><%= c.getGlosa() %></td>
			<td><%= fechaInicio %></td>
			<td><%= fechaTermino %></td>
			<td><a href="Convenios?accion=detalleExcel&id=<%= c.getCodigo() %>">Exportar como Excel</a></td>
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

	<p>
	<form method="post" action="Convenios">
	<input type="hidden" name="accion" value="crear">
	<input type="submit" value="Agregar un nuevo Convenio" class="submit">
	</form>
	</p>

	</div>
	

	<script language="javascript">
	  function mostrarId(){
	    if (document.formulario.ide.selectedIndex > 0){
	  
		    if (document.getElementById("div-Id")){
		    	document.getElementById("div-Id").style.display = "";
		    }
	    } else {
		    if (document.getElementById("div-Id")){
		    	document.getElementById("div-Id").style.display = "none";
		    }
	    }
	  }
	
	  function eliminar(codigo, nombre){
	    if (confirm("Confirme que desea eliminar el siguiente registro:\n ''" + nombre + "'' ")){
	       document.location = "Convenios?accion=eliminar&codigo=" + codigo;
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

	  // En esta pÃ¡gina, si viene la cookie "update", simplemente se consume la cookie
	  // Si no se encuentra la cookie "update", se fuerza un refresco de la pagina
	  // if (!GetCookie('update')){ document.formulario.submit(); } else { DeleteCookie('update'); }

	</script>

<jsp:include page="pie.jsp" flush="true"/>

