<%@ page import="java.text.*" %>
<%@ page import="java.util.*" %>
<%@ page import="bmweb.util.*" %>
<%


  SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

  List filasReporte = new ArrayList();

  if (request.getAttribute("filasReporte") != null){
	  filasReporte = (List) request.getAttribute("filasReporte");
  }

  // coloco el titulo de la pagina
  request.setAttribute("titulo", "Reportes");
%>
<jsp:include page="cabecera.jsp" flush="true"/>

<div>

	<h1>Reporte Estadístico</h1>

	<table id="listado">

		<tr class="encabezados-tabla">
			<td>Especialidad</td>
			<td>Repartici&oacute;n</td>
			<td>Imponente</td>
			<td>Sexo</td>
			<td>Subtotal</td>
		</tr>
<%
		int numFila = 1;
		for (Iterator it = filasReporte.iterator(); it.hasNext();) {
		    HashMap fila = (HashMap) it.next();

		    // Determino un string que se alterna para cambiar la grafica de las filas
		    String clase= (numFila%2 == 0)? "fila-detalle-par":"fila-detalle-impar";
		    numFila++;
		    
		    String especialidad = (String) fila.get("especialidad");
		    Integer reparticion = (Integer) fila.get("reparticion");
		    String imp_carga = (String) fila.get("imp_carga");
		    String sexo = (String) fila.get("sexo");
		    Integer subtotal = (Integer) fila.get("subtotal");		    
%>
		<tr class="<%=clase%>">
			<td><%= especialidad %></td>
			<td><%= reparticion %></td>
			<td><%= imp_carga %></td>
			<td><%= sexo %></td>
			<td><%= subtotal %></td>
		</tr>
		
<%
		}
%>
	</table>

	</div>
	

	<script language="javascript">

	  // En esta pÃ¡gina, si viene la cookie "update", simplemente se consume la cookie
	  // Si no se encuentra la cookie "update", se fuerza un refresco de la pagina
	  if (!GetCookie('update')){ document.formulario.submit(); } else { DeleteCookie('update'); }

	</script>

<jsp:include page="pie.jsp" flush="true"/>

