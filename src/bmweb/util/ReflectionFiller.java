package bmweb.util;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

/*
 * Created on 10-feb-2006
 *
 */

/**
 * @author denis.fuenzalida
 *
 */
public class ReflectionFiller {
	
	public String nombre;
	public Integer edad;
	public Date fecha;

	public Integer getEdad() { return edad; }
	public void setEdad(Integer edad) { this.edad = edad; }
	public Date getFecha() { return fecha; }
	public void setFecha(Date fecha) { this.fecha = fecha; }
	public String getNombre() { return nombre; }
	public void setNombre(String nombre) { this.nombre = nombre; }
	

	/**
	 * 
	 * Intenta llenar un bean mediante reflection con datos provenientes de un ResultSet
	 * 
	 * Se usa para evitar programacion repetitiva en los DAO que usan MappingSqlQuery
	 * donde se debe hacer el mapeo de las columnas con los datos del bean en la funcion mapRow()
	 * 
	 * @param mapa Un arreglo de strings {"propiedad1", "columna1", "propiedad2", "columna2" ... }
	 * @param rs Un ResultSet proveniente de una base de datos
	 * @param bean El bean que se intenta llenar mediante reflection
	 * @return El bean al que se intento llenar con datos del ResultSet
	 */
	
	public static Object fill(String[] mapa, ResultSet rs, Object bean){
		
		for (int i=0; mapa != null && (i+1<mapa.length); i+=2){
			String propiedad = mapa[i];
			String columna = mapa[i+1];

			try {
				Object dato = rs.getObject(columna);
				
				if (dato != null){
					Class claseDato = dato.getClass();
					String nombreMetodo = "set" + propiedad.substring(0,1).toUpperCase() + propiedad.substring(1);
					
					// para encontrar el metodo
					Method metodo = bean.getClass().getMethod(nombreMetodo, new Class[]{claseDato});
					metodo.invoke(bean, new Object[]{dato});
				}
				
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		
		return bean;
	}
	
	public Object fill(HashMap datos, Object bean){
		
		try {
			Iterator iLlaves = datos.keySet().iterator();
			
			while (iLlaves.hasNext()){
				String llave = (String) iLlaves.next();
				Object dato = datos.get(llave);
				Class claseDato = dato.getClass();
				String nombreMetodo = "set" + llave.substring(0,1).toUpperCase() + llave.substring(1);
				
				// para encontrar el metodo
				Method metodo = bean.getClass().getMethod(nombreMetodo, new Class[]{claseDato});
				metodo.invoke(bean, new Object[]{dato});
			}
			
			return bean;

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
	}
	
	

	public static void main(String[] args) {
	
		ReflectionFiller filler = new ReflectionFiller();
		HashMap datos = new HashMap();
		datos.put("nombre", "Denis Fuenzalida");
		datos.put("fecha", new Date());
		datos.put("edad", new Integer(29));
		
		filler.fill(datos, filler);
		
		System.out.println("Nombre = '" + filler.getNombre() + "'");
		System.out.println("Fecha = '" + filler.getFecha() + "'");
		System.out.println("Edad = '" + filler.getEdad() + "'");
		
	}
}
