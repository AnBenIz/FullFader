package controlador;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import modelo.Usuario;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;

import dao.DaoUsuarios;

/**
 * @author Ángel Benítez Izquierdo
 * @version 1.0
 * 
 * Servlet implementation class Verificar
 */
@WebServlet("/FullFader")
public class Verificar extends HttpServlet {
	private static final long serialVersionUID = 1L;
	HttpSession sesion;   

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public Verificar() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * Maneja las solicitudes HTTP GET.
	 * 
	 * @param request  El objeto HttpServletRequest que contiene la información de la solicitud.
	 * @param response El objeto HttpServletResponse que se utilizará para enviar la respuesta.
	 * @throws ServletException Si ocurre un error general de servlet.
	 * @throws IOException      Si ocurre un error de entrada/salida al procesar la solicitud o enviar la respuesta.
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	    // Verificar si hay un parámetro "logout" en la URL
	    String logoutParam = request.getParameter("logout");
	    if (logoutParam != null && logoutParam.equals("true")) {
	        // Si se encuentra el parámetro "logout", cerrar la sesión y redirigir al usuario a la página de inicio de sesión
	        HttpSession session = request.getSession(false);
	        if (session != null) {
	            session.invalidate(); // Invalidar la sesión
	        }
	        response.sendRedirect("index.html");
	        return; // Detener la ejecución del método para evitar que se procese la solicitud GET normal
	    }

	    // Si no se encontró el parámetro "logout", continuar con el manejo normal de la solicitud GET
	    // Obtener el nombre de usuario de la sesión
	    String nombre = (String) request.getSession().getAttribute("nombre");

	    // Configurar la respuesta para devolver el nombre de usuario
	    response.setContentType("text/plain"); // Establecer el tipo de contenido de la respuesta como texto plano
	    response.setCharacterEncoding("UTF-8"); // Establecer la codificación de caracteres de la respuesta como UTF-8
	    if (nombre != null) {
	        response.getWriter().write(nombre); // Escribir el nombre de usuario en la respuesta si está presente en la sesión
	    } else {
	        response.getWriter().write(""); // Si nombre es null, escribir una cadena vacía en la respuesta
	    }
	}



	/**
	 * Maneja las solicitudes HTTP POST para iniciar sesión de usuario.
	 * 
	 * @param request  El objeto HttpServletRequest que contiene la información de la solicitud.
	 * @param response El objeto HttpServletResponse que se utilizará para enviar la respuesta.
	 * @throws ServletException Si ocurre un error general de servlet.
	 * @throws IOException      Si ocurre un error de entrada/salida al procesar la solicitud o enviar la respuesta.
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	    // Obtener el correo y la contraseña desde el formulario de inicio de sesión
	    String correo = request.getParameter("correo");
	    String password = getMD5(request.getParameter("password")); // Se obtiene el hash MD5 de la contraseña

	    // Crear un objeto Usuario
	    Usuario u = new Usuario();
	    u.setCorreoElectronico(correo); // Establecer el correo electrónico

	    try {
	        // Intentar iniciar sesión con el correo electrónico y la contraseña proporcionados
	        if(u.logeo(password)) { // Si el inicio de sesión es exitoso
	            // Obtener la sesión actual o crear una nueva si no existe
	            HttpSession session = request.getSession();
	            // Establecer los atributos de sesión para el usuario
	            session.setAttribute("idUsuarios", u.getIdUsuario());
	            session.setAttribute("rol", u.getRol());
	            session.setAttribute("nombre", u.getNombre()); // Añadir el nombre para que aparezca en la sesión web
	            // Redirigir a la página de listarTracks.html después de iniciar sesión exitosamente
	            response.sendRedirect("listarTracks.html");
	        } else { // Si el inicio de sesión falla
	            // Redirigir a la página de listarUsuarios.html si las credenciales son incorrectas
	            response.sendRedirect("listarUsuarios.html");
	        }
	    } catch (SQLException e) {
	        // Manejar cualquier excepción SQL
	        e.printStackTrace(); // Imprimir la traza de la pila en caso de error
	    } catch (IOException e) {
	        // Manejar cualquier error de entrada/salida al enviar la respuesta
	        e.printStackTrace(); // Imprimir la traza de la pila en caso de error
	    }
	}


	/**
	 * Método para calcular el hash MD5 de una cadena de entrada.
	 * 
	 * @param input La cadena de entrada para la cual se calculará el hash MD5.
	 * @return El hash MD5 de la cadena de entrada como una cadena hexadecimal.
	 */
	private String getMD5(String input) {
	    // Verificar si la entrada es nula
	    if (input == null) {
	        return null; // O alguna lógica apropiada para manejar un valor nulo
	    }

	    try {
	        // Obtener la instancia de MessageDigest para MD5
	        MessageDigest md = MessageDigest.getInstance("MD5");
	        // Calcular el hash MD5 de la entrada
	        byte[] messageDigest = md.digest(input.getBytes(StandardCharsets.UTF_8));
	        // Convertir el arreglo de bytes del hash MD5 a un número grande sin signo
	        BigInteger number = new BigInteger(1, messageDigest);
	        // Convertir el número grande a una cadena hexadecimal
	        String hashtext = number.toString(16);

	        // Asegurar que la cadena hexadecimal tenga una longitud de 32 caracteres
	        while (hashtext.length() < 32) {
	            hashtext = "0" + hashtext;
	        }
	        return hashtext; // Devolver el hash MD5 como una cadena hexadecimal
	    } catch (NoSuchAlgorithmException e) {
	        // Si ocurre una excepción NoSuchAlgorithmException, lanzar una RuntimeException
	        throw new RuntimeException(e);
	    }
	}

	/**
	 * Método para manejar las solicitudes HTTP DELETE para cerrar la sesión actual y redirigir a la página de inicio de sesión.
	 * 
	 * @param request  El objeto HttpServletRequest que contiene la información de la solicitud.
	 * @param response El objeto HttpServletResponse que se utilizará para enviar la respuesta.
	 * @throws ServletException Si ocurre un error general de servlet.
	 * @throws IOException      Si ocurre un error de entrada/salida al procesar la solicitud o enviar la respuesta.
	 */
	protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	    // Cierra la sesión actual si existe
	    HttpSession session = request.getSession(false); // Obtiene la sesión sin crear una nueva si no existe
	    if (session != null) {
	        session.invalidate(); // Invalida la sesión
	    }
	    // Redirige a la página de inicio de sesión
	    response.sendRedirect("index.html");
	}

}

