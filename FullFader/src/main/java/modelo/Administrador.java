package modelo;

import modelo.Usuario.Rol;

public class Administrador extends Usuario {

	private int idAdministrador; // Aun sin uso



	public Administrador(int idUsuario, String nombre, String correo, String password, Rol rol) {
		super(idUsuario, nombre, correo, password, rol);
		this.setRol(Rol.ADMINISTRADOR); // Asignando el rol de administrador al crear una instancia de Administrador
	}

	public Administrador(String nombre, String correo, String password, Rol rol) {
		super(nombre, correo, password, rol);
		this.setRol(Rol.ADMINISTRADOR); // Asignando el rol de administrador al crear una instancia de Administrador
	}

	public Administrador(String correo, String password) {
		super(correo, password);
	}


}
