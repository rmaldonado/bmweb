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
  request.setAttribute("titulo", "Administraci�n de Convenios");
%>
<jsp:include page="cabecera.jsp" flush="true"/>

<div>

	<h1>Administraci�n de Convenios</h1>

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
		<input type="hidden" name="inicio" value="">
		<input type="hidden" name="dpp" value="">
		
		<tr class="encabezados-tabla">
			<td colspan="2">
				Buscar Habilitados
			</td>
			<td>
				<span style="width:100%;text-align:right">
				<a href="javascript:ocultar_filtros()" title="Ocultar los filtros del listado">Ocultar filtros</a>
				</span>
			</td>
		</tr>
		<tr class="fila-detalle-impar">
			<td>C&oacute;digo</td>
			<td style="text-align:left">
				<select name="opcodigo">
				<option value="no" <%= "no".equals(opcodigo)?"selected":"" %>>No filtrar por c&oacute;digo</option>
				<option value="le" <%= "le".equals(opcodigo)?"selected":"" %>>tenga un valor menor o igual a</option>
				<option value="eq" <%= "eq".equals(opcodigo)?"selected":"" %>>tenga un valor igual a</option>
				<option value="gt" <%= "gt".equals(opcodigo)?"selected":"" %>>tenga un valor igual o mayor a</option>
				</select><input type="text" name="codigo" size="20" maxlength="30" class="input" value="<%= codigo %>"></td>

			<!-- rowspan tantas filas como tenga el filtro -->
			<td rowspan="7" style="text-align:center; vertical-align:middle">
				<input type="submit" value="Filtrar datos" class="submit" 
				title="Muestra el listado de Habilitados usando los criterios de b&uacute;squeda"
				style="width:120px">
				<br>
				<br>
				<input type="reset" class="button" value="Restaurar Valores" 
				title="Volver a los valores anteriores de los criterios de b&uacute;squeda"
				style="width:120px">
				<br>
				<input type="button" class="button" value="Limpiar filtros" onclick="limpiar_filtros()" 
				title="Limpiar los valores de los criterios de b&uacute;squeda"
				style="width:120px">
			</td>
		</tr>

		<tr class="fila-detalle-par">
			<td>Nombre</td>
			<td style="text-align:left">
				<select name="opnombre">
				<option value="no" <%= "no".equals(opnombre)?"selected":"" %>>No filtrar por nombre</option>
				<option value="comienza" <%= "comienza".equals(opnombre)?"selected":"" %>>comienza con el siguiente texto</option>
				<option value="contiene" <%= "contiene".equals(opnombre)?"selected":"" %>>contiene el siguiente texto</option>
				<option value="termina"  <%= "termina".equals(opnombre)?"selected":"" %>>termina con el siguiente texto</option>
				</select><input type="text" name="nombre" size="20" maxlength="30" class="input" value="<%= nombre %>"></td>
		</tr>

		<tr class="fila-detalle-impar">
			<td>Ubicaci&oacute;n</td>
			<td style="text-align:left">
				<select name="opubicacion">
				<option value="no" <%= "no".equals(opubicacion)?"selected":"" %>>No filtrar por ubicaci&oacute;n</option>
				<option value="comienza" <%= "comienza".equals(opubicacion)?"selected":"" %>>comienza con el siguiente texto</option>
				<option value="contiene" <%= "contiene".equals(opubicacion)?"selected":"" %>>contiene el siguiente texto</option>
				<option value="termina"  <%= "termina".equals(opubicacion)?"selected":"" %>>termina con el siguiente texto</option>
				</select><input type="text" name="ubicacion" size="20" maxlength="30" class="input" value="<%= ubicacion %>"></td>
		</tr>
		
		<tr class="fila-detalle-par">
			<td>Ciudad:</td>
			<td style="text-align:left">
				<select name="dom_ciudad">
				<option value="">No filtrar por Ciudad</option>
			<%
				for (int i=0; i<listaCiudades.size(); i++){
				CiudadDTO c = (CiudadDTO) listaCiudades.get(i);
				String nombreCiudad = c.getNombre();
				String codigoCiudad = "" + c.getCodigo();
				
				String selected = "";
				if (ciudad.equals(codigoCiudad)){ selected = "selected"; }
				
			%>
				<option value="<%= codigoCiudad  %>" <%= selected  %>><%= nombreCiudad %></option>
			<%
				}
			%>
				</select>		

			</td>		
		</tr>

		<tr class="fila-detalle-impar">
			<td>Direcci&oacute;n</td>
			<td style="text-align:left">
				<select name="opdireccion">
				<option value="no" <%= "no".equals(opdireccion)?"selected":"" %>>No filtrar por Direcci&oacute;n</option>
				<option value="comienza" <%= "comienza".equals(opdireccion)?"selected":"" %>>comienza con el siguiente texto</option>
				<option value="contiene" <%= "contiene".equals(opdireccion)?"selected":"" %>>contiene el siguiente texto</option>
				<option value="termina"  <%= "termina".equals(opdireccion)?"selected":"" %>>termina con el siguiente texto</option>
				</select><input type="text" name="direccion" size="20" maxlength="30" class="input" value="<%= direccion %>"></td>
		</tr>
		
		<tr class="fila-detalle-par">
			<td>Responsable</td>
			<td style="text-align:left">
				<select name="opresponsable">
				<option value="no" <%= "no".equals(opresponsable)?"selected":"" %>>No filtrar por nombre</option>
				<option value="comienza" <%= "comienza".equals(opresponsable)?"selected":"" %>>comienza con el siguiente texto</option>
				<option value="contiene" <%= "contiene".equals(opresponsable)?"selected":"" %>>contiene el siguiente texto</option>
				<option value="termina"  <%= "termina".equals(opresponsable)?"selected":"" %>>termina con el siguiente texto</option>
				</select><input type="text" name="responsable" size="20" maxlength="30" class="input" value="<%= responsable %>"></td>
		</tr>

		<tr class="fila-detalle-par">
			<td>Estado</td>
			<td style="text-align:left">
				<select name="opactivo">
				<option value="no" <%= "no".equals(opactivo)?"selected":"" %>>No filtrar por estado</option>
				<option value="activo" <%= "activo".equals(opactivo)?"selected":"" %>>Activo</option>
				<option value="noactivo" <%= "noactivo".equals(opactivo)?"selected":"" %>>No Activo</option>
				</select>
			</td>
		</tr>


		
		<tr class="encabezados-tabla">
			<td colspan="3"></td>
		</tr>
	</table>

	
	<table id="listado">
		<tr class="encabezados-tabla">
			<td>C&oacute;digo</td>
			<td>C&oacute;digo Prestador</td>
			<td>Nombre del Convenio</td>
			<td>Inicio</td>
			<td>T�rmino</td>
			<td colspan="2"></td>
		</tr>
	
