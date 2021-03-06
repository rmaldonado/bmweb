<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ page import="java.text.*" %>
<%@ page import="java.util.*" %>
<%@ page import="bmweb.dto.*" %>
<%@ page import="bmweb.util.*" %>
<%

  // Recupero el usuario en esta pantalla para ver si es administrador y dibujar elementos extra
  UsuarioWeb uw = (UsuarioWeb) request.getSession().getAttribute(UsuarioWeb.ATRIBUTO_USUARIO_WEB);
  boolean esAdministrador = "09".equals(uw.getNivel());

  SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

  ConvenioDTO convenio = new ConvenioDTO();
  if (request.getAttribute("convenio") != null){
	convenio = (ConvenioDTO) request.getAttribute("convenio");
  }
  
  // INDICO SI LOS CAMPOS DEL CONVENIO Y LISTA SON MODIFICABLES
  boolean esEditable = esAdministrador && (convenio.getFechaInicio() == null);

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

  List tiposConvenio = new ArrayList();  
  if (request.getAttribute("tiposConvenio") != null){
	  tiposConvenio = (List) request.getAttribute("tiposConvenio");
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
  }
  
  String fechaTermino = "";
  if (null != convenio.getFechaTermino()) {
	  fechaTermino = sdf.format(convenio.getFechaTermino());
  }
  
  String fechaResolucion = "";
  if (null != convenio.getFechaConcurrencia()) {
	  fechaResolucion = sdf.format(convenio.getFechaConcurrencia());
  }
  
  Map estadosConvenio = new HashMap();
  estadosConvenio.put(new Integer(0), "Convenio vigente");
  estadosConvenio.put(new Integer(ConvenioDTO.CONVENIO_NUEVO), "Nuevo convenio");
  estadosConvenio.put(new Integer(ConvenioDTO.CONVENIO_MODIFICADO), "Convenio Modificado");
  estadosConvenio.put(new Integer(ConvenioDTO.CONVENIO_ELIMINADO), "Convenio Eliminado");
  estadosConvenio.put(new Integer(ConvenioDTO.CONVENIO_RECHAZADO), "Convenio Rechazado");
  
  Map estadosValcon = new HashMap();
  estadosValcon.put(new Integer(0), " ");
  estadosValcon.put(new Integer(ValconDTO.ESTADO_NUEVO), "Nueva Prestacion");
  estadosValcon.put(new Integer(ValconDTO.ESTADO_MODIFICADO), "Prestacion Modificada");
  estadosValcon.put(new Integer(ValconDTO.ESTADO_ELIMINADO), "Prestacion Eliminada");
  estadosValcon.put(new Integer(ValconDTO.ESTADO_RECHAZADO), "Prestacion Rechazado");
  estadosValcon.put(new Integer(ValconDTO.ESTADO_PRESTACIONINVALIDA), "Codigo Prestacion No Existe");
%>
<jsp:include page="cabecera.jsp" flush="true"/>

