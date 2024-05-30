package controlador;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import modelo.Administrador;
import modelo.Cliente;
import modelo.Track;
import modelo.Usuario;
import modelo.Usuario.Rol;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.ArrayList;

import com.google.gson.Gson;

import dao.DaoTracks;
import dao.DaoUsuarios;

/**
 * @author Ángel Benítez Izquierdo
 * @version 1.0
 * 
 * Servlet que gestiona las operaciones relacionadas con los usuarios del sistema.
 */
//@WebServlet("/Usuarios") // Define el mapeo del servlet Pero falla si lo descomento
@MultipartConfig
public class Usuarios extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor por defecto de la clase Usuarios.
	 */
	public Usuarios() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * Maneja las solicitudes GET para obtener información sobre los usuarios.
	 * 
	 * @param request Objeto HttpServletRequest que contiene la solicitud HTTP
	 * @param response Objeto HttpServletResponse que contiene la respuesta HTTP
	 * @throws ServletException Si ocurre un error durante la solicitud
	 * @throws IOException Si ocurre un error de entrada o salida durante la solicitud
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// Objeto Gson para convertir objetos Java en formato JSON
		Gson gson = new Gson();

		String idUsuario = request.getParameter("idUsuarios");
		try {
			if (idUsuario == null) {
				System.out.println("Entra en el if");
				// Obtener todos los usuarios
				ArrayList<Usuario> usuarios = DaoUsuarios.getInstance().listarUsuarios();
				// Establece el tipo de contenido de la respuesta como JSON
				response.setContentType("application/json");
				// Escribe la lista de usuarios como JSON en la respuesta
				response.getWriter().println(gson.toJson(usuarios));
			} else {
				System.out.println("Entra en el else");
				// Obtener un usuario por ID
				int id = Integer.parseInt(idUsuario);
				System.out.println("id del doGet Usuarios "+id);
				Usuario usuario = DaoUsuarios.getInstance().obtenerUsuarioPorId(id);
				if (usuario != null) {
					// Establece el tipo de contenido de la respuesta como JSON
					response.setContentType("application/json");
					// Escribe el usuario encontrado como JSON en la respuesta
					response.getWriter().println(gson.toJson(usuario));
				} else {
					// Si no se encuentra el usuario, establece el código de estado 404 (Not Found)
					response.setStatus(HttpServletResponse.SC_NOT_FOUND);
				}
			}
		} catch (SQLException e) {
			// Si ocurre un error de SQL, establece el código de estado 500 (Internal Server Error)
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			e.printStackTrace(); // Imprime el stack trace del error en la consola
		}

	}

	/**
	 * Maneja las solicitudes POST para crear un nuevo usuario.
	 * 
	 * @param request Objeto HttpServletRequest que contiene la solicitud HTTP
	 * @param response Objeto HttpServletResponse que contiene la respuesta HTTP
	 * @throws ServletException Si ocurre un error durante la solicitud
	 * @throws IOException Si ocurre un error de entrada o salida durante la solicitud
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// Obtener parámetros del formulario de registro		
		String nombre = request.getParameter("nombre");
		String correo = request.getParameter("correo");
		String password = getMD5(request.getParameter("password")); // cifrar la pass
		String rolString = request.getParameter("rol"); // Obtener el rol como una cadena para simplificar

		// Convertir la cadena del rol a un valor del enum Rol
		Rol rol = Rol.CLIENTE; // Iniciar con un valor predeterminado

		try {
			// Intentar convertir la cadena del rol a un valor del enum Rol
			rol = Rol.valueOf(rolString.toUpperCase());  // Convierte el valor introducido por el usuario a MAYUSCULAS
		} catch (IllegalArgumentException | NullPointerException e) {
			// Si ocurre un error al convertir el rol, imprimir el stack trace y enviar un error al cliente
			e.printStackTrace(); // Imprime el rastro de la excepción en la consola
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error al insertar datos en la base de datos.");
			return;
		}

		if (rolString != null && !rolString.isEmpty()) {
			rol = Rol.valueOf(rolString.toUpperCase()); // Convierte el valor introducido por el usuario a MAYUSCULAS
		} else {
			// Asignar un valor predeterminado si no se proporciona un rol válido
			rol = Rol.CLIENTE; // Por ejemplo, asumir que el nuevo usuario es un cliente por defecto
		}

		// Crear una instancia de Administrador o Cliente según el rol
		Usuario usuario;
		if (rol == Rol.ADMINISTRADOR) {
			usuario = new Administrador(nombre, correo, password, rol);
		} else {
			usuario = new Cliente(nombre, correo, password, rol);
		}

		try {
			// Insertar el usuario en la base de datos
			usuario.insertarUsuario();
		} catch (SQLException e) {
			// Si ocurre un error de SQL, imprimir el stack trace
			e.printStackTrace();
		}
		// Realizar acciones adicionales, como guardar en la base de datos

		// Redirigir a una página de confirmación o inicio de sesión
		response.getWriter().println("Datos insertados correctamente en la base de datos.");
		response.sendRedirect("listarUsuarios.html");

	}

	/**
	 * Método para manejar las solicitudes HTTP DELETE para eliminar un usuario de la base de datos.
	 * 
	 * @param request  El objeto HttpServletRequest que contiene la información de la solicitud.
	 * @param response El objeto HttpServletResponse que se utilizará para enviar la respuesta.
	 * @throws ServletException Si ocurre un error general de servlet.
	 * @throws IOException      Si ocurre un error de entrada/salida al procesar la solicitud o enviar la respuesta.
	 */
	protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	    // Imprimir un mensaje para indicar que la solicitud ha ingresado al método
	    System.out.println("Entra en doDelete Usuarios");
	    
	    // Obtener el ID del usuario a eliminar desde los parámetros de la solicitud
	    int idUsuario = Integer.parseInt(request.getParameter("idUsuarios"));

	    try {
	        // Crear un objeto Usuario con el ID obtenido
	        Usuario usuario = new Usuario(idUsuario);

	        // Intentar eliminar el usuario utilizando el DAO
	        boolean eliminado = DaoUsuarios.getInstance().eliminarUsuario(usuario);

	        if (eliminado) {
	            // Si la eliminación fue exitosa, establecer el código de estado 204 (No Content)
	            response.setStatus(HttpServletResponse.SC_NO_CONTENT);
	        } else {
	            // Si no se pudo eliminar el usuario, enviar un error interno del servidor
	            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "No se pudo eliminar al usuario.");
	        }
	    } catch (SQLException e) {
	        // Si ocurre alguna excepción SQL, imprimir el mensaje de error en la consola
	        e.printStackTrace();
	        // Enviar un error interno del servidor junto con el mensaje de error
	        response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error al eliminar el usuario: " + e.getMessage());
	    }
	}


	/**
	 * Método para manejar las solicitudes HTTP PUT para actualizar un usuario en la base de datos.
	 * 
	 * @param request  El objeto HttpServletRequest que contiene la información de la solicitud.
	 * @param response El objeto HttpServletResponse que se utilizará para enviar la respuesta.
	 * @throws IOException Si ocurre un error de entrada/salida al procesar la solicitud o enviar la respuesta.
	 */
	protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
	    // Obtener los parámetros de la solicitud
	    int idUsuario = Integer.parseInt(request.getParameter("idUsuarios"));
	    String nombre = request.getParameter("nombre");
	    String correo = request.getParameter("correo");
	    
	    // Calcular el hash MD5 de la contraseña
	    String password = getMD5(request.getParameter("password"));
	    
	    // Obtener y validar el rol del usuario
	    String rolString = request.getParameter("rol");
	    Rol rol = null;
	    if (rolString == null || rolString.isEmpty()) {
	        // Si el rol no se proporciona, enviar un error de solicitud incorrecta
	        response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Rol no proporcionado.");
	        return;
	    } else {
	        // Convertir el string del rol a un valor de la enumeración Rol
	        rol = Rol.valueOf(rolString.toUpperCase());
	    }

	    // Crear un objeto Usuario con los datos proporcionados
	    Usuario u1 = new Usuario(idUsuario, nombre, correo, password, rol);
	    
	    try {
	        // Intentar actualizar el usuario en la base de datos
	        DaoUsuarios.actualizarUsuario(u1);
	        
	        // Si la actualización es exitosa, enviar un mensaje de éxito
	        response.getWriter().println("Datos insertados correctamente en la base de datos");

	    } catch (SQLException e) {
	        // Si ocurre un error al interactuar con la base de datos, enviar un error interno del servidor
	        response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error al insertar datos en la base de datos.");
	        e.printStackTrace(); // Imprimir la traza para fines de depuración
	    }
	}


	/**
	 * Método para obtener el hash MD5 de una cadena de entrada.
	 * 
	 * @param input La cadena de entrada para la cual se calculará el hash MD5.
	 * @return El hash MD5 como una cadena de 32 caracteres.
	 */
	private String getMD5(String input) {
	    try {
	        // Obtener una instancia de MessageDigest con el algoritmo MD5
	        MessageDigest md = MessageDigest.getInstance("MD5");

	        // Calcular el hash MD5 de la cadena de entrada
	        byte[] messageDigest = md.digest(input.getBytes());

	        // Convertir el arreglo de bytes del hash a una representación hexadecimal
	        BigInteger number = new BigInteger(1, messageDigest);
	        String hashtext = number.toString(16);

	        // Asegurarse de que la cadena de hash tenga una longitud de 32 caracteres
	        while (hashtext.length() < 32) {
	            hashtext = "0" + hashtext; // Agregar ceros a la izquierda si es necesario
	        }
	        return hashtext; // Devolver el hash MD5 como una cadena de 32 caracteres
	    } catch (NoSuchAlgorithmException e) {
	        // Capturar cualquier excepción NoSuchAlgorithmException y relanzarla como RuntimeException
	        // Esto asegura que el método no necesite manejar explícitamente esta excepción
	        throw new RuntimeException(e);
	    }
	}


}
