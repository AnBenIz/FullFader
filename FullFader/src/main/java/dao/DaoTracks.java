package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.google.gson.Gson;

import modelo.Track;
import modelo.Usuario;
import modelo.Track.Genero;
import modelo.Usuario.Rol;

/**
 * @author Angel BenItez Izquierdo
 * @version 1.0
 * 
 * Clase que maneja la conexiOn y operaciones relacionadas con la tabla "tracks" en la base de datos.
 */
public class DaoTracks {

	// Patron Singleton aplicado.
	private static Connection con = null;
	private static DaoTracks instance = null;	// Instancia para devolver el objeto DaoTechno

	/**
	 * Constructor privado de la clase DaoTracks para aplicar el patron Singleton.
	 * 
	 * @throws SQLException Si ocurre un error al establecer la conexión a la base de datos.
	 */
	public DaoTracks () throws SQLException {

		this.con = DBConexion.getConexion();
	} // Al ser un objeto cuando no este en uso se desconectara. Siendo inecesaria la funcion desconectar.

	/**
	 * Metodo estatico que devuelve una instancia de la clase DaoTracks. Implementa el patrón Singleton.
	 * 
	 * @return Una instancia de la clase DaoTracks.
	 * @throws SQLException Si ocurre un error al establecer la conexion a la base de datos.
	 */
	public static DaoTracks getInstance () throws SQLException {// Metodo que devuelve el Objeto de la misma Clase, con todos sus metodos.
		// Verifica si ya existe una instancia de DaoTracks
		if (instance == null) {
			// Si no existe, crea una nueva instancia
			instance = new DaoTracks ();
		}
		// Devuelve la instancia existente o recien creada
		return instance;
	}

	/**
	 * Inserta un nuevo track en la base de datos.
	 * 
	 * @param track El objeto Track que se va a insertar.
	 * @throws SQLException Si ocurre un error al ejecutar la consulta SQL.
	 */
	public void insertarTrack(modelo.Track track) throws SQLException {
		// Sentencia JDBC
		String sql = "INSERT INTO track (genero, titulo, sello, year, duracion, archivo, portada) VALUES (?, ?, ?, ?, ?, ?, ?)";
		PreparedStatement ps = con.prepareStatement(sql);

		// Establecer los valores de los parametros de la sentencia SQL
		ps.setString(1, track.getGenero().name()); // Convertimos el enum Genero a cadena utilizando el método name()
		ps.setString(2, track.getTitulo());
		ps.setString(3, track.getSello());
		ps.setInt(4, track.getYear());
		ps.setString(5, track.getDuracion());

		ps.setString(6, track.getArchivo());
		ps.setString(7, track.getPortada());

		ps.executeUpdate(); // Ejecutar la consulta SQL para insertar el track
		ps.close(); // Cerrar el PreparedStatement para liberar recursos
	}

	/**
	 * Recupera todos los tracks almacenados en la base de datos y los devuelve como una lista.
	 * 
	 * @return Una lista de objetos Track que representa todos los tracks almacenados en la base de datos.
	 * @throws SQLException Si ocurre un error al ejecutar la consulta SQL.
	 */
	public ArrayList<Track> listarTracks () throws SQLException { // Puede ser privado
	    PreparedStatement ps = con.prepareStatement("SELECT * FROM track"); // Preparar la consulta SQL para seleccionar todos los tracks
	    ResultSet rs = ps.executeQuery(); // Ejecutar la consulta y obtener el ResultSet con los resultados

	    ArrayList<Track> result = null; // Inicializar una lista para almacenar los tracks

	    // Iterar sobre los resultados del ResultSet
	    while (rs.next()) {
	        // Si la lista de resultados es nula, crear una nueva lista
	        if (result == null) {
	            result = new ArrayList<>();
	        }
	        // Crear un nuevo objeto Track utilizando los datos del ResultSet y agregarlo a la lista
	        result.add(new Track(rs.getInt(1), Genero.valueOf(rs.getString(2)), rs.getString(3), 
	                             rs.getString(4), rs.getInt(5), rs.getString(6), rs.getString(7), rs.getString(8)));
	    }	
	    return result; // Devolver la lista de tracks
	}

	/**
	 * Metodo que devuelve un objeto JSON que representa una lista de tracks.
	 * @return Una cadena JSON que representa la lista de tracks.
	 * @throws SQLException Si ocurre un error al acceder a la base de datos.
	 */
	public String dameJson () throws SQLException {
		// Inicializa una cadena vacia para almacenar el JSON resultante
		String json = "";
		// Inicializa un objeto Gson para convertir objetos Java a JSON
		Gson gson = new Gson();

		// Convierte la lista de tracks a formato JSON utilizando Gson
		json = gson.toJson(this.listarTracks());

		// Devuelve la cadena JSON resultante
		return json;
	}