<div>

	<h1>Información del Convenio</h1>

	<% if ((!esAdministrador) && (convenio.getFechaInicio() == null)) { %>
	<p>
	Este convenio no está vigente todavía. 
	Sólo un usuario administrador puede aprobarlo y convertirlo en el convenio vigente.
	<br><br>	
	<% } %>
	
	<form name="formulario1" method="post" action="Convenios">
	<input type="hidden" name="id" value="<%= convenio.getCodigo() %>">
	<input type="hidden" name="accion" value="">
	
	<table id="listado">
		<tr class="encabezados-tabla">
			<td colspan="4">
			Convenio #<%= convenio.getCodigo() %>: "<%= convenio.getGlosa() %>",
			Estado <%= estadosConvenio.get(new Integer(convenio.getEstadoConvenio())) %>
			</td>
		</tr>
				
		<% if (esEditable) { %>
		<tr class="fila-detalle-impar">
			<td><strong>Glosa</strong></td>
			<td colspan="3" align="left">
			<input type="text" size="40" name="glosa" value="<%= (""+convenio.getGlosa()).trim() %>">
			</td>
		</tr>
		<% } %>
		
		<tr class="fila-detalle-impar">
			<td><strong>Código Prestador</strong></td><td><%= convenio.getCodigoPrestador() %></td>
			<td><strong>Código Arancel Fonasa</strong></td>
			<td>
			<% if (esEditable) { %>
				<input type="text" size="8" name="codigoArancelFonasa"
				 value="<%= convenio.getCodigoArancelFonasa() %>" onBlur="CampoEsNumeroEnRango(this, 101001, 9999999)">
			<% } else { %>
				<%= convenio.getCodigoArancelFonasa() %>
			<% } %>
			</td>
		</tr>	
		<tr class="fila-detalle-par">
			<td><strong>Fecha Inicio Convenio</strong></td>
			<td>
			<% if (esEditable) { %>
			<!-- calendario -->
			
				<span id="span-fecha-inicio"><%= ("".equals(fechaInicio))?"Convenio nuevo":fechaInicio %></span>
				<input type="hidden" id="fechaInicio" name="fechaInicio" size="10" class="input" value="<%= fechaInicio %>">
				
				<img src="img/calendar.gif" id="f_trigger_c1"
				     style="cursor: pointer; border: 1px solid green;"
				     title="Seleccion de fecha"
				     onmouseover="this.style.background='green';"
				     onmouseout="this.style.background=''" />
				<script type="text/javascript">
				    Calendar.setup({
				        displayArea    :    "span-fecha-inicio",
				        inputField     :    "fechaInicio",
				        daFormat       :    "%d/%m/%Y",
				        ifFormat       :    "%d/%m/%Y",
				        button         :    "f_trigger_c1",
				        align          :    "Tl",
				        weekNumbers    :    false,
				        singleClick    :    true
				    });
				</script>
			
			<% } else { %>
				<%= ("".equals(fechaInicio))?"Convenio nuevo":fechaInicio %>
			<% } %>
			
			</td>
			<td><strong>Fecha Término Convenio</strong></td>

			<td>
			<% if (esEditable) { %>
			<!-- calendario -->
			
				<span id="span-fecha-termino"><%= ("".equals(fechaTermino))?"Convenio nuevo":fechaTermino %></span>
				<input type="hidden" id="fechaTermino" name="fechaTermino" size="10" class="input" value="<%= fechaTermino %>">
				
				<img src="img/calendar.gif" id="f_trigger_c2"
				     style="cursor: pointer; border: 1px solid green;"
				     title="Seleccion de fecha"
				     onmouseover="this.style.background='green';"
				     onmouseout="this.style.background=''" />
				<script type="text/javascript">
				    Calendar.setup({
				        displayArea    :    "span-fecha-termino",
				        inputField     :    "fechaTermino",
				        daFormat       :    "%d/%m/%Y",
				        ifFormat       :    "%d/%m/%Y",
				        button         :    "f_trigger_c2",
				        align          :    "Tl",
				        weekNumbers    :    false,
				        singleClick    :    true
				    });
				</script>
			
			<% } else { %>
				<%= ("".equals(fechaTermino))?"Convenio nuevo":fechaTermino %>
			<% } %>
			
			</td>

		</tr>
		<tr class="fila-detalle-impar">
			<td><strong>Tipo de Convenio</strong></td>
			<td>
			
			<% if (esEditable) { %>
			    <select name="tipoConvenio">
				<% 
				for(int i=0; i<tiposConvenio.size(); i++){ 
						CiudadDTO tipoConvenio = (CiudadDTO) tiposConvenio.get(i);
						String selected = "";
						if (convenio.getTipoConvenio() == tipoConvenio.getCodigo() ){
							selected  = "selected";
						}
				%>
				<option value="<%= tipoConvenio.getCodigo() %>" <%= selected %>><%= tipoConvenio.getNombre() %></option>
				<% } %>
			    </select>
			<% } else { %>
				<%= convenio.getTipoConvenio() %>
			<% } %>		
			</td>
			
			<td><strong>Resolución de Concurrencia</strong></td>
			<td>
				<% if (esEditable) { %>
					<input name="resolucionConcurrencia" type="text" size="10" 
					 value="<%= (null==convenio.getCodigoConcurrencia())?"":convenio.getCodigoConcurrencia() %>"
					 onBlur="CampoEsNumero(this)">
				<% } else { %>
					<%= (null==convenio.getCodigoConcurrencia())?"":convenio.getCodigoConcurrencia() %>
				<% } %>
			</td>
		</tr>
		
		<tr class="fila-detalle-impar">
			<td><strong>Fecha de Resolución</strong></td>
			<td colspan="3">
			
			<% if (esEditable) { %>
			<!-- calendario -->
			
				<span id="span-fecha-resolucion"><%= ("".equals(fechaTermino))?"Convenio nuevo":fechaTermino %></span>
				<input type="hidden" id="fechaResolucion" name="fechaResolucion" size="10" class="input" value="<%= fechaResolucion %>">
				
				<img src="img/calendar.gif" id="f_trigger_c3"
				     style="cursor: pointer; border: 1px solid green;"
				     title="Seleccion de fecha"
				     onmouseover="this.style.background='green';"
				     onmouseout="this.style.background=''" />
				<script type="text/javascript">
				    Calendar.setup({
				        displayArea    :    "span-fecha-resolucion",
				        inputField     :    "fechaResolucion",
				        daFormat       :    "%d/%m/%Y",
				        ifFormat       :    "%d/%m/%Y",
				        button         :    "f_trigger_c3",
				        align          :    "Tl",
				        weekNumbers    :    false,
				        singleClick    :    true
				    });
				</script>
			
			<% } else { %>
				<%= ("".equals(fechaTermino))?"Convenio nuevo":fechaResolucion %>
			<% } %>
			
			</td>
		</tr>
			
		<tr class="fila-detalle-par">
			<td><strong>Moneda</strong></td><td>Peso</td>
			<td><strong>Convenio hace referencia a FONASA?</strong></td>
			<td>
			<% if (esEditable) { %>
				<select name="referenciaFonasa">
					<option value="S" <%= ("S".equals(convenio.getReferenciaFonasa()))?"selected":"" %>>Sí</option>
					<option value="N" <%= ("N".equals(convenio.getReferenciaFonasa()))?"selected":"" %>>No</option>
				</select>
			<% } else { %>
				<%= "" + convenio.getReferenciaFonasa() %>
			<% } %>
			</td>
		</tr>
		<tr class="fila-detalle-impar">
			<td><strong>Nivel de Referencia FONASA</strong></td>
			<td>
			<% if (esEditable) { %>
				<select name="nivelReferenciaFonasa">
					<option value="1" <%= (1 == convenio.getNivelReferenciaFonasa())?"selected":"" %>>1</option>
					<option value="2" <%= (2 == convenio.getNivelReferenciaFonasa())?"selected":"" %>>2</option>
					<option value="3" <%= (3 == convenio.getNivelReferenciaFonasa())?"selected":"" %>>3</option>
				</select>
			<% } else { %>
				<%= convenio.getNivelReferenciaFonasa() %>
			<% } %>
			</td>
			<td><strong>Factor de Referencia FONASA</strong></td>
			<td>
			<% if (esEditable) { %>
				<input type="text" size=""8" name="factorReferenciaFonasa" value="<%= convenio.getFactorRefFonasa() %>"
				 onBlur="CampoEsNumero(this)">
			<% } else { %>
				<%= convenio.getFactorRefFonasa() %>
			<% } %>
			</td>
		</tr>	
		<tr class="encabezados-tabla">
			<td colspan="4" style="text-align:right">

<% if (esAdministrador && (convenio.getFechaInicio() == null)) { %>
				<input type="button" class="submit" value="Guardar y Aprobar convenio"
				 onClick="validarDatosConvenio()">
<% } %>
			
				<input type="button" class="submit" value="Exportar listado de valores en formato Excel"
				 onClick="document.formulario1.accion.value='detalleExcel';document.formulario1.submit()">
				 
			</td>
		</tr>		
			
	</table>
	</form>

	<h1>Detalle del Convenio</h1>
	<form name="formulario" method="post" action="Convenios">
		<input type="hidden" name="inicio" value="<%= inicio %>">
		<input type="hidden" name="dpp" value="<%= dpp %>">
		<input type="hidden" name="id" value="<%= convenio.getCodigo() %>">
		<input type="hidden" name="accion" value="detalle">

		<% if (esEditable) { %>		
		<input type="hidden" name="convenioRechazado" value="">
		<input type="hidden" name="prestacionRechazada" value="">
		<% } %>

	<table id="listado">
		<tr class="encabezados-tabla">
			<td>C&oacute;digo Prestación</td>
			<td>Valor Convenido</td>
			<td>Valor Lista</td>
			<td>Estado</td>
			<td>Fonasa Nivel Uno</td>
			<td colspan="2"></td>
		</tr>
	
