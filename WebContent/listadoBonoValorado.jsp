<%@ page import="java.text.*" %>
<%@ page import="java.util.*" %>
<%@ page import="bmweb.util.*" %>
<%@ page import="bmweb.dto.BonoDTO" %>
<%


  SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
  //SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");

  HashMap mapaTiposBono = new HashMap();
  mapaTiposBono.put( BonoDTO.TIPOBONO_ABIERTO,		"Bono Abierto");
  mapaTiposBono.put( BonoDTO.TIPOBONO_DIGITADO,		"Bono Digitado");
  mapaTiposBono.put( BonoDTO.TIPOBONO_FACTURADO,	"Bono Facturado");
  mapaTiposBono.put( BonoDTO.TIPOBONO_SINDETALLE,	"Bono Sin Detalle");
  mapaTiposBono.put( BonoDTO.TIPOBONO_VALORADO,		"Bono Valorado");
  mapaTiposBono.put( BonoDTO.TIPOBONO_WEB,			"Bono Internet");


  List listaBonos = new ArrayList();
  HashMap mapaCiudades = new HashMap();

  if (request.getAttribute("listaBonos") != null){
  	listaBonos = (List) request.getAttribute("listaBonos");
  }

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

  // deteccion de parametros para filtros
  boolean mostrarFiltros = false;
  if ("1".equals(request.getParameter("mostrarFiltros"))) { mostrarFiltros = true; }

  String opfolio = "";  
  if (request.getParameter("opfolio") != null) { opfolio = request.getParameter("opfolio"); }

  String folio = "";
  if (request.getParameter("folio") != null) { folio = request.getParameter("folio"); }
  
  String opfecha = "";
  if (request.getParameter("opfecha") != null) { opfecha = request.getParameter("opfecha"); }

  String fechaDesde = sdf.format(new Date());
  if (request.getParameter("fechaDesde") != null) { fechaDesde = request.getParameter("fechaDesde"); }

  String fechaHasta = sdf.format(new Date());
  if (request.getParameter("fechaHasta") != null) { fechaHasta = request.getParameter("fechaHasta"); }

  String paramTipoBono = "";
  if (request.getParameter("paramTipoBono") != null) { paramTipoBono = request.getParameter("paramTipoBono"); }

  String opemisor = "";
  if (request.getParameter("opemisor") != null) { opemisor = request.getParameter("opemisor"); }

  String emisor = "";
  if (request.getParameter("emisor") != null) { emisor = request.getParameter("emisor"); }


  // filtro por rut del prestador y por cmc del beneficiario

  String oprutprestador = "";
  if (request.getParameter("oprutprestador") != null) { oprutprestador = request.getParameter("oprutprestador"); }

  String rutprestador = "";
  if (request.getParameter("rutprestador") != null) { rutprestador = request.getParameter("rutprestador"); }

  String opcmcbeneficiario = "";
  if (request.getParameter("opcmcbeneficiario") != null) { opcmcbeneficiario = request.getParameter("opcmcbeneficiario"); }

  String cmcbeneficiario = "";
  if (request.getParameter("cmcbeneficiario") != null) { cmcbeneficiario = request.getParameter("cmcbeneficiario"); }


  // coloco el titulo de la pagina
  request.setAttribute("titulo", "Consulta de Bonos");
%>
<jsp:include page="cabecera.jsp" flush="true"/>