	/**
	 * Metodo que obtiene un objeto Track de la base de datos basado en su ID.
	 * @param idTrack El ID del track que se desea obtener.
	 * @return Un objeto Track que corresponde al ID especificado, o null si no se encuentra.
	 * @throws SQLException Si ocurre un error al acceder a la base de datos.
	 */
	public Track obtenerTrackPorId(int idTrack) throws SQLException {
		PreparedStatement ps = null;
		ResultSet rs = null;
		Track track = null;

		 try {
		        // Preparar la consulta SQL para obtener el track por su ID
		        String sql = "SELECT * FROM track WHERE idTrack = ?";
		        ps = con.prepareStatement(sql);
		        ps.setInt(1, idTrack);
		        rs = ps.executeQuery();

		        // Si se encuentra un resultado en el ResultSet, crear un objeto Track
		        if (rs.next()) {
		            // Extraer los datos del ResultSet para construir un objeto Track
		            Genero genero = Genero.valueOf(rs.getString("genero"));
		            String titulo = rs.getString("titulo");
		            String sello = rs.getString("sello");
		            int year = rs.getInt("year");
		            String duracion = rs.getString("duracion");
		            String archivo = rs.getString("archivo");
		            String portada = rs.getString("portada");

		            // Construir el objeto Track con los datos extraídos
		            track = new Track(idTrack, genero, titulo, sello, year, duracion, archivo, portada);
		        }
		    } finally {
		        // Cerrar los recursos en un bloque finally para garantizar su liberacion
		        if (rs != null) {
		            try {
		                rs.close();
		            } catch (SQLException e) {
		                e.printStackTrace();
		            }
		        }
		        if (ps != null) {
		            try {
		                ps.close();
		            } catch (SQLException e) {
		                e.printStackTrace();
		            }
		        }
		    }
		    return track; // Devolver el objeto Track encontrado o null si no se encontro ningun track con ese ID
	}

	/**
	 * Metodo que elimina un track de la base de datos.
	 * @param track El objeto Track que se desea eliminar.
	 * @return true si el track fue eliminado correctamente, false si no.
	 * @throws SQLException Si ocurre un error al acceder a la base de datos.
	 */
	public boolean eliminarTrack(Track track) throws SQLException {
		// Definir la consulta SQL para eliminar el track por su ID
		String sql = "DELETE FROM track WHERE idTrack = ?";
		try (PreparedStatement statement = con.prepareStatement(sql)) {
			// Establecer el valor del parametro ID del track a eliminar
			statement.setInt(1, track.getIdTrack());
			// Ejecutar la consulta y obtener el numero de filas afectadas
			int affectedRows = statement.executeUpdate();
			// Devolver true si se elimino al menos una fila, false de lo contrario
			return affectedRows > 0;
		} catch (SQLException e) {
			// Capturar y manejar cualquier excepcion SQL que ocurra durante el proceso
			throw e;
		}
	}

	/**
	 * Metodo estático que actualiza un track en la base de datos.
	 * @param track El objeto Track que se desea actualizar.
	 * @throws SQLException Si ocurre un error al acceder a la base de datos.
	 */
	public static void actualizarTrack(Track track) throws SQLException {
		Connection con = null;
		PreparedStatement ps = null;

		try {
			con = DBConexion.getConexion(); // Obtener la conexion a la base de datos
			String sql = "UPDATE track SET genero=?, titulo=?, sello=?, year=?, duracion=?, archivo=?, portada=? WHERE idTrack=?";
			ps = con.prepareStatement(sql); // Preparar la consulta SQL
			// Establecer los valores de los parametros en la consulta SQL
			ps.setString(1, track.getGenero().name());
			ps.setString(2, track.getTitulo());
			ps.setString(3, track.getSello());
			ps.setInt(4, track.getYear());
			ps.setString(5, track.getDuracion());
			ps.setString(6, track.getArchivo());
			ps.setString(7, track.getPortada());
			ps.setInt(8, track.getIdTrack()); // Utilizar el ID para la condicion WHERE

			ps.executeUpdate(); // Ejecutar la consulta de actualizacion
		} finally {
			// Cerrar los recursos en un bloque finally para garantizar su liberacion
			if (ps != null) {
				ps.close();
			}
		}
	}

}
