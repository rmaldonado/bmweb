<%@ page import="java.util.*" %>
<%@ page import="bmweb.dto.HabilitadoDTO" %>
<%@ page import="bmweb.dto.CiudadDTO" %>

<%
  // registro nuevo?
  boolean nuevo = false;
  if (request.getAttribute("nuevo") != null){ nuevo = true; }

  String ts = "" + (new Date()).getTime();
  
  List listaCiudades = new ArrayList();
  
  if (request.getAttribute("ciudades") != null){
  	listaCiudades = (List) request.getAttribute("ciudades");
  }
  
  if (request.getAttribute("habilitado") != null){
  	HabilitadoDTO h = (HabilitadoDTO) request.getAttribute("habilitado");
%>
<jsp:include page="cabecera.jsp" flush="true"/>

<div>

    <h1>Mantenedor de Habilitados</h1>
    	
	<form name="formulario" method="post" action="Habilitados">
	<input type="hidden" name="ts" value="<%= ts %>">
<%  if (!nuevo){ %>
	<input type="hidden" name="accion" value="modificar">
    <input type="hidden" name="codigo" value="<%= h.getCodigo() %>">
<% } else { %>
	<input type="hidden" name="accion" value="insertar">
<% } %>
	
	<table id="listado">	

<%  if (nuevo){ %>
		<tr class="encabezados-tabla">
			<td colspan="2">Creando Nuevo Habilitado</td>
		</tr>
		<tr class="fila-detalle-impar">
			<td>C&oacute;digo:</td>
			<td id="celda-codigo" style="text-align:left">
			    <iframe name="frame_pregunta" src="" style="width:1px;height:1px;display:none"></iframe>
			    <div id="duplicado" class="error" style="display:none">
			    Este c&oacute;digo ya existe en la base de datos.</div>
				<input type="text" name="codigo" value="<%= (h.getCodigo() == null)? "": ""+h.getCodigo() %>"
				onBlur="revisar_codigo()">
				
				<script language="javascript">
				  function revisar_codigo(){
				    // Deshabilito el boton para grabar
				    document.formulario.btnsubmit.disabled = true;
				    var valorCodigo = document.formulario.codigo.value;
				    
				    // Invoco la accion que revisa los codigos duplicados
				    window.frames[0].location = "Habilitados?accion=revisar&codigo=" + valorCodigo;
				  }
				</script>
			</td>		
		</tr>
<% } else { %>
		<tr class="encabezados-tabla">
			<td colspan="2">Modificando Habilitado #<%= h.getCodigo() %></td>
		</tr>
<% }%>


		<tr class="fila-detalle-par">
			<td>Nombre:</td>
			<td style="text-align:left"><input type="text" name="nombre" size="30" maxlength="30" value="<%= (h.getNombre() == null)? "": h.getNombre().trim() %>"></td>		
		</tr>

		<tr class="fila-detalle-impar">
			<td>Ubicaci&oacute;n:</td>
			<td style="text-align:left"><input type="text" name="ubicacion" size="30" maxlength="30" value="<%= (h.getUbicacion() == null)? "": h.getUbicacion().trim() %>"></td>		
		</tr>

		<tr class="fila-detalle-par">
			<td>Ciudad:</td>
			<td style="text-align:left">
			<!-- input type="text" name="dom_ciudad" value="<%= (h.getDom_ciudad() == null)? "": ""+h.getDom_ciudad()  %>" -->
				<select name="dom_ciudad">
			<%
				for (int i=0; i<listaCiudades.size(); i++){
				CiudadDTO c = (CiudadDTO) listaCiudades.get(i);
				String nombreCiudad = c.getNombre();
				String codigoCiudad = "" + c.getCodigo();
				
				String selected = "";
				if (codigoCiudad != null && h.getDom_ciudad() != null && codigoCiudad.equals(h.getDom_ciudad().toString())){ selected = "selected"; }
				
			%>
				<option value="<%= codigoCiudad  %>" <%= selected  %>><%= nombreCiudad %></option>
			<%
				}
			%>
				</select>		

			</td>		
		</tr>

		<tr class="fila-detalle-impar">
			<td>Direcci&oacute;n:</td>
			<td style="text-align:left"><input type="text" name="direccion" size="35" maxlength="35" value="<%= (h.getDireccion() == null)? "": h.getDireccion().trim() %>"></td>		
		</tr>

		<tr class="fila-detalle-par">
			<td>Responsable:</td>
			<td style="text-align:left"><input type="text" name="responsable" size="35" maxlength="35" value="<%= (h.getResponsable() == null)? "": h.getResponsable().trim() %>"></td>		
		</tr>
		
		<tr class="fila-detalle-impar">
			<td>Estado:</td>
			<td style="text-align:left">
				<select name="activo">
				<option value="<%= HabilitadoDTO.HABILITADO_ACTIVO %>">Activo</option>
				<option value="<%= HabilitadoDTO.HABILITADO_NO_ACTIVO %>" <%= HabilitadoDTO.HABILITADO_ACTIVO.equals(h.getActivo())?"":"selected" %>>No Activo</option>
				</select>
			</td>		
		</tr>
		
		<tr class="fila-detalle-impar">
			<td colspan="2">
				<input type="button" class="button" onClick="document.formulario.accion.value='';document.formulario.submit()" value="Volver al listado">
				<input type="reset"  class="button" value="Restaurar valores originales">
				<input name="btnsubmit" type="button" onClick="validar()" value="Guardar datos" class="submit" <%= (nuevo)?"disabled=\"true\"":"" %>>
			</td>
		</tr>
		
	</table>
	</form>
	
	<script language="javascript">
	  function validar(){
	    if (document.formulario.codigo){
	      if (!CampoEsNumeroEnRango(document.formulario.codigo, 1, 999999999)){
	        return false;
	      }
	    }
	    
	    if (
	    	CampoEsNoNulo(document.formulario.nombre) && 
	    	CampoEsNoNulo(document.formulario.ubicacion) && 
	    	CampoEsNoNulo(document.formulario.direccion) && 
	    	CampoEsNoNulo(document.formulario.responsable)
	    	) {
	      document.formulario.submit();
	    }    
	    
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