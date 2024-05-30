package modelo;
import java.sql.SQLException;
//import java.time.Duration;

import dao.DaoTracks;

/**
 * @author Ángel Benítez Izquierdo
 * @version 1.0
 * 
 * Clase que representa un track de música.
 */
public class Track {

	private int idTrack;
	private Genero genero;
	private String	titulo;
	private String	sello;
	private int year;
	//	private Duration duracion; // Lo cambio a Sting para simplificar
	// ejemplo : Duration duracionCancion = Duration.ofMinutes(3).plusSeconds(30);
	private String duracion;
	private String archivo;
	private String portada;

	/**
	 * Constructor por defecto de la clase Track.
	 */
	public Track () {

	}

	/**
	 * Constructor de la clase Track.
	 * 
	 * @param idTrack	- El ID del track.					- Tipo int
	 * @param genero	- El género musical del track.		- Tipo enum
	 * @param titulo	- El título del track.				- Tipo String
	 * @param sello		- El sello del track.				- Tipo String
	 * @param year		- El año de lanzamiento del track.	- Tipo int
	 * @param duracion	- La duración del track.			- Tipo String
	 * @param archivo	- El archivo de audio del track.	- Tipo String
	 * @param portada	- La foto de la portada del track.	- Tipo String
	 */
	public Track(int idTrack, Genero genero, String titulo, String sello, int year, String duracion, String archivo, String portada) {
		super();
		this.idTrack = idTrack;
		this.genero = genero;
		this.titulo = titulo;
		this.sello = sello;
		this.year = year;
		this.duracion = duracion;
		this.archivo = archivo;
		this.portada = portada;
	}

	// Constructor sin el id
	public Track(Genero genero, String titulo, String sello, int year, String duracion, String archivo, String portada) {
		super();
		this.genero = genero;
		this.titulo = titulo;
		this.sello = sello;
		this.year = year;
		this.duracion = duracion;
		this.archivo = archivo;
		this.portada = portada;
	}

	// Constructor sin archivo
	public Track(int idTrack,Genero genero, String titulo, String sello, int year, String duracion, String portada) {
		super();
		this.idTrack = idTrack;
		this.genero = genero;
		this.titulo = titulo;
		this.sello = sello;
		this.year = year;
		this.duracion = duracion;
		this.portada = portada;
	}

	// Constructor solo con el id
	public Track(int idTrack) {
		this.idTrack = idTrack;
	}

	/**
	 * Enumeración que representa los diferentes géneros musicales.
	 */
	public enum Genero {
		TECHNO,
		HOUSE,
		TRANCE,
		DEEP
	}

	/**
	 * Método para insertar un nuevo track en la base de datos.
	 * 
	 * @throws SQLException Si ocurre un error al insertar el track.
	 */
	public void insertarTrack () throws SQLException {

		//		DaoTechno daoTechno = new DaoTechno(); // No es necesario al usar el Patron Singleton
		//		daoTechno.Insertar(this);	

		DaoTracks.getInstance().insertarTrack(this);
	}

	/**
	 * Método para actualizar un track en la base de datos.
	 * 
	 * @param track		- El track a actualizar.
	 * @return `true` si la actualización fue exitosa, `false` de lo contrario.
	 */	
	public boolean actualizarTrack(Track track) {
		try {
			DaoTracks.actualizarTrack(track);
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	public int getIdTrack() {
		return idTrack;
	}
	public void setId(int idTrack) {
		this.idTrack = idTrack;
	}
	public Genero getGenero() {
		return genero;
	}
	public void setGenero(Genero genero) {
		this.genero = genero;
	}
	public String getTitulo() {
		return titulo;
	}
	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}
	public String getSello() {
		return sello;
	}
	public void setSello(String sello) {
		this.sello = sello;
	}
	public int getYear() {
		return year;
	}
	public void setYear(int year) {
		this.year = year;
	}
	public String getDuracion() {
		return duracion;
	}
	public void setDuracion(String duracion) {
		this.duracion = duracion;
	}

	public String getArchivo() {
		return archivo;
	}
	public void setArchivo(String archivo) {
		this.archivo = archivo;
	}

	public String getPortada() {
		return portada;
	}
	public void setPortada(String portada) {
		this.portada = portada;
	}

	/**
	 * Sobrescritura del método `toString` para representar el objeto Track como una cadena de texto.
	 * 
	 * @return Una cadena que representa el objeto Track.
	 */
	@Override
	public String toString() {
		return "Track [idTrack=" + idTrack + ", genero=" + genero + ", titulo=" + titulo + ", sello=" + sello + ", year=" + year
				+ ", duracion=" + duracion + ", archivo=" + archivo + ", portada=" + portada + "]";
	}


}
