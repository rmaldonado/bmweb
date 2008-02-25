package bmweb.servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.context.ApplicationContext;

import bmweb.dao.IBeneficiariosDao;
import bmweb.dao.ICiudadDao;
import bmweb.dao.IHabilitadoDao;
import bmweb.dao.IPermisosDao;
import bmweb.dao.IPrestadoresDao;
import bmweb.dto.BeneficiarioDTO;
import bmweb.dto.CiudadDTO;
import bmweb.dto.HabilitadoDTO;
import bmweb.dto.PrestadorDTO;
import bmweb.dto.RolbeneDTO;
import bmweb.util.TextUtil;
import bmweb.util.UsuarioWeb;

/**
 * Clase que realiza la operacion de autenticacion de usuarios en la aplicacion.
 * Crea un objeto UsuarioWeb asociado a la sesion.
 * 
 * @author denis.fuenzalida
 * 
 */
public class LoginServlet extends HttpServlet {

	private ApplicationContext appCtx;
	private IPermisosDao permisosDao;
	private IBeneficiariosDao beneficiariosDao;
	private IHabilitadoDao habilitadoDao;
	private ICiudadDao ciudadDao;
	private IPrestadoresDao prestadoresDao;

	private String[] direccionesValidas;

	private int _codigoSantiago = -100;

	public void init() throws ServletException {
		super.init();
		appCtx = DBServlet.getApplicationContext();
		permisosDao = (IPermisosDao) appCtx.getBean("permisosDao");
		beneficiariosDao = (IBeneficiariosDao) appCtx.getBean("beneficiariosDao");
		habilitadoDao = (IHabilitadoDao) appCtx.getBean("habilitadoDao");
		ciudadDao = (ICiudadDao) appCtx.getBean("ciudadDao");
		prestadoresDao = (IPrestadoresDao) appCtx.getBean("prestadoresDao");

		try {
			StringTokenizer tok = new StringTokenizer(getInitParameter("direccionesAutorizadas"), ",");
			List listaDirecciones = new ArrayList();
			while (tok.hasMoreTokens()){ listaDirecciones.add(tok.nextToken()); }
			direccionesValidas = new String[listaDirecciones.size()];
			for (int i=0; i<listaDirecciones.size();i++){
				direccionesValidas[i] = (String) listaDirecciones.get(i);
			}
			
		} catch (Exception e) {
			direccionesValidas = new String[]{"127.0.0.1"};
			e.printStackTrace();
		}

		getCodigoSantiago();
	}

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		doPost(request, response);

	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		try {

			String usuario = request.getParameter("usuario");
			String password = request.getParameter("password");

			String rutUsuario = usuario.substring(0, usuario.length() - 2);
			String nivelUsuario = usuario.substring(usuario.length() - 2);

			UsuarioWeb usuarioWeb = permisosDao.getUsuarioWeb(rutUsuario, nivelUsuario);

			// TODO Habilitar tabla con usuarios y passwords para uso interno
			// Usuario y password para 'admin'

			if ("admin09".equals(usuario) && !"mefis21".equals(password)) {
				usuarioWeb = null;
			}

			// Filtro por Referer: Deja pasar a usuarios que provengan de unas
			// pocas
			// direcciones web seleccionadas

			// String direccionesValidas[] = new
			// String[]{"llatin","127.0.0.1","dipreca.cl","172.16.1.13","172.16.2.110"
			// };

			String referer = request.getHeader("Referer");
			boolean vieneDireccionValida = false;
			// si el referer es null, no permito que pase
			if (referer == null) {
				usuarioWeb = null;
			} else {

				 for (int i=0; i<direccionesValidas.length; i++){ 
					 if (referer.indexOf(direccionesValidas[i]) > -1){
						 vieneDireccionValida = true;
						 break;
					 }
				 }
			}

			// Si no viene de una direccion autorizada, no lo dejo pasar
			if (!vieneDireccionValida) {
				usuarioWeb = null;
			}

			if (usuarioWeb != null) {

				// le coloco un permiso extra para ir a la pagina de inicio
				usuarioWeb.agregarPermiso("/INICIO");
				usuarioWeb.setRutEmisor(rutUsuario);
				usuarioWeb.setIP(request.getRemoteAddr());
				usuarioWeb.setNivel(nivelUsuario);

				if ("admin09".equals(usuario)) {
					usuarioWeb.setRutEmisor("1");
				}

				// 2006 - Si el nivelUsuario es 00 o 01, busco al usuario en la
				// tabla de beneficiarios
				if ("00,01".indexOf(nivelUsuario) > -1) {

					// Completo los datos personales
					BeneficiarioDTO ben = beneficiariosDao.leeBeneficiario(Integer.parseInt(rutUsuario), usuarioWeb);
					usuarioWeb.setNombreCompleto(ben.getNombre() + " " + ben.getPat() + " " + ben.getMat());

					// Recupero el CMC para restringir los bonos que puede
					// obtener
					RolbeneDTO rolbene = beneficiariosDao.leeRolbenePorRut(Integer.parseInt(rutUsuario), usuarioWeb);
					String CMC = TextUtil.formarCMC(rolbene.getRepart(), rolbene.getImpo(), rolbene.getCorrel());
					usuarioWeb.setCMC(CMC);

					// Nungun imponente se puede sacar bonos en Santiago -- 2006.03.31
					usuarioWeb.setPuedeHacerBonosSantiago(false);
				}

				/* Habilitados */
				if ("22".equals(nivelUsuario)) {

					UsuarioWeb uwTemp = new UsuarioWeb(rutUsuario);
					uwTemp.setNombreUsuario(rutUsuario);
					HabilitadoDTO h = habilitadoDao.getHabilitadoPorCodigo(Integer.parseInt(rutUsuario), uwTemp);

					// Si el habilitado no esta activo, no puede entrar a la
					// aplicacion - 2006.04.21
					if (h.getActivo().equals("N")) {
						response.addCookie(new Cookie("mensaje", "Error: Usuario deshabilitado. No ha ingresado al sistema."));
						RequestDispatcher rd = request.getRequestDispatcher("index.jsp");
						rd.forward(request, response);
						return;
					}

					usuarioWeb.setNombreCompleto(h.getNombre());

					int codSantiago = getCodigoSantiago();

					// Si la ciudad del habilitado NO es Santiago, no puede
					// hacer bonos en Santiago
					if (h.getDom_ciudad().intValue() == codSantiago) {
						usuarioWeb.setPuedeHacerBonosSantiago(true);
					} else {
						usuarioWeb.setPuedeHacerBonosSantiago(false);
					}

				}

				if ("23".equals(nivelUsuario) || "24".equals(nivelUsuario)
						|| "25".equals(nivelUsuario)) {
					UsuarioWeb uwTemp = new UsuarioWeb(rutUsuario);
					uwTemp.setNombreUsuario(rutUsuario);

					PrestadorDTO p = prestadoresDao.prestadorPorRutAux(rutUsuario, uwTemp);
					usuarioWeb.setNombreCompleto(p.getRazonSocial());
					usuarioWeb.setPuedeHacerBonosSantiago(false); // 2006.03.31
				}

				HttpSession sesion = request.getSession(true);
				sesion.setAttribute(UsuarioWeb.ATRIBUTO_USUARIO_WEB, usuarioWeb);

				// Redirijo a una página que me lleva al menu de inicio de la
				// aplicacion, para usuarios validos
				RequestDispatcher rd = request.getRequestDispatcher("irInicio.jsp");
				rd.forward(request, response);

			} else {

				// Anulo la sesion si hay un error de login
				request.getSession().removeAttribute(UsuarioWeb.ATRIBUTO_USUARIO_WEB);
				request.getSession().invalidate();

				response.setCharacterEncoding("ISO-8859-1");
				response.addCookie(new Cookie("mensaje", "Error: Usuario o Contrasena incorrectos. No ha ingresado al sistema."));
				RequestDispatcher rd = request.getRequestDispatcher("index.jsp");
				rd.forward(request, response);

			}
		} catch (Exception ex) {
			response.addCookie(new Cookie("mensaje", "Error: No ha ingresado al sistema."));
			RequestDispatcher rd = request.getRequestDispatcher("index.jsp");
			rd.forward(request, response);
		}
	}

	private int getCodigoSantiago() {

		if (_codigoSantiago > -1) {
			return _codigoSantiago;
		}

		List listaCiudades = ciudadDao.lista();

		for (int i = 0; listaCiudades != null && i < listaCiudades.size(); i++) {
			CiudadDTO ciudadDto = (CiudadDTO) listaCiudades.get(i);

			if ("SANTIAGO".equals(ciudadDto.getNombre().trim())) {
				_codigoSantiago = ciudadDto.getCodigo();
				return ciudadDto.getCodigo();
			}
		}

		return -1;
	}

}
