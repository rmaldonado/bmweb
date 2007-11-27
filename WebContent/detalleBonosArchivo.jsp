<%@ page import="java.text.*" %>
<%@ page import="java.util.*" %>
<%@ page import="bmweb.dto.*" %>
<%@ page import="bmweb.util.*" %>
<%

  SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

  HashMap mapaTiposBono = new HashMap();
  mapaTiposBono.put( BonoDTO.TIPOBONO_ABIERTO,		"Bono Abierto");
  mapaTiposBono.put( BonoDTO.TIPOBONO_DIGITADO,		"Bono Digitado");
  mapaTiposBono.put( BonoDTO.TIPOBONO_FACTURADO,	"Bono Facturado");
  mapaTiposBono.put( BonoDTO.TIPOBONO_SINDETALLE,	"Bono Sin Detalle");
  mapaTiposBono.put( BonoDTO.TIPOBONO_VALORADO,		"Bono Valorado");
  mapaTiposBono.put( BonoDTO.TIPOBONO_WEB,			"Bono Internet");

  HashMap mapaTiposPrestacion = new HashMap();
  mapaTiposPrestacion.put( "1",	"Ambulatoria");
  mapaTiposPrestacion.put( "2",	"Hospitalaria");
  mapaTiposPrestacion.put( "7",	"Cirugia Ambulatoria");
  
  HashMap mapaCobroPabellon = new HashMap();
  mapaCobroPabellon.put( "0",	"S&oacute;lo el pabell&oacute;n");
  mapaCobroPabellon.put( "1",	"S&oacute;lo la prestaci&oacute;n");
  mapaCobroPabellon.put( "2",	"Prestaci&oacute;n y pabell&oacute;n");

  HashMap mapaDiaCama = new HashMap();
  mapaDiaCama.put("0",	"Sala com&uacute;n");
  mapaDiaCama.put("1",	"Pensionado");

  HashMap mapaCobro = new HashMap();
  mapaCobro.put("1", "1er. Cirujano");
  mapaCobro.put("2", "2do. Cirujano");
  mapaCobro.put("3", "3er. Cirujano");
  mapaCobro.put("4", "Arsenalero");
  mapaCobro.put("5", "Anestesista");
  mapaCobro.put("6", "Todos");


  BonoDTO bono = new BonoDTO();
  if (request.getAttribute("bono") != null){
  	bono = (BonoDTO) request.getAttribute("bono");
  }
  
  //ArrayList bonoItems = new ArrayList( (List)request.getAttribute("bonoItems") );

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

  List prestacionesGenericas = new ArrayList();
  if (request.getAttribute("prestacionesGenericas") != null){
  	prestacionesGenericas = (List) request.getAttribute("prestacionesGenericas");
  }

  String ciudad = "Ciudad no encontrada";
  if (mapaCiudades.containsKey( bono.getIdCiudad() )){
    ciudad = (String) mapaCiudades.get( bono.getIdCiudad() );
  }

  // coloco el titulo de la página
  request.setAttribute("titulo", "Detalle de Bono");
  
  PrestadorDTO prestador = new PrestadorDTO();
  if (request.getAttribute("prestador") != null){
  	prestador = (PrestadorDTO) request.getAttribute("prestador");
  }

  RolbeneDTO rolbene = new RolbeneDTO();
  if (request.getAttribute("rolbene") != null){
  	rolbene = (RolbeneDTO) request.getAttribute("rolbene");
  }
  
  BeneficiarioDTO beneficiario = new BeneficiarioDTO();
  if (request.getAttribute("beneficiario") != null){
  	beneficiario = (BeneficiarioDTO) request.getAttribute("beneficiario");
  }
  
  BeneficiarioDTO beneficiario2 = new BeneficiarioDTO();
  if (request.getAttribute("beneficiario2") != null){
  	beneficiario2 = (BeneficiarioDTO) request.getAttribute("beneficiario2");
  }
  
  String folio = (String) request.getParameter("folio");
  // Parche para la ver el detalle desde la creacion del bono
  if (request.getAttribute("folio") != null) {
    folio = (String) request.getAttribute("folio");
  }
  
  String rutImponente = beneficiario2.getBene() + "-" + beneficiario2.getDgv() + "(" + beneficiario2.getNombre()+ " " + beneficiario2.getPat() + " " + beneficiario2.getMat() + ")"; 
  if (beneficiario2.getBene() == 0){
  	rutImponente = "Hay problemas con el registro del Beneficiario. Contacte al administrador.";
  }

  // La lista de prestaciones que se van a agregar al bono está en la sesion
  List items = new ArrayList();
  if (request.getSession().getAttribute("items") != null) {
    items = (List) request.getSession().getAttribute("items");
  }

  List prestacionesEncontradas = null;
  if (request.getAttribute("prestacionesEncontradas") != null) {
    prestacionesEncontradas = (List) request.getAttribute("prestacionesEncontradas");
  }

  UsuarioWeb usuarioWeb = (UsuarioWeb) request.getSession().getAttribute(UsuarioWeb.ATRIBUTO_USUARIO_WEB);
  
  boolean hayPrestacionesHospitalarias = false;
  for (int i=0; items != null && i<items.size(); i++){
    BonoItemDTO item = (BonoItemDTO) items.get(i);
    if ("2".equals(item.getCodTipoPrestacion().toString())){ hayPrestacionesHospitalarias = true; }
    
  }
  
  // Prestaciones que tienen pabellon
  Map mapaCirugias = new HashMap();
  if (request.getAttribute("mapaCirugias") != null) {
    mapaCirugias = (Map) request.getAttribute("mapaCirugias");
  }
  
