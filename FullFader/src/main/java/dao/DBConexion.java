package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * @author Angel Benitez Izquierdo
 * @version 1.0
 * 
 * Clase para gestionar la conexion a la base de datos.
 */
public class DBConexion {

	/**
	 * URL de conexion a la base de datos.
	 */
	public static final String JDBC_URL = "jdbc:mysql://localhost:3306/fullfader";

	/**
	 * Instancia de la conexion a la base de datos.
	 */
	public static Connection instance = null;

	/**
	 * Metodo para obtener una conexion a la base de datos.
	 * 
	 * @return Una conexion a la base de datos
	 * @throws SQLException Si ocurre un error al establecer la conexión a la base de datos
	 */
	public static Connection getConexion () throws SQLException {

		// Verifica si ya hay una instancia de conexión
		if (instance == null) {
			// Opcional: configuracion adicional de la conexion
			Properties props = new Properties();
			props.put("user", "root");
			props.put("password", "");
			props.put("charset", "UTF-8");
			// Fin de props Opcionales.

			// Crea una nueva conexion utilizando la URL JDBC y las propiedades
			instance = DriverManager.getConnection(JDBC_URL, "root", "");
		}
		return instance;
	}

}