<div>

	<h1>Consulta de Bonos Valorados</h1>

	<table class="tabla-borde-delgado" id="filtro-min" style="<%= (mostrarFiltros)? "display:none":"" %>">
		<tr class="encabezados-tabla">
			<td style="text-align:right">
				<a href="javascript:mostrar_filtros()" title="Mostrar">
				Mostrar opciones de b&uacute;squeda</a>
			</td>
		</tr>
	</table>

	<table class="tabla-borde-delgado" id="filtro-max" style="<%= (mostrarFiltros)? "":"display:none" %>">
	<form name="formulario" method="post" action="BonoValorado">
		<input type="hidden" name="mostrarFiltros" value="<%= (mostrarFiltros)? "1":"0" %>">
		<input type="hidden" name="accion" value="listado">
		<input type="hidden" name="inicio" value="">
		<input type="hidden" name="dpp" value="">
		
		<tr class="encabezados-tabla">
			<td colspan="2">
				Buscar Bonos
			</td>
			<td>
				<span style="width:100%;text-align:right">
				<a href="javascript:ocultar_filtros()" title="Ocultar">Ocultar opciones de b&uacute;squeda</a>
				</span>
			</td>
		</tr>
		<tr class="fila-detalle-impar">
			<td>N&uacute;mero de Bono</td>
			<td style="text-align:left">
				<select name="opfolio">
				<option value="no" <%= "no".equals(opfolio)?"selected":"" %>>No buscar por n&uacute;mero de bono</option>
				<option value="le" <%= "le".equals(opfolio)?"selected":"" %>>tenga un valor menor o igual a</option>
				<option value="eq" <%= "eq".equals(opfolio)?"selected":"" %>>tenga un valor igual a</option>
				<option value="gt" <%= "gt".equals(opfolio)?"selected":"" %>>tenga un valor igual o mayor a</option>
				</select><input type="text" name="folio" size="20" maxlength="30" class="input" value="<%= folio %>"></td>

			<!-- rowspan tantas filas como tenga el filtro -->
			<td rowspan="5" style="text-align:center; vertical-align:middle">
				<input type="button" value="Buscar Datos" class="submit" 
				onClick="document.formulario.accion.value='listado';document.formulario.submit()"
				title="Muestra el listado de Habilitados usando los criterios de b&uacute;squeda"
				style="width:120px">
				<br>
				<br>
				<input type="reset" class="button" value="Restaurar Valores" 
				title="Volver a los valores anteriores de los criterios de b&uacute;squeda"
				style="width:120px">
				<br>
				<input type="button" class="button" value="Nueva B&uacute;squeda" onclick="limpiar_filtros()" 
				title="Limpiar los valores de los criterios de b&uacute;squeda"
				style="width:120px">
			</td>
		</tr>

		<tr class="fila-detalle-par">
			<td>Fecha</td>
			<td style="text-align:left">
			<%
			String estiloDivCalendario = "";
			if ("no".equals(opfecha)) estiloDivCalendario = "display:none";
			if ("".equals(opfecha)) estiloDivCalendario = "display:none";
			%>
				<select name="opfecha" onChange="mostrarCalendario()">
				<option value="no"    <%= "no".equals(opfecha)?"selected":"" %>>No filtrar por Fecha</option>
				<option value="entre" <%= "entre".equals(opfecha)?"selected":"" %>>Entre estas Fechas</option>
				</select>

				<!-- fecha desde -->
				<span id="div-calendario" style="<%= estiloDivCalendario %>">
				<span id="span-fecha-desde"><%= fechaDesde %></span>
				<input type="hidden" id="fechaDesde" name="fechaDesde" size="10" class="input" value="<%= fechaDesde %>">
				
				<img src="img/calendar.gif" id="f_trigger_c1"
				     style="cursor: pointer; border: 1px solid green;"
				     title="Seleccion de fecha"
				     onmouseover="this.style.background='green';"
				     onmouseout="this.style.background=''" />
				<script type="text/javascript">
				    Calendar.setup({
				        displayArea    :    "span-fecha-desde",
				        inputField     :    "fechaDesde",
				        daFormat       :    "%d/%m/%Y",
				        ifFormat       :    "%d/%m/%Y",
				        button         :    "f_trigger_c1",
				        align          :    "Tl",
				        weekNumbers    :    false,
				        singleClick    :    true
				    });
				</script>
				
				-

				<!-- fecha hasta -->
				<span id="span-fecha-hasta"><%= fechaHasta %></span>
				<input type="hidden" id="fechaHasta" name="fechaHasta" size="10" class="input" value="<%= fechaHasta %>">
				
				<img src="img/calendar.gif" id="f_trigger_c2"
				     style="cursor: pointer; border: 1px solid green;"
				     title="Seleccion de fecha"
				     onmouseover="this.style.background='green';"
				     onmouseout="this.style.background=''" />
				<script type="text/javascript">
				    Calendar.setup({
				        displayArea    :    "span-fecha-hasta",
				        inputField     :    "fechaHasta",
				        daFormat       :    "%d/%m/%Y",
				        ifFormat       :    "%d/%m/%Y",
				        button         :    "f_trigger_c2",
				        align          :    "Tl",
				        weekNumbers    :    false,
				        singleClick    :    true
				    });
				</script>
				
				</span></td>
		</tr>
<%--
		<tr class="fila-detalle-impar">
			<td>Tipo de Bono</td>
			<td style="text-align:left">
				<select name="paramTipoBono">
				<option value="">No filtrar por Tipo de Bono</option>
				<%
				Iterator iTipo = mapaTiposBono.keySet().iterator();
				while (iTipo.hasNext()){
				  String llave = (String) iTipo.next();
				  String valor = (String) mapaTiposBono.get(llave);
				%>
				<option value="<%= llave %>" <%= llave.equals(paramTipoBono)?"selected":"" %>><%= valor %></option>
				<% } %>
				</select>
			</td>
		</tr>
--%>
		<tr class="fila-detalle-impar">
			<td>Rut de Emisor</td>
			<td style="text-align:left">
				<select name="opemisor">
				<option value="">No filtrar</option>
				<option value="eq" <%= opemisor.equals("eq")?"selected":"" %>>igual a</option>
				</select><input type="text" name="emisor" size="20" maxlength="30" class="input" value="<%= emisor %>">
			</td>
		</tr>

<%--
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

--%>

		<tr class="fila-detalle-par">
			<td>Rut del Prestador</td>
			<td style="text-align:left">
				<select name="oprutprestador">
				<option value="">No filtrar</option>
				<option value="eq" <%= oprutprestador.equals("eq")?"selected":"" %>>Rut igual a</option>
				</select><input type="text" name="rutprestador" size="20" maxlength="30" class="input" value="<%= rutprestador %>">
			</td>
		</tr>

		<tr class="fila-detalle-impar">
			<td>CMC del beneficiario</td>
			<td style="text-align:left">
				<select name="opcmcbeneficiario">
				<option value="">No filtrar</option>
				<option value="eq" <%= opcmcbeneficiario.equals("eq")?"selected":"" %>>CMC igual a</option>
				</select><input type="text" name="cmcbeneficiario" size="20" maxlength="30" class="input" value="<%= cmcbeneficiario %>">
			</td>
		</tr>


		
		<tr class="encabezados-tabla">
			<td colspan="3"></td>
		</tr>
	</table>

	<table id="listado">
<%
if (!"listadoSoloFiltro".equals(request.getAttribute("listadoSoloFiltro"))){
%>	
		<tr class="encabezados-tabla">
			<td>Numero Bono</td>
			<td>Fecha de<br />emisi&oacute;n</td>
			<td>Tipo</td>
			<td>Carn&eacute; de<br />medicina<br />curativa</td>
			<td>Emisor</td>
			<td colspan="4"></td>
		</tr>
<%
}
%>