%>
<jsp:include page="cabecera.jsp" flush="true"/>

<div>

	<h1>Detallar Bono Abierto</h1>
	
	<table id="listado">

		<tr class="encabezados-tabla">
			<td colspan="2" style="text-align:left">Detalle de Bono</td>
		</tr>

		<tr class="fila-detalle-impar">
			<td style="text-align:left">Folio</td><td style="text-align:left"><%= bono.getFolio() %></td>
		</tr>
		<tr class="fila-detalle-par">
			<td style="text-align:left">Tipo de Bono</td><td style="text-align:left"><%= (String)mapaTiposBono.get( bono.getTipoBono() ) %></td>
		</tr>
	 
		
		<tr class="fila-detalle-par">
			<td style="text-align:left">Emisor</td><td style="text-align:left"><%= request.getAttribute("emisor") %> <!-- <%= usuarioWeb.getNombreCompleto() %> --></td>
		</tr>
	
		<tr class="fila-detalle-impar">
			<td style="text-align:left">N&uacute;mero de Atenci&oacute;n</td><td style="text-align:left"><%= (bono.getNumeroAtencion()==null)?"1":(bono.getNumeroAtencion().toString()) %></td>
		</tr>

		<tr class="fila-detalle-par">
			<td style="text-align:left">Carnet Medicina Curativa</td><td style="text-align:left"><%= bono.getCarneBeneficiario() %></td>
		</tr>
		
		<tr class="fila-detalle-par">
			<td style="text-align:left">Rut Beneficiario</td><td style="text-align:left"><%= beneficiario.getBene()+"-"+beneficiario.getDgv()%> (<%=beneficiario.getNombre()+" "+beneficiario.getPat()+" "+beneficiario.getMat() %>)</td>
		</tr>
	
		<tr class="fila-detalle-par">
			<td style="text-align:left">Rut Profesional/Instituci&oacute;n</td><td style="text-align:left"><%= bono.getRutPrestador() %> (<%= prestador.getRazonSocial() %>)</td>
		</tr>

		<tr class="fila-detalle-impar">
			<td style="text-align:left">Ciudad</td><td style="text-align:left"><%= mapaCiudades.get( bono.getIdCiudad() ) %></td>
		</tr>
		

		<tr class="encabezados-tabla">
			<td colspan="2" style="text-align:left">Imponente</td>
		</tr>

		<tr class="fila-detalle-par">
			<td style="text-align:left">Carnet Medicina Curativa</td><td style="text-align:left"><%= bono.getCarneBeneficiario().substring(0,8) %>-00</td>
		</tr>
		<tr class="fila-detalle-impar">
			<td style="text-align:left">RUT del Imponente</td><td style="text-align:left"><%= rutImponente %></td>
		</tr>

		<tr class="encabezados-tabla">
			<td colspan="2" style="text-align:left">Detalle de Prestaciones</td>
		</tr>

		<tr class="fila-detalle-impar">
			<td style="text-align:left">Familia de Prestaciones</td>
			<td style="text-align:left"><%= ((PrestacionGenericaDTO)(prestacionesGenericas.get(0))).getNombre() %></td>
		</tr>
	</table>

	<div style="font-size:4px">&nbsp;</div>

	<a name="prestaciones"><h1>Prestaciones agregadas al Bono Abierto</h1></a>

<% if (items == null || items.size() == 0) { %>
	<table id="listado">
	<tr class="fila-detalle-impar"><td>No se han agregado prestaciones a este bono</td></tr>
	</table>
<% } else { %>
	<table id="listado">

		<tr class="encabezados-tabla">
			<td style="font-size:x-small">C&oacute;digo</td>
			<td style="font-size:x-small">Nombre</td>

			<td style="font-size:x-small">Tipo<br>Prestaci&oacute;n</td>
			<td style="font-size:x-small">Fecha<br>Atenci&oacute;n</td>
			<td style="font-size:x-small">Cantidad</td>

			<% if (hayPrestacionesHospitalarias) { %>
			<!-- <td style="font-size:x-small">Pabell&oacute;n</td> -->
			<td style="font-size:x-small">Cobro<br>Pabell&oacute;n</td>
			<td style="font-size:x-small">D&iacute;a<br>Camaaqui</td>
			<td style="font-size:x-small">Cobro</td>
			<% } %>

			<td style="font-size:x-small">Aporte<br>Dipreca</td>
			<td style="font-size:x-small">Aporte<br>Seguro</td>
			<td style="font-size:x-small">Valor<br>Copago</td>
			<td></td>
		</tr>
<%
			for (int i=0; i<items.size(); i++) {
			   BonoItemDTO item = (BonoItemDTO) items.get(i);
			    // Determino un string que se alterna para cambiar la grafica de las filas
			    String clase = (i%2 == 0)? "fila-detalle-par":"fila-detalle-impar";
			    
			    String tipoPrestacion = "";
			    try { tipoPrestacion = (String) mapaTiposPrestacion.get( item.getCodTipoPrestacion().toString() ); }
			    catch (Exception e) { }
			    
			    String fechaAtencion = "";
			    try { fechaAtencion = sdf.format( item.getFechaEfectivaAtencionMedica()); }
			    catch (Exception e) { }
			    
			    String pabellon = "";
			    try { pabellon = item.getCodigoPabellon().toString(); } 
			    catch (Exception e) { }
			    
			    String cantidad = "";
			    try { cantidad = item.getCantidadAtenciones().toString(); } 
			    catch (Exception e) { }
			    
			    String cobroPabellon = "";
			    try { cobroPabellon = (String) mapaCobroPabellon.get( item.getIncluyePabellon().toString() ); }
			    catch (Exception e) { }
			    
			    String diaCama = "";
			    try { diaCama = (String) mapaDiaCama.get( item.getPensionadoOSalaComun().toString() ); }
			    catch (Exception e) { }

			    String cobro = "";
			    try { cobro = (String) mapaCobro.get( item.getCodProfesional().toString() ); }
			    catch (Exception e) { }

				if ("1".equals(item.getCodTipoPrestacion().toString())){
					cobroPabellon = "";
					diaCama = "";
					cobro = "";
				}
%>

		<tr class="<%=clase%>">
			<td style="text-align:left;font-size:x-small"><%= item.getCodPrestacion() %></td>
			<td style="text-align:left;font-size:x-small"><%= item.getNombrePrestacion() %></td>

			<td style="font-size:x-small"><%= tipoPrestacion %></td>
			<td style="font-size:x-small"><%= fechaAtencion %></td>
			<td style="font-size:x-small"><%= cantidad %></td>

			<% if (hayPrestacionesHospitalarias) { %>
			<!-- <td style="font-size:x-small"><%= pabellon %></td> -->
			<td style="font-size:x-small"><%= cobroPabellon %></td>
			<td style="font-size:x-small"><%= diaCama %></td>
			<td style="font-size:x-small"><%= cobro %></td>
			<% } %>

			<td style="text-align:right;font-size:x-small"><%= item.getValorAporteDipreca() %></td>
			<td style="text-align:right;font-size:x-small"><%= item.getValorAporteSeguro() %></td>
			<td style="text-align:right;font-size:x-small"><%= item.getValorCopago().intValue() %></td>
			<td style="text-align:left;font-size:x-small"><input type="button" value="eliminar" onClick="eliminar(<%= bono.getFolio() %>, '<%= item.getNombrePrestacion().trim() %>', <%= item.getCodPrestacion() %>)"></td>
		</tr>

<%
		}
%>

	<!-- boton para grabar el detalle del bono -->
		<tr class="fila-detalle-impar">
			<td style="text-align:right" colspan="<%= hayPrestacionesHospitalarias?"13":"9" %>">
				<input type="button" value="Grabar Bono" onClick="grabar(<%= bono.getFolio() %>)">
			</td>

	</table>
	
<%
	}
