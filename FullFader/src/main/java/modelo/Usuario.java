package modelo;

import java.sql.SQLException;

import dao.DaoTracks;
import dao.DaoUsuarios;

/**
 * @author Ángel Benítez Izquierdo
 * @version 1.0
 * 
 * Clase que representa un usuario en el sistema.
 */
public class Usuario {

	private int idUsuario;
	private String nombre;
	private String correoElectronico;
	private String password;
	private Rol rol; // Podra ser "Administrador" o "Cliente"

	/**
	 * Constructor por defecto de la clase Usuario.
	 */
	public Usuario () {

	}

	/**
	 * Constructor de la clase Usuario con todos los atributos.
	 * 
	 * @param idUsuario			- El ID del usuario
	 * @param nombre			- El nombre del usuario
	 * @param correoElectronico	- El correo electrónico del usuario
	 * @param password 			- La contraseña del usuario
	 * @param rol 				- El rol del usuario (ADMINISTRADOR o CLIENTE)
	 */
	public Usuario(int idUsuario, String nombre, String correoElectronico, String password, Rol rol) {
		super();
		this.idUsuario = idUsuario;
		this.nombre = nombre;
		this.correoElectronico = correoElectronico;
		this.password = password;
		this.rol = rol;
	}

	/**
	 * Constructor de la clase Usuario sin el Id del suario.
	 * 
	 * @param nombre			- El nombre del usuario
	 * @param correoElectronico	- El correo electrónico del usuario
	 * @param password 			- La contraseña del usuario
	 * @param rol 				- El rol del usuario (ADMINISTRADOR o CLIENTE)
	 */
	public Usuario(String nombre, String correoElectronico, String password, Rol rol) {
		super();
		this.nombre = nombre;
		this.correoElectronico = correoElectronico;
		this.password = password;
		this.rol = rol;
	}

	/**
	 * Constructor de la clase Usuario para autenticación.
	 * 
	 * @param correoElectronico El correo electrónico del usuario
	 * @param password La contraseña del usuario
	 */	
	public Usuario(String correoElectronico, String password) {
		super();
		this.correoElectronico = correoElectronico;
		this.password = password;
	}

	public Usuario (int idUsuario) {
		super();
		this.idUsuario = idUsuario;
	}

	/**
	 * Enumeración que representa los roles posibles de un usuario.
	 */
	public enum Rol {
		ADMINISTRADOR,
		CLIENTE
	}

	/**
	 * Método para insertar un nuevo usuario en la base de datos.
	 * 
	 * @throws SQLException Si ocurre un error al insertar el usuario en la base de datos
	 */
	public void insertarUsuario () throws SQLException {
		// Llama al método insertarUsuario del DaoUsuarios para insertar este usuario en la base de datos
		DaoUsuarios.getInstance().insertarUsuario(this);
	}

	/**
	 * Método para iniciar sesión de un usuario.
	 * 
	 * Este método verifica las credenciales del usuario utilizando la contraseña proporcionada.
	 * Si las credenciales son correctas, se inicializan las propiedades del usuario con los datos
	 * obtenidos de la base de datos.
	 * 
	 * @param password La contraseña proporcionada por el usuario para iniciar sesión.
	 * @return true si el inicio de sesión es exitoso, false en caso contrario.
	 * @throws SQLException Si ocurre un error durante la operación de la base de datos.
	 */
	public boolean logeo(String password) throws SQLException {

	    boolean ok = false; // Variable para rastrear si el inicio de sesión es exitoso o no

	    DaoUsuarios dao = new DaoUsuarios(); // Crea una instancia de DaoUsuarios para interactuar con la base de datos
	    Usuario aux = dao.logeando(this, password); // Verifica las credenciales del usuario en la base de datos

	    if (aux != null) { // Si se encuentra un usuario con las credenciales proporcionadas
	        ok = true; // Marca el inicio de sesión como exitoso
	        // Inicializa las propiedades del usuario con los datos obtenidos de la base de datos
	        this.setIdUsuario(aux.getIdUsuario());
	        this.setNombre(aux.getNombre());
	        this.setCorreoElectronico(aux.getCorreoElectronico());
	        this.setPassword(aux.getPassword());
	        this.setRol(aux.getRol());
	    }

	    return ok; // Retorna true si el inicio de sesión fue exitoso, false en caso contrario
	}


	public int getIdUsuario() {
		return idUsuario;
	}

	public void setIdUsuario(int idUsuario) {
		this.idUsuario = idUsuario;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getCorreoElectronico() {
		return correoElectronico;
	}

	public void setCorreoElectronico(String correoElectronico) {
		this.correoElectronico = correoElectronico;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Rol getRol() {
		return rol;
	}

	public void setRol(Rol rol) {
		this.rol = rol;
	}

	/**
	 * Devuelve una representación en forma de cadena del objeto Usuario, mostrando sus atributos principales.
	 * 
	 * @return Una cadena que representa el objeto Usuario
	 */
	@Override
	public String toString() {
		return "Usuario [nombre=" + nombre + ", correoElectronico=" + correoElectronico + ", password=" + password
				+ ", rol=" + rol + "]";
	}




}
