<jsp:include page="cabecera.jsp" flush="true"/>



				<div>
								
						<h3>Obtenci&oacute;n de Bonos Abiertos</h3>

						<div>

						Paso 1 de 2 - Busque y seleccione el nombre de la especialidad, examen o prestaci&oacute;n que busca: <br/>
						(<i>por ejemplo: "pediatria", "dentista", "glicemia" </i>)

						<form name="formulario" method="post" action="">

							<div id="prestaciones_sin" class="destacado">
								Nombre de la especialidad, examen o prestaci&oacute;n:
								&nbsp;<input name="prestacion" value="" size="40" type="text" class="input">
								&nbsp;<input name="search" value="Buscar" type="button" onClick="mostrarPrestaciones()">
							</div>

							<div id="prestaciones_con" class="destacado" style="display:none">

								<table style="listado">
									<tr><td>Nombre de la especialidad, examen o prestaci&oacute;n:</td>
									<td>
								<input name="prestacion" value="" size="40" type="text" class="input">
								&nbsp;<input name="search" value="Volver a buscar" type="button" onClick="ocultarPrestaciones()">
								<br />
									</td></tr>

									<tr><td style="text-align:right">Prestaciones encontradas:</td>
									<td>
										<select size="3" multiple="false" style="width:300px;">
											<option>Pediatria</option>
										</select>
									</td></tr>

									<tr><td style="text-align:right">Prestadores:</td>
									<td>
										<select size="5" multiple="no" style="width:300px;" 
											onChange="document.getElementById('paso2').style.display=''">
											<option selected>Seleccione un Prestador de la lista</option>
											<option>Hospital institucional Dipreca</option>
											<option>Centro Medico Los Alerces</option>
											<option>Centro Medico Megasalud</option>
											<option>. . .</option>
											<option>. . .</option>
											<option>. . .</option>
										</select>
									</td></tr>

								</table>

							</div>

						</form>

								<div id="paso2" style="display:none">
						Paso 2 de 2 - Presione el bot&oacute;n "Crear Bono" para 
						crear un bono para la prestaci&oacute;n y prestador que ha seleccionado: <br/><br/>
						<div style="text-align:center">
						<input type="button" value="  Crear Bono  " onClick="alert('En Construccion')">
						</div>
								</div>

						</div>


						</div>

						<div class="pie-de-pagina">
							Prototipo version 1.4
						</div>

	<script language="javascript">

		function mostrarPrestaciones(){
			document.getElementById("prestaciones_sin").style.display = "none";
			document.getElementById("prestaciones_con").style.display = "";
		}

		function ocultarPrestaciones(){
			document.getElementById("prestaciones_sin").style.display = "";
			document.getElementById("prestaciones_con").style.display = "none";
			document.getElementById('paso2').style.display="none";
		}

		function validar(){

			// validar un usuario y contrase�a no nulos
			if (!document.formulario.login.value || !document.formulario.password.value){
				alert("Debe ingresar un usuario y contrase�a v�lidos para utilizar el sistema.");
				return;
			}

			// usuario beneficiario
			if (document.formulario.login.value == "beneficiario"){
				document.location="consulta-proveedores.html";
			}

			// usuario admin
			if (document.formulario.login.value == "admin"){
				document.location="admin.html";
			}

			// ingreso no valido
			alert("Ha ingresado un usuario o contrase�a no v�lidos. Intente nuevamente.");
			document.formulario.login.value = "";
			document.formulario.password.value = "";
			return;

		}
	</script>
	</body>
</html>