%>


<%  if (true || prestacionesGenericas.size() == 0) { %>

	<a name="busqueda"><h1>B&uacute;squeda de Prestaciones</h1></a>
	
	<table id="listado">
		<tr class="fila-detalle-impar">
			<td>
				<form name="formulario" method="post" action="DetallarBonos#busqueda">
				<input type="hidden" name="folio" value="<%= bono.getFolio() %>">
				<input type="hidden" name="accion" value="procesarArchivo">
				Buscar: 
				<input type="text" name="buscar" size="20" value="<%= request.getParameterMap().containsKey("buscar")?request.getParameter("buscar"):"" %>">

				&nbsp;&nbsp;&nbsp;&nbsp;
				<select name="opbuscar">
					<option value="codigo">por c&oacute;digo</option>
					<option value="nombre" <%= "nombre".equals(request.getParameter("opbuscar"))?"selected":"" %>>por nombre</option>
				</select>
				
				<br>
				Fecha de la prestaci&oacute;n:
				<input type="text" name="fecha" size="10" maxlength="10" value="<%= request.getParameter("fecha")==null?sdf.format(new Date()):request.getParameter("fecha") %>">
				
				<br>
				<input type="submit" value="Buscar Prestaciones">
				</form>
			</td>
		</tr>
	</table>
		
		
		<%
		if ( prestacionesEncontradas != null ) {
		  if ( prestacionesEncontradas.size() == 0) {
		%>
	<table id="listado">
		<tr class="fila-detalle-impar">
			<td class="fila-detalle-impar">No se han encontrado prestaciones</td>
		</tr>
	</table>
		<% } else { %>


	<div id="prestacionesEncontradas">
	<table id="listado">
		<tr class="encabezados-tabla">
			<td colspan="3">Prestaciones encontradas</td>
		</tr>
		<tr class="encabezados-tabla">
			<td>C&oacute;digo</td>
			<td>Nombre</td>
			<td></td>
		</tr>
		
		<%
			for (int i=0; i<prestacionesEncontradas.size(); i++) {
			   String[] fila = (String[]) prestacionesEncontradas.get(i);
			    // Determino un string que se alterna para cambiar la grafica de las filas
			    String clase = (i%2 == 0)? "fila-detalle-par":"fila-detalle-impar";
			    
			    String codigo = fila[0];
			    String nombre = fila[1];
			    Integer iPabellon = (Integer) mapaCirugias.get(codigo);
			    int codPabellon = 0;
			    
			    if (iPabellon != null) { codPabellon = iPabellon.intValue(); }
		%>

		<tr class="<%=clase%>">
			<td><%= codigo %></td>
			<td><%= nombre %></td>
			<td><input type="button" value="agregar" onClick="mostrarAgregar(<%= fila[0] %>, '<%= fila[1] %>', <%= bono.getFolio() %>, <%= codPabellon %>)"></td>
		</tr>
		<%
			}
		  }
		} 
		%>
		
	</table>
	</div>
	
	<div id="divAgregar" style="display:none">
		<form name="formAgregar" method="post" action="DetallarBonos#prestaciones">
		<input type="hidden" name="accion" value="agregar">
		<input type="hidden" name="codigo">
		<input type="hidden" name="folio">
		
		<table id="listado">

			<tr class="encabezados-tabla">
				<td colspan="2"><span id="spanTituloAgregar"></span></td>
			</tr>

			<tr class="fila-detalle-par">
				<td>Tipo de prestaci&oacute;n</td>
				<td style="text-align:left">
					<select name="tipoPrestacion" onChange="cambioTipoPrestacion()">
<%
  if ( 2 == ((PrestacionGenericaDTO)(prestacionesGenericas.get(0))).getCodigo() ) {
%>					
					<option value="2">Hospitalaria</option>
<%
  } else {
%>					
					<option value="1">Ambulatoria</option>
					<option value="7">Cirugia Ambulatoria</option>
<%
  }
%>					
					</select>
				</td>
			</tr>
		
			<tr class="fila-detalle-par" style="display:none">
				<td>Fecha de atenci&oacute;n</td>
				<td style="text-align:left">
				<span id="span-fecha"></span>
				<input type="hidden" id="fecha" name="fecha" size="10" class="input" value="<%= request.getParameter("fecha")==null?sdf.format(new Date()):request.getParameter("fecha") %>">
				
				<img src="img/calendar.gif" id="f_trigger_c"
				     style="cursor: pointer; border: 1px solid green;"
				     title="Seleccion de fecha"
				     onmouseover="this.style.background='green';"
				     onmouseout="this.style.background=''" />
				<script type="text/javascript">
				    Calendar.setup({
				        displayArea    :    "span-fecha",
				        inputField     :    "fecha",
				        daFormat       :    "%d/%m/%Y",
				        ifFormat       :    "%d/%m/%Y",
				        button         :    "f_trigger_c",
				        align          :    "Tl",
				        weekNumbers    :    false,
				        singleClick    :    true
				    });
				</script>
				
				</td>
			</tr>

			<tr class="fila-detalle-impar" id="datoHospital1" style="display:none">
				<td>N&uacute;mero de pabell&oacute;n</td>
				<td style="text-align:left"><select name="pabellon">
					<option value="1">1</option>
					<option value="2">2</option>
					<option value="3">3</option>
					<option value="4">4</option>
					<option value="5">5</option>
					<option value="6">6</option>
					<option value="7">7</option>
					<option value="8">8</option>
					<option value="9">9</option>
					<option value="10">10</option>
					<option value="11">11</option>
					<option value="12">12</option>
					<option value="13">13*</option>
					<option value="14">14*</option>
					</select>
				</td>
			</tr>

			<tr class="fila-detalle-par">
				<td>Cantidad de atenciones</td>
				<td style="text-align:left"><input type="text" name="cantidad" value="1" size="2" maxlength="2"></td>
			</tr>
		
			<tr class="fila-detalle-impar" id="datoHospital2" style="display:none">
				<td>Cobro de pabell&oacute;n o prestaci&oacute;n</td>
				<td style="text-align:left"><select name="cobroPabellon">
					<option value="0">S&oacute;lo la prestaci&oacute;n</option>
					<option value="1">S&oacute;lo el pabell&oacute;n</option>
					<option value="2">Ambos</option>
					</select>
				</td>
			</tr>
		
			<tr class="fila-detalle-par" id="datoHospital3" style="display:none">
				<td>D&iacute;a cama en sala com&uacute;n o pensionado</td>
				<td style="text-align:left"><select name="salaComun">
					<option value="0">Sala com&uacute;n</option>
					<option value="1">Pensionado</option>
					</select>
				</td>
			</tr>
		
			<tr class="fila-detalle-impar" id="datoHospital4" style="display:none">
				<td>Cobro de atenci&oacute;n en cirug&iacute;a</td>
				<td style="text-align:left"><select name="codProfesional">
					<option value="6">Todos</option>
					<option value="1">Primer cirujano</option>
					<option value="2">Segundo cirujano</option>
					<option value="3">Tercer cirujano</option>
					<option value="4">Anestesista</option>
					<option value="5">Arsenalero</option>
					</select>
				</td>
			</tr>
		
			<tr class="fila-detalle-par">
				<td colspan="2">
					<input type="button" value="Cancelar" onClick="cancelarAgregar()" class="button">
					<input type="button" value="Agregar prestaci&oacute;n" onClick="agregar()" class="submit">
				</td>
			</tr>
		
		
		</table>
		</form>
	</div>

	<br>
	<table id="listado">

		<tr class="encabezados-tabla">
			<td style="text-align:left">Archivo de detalle de prestaciones</td>
		</tr>
		<tr class="fila-detalle-impar">
			<td>
				<form name="formularioArchivo" method="post" action="DetallarBonos#prestaciones" enctype="multipart/form-data">
				
				Use <a href="bono_items.xls">este archivo</a> de Microsoft Excel como 
				planilla para detallar el bono. Las instrucciones para el llenado de
				la planilla se encuentran en <a href="Como_ingresar_el_detalle_de_bonos_web.doc">este documento</a>.<br><br>
				
				Subir archivo CSV con detalle de prestaciones: 
				<input type="file" name="archivo" id="archivo">
				<input type="hidden" name="accion" value="procesarArchivo">
				<input type="hidden" name="folio" value="<%= bono.getFolio() %>"><!-- <%= folio %> -->
				<input type="button" value="Procesar archivo" onClick="procesarArchivo()">
				</form>	
				
				<script language="javascript">
				  function procesarArchivo(){
				    if ((document.getElementById('archivo').value) != "") {
				      document.formularioArchivo.submit();
				    } else {
				      alert("Debe seleccionar un archivo para procesar.");
				    }
				  }
				</script>
				
			</td>
		</tr>
	</table>
<% } %>




    <!-- input type="button" class="button" onClick="document.location='Bonos?accion=crear'" value="Crear un nuevo Bono Abierto" -->
    <!-- input type="button" class="button" onClick="pdf(<%= folio %>)" value="Imprimir Bono" -->

	<p>
	<a href="http://www.adobe.com/products/acrobat/readstep2.html" target="_blank"><img src="img/get_adobe_reader.gif" border="0"></a><br>
	Para imprimir bonos, requiere tener instalado el programa <a href="http://www.adobe.com/products/acrobat/readstep2.html">Acrobat Reader</a> (gratuito).

	</div>


	<script language="javascript">
	
	  function cambioTipoPrestacion(){
	  	if ( document.formAgregar.tipoPrestacion.selectedIndex == 0){
	  		document.getElementById("datoHospital1").style.display = "none";
	  		//document.getElementById("datoHospital2").style.display = "none";
	  		//document.getElementById("datoHospital3").style.display = "none";
	  		//document.getElementById("datoHospital4").style.display = "none";
	  	} else {
	  		document.getElementById("datoHospital1").style.display = "none";
	  		//document.getElementById("datoHospital2").style.display = "";
	  		//document.getElementById("datoHospital3").style.display = "";
	  		//document.getElementById("datoHospital4").style.display = "";
	  	}
	  }

	  function validarCantidad(campo){
	      if (!CampoEsNumeroEnRango(campo, 1, 99)){
	        alert("Ingrese un numero valido para la cantidad de atenciones.");
	        campo.focus();
	      }
	  }
	  	
	  function grabar(folioBono){
	  
	    if (confirm('Confirme que desea grabar este bono.')){
			document.formAgregar.accion.value = "guardar";
			document.formAgregar.folio.value = folioBono;
			document.formAgregar.submit();
	    }
	  }
	
	  function pdf (idFolio ){
	    document.formulario.folio.value = idFolio;
	    document.formulario.submit();
	  }

		function mostrarAgregar(codigo, nombre, folioBono, codPabellon){
		    // oculto el listado, despliego el formulario para el detalle
		    document.getElementById("prestacionesEncontradas").style.display = "none";
		    document.getElementById("divAgregar").style.display = "";

			// coloco el codigo y nombre de la prestacion a agregar en el titulo del formulario
			// y copio los valores del codigo y folio en el formulario
			
			if (codPabellon == 0){
			  // no tiene codigo de pabellon, no se selecciona "quien cobra"
			  document.getElementById("datoHospital2").style.display = "none";
			  document.getElementById("datoHospital4").style.display = "none";
			} else {
			  document.getElementById("datoHospital2").style.display = "";
			  document.getElementById("datoHospital4").style.display = "";
			}
			
			// si el codigo de prestacion no es de la familia '02' no se
			// ingresa si es sala comun o pensionado
			
			if ((Math.floor(codigo/100000) == 2)) {
			  document.getElementById("datoHospital3").style.display = "";
			} else {
			  document.getElementById("datoHospital3").style.display = "none";
			}

			document.getElementById("spanTituloAgregar").innerHTML = "Agregando prestaci&oacute;n '" + nombre + "' (" + codigo + ")";
			document.formAgregar.codigo.value = codigo;
			document.formAgregar.folio.value = folioBono;
			//document.formAgregar.submit();
		}
		
		function cancelarAgregar(){
		    document.getElementById("prestacionesEncontradas").style.display = "";
		    document.getElementById("divAgregar").style.display = "none";
		}
		
		function agregar(){
		  if (document.formAgregar.cantidad){
		      if (!CampoEsNumeroEnRango(document.formAgregar.cantidad, 1, 99)){
		        return false;
		      }
		  }
		  
		  document.formAgregar.submit();
		}

		function eliminar(folioBono, nombre, codigo ){
		
			if (confirm("Confirme que desea eliminar la prestacion '" + nombre + "' del bono")){
				document.formEliminar.codigo.value = codigo;
				document.formEliminar.folio.value = folioBono;
				document.formEliminar.submit();
			}
			
		}

	  // En esta página, si viene la cookie "update", simplemente se consume la cookie
	  // Si no se encuentra la cookie "update", se fuerza un refresco de la pagina
	  //if (!GetCookie('update')){ document.formulario.submit(); } else { DeleteCookie('update'); }

	</script>

<form name="formEliminar" method="post" action="DetallarBonos#prestaciones">
<input type="hidden" name="accion" value="eliminar">
<input type="hidden" name="codigo">
<input type="hidden" name="folio">
</form>

<jsp:include page="pie.jsp" flush="true"/>

