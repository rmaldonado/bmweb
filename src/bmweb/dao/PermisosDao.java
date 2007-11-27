/*
 * Creado en 23-11-2005 por denis
 *
 */
package bmweb.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.object.MappingSqlQuery;

import bmweb.util.UsuarioWeb;

/**
 * 
 * Clase que encapsula la recuperacion de permisos para un usuario
 * 
 * @author denis.fuenzalida
 *
 */
public class PermisosDao implements IPermisosDao {

	HashMap usuariosValidos = new HashMap();
	private DataSource dataSource;

	public void setDataSource(DataSource ds){ this.dataSource = ds; }

	public PermisosDao(){
				
		UsuarioWeb u = new UsuarioWeb("admin");
		u.setNombreCompleto("Administrador Benmed. Web");
		u.agregarPermiso("habilitados");
		u.agregarPermiso("habilitados.agregar");
		u.agregarPermiso("habilitados.modificar");
		u.agregarPermiso("habilitados.eliminar");
		u.agregarPermiso("bonos");
		
		// NUEVOS PERMISOS -- DEMO - 20051123
		u.agregarPermiso("/HABILITADOS");
		u.agregarPermiso("/HABILITADOS.CREAR");
		u.agregarPermiso("/HABILITADOS.EDITAR");
		u.agregarPermiso("/HABILITADOS.INSERTAR");
		u.agregarPermiso("/HABILITADOS.MODIFICAR");
		u.agregarPermiso("/HABILITADOS.ELIMINAR");
		u.agregarPermiso("/HABILITADOS.REVISAR");
		u.agregarPermiso("/HABILITADOS.ACTIVAR");
		u.agregarPermiso("/HABILITADOS.DESACTIVAR");
		u.agregarPermiso("/HABILITADOS.LISTADO");
		u.agregarPermiso("/BONOS");
		u.agregarPermiso("/BONOS.DETALLE");
		u.agregarPermiso("/BONOS.CREAR");
		u.agregarPermiso("/BONOS.REVISARCMC");
		u.agregarPermiso("/BONOS.INSERTAR");
		u.agregarPermiso("/BONOS.LISTADO");
		u.agregarPermiso("/BONOS.BUSCARBENEFICIARIO");
		u.agregarPermiso("/BONOVALORADO");
		u.agregarPermiso("/BONOVALORADO.CREAR");
		u.agregarPermiso("/BONOVALORADO.INSERTAR");
		u.agregarPermiso("/BONOVALORADO.CAMBIARPRESTACIONES");
		u.agregarPermiso("/BONOVALORADO.BUSCAR");
		u.agregarPermiso("/BONOVALORADO.AGREGAR");
		u.agregarPermiso("/BONOVALORADO.ELIMINAR");
		u.agregarPermiso("/BONOVALORADO.CAMBIARCANTIDADES");
		u.agregarPermiso("/BONOVALORADO.BUSCARPRESTADORES");
		u.agregarPermiso("/BONOVALORADO.LISTADO");
		u.agregarPermiso("/BONOPDF.PDF");
		u.agregarPermiso("/BONOVALORADOPDF.PDF");

		u.agregarPermiso("/FACTURA");
		u.agregarPermiso("/FACTURA.CREAR");

		usuariosValidos.put(u.getNombreUsuario(), u);

		u = new UsuarioWeb("leer");
		u.setNombreCompleto("usuario solo permiso lectura");
		u.agregarPermiso("/HABILITADOS");
		u.agregarPermiso("/HABILITADOS.LISTADO");
		u.agregarPermiso("/BONOS");
		u.agregarPermiso("/BONOS.LISTADO");
		u.agregarPermiso("/BONOS.DETALLE");
		u.agregarPermiso("/BONOS.BUSCARBENEFICIARIO");
		u.agregarPermiso("/BONOPDF.PDF");
		usuariosValidos.put(u.getNombreUsuario(), u);


		u = new UsuarioWeb("13496019");
		u.setNombreCompleto("usuario bonos no valorados");
		u.agregarPermiso("/BONOS");
		u.agregarPermiso("/BONOS.DETALLE");
		u.agregarPermiso("/BONOS.CREAR");
		u.agregarPermiso("/BONOS.REVISARCMC");
		u.agregarPermiso("/BONOS.INSERTAR");
		u.agregarPermiso("/BONOS.LISTADO");
		u.agregarPermiso("/BONOS.BUSCARBENEFICIARIO");
		u.agregarPermiso("/BONOPDF.PDF");
		usuariosValidos.put(u.getNombreUsuario(), u);
		
		// Usuario de pruebas con reglas de validacion para las atenciones
		
		u = new UsuarioWeb("demo");
		u.setNombreCompleto("Usuario de pruebas de reglas");
		u.agregarPermiso("/BONOVALORADO");
		u.agregarPermiso("/BONOVALORADO.CREAR");
		u.agregarPermiso("/BONOVALORADO.INSERTAR");
		u.agregarPermiso("/BONOVALORADO.CAMBIARPRESTACIONES");
		u.agregarPermiso("/BONOVALORADO.BUSCAR");
		u.agregarPermiso("/BONOVALORADO.AGREGAR");
		u.agregarPermiso("/BONOVALORADO.ELIMINAR");
		u.agregarPermiso("/BONOVALORADO.CAMBIARCANTIDADES");
		u.agregarPermiso("/BONOVALORADO.BUSCARPRESTADORES");
		u.agregarPermiso("/BONOPDF.PDF");
		usuariosValidos.put(u.getNombreUsuario(), u);

	}
	
	public UsuarioWeb getUsuarioWeb(String username, String nivel) {
		
		/*
		HibernateTemplate ht = getHibernateTemplate();
		UsuarioWeb u = (UsuarioWeb) ht.execute( new PermisosPorUsuario(username, nivel));
		return u;
		*/
		
		try {
			PermisosPorUsuarioMappingQuery buscaPermisos = new PermisosPorUsuarioMappingQuery(dataSource, nivel);
			List lista = buscaPermisos.execute(); // lista de strings con permisos
			
			if (lista != null && lista.size() != 0){
				UsuarioWeb usuarioEncontrado = new UsuarioWeb(username);
				usuarioEncontrado.setNombreCompleto(username);
				
				// agrego los permisos de la lista al usuario
				for (int i=0; i<lista.size(); i++){
					String nombrePermiso = (String) lista.get(i);
					usuarioEncontrado.agregarPermiso(nombrePermiso.trim());
				}
				
				return usuarioEncontrado;

			} else {
				return null;
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
		

	class PermisosPorUsuarioMappingQuery extends MappingSqlQuery {

		public PermisosPorUsuarioMappingQuery(DataSource ds, String nivel) {

			String query = "" +
			" select p.perm_nombre as permiso" +
			" from bmw_perfusuario pu, bmw_permperf pp, bmw_permiso p" +
			" where pu.usua_id = '" + nivel + "'" +
			" and pu.perf_id = pp.perf_id" +
			" and pp.perm_id = p.perm_id";
			
			setDataSource(ds);
			setSql(query);
			compile();
		}
		
		protected Object mapRow(ResultSet rs, int rowNumber) throws SQLException {
			try { return (String) rs.getObject("permiso"); } 
			catch (Exception e) { return null;  }
		}
	}
	
}
