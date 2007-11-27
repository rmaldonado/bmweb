<%@ page import="java.text.*" %>
<%@ page import="java.util.*" %>
<%@ page import="bmweb.dto.*" %>
<%@ page import="bmweb.util.*" %>
<%

  SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");

  HashMap mapaTiposBono = new HashMap();
  mapaTiposBono.put( BonoDTO.TIPOBONO_ABIERTO,		"Bono Abierto");
  mapaTiposBono.put( BonoDTO.TIPOBONO_DIGITADO,		"Bono Digitado");
  mapaTiposBono.put( BonoDTO.TIPOBONO_FACTURADO,	"Bono Facturado");
  mapaTiposBono.put( BonoDTO.TIPOBONO_SINDETALLE,	"Bono Sin Detalle");
  mapaTiposBono.put( BonoDTO.TIPOBONO_VALORADO,		"Bono Valorado");
  mapaTiposBono.put( BonoDTO.TIPOBONO_WEB,			"Bono Internet");

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
  
  if (folio == null){
    folio = bono.getFolio().toString();
  }
  
  String rutImponente = beneficiario2.getBene() + "-" + beneficiario2.getDgv() + "(" + beneficiario2.getNombre()+ " " + beneficiario2.getPat() + " " + beneficiario2.getMat() + ")"; 
  if (beneficiario2.getBene() == 0){
  	rutImponente = "Hay problemas con el registro del Beneficiario. Contacte al administrador.";
  }
  
  String [] nombreCiudad = (String []) request.getAttribute("nombreCiudad");
  String nombreEmisor = nombreCiudad[0];

  UsuarioWeb usuarioWeb = (UsuarioWeb) request.getSession().getAttribute(UsuarioWeb.ATRIBUTO_USUARIO_WEB);
%>
<jsp:include page="cabecera.jsp" flush="true"/>

<div>

	<h1>Detalle de Bono</h1>
	
	<table id="listado">

		<tr class="encabezados-tabla">
			<td colspan="2" style="text-align:left">Beneficiario</td>
		</tr>

		<tr class="fila-detalle-impar">
			<td style="text-align:left">Folio</td><td style="text-align:left"><%= bono.getFolio() %></td>
		</tr>
		<tr class="fila-detalle-par">
			<td style="text-align:left">Tipo de Bono</td><td style="text-align:left"><%= (String)mapaTiposBono.get( bono.getTipoBono() ) %><!-- <%= bono.getTipoBono() %> --></td>
		</tr>
	
		
		<tr class="fila-detalle-par">
			<td style="text-align:left">Emisor</td><td style="text-align:left"><%= nombreEmisor %> <!-- <%= usuarioWeb.getNombreCompleto() %> --></td>
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

		<tr class="fila-detalle-par">
			<td style="width:50%"></td><td style="width:50%"></td>	
		</tr>
	
	</table>

	<div style="font-size:4px">&nbsp;</div>

	<table id="listado">

		<tr class="encabezados-tabla">
			<td style="text-align:left">Detalle de Prestaciones</td>
		</tr>
<% 		if ( "22".equals(usuarioWeb.getNivel()) ){ %>

		<tr class="fila-detalle-impar">
			<td style="text-align:left">No tiene autorizaci�n para ver el detalle</td>
		</tr>
		
<% 		} else { %>

<%
			for (int i=0; i<prestacionesGenericas.size(); i++) {
			    PrestacionGenericaDTO p = (PrestacionGenericaDTO) prestacionesGenericas.get(i);
			    // Determino un string que se alterna para cambiar la grafica de las filas
			    String clase= (i%2 == 0)? "fila-detalle-par":"fila-detalle-impar";
		    		    
%>
		<tr class="<%=clase%>">
			<td style="text-align:left">&nbsp;&nbsp;&nbsp;&nbsp;<%= p.getNombre() %></td>
		</tr>
		
<%
			}
		}
%>

<%  if (prestacionesGenericas.size() == 0) { %>
		<tr class="fila-detalle-impar">
			<td><em>Bono Sin Prestaciones</em></td>
		</tr>
<% } %>

	</table>

	<form name="formulario" action="BonoPDF.pdf" target="_blank"><input type="hidden" name="folio" value=""></form>	

    <!-- input type="button" class="button" onClick="document.location='Bonos?accion=crear'" value="Crear un nuevo Bono Abierto" -->
    <input type="button" class="button" onClick="pdf(<%= folio %>)" value="Imprimir Bono">

	<p>
	<a href="http://www.adobe.com/products/acrobat/readstep2.html" target="_blank"><img src="img/get_adobe_reader.gif" border="0"></a><br>
	Para imprimir bonos, requiere tener instalado el programa <a href="http://www.adobe.com/products/acrobat/readstep2.html">Acrobat Reader</a> (gratuito).

	</div>


	<script language="javascript">
	
	  function pdf (idFolio ){
	    document.formulario.folio.value = idFolio;
	    document.formulario.submit();
	  }



	  // En esta página, si viene la cookie "update", simplemente se consume la cookie
	  // Si no se encuentra la cookie "update", se fuerza un refresco de la pagina
	  if (!GetCookie('update')){ document.formulario.submit(); } else { DeleteCookie('update'); }

	</script>

<jsp:include page="pie.jsp" flush="true"/>