<%
		int numFila = 1;
		for (Iterator it = lista.iterator(); it.hasNext();) {
			ValconDTO valcon = (ValconDTO) it.next();

		    // Determino un string que se alterna para cambiar la grafica de las filas
		    String clase= (numFila%2 == 0)? "fila-detalle-par":"fila-detalle-impar";
		    numFila++;
		    
		    String mensajeValorConvenido = "";
		    
		    // El texto va a decir "Valor no existía" o "+10%" o "-15%"
		    if ((valcon.getValorCovenido() == 0) || (valcon.getValorFonasa() == 0)) {
		      mensajeValorConvenido = "Prestacion no en Fonasa";
		    } else {		    	
		    	float variacionValor = ((100 * (valcon.getValorCovenido()-valcon.getValorFonasa())))/valcon.getValorFonasa();
		    	if (variacionValor > 0) { mensajeValorConvenido = "+"; }
		    	
		    	mensajeValorConvenido = mensajeValorConvenido + ((int)variacionValor) + "%";
		    }
		    
%>

		<tr class="<%=clase%>">
			<td><%= valcon.getCodigoPrestacion() %></td>
			<td><%= valcon.getValorCovenido() %></td>
			<td><%= valcon.getValorLista() %></td>
			<td><%= estadosValcon.get(new Integer(valcon.getEstado())) %></td>
       <!--     <td><%= ((valcon.getValorCovenido()*100)/valcon.getValorFonasa()) %></td> -->
            <td><%= mensajeValorConvenido %></td>
			<td>
			<% if (esEditable) { %>
			    <a href="javascript:rechazarConvenio(<%= convenio.getCodigo() %>, <%= valcon.getCodigoPrestacion() %>)">Rechazar convenio</a>
			<% } %>
			</td>
			<td>&nbsp;</td>
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
	
	</table>	
	</form>	
</div>


<script language="javascript">

  function validarDatosConvenio(){
  
    if ("" == document.formulario1.glosa.value ) {
		alert("Error: Debe especificar un valor para la glosa");
		return;
    }
  
    if ("" == document.formulario1.codigoArancelFonasa.value ) {
		alert("Error: Debe especificar un valor para el código de arancel FONASA");
		return;
    }
  
    if ("" == document.formulario1.fechaInicio.value ) {
		alert("Error: Debe especificar una Fecha de Inicio del convenio");
		return;
    }
  
    if ("" == document.formulario1.fechaTermino.value ) {
		alert("Error: Debe especificar una Fecha de Termino del convenio");
		return;
    }
  
    if ("" == document.formulario1.resolucionConcurrencia.value ) {
		alert("Error: Debe especificar una Resolución de concurrencia");
		return;
    }
  
    if ("" == document.formulario1.fechaResolucion.value ) {
		alert("Error: Debe especificar una Resolución de concurrencia");
		return;
    }
    
    if ("" == document.formulario1.factorReferenciaFonasa.value ) {
		alert("Error: Debe especificar una Resolución de concurrencia");
		return;
    }
    
  
    if (confirm("¿Está seguro de que desea autorizar este convenio?")){
	    document.formulario1.accion.value = "autorizarConvenio";
	    document.formulario1.submit();
    }  
  }
  
  function rechazarConvenio(convenio, prestacion) {
		if (confirm("¿Está seguro de que rechaza este valor y convenio?")){
			document.formulario.convenioRechazado.value = convenio;
			document.formulario.prestacionRechazada.value = prestacion;
			document.formulario.accion.value = "rechazarConvenio";
			document.formulario.submit();
		}
  }
  
</script>

<jsp:include page="pie.jsp" flush="true"/>