<%
		int numFila = 1;
		for (Iterator it = lista.iterator(); it.hasNext();) {
			ConvenioDTO c = (ConvenioDTO) it.next();

		    // Determino un string que se alterna para cambiar la grafica de las filas
		    String clase= (numFila%2 == 0)? "fila-detalle-par":"fila-detalle-impar";
		    numFila++;
		    
		    
%>

		<tr class="<%=clase%>">
			<td><a href="Convenios?accion=detalle&id=<%= c.getCodigo() %>"><%= c.getCodigo() %></a></td>
			<td><%= c.getCodigoPrestador() %></td>
			<td><%= c.getGlosa() %></td>
			<td><%= sdf.format(c.getFechaInicio()) %></td>
			<td><%= sdf.format(c.getFechaTermino()) %></td>
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

	<p>
	<form method="post" action="Convenios">
	<input type="hidden" name="accion" value="crear">
	<input type="submit" value="Agregar un nuevo Convenio" class="submit">
	</form>
	</p>

	</div>
	

	<script language="javascript">
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

	  // En esta página, si viene la cookie "update", simplemente se consume la cookie
	  // Si no se encuentra la cookie "update", se fuerza un refresco de la pagina
	  // if (!GetCookie('update')){ document.formulario.submit(); } else { DeleteCookie('update'); }

	</script>

<jsp:include page="pie.jsp" flush="true"/>