<%
		int numFila = 1;
		for (Iterator it = listaBonos.iterator(); it.hasNext();) {
		    BonoDTO b = (BonoDTO) it.next();

		    // Determino un string que se alterna para cambiar la grafica de las filas
		    String clase= (numFila%2 == 0)? "fila-detalle-par":"fila-detalle-impar";
		    numFila++;
		    
		    String tipoBono = "--";
		    if (mapaTiposBono.containsKey( b.getTipoBono() )){
		    	tipoBono = (String) mapaTiposBono.get(b.getTipoBono().trim());
		    }
		    
		    String fechaEmision = "";
		    try { fechaEmision = sdf.format( b.getFechaEmision() ); } catch (Exception ex){ }
%>
		<tr class="<%=clase%>">
			<td><a href="BonoValorado?accion=detalle&folio=<%= b.getFolio() %>" title="Ver detalle"><%= b.getFolio() %></a></td>
			<td><%= fechaEmision %></td>
			<td><%= tipoBono %></td>
			<td><%= b.getCarneBeneficiario() %></td>
			<td><%= b.getCodigoHabilitado() %></td>

			<td><a href="BonoValorado?accion=detalle&folio=<%= b.getFolio() %>">Ver Detalle</a></td>
			<td><a href="BonoValoradoPDF.pdf?folio=<%= b.getFolio() %>" target="_blank">Ver Archivo PDF</a></td>
			<td><a href="javascript:anular(<%= b.getFolio() %>)">Anular Bono</a></td>
			<td><!-- a href="javascript:eliminar(<%= b.getFolio() %>, '<%= b.getFolio() %>')" title="Eliminar Registro">Eliminar</a--> </td>
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
			<!-- a href="Habilitados?inicio=<%= (inicio-dpp) %>&dpp=<%= dpp %>" title="Ir la la p&aacute;gina anterior">&lt;&lt;</a -->
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
			<!-- a href="Habilitados?inicio=<%= (inicio+dpp) %>&dpp=<%= dpp %>" title="Ir la la p&aacute;gina siguiente">&gt;&gt;</a --> 
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
	<form method="post" action="BonoValorado">
	<input type="hidden" name="accion" value="crear">
	<input type="submit" value="Crear un Bono Valorado" class="submit">
	</form>
	</p>

	</div>
	

	<script language="javascript">
	  function anular(folio){
	    if (confirm("Confirme que desea anular el siguiente bono: ''" + folio + "'' ")){
	       document.location = "BonoValorado?accion=anular&folio=" + folio;
	    }
	  }
	  
	  function eliminar(codigo, nombre){
	    if (confirm("Confirme que desea eliminar el siguiente registro:\n ''" + nombre + "'' ")){
	       document.location = "Habilitados?accion=eliminar&codigo=" + codigo;
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
	  	
	  	document.formulario.opfolio.selectedIndex = 0;
	  	document.formulario.folio.value = "";
	  	//document.formulario.opfecha.selectedIndex = 0;
	  	//document.formulario.fecha.value = "";
	  	
	  	document.formulario.opemisor.selectedIndex = 0;
		document.formulario.emisor.value = "";
	  	
	  	// limpio rut del prestador y cmc beneficiario
		document.formulario.oprutprestador.selectedIndex = 0;
		document.formulario.rutprestador.value = "";

		document.formulario.opcmcbeneficiario.selectedIndex = 0;
		document.formulario.cmcbeneficiario.value = "";
	  	
	  	/*
	  	document.formulario.opubicacion.selectedIndex = 0;
	  	document.formulario.ubicacion.value = "";
	  	
	  	document.formulario.dom_ciudad.selectedIndex = 0;

	  	document.formulario.direccion.value = "";
	  	document.formulario.opdireccion.selectedIndex = 0;

	  	document.formulario.responsable.value = "";
	  	document.formulario.opresponsable.selectedIndex = 0;
	  	*/
	  }
	  
	  function mostrarCalendario(){
	  
	    if (document.formulario.opfecha.selectedIndex > 0){
	  
		    if (document.getElementById("div-calendario")){
		    	document.getElementById("div-calendario").style.display = "";
		    }
	    } else {
		    if (document.getElementById("div-calendario")){
		    	document.getElementById("div-calendario").style.display = "none";
		    }
	    }
	  }

	  // En esta página, si viene la cookie "update", simplemente se consume la cookie
	  // Si no se encuentra la cookie "update", se fuerza un refresco de la pagina
	  if (!GetCookie('update')){ document.formulario.submit(); } else { DeleteCookie('update'); }

	</script>

<jsp:include page="pie.jsp" flush="true"/>

