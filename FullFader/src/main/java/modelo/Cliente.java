package modelo;

import modelo.Usuario.Rol;

public class Cliente extends Usuario {

	private int idCliente; // Aun sin uso

	public Cliente(int idUsuario, String nombre, String correo, String password, Rol rol) {
		super(idUsuario, nombre, correo, password, rol);
		this.setRol(Rol.CLIENTE); // Asignando el rol de administrador al crear una instancia de Administrador
	}

	public Cliente(String nombre, String correo, String password, Rol rol) {
		super(nombre, correo, password, rol);
		this.setRol(Rol.CLIENTE); // Asignando el rol de administrador al crear una instancia de Administrador
	}

	public Cliente(String correo, String password) {
		super(correo, password);
	}

}