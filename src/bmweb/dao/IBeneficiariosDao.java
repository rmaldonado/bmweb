/*
 * Creado en 23-10-2005 por denis
 *
 */
package bmweb.dao;

import bmweb.dto.BeneficiarioDTO;
import bmweb.dto.RolbeneDTO;
import bmweb.util.UsuarioWeb;

/**
 * @author denis.fuenzalida
 */
public interface IBeneficiariosDao {
	public abstract boolean validarCMC(String CMC, UsuarioWeb uw);

	public abstract RolbeneDTO leeRolbene(String CMC, UsuarioWeb uw);

	public abstract BeneficiarioDTO leeBeneficiario(int RUT, UsuarioWeb uw);

	// public abstract RolbeneDTO leeRolbenePorCarne(String carneBeneficiario);
	
	public abstract RolbeneDTO leeRolbenePorRut(int RUT, UsuarioWeb uw);
}