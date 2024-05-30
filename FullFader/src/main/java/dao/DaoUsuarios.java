package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;

import modelo.Administrador;
import modelo.Cliente;
import modelo.Track;
import modelo.Usuario;
import modelo.Usuario.Rol;

/**
 * @author Ángel Benítez Izquierdo
 * @version 1.0
 * 
 * Clase que gestiona las operaciones relacionadas con los usuarios en la base de datos.
 */
public class DaoUsuarios {

	private static Connection con = null;
	private static DaoUsuarios instance = null;

	/**
	 * Constructor de la clase DaoUsuarios. Establece la conexión a la base de datos.
	 * 
	 * @throws SQLException Si ocurre un error al establecer la conexión a la base de datos
	 */
	public DaoUsuarios () throws SQLException {
		this.con = DBConexion.getConexion();
	} 	// Al ser un objeto cuando no este en uso se desconectara. Siendo inecesaria la funcion desconectar.


	/**
	 * Obtiene una instancia única de la clase DaoUsuarios.
	 * 
	 * @return Una instancia única de la clase DaoUsuarios
	 * @throws SQLException Si ocurre un error al obtener la instancia
	 */
	public static DaoUsuarios getInstance () throws SQLException { // Metodo que devuelve el Objeto de la misma Clase, con todos sus metodos.
		// Verifica si la instancia es nula (es decir, no se ha creado)
		if (instance == null) {
			// Si la instancia es nula, crea una nueva instancia de DaoUsuarios
			instance = new DaoUsuarios();
		}
		// Devuelve la instancia existente o recién creada de DaoUsuarios
		return instance;
	}

	/**
	 * Inserta un nuevo usuario en la base de datos.
	 * 
	 * @param usuario El usuario a insertar en la base de datos
	 * @throws SQLException Si ocurre un error al insertar el usuario en la base de datos
	 */
	public void insertarUsuario (modelo.Usuario usuario) throws SQLException {
		// Consulta SQL para insertar un nuevo usuario en la base de datos
		String sql = "INSERT INTO usuarios (nombre, correo, password, rol) VALUES (?, ?, ?, ?)";
		// Preparar la declaración SQL
		PreparedStatement ps = con.prepareStatement(sql);

		// Establecer los valores de los parámetros en la consulta SQL
		ps.setString(1, usuario.getNombre());               // Nombre del usuario
		ps.setString(2, usuario.getCorreoElectronico());    // Correo electrónico del usuario
		ps.setString(3, usuario.getPassword());             // Contraseña del usuario
		ps.setString(4, usuario.getRol().name());           // Rol del usuario (convertido a cadena)

		// Ejecutar la consulta SQL para insertar el usuario en la base de datos
		ps.executeUpdate();

		// Cerrar la declaración preparada para liberar recursos
		ps.close();
	}

	/**
	 * Recupera todos los usuarios de la base de datos.
	 * 
	 * @return Una lista de todos los usuarios en la base de datos
	 * @throws SQLException Si ocurre un error al recuperar los usuarios de la base de datos
	 */
	public ArrayList<Usuario> listarUsuarios () throws SQLException {
		// Preparar la consulta SQL para seleccionar todos los usuarios de la tabla
		PreparedStatement ps = con.prepareStatement("SELECT * FROM usuarios");
		// Ejecutar la consulta y obtener el conjunto de resultados
		ResultSet rs = ps.executeQuery();

		// Inicializar la lista de usuarios resultante como nula
		ArrayList<Usuario> result = null;

		// Iterar a través de los resultados del conjunto de resultados
		while (rs.next()) {
			// Si la lista resultante aún no se ha inicializado, crear una nueva instancia
			if (result == null) {
				result = new ArrayList<>();
			}
			// Crear un nuevo objeto Usuario con los datos obtenidos del conjunto de resultados
			Usuario usuario = new Usuario(rs.getInt(1), rs.getString(2), rs.getString(3), 
											rs.getString(4), Rol.valueOf(rs.getString(5)));
			// Agregar el usuario a la lista resultante
			result.add(usuario);
		}

		// Devolver la lista de usuarios
		return result;
	}

	/**
	 * Genera una representación JSON de todos los usuarios en la base de datos.
	 * 
	 * @return Una cadena JSON que contiene información de todos los usuarios
	 * @throws SQLException Si ocurre un error al obtener los usuarios de la base de datos
	 */
	public String dameJson () throws SQLException {
		String json = ""; // Cadena que contendrá el JSON resultante
		Gson gson = new Gson(); // Instancia de Gson para convertir objetos Java a JSON

		// Convertir la lista de usuarios a formato JSON utilizando Gson
		// Llama al método listarUsuarios() para obtener la lista de usuarios
		// y luego convierte esa lista a JSON
		json = gson.toJson(this.listarUsuarios());

		// Devuelve la cadena JSON resultante
		return json;
	}

	/**
	 * Obtiene un usuario de la base de datos utilizando su ID.
	 * 
	 * @param idUsuario El ID del usuario a recuperar
	 * @return El objeto Usuario correspondiente al ID proporcionado, o null si no se encuentra
	 * @throws SQLException Si ocurre un error al obtener el usuario de la base de datos
	 */
	public Usuario obtenerUsuarioPorId(int idUsuario) throws SQLException {
		// Declaración de variables para preparar la consulta SQL y manejar el resultado
		PreparedStatement ps = null;
		ResultSet rs = null;
		Usuario usuario = null; // Variable para almacenar el usuario recuperado

		try {
			// Consulta SQL para obtener un usuario por su ID
			String sql = "SELECT * FROM usuarios WHERE idUsuarios = ?";
			ps = con.prepareStatement(sql); // Preparar la consulta SQL con el ID del usuario
			ps.setInt(1, idUsuario); // Establecer el valor del parámetro en la consulta preparada
			rs = ps.executeQuery(); // Ejecutar la consulta y obtener el resultado

			// Si se encuentra un usuario con el ID proporcionado, crear un objeto Usuario
			if (rs.next()) {
				// Extraer los datos del usuario del resultado de la consulta
				String nombre = rs.getString("nombre");
				String correo = rs.getString("correo");
				String password = rs.getString("password");
				Rol rol = Rol.valueOf(rs.getString("rol")); // Convertir el valor del rol a un objeto Enum Rol

				// Crear un nuevo objeto Usuario con los datos recuperados
				usuario = new Usuario(idUsuario, nombre, correo, password, rol);
			}
		} finally {
			// Cerrar recursos en un bloque finally para garantizar que se cierren incluso si ocurre una excepción
			if (rs != null) {
				try {
					rs.close(); // Cerrar el ResultSet para liberar recursos
				} catch (SQLException e) {
					e.printStackTrace(); // Manejar cualquier excepción al cerrar el ResultSet
				}
			}
			if (ps != null) {
				try {
					ps.close(); // Cerrar el PreparedStatement para liberar recursos
				} catch (SQLException e) {
					e.printStackTrace(); // Manejar cualquier excepción al cerrar el PreparedStatement
				}
			}
		}
		return usuario; // Devolver el objeto Usuario recuperado (puede ser null si no se encuentra ningún usuario con el ID)
	}

	/**
	 * Valida un usuario en la base de datos utilizando su correo electrónico y contraseña.
	 * 
	 * @param correo El correo electrónico del usuario a validar
	 * @param password La contraseña del usuario a validar
	 * @return El objeto Usuario correspondiente al correo electrónico y contraseña proporcionados, o null si no se encuentra
	 * @throws SQLException Si ocurre un error al validar el usuario en la base de datos
	 */
	public static Usuario validarUsuario(String correo, String password) throws SQLException {
		Usuario usuario = null; // Variable para almacenar el usuario validado
		Connection con = null; // Conexión a la base de datos
		PreparedStatement ps = null; // Sentencia preparada para la consulta SQL
		ResultSet rs = null; // Resultado de la consulta

		try {
			con = DBConexion.getConexion(); // Obtener la conexión a la base de datos
			String sql = "SELECT * FROM usuarios WHERE correo = ? AND password = ?"; // Consulta SQL para validar las credenciales
			ps = con.prepareStatement(sql); // Preparar la consulta SQL
			ps.setString(1, correo); // Establecer el valor del primer parámetro en la consulta (correo)
			ps.setString(2, password); // Establecer el valor del segundo parámetro en la consulta (password)

			rs = ps.executeQuery(); // Ejecutar la consulta y obtener el resultado

			// Si se encuentra un usuario con las credenciales proporcionadas
			if (rs.next()) {
				// Crear una instancia de usuario dependiendo del rol obtenido de la base de datos
				Rol rol = Rol.valueOf(rs.getString("rol").toUpperCase()); // Obtener el rol del usuario y convertirlo a enum Rol
				if (rol == Rol.ADMINISTRADOR) {
					usuario = new Administrador(correo, password); // Crear una instancia de Administrador si el rol es ADMINISTRADOR
				} else {
					usuario = new Cliente(correo, password); // Crear una instancia de Cliente si el rol es diferente de ADMINISTRADOR
				}
				// Nota: Suponiendo que tienes constructores adecuados en las clases Administrador y Cliente
			}
		} finally {
			// Cerrar recursos en un bloque finally para garantizar que se cierren incluso si ocurre una excepción
			if (rs != null) try { rs.close(); } catch (SQLException e) { /* Manejo de error */ }
			if (ps != null) try { ps.close(); } catch (SQLException e) { /* Manejo de error */ }
			if (con != null) try { con.close(); } catch (SQLException e) { /* Manejo de error */ }
		}
		return usuario; // Devolver el objeto Usuario validado (puede ser null si las credenciales no son válidas)
	}

	/**
	 * Actualiza los datos de un usuario en la base de datos.
	 * 
	 * @param usuario El objeto Usuario con los datos actualizados.
	 * @throws SQLException Si ocurre un error al ejecutar la consulta SQL.
	 */
	public static void actualizarUsuario(Usuario usuario) throws SQLException {
	    Connection con = null; // Conexión a la base de datos
	    PreparedStatement ps = null; // Sentencia preparada para la consulta SQL

	    try {
	        con = DBConexion.getConexion(); // Obtener la conexión a la base de datos
	        String sql = "UPDATE usuarios SET nombre=?, correo=?, password=?, rol=? WHERE idUsuarios=?"; // Consulta SQL para actualizar los datos del usuario
	        ps = con.prepareStatement(sql); // Preparar la consulta SQL

	        // Establecer los valores de los parámetros en la consulta SQL
	        ps.setString(1, usuario.getNombre()); // Establecer el nombre del usuario
	        ps.setString(2, usuario.getCorreoElectronico()); // Establecer el correo electrónico del usuario
	        ps.setString(3, usuario.getPassword()); // Establecer la contraseña del usuario
	        ps.setString(4, usuario.getRol().name()); // Establecer el rol del usuario (convertido a cadena)
	        ps.setInt(5, usuario.getIdUsuario()); // Utilizar el ID del usuario para la condición WHERE

	        ps.executeUpdate(); // Ejecutar la consulta de actualización
	    } finally {
	        // Cerrar los recursos en un bloque finally para garantizar su liberación
	        if (ps != null) {
	            ps.close(); // Cerrar la sentencia preparada
	        }
	    }
	}


	/**
	 * Elimina un usuario de la base de datos.
	 * 
	 * @param usuario El objeto Usuario a eliminar.
	 * @return true si se eliminó el usuario correctamente, false de lo contrario.
	 * @throws SQLException Si ocurre un error al ejecutar la consulta SQL.
	 */
	public boolean eliminarUsuario(Usuario usuario) throws SQLException {
	    // Definir la consulta SQL para eliminar al usuario por su ID
	    String sql = "DELETE FROM usuarios WHERE idUsuarios = ?";
	    try (PreparedStatement statement = con.prepareStatement(sql)) { // Crear una sentencia preparada con autocierre
	        // Establecer el valor del parámetro ID del usuario a eliminar
	        statement.setInt(1, usuario.getIdUsuario());
	        // Ejecutar la consulta y obtener el número de filas afectadas
	        int affectedRows = statement.executeUpdate();
	        // Devolver true si se eliminó al menos una fila, false de lo contrario
	        return affectedRows > 0;
	    } catch (SQLException e) {
	        // Capturar y manejar cualquier excepción SQL que ocurra durante el proceso
	        // Aquí podrías manejar logs o re-lanzar la excepción para manejarla en niveles más altos.
	        throw e;
	    }
	}


	/**
	 * Realiza el proceso de inicio de sesión del usuario.
	 * 
	 * @param u El objeto Usuario que contiene el correo electrónico.
	 * @param password La contraseña proporcionada por el usuario.
	 * @return El objeto Usuario si la autenticación es exitosa, null de lo contrario.
	 * @throws SQLException Si ocurre un error al ejecutar la consulta SQL.
	 */
	public Usuario logeando(Usuario u, String password) throws SQLException {
	    Usuario aux = null;
	    String sql = "SELECT * FROM usuarios WHERE correo=? AND password=?";

	    try (PreparedStatement ps = con.prepareStatement(sql)) { // Utiliza un bloque try-with-resources para la conexión y el PreparedStatement
	        ps.setString(1, u.getCorreoElectronico()); // Establece el correo electrónico en la consulta
	        ps.setString(2, password); // Establece la contraseña en la consulta

	        try (ResultSet rs = ps.executeQuery()) { // Ejecuta la consulta y utiliza un bloque try-with-resources para el ResultSet
	            if (rs.next()) {
	                // Si se encuentra un registro en el ResultSet, crea un objeto Usuario con los datos obtenidos de la consulta
	                aux = new Usuario(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4), Rol.valueOf(rs.getString(5)));
	            }
	        } catch (SQLException e) {
	            // Captura y maneja la excepción SQLException en el bloque interno
	            e.printStackTrace();
	            throw e; // Opcional: relanza la excepción para que el servlet la maneje
	        }
	    } catch (SQLException e) {
	        // Captura y maneja la excepción SQLException en el bloque externo
	        e.printStackTrace();
	        throw e; // Opcional: relanza la excepción para que el servlet la maneje
	    }

	    return aux; // Devuelve el objeto Usuario obtenido de la consulta
	}



	/**
	 * Verifica si un usuario con el correo electrónico y la contraseña proporcionados existe en la base de datos.
	 * 
	 * @param correo El correo electrónico del usuario.
	 * @param password La contraseña del usuario.
	 * @return true si el usuario existe y las credenciales son correctas, false de lo contrario.
	 */
	public boolean verificarUsuario(String correo, String password) {
	    boolean ingreso = false; // Inicializa la variable de ingreso como falso
	    Connection con = null;
	    PreparedStatement ps = null;
	    ResultSet rs = null;

	    try {
	        // Obtener conexión a la base de datos
	        con = DBConexion.getConexion();

	        // Consultar si existe un usuario con el correo y contraseña proporcionados
	        String sql = "SELECT * FROM usuarios WHERE correo = ? AND password = ?";
	        ps = con.prepareStatement(sql); // Preparar la consulta SQL
	        ps.setString(1, correo); // Establecer el correo electrónico en la consulta
	        ps.setString(2, password); // Establecer la contraseña en la consulta
	        rs = ps.executeQuery(); // Ejecutar la consulta y obtener el ResultSet

	        // Si se encuentra un usuario con las credenciales proporcionadas, establecer ingreso a true
	        if (rs.next()) {
	            ingreso = true;
	        }
	    } catch (SQLException e) {
	        e.printStackTrace(); // Manejar cualquier excepción SQLException imprimiendo la traza de la pila
	    } finally {
	        // Cerrar recursos en un bloque finally para garantizar que se cierren incluso si ocurre una excepción
	        try {
	            if (rs != null) rs.close(); // Cerrar el ResultSet si no es nulo
	            if (ps != null) ps.close(); // Cerrar el PreparedStatement si no es nulo
	        } catch (SQLException e) {
	            e.printStackTrace(); // Manejar cualquier excepción SQLException imprimiendo la traza de la pila
	        }
	    }

	    return ingreso; // Devolver el valor de ingreso (true si el usuario existe y las credenciales son correctas)
	}


}
