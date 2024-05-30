package controlador;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import modelo.Track;
import modelo.Usuario;
import modelo.Track.Genero;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.util.ArrayList;

import com.google.gson.Gson;

import dao.DaoTracks;
import dao.DaoUsuarios;

/** 
 * @author Ángel Benítez Izquierdo
 * @version 1.0
 * 
 * Servlet implementation class Tracks
 *  
 * Servlet para manejar operaciones relacionadas con tracks de música.
 */
//@WebServlet("/Tracks") // Define el mapeo del servlet Pero falla si lo descomento
@MultipartConfig
public class Tracks extends HttpServlet {
	private static final long serialVersionUID = 1L;

	// Ruta del directorio donde se almacenarán los archivos de audio
	private String pathFiles = "C:\\Users\\veyro\\eclipse-workspace\\FullFader\\src\\main\\webapp\\archivos";
	private File uploads = new File(pathFiles);

	/**
	 * Constructor por defecto del servlet.
	 */
	public Tracks() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * Método que maneja las solicitudes GET para el servlet Tracks.
	 * @param request Objeto HttpServletRequest que representa la solicitud HTTP recibida.
	 * @param response Objeto HttpServletResponse que representa la respuesta HTTP que se enviará al cliente.
	 * @throws ServletException Si ocurre un error interno en el servlet.
	 * @throws IOException Si ocurre un error de entrada/salida al procesar la solicitud o enviar la respuesta.
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {		
		// Obtiene la parte de la ruta de la URL de la solicitud
		String pathInfo = request.getPathInfo();
		Gson gson = new Gson(); // Inicializa un objeto Gson para convertir objetos Java a JSON

		try {
			if (pathInfo == null || pathInfo.equals("/")) {
				// Obtener todos los Tracks
				// Si la parte de la ruta es nula o vacía, significa que se solicitan todos los tracks
				// Obtiene todos los tracks de la base de datos utilizando el DaoTracks
				ArrayList<Track> track = DaoTracks.getInstance().listarTracks();
				response.setContentType("application/json");
				response.getWriter().println(gson.toJson(track));
			} else {
				// Obtener un usuario por ID
				// Si la parte de la ruta no es nula ni vacía, se espera que sea el ID de un track específico
				// Divide la parte de la ruta en partes utilizando "/" como separador
				String[] pathParts = pathInfo.split("/");
				int id = Integer.parseInt(pathParts[1]);
				Track track = DaoTracks.getInstance().obtenerTrackPorId(id);
				// Obtiene el track correspondiente al ID especificado utilizando el DaoTracks
				if (track != null) {
					// Si se encuentra el track, establece el tipo de contenido de la respuesta como JSON
					response.setContentType("application/json");
					// Convierte el objeto Track a JSON y lo envía como respuesta
					response.getWriter().println(gson.toJson(track));
				} else {
					// Si no se encuentra el track, establece el estado de la respuesta como 404 (No encontrado)
					response.setStatus(HttpServletResponse.SC_NOT_FOUND);
				}
			}
		} catch (SQLException e) {
			// Si ocurre un error SQL, establece el estado de la respuesta como 500 (Error interno del servidor)
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			// Imprime el rastreo de la pila del error en la consola
			e.printStackTrace();
		}
	}

	/**
	 * Método que maneja las solicitudes POST para la inserción de un nuevo track en la base de datos.
	 * 
	 * @param request Objeto HttpServletRequest que contiene la solicitud HTTP
	 * @param response Objeto HttpServletResponse que contiene la respuesta HTTP
	 * @throws ServletException Si ocurre un error durante la solicitud
	 * @throws IOException Si ocurre un error de entrada o salida durante la solicitud
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		boolean valid = true; // Este booleano rastrea si los datos son válidos durante todo el proceso

		// Obtener el parámetro de género
		String generoString = request.getParameter("genero");
		Genero genero = null; // Enum que representa el género de la canción

		// Validar si el género está presente
		if (generoString == null || generoString.isEmpty()) {
			// Si no se proporciona el género, se envía un error y se marca 'valid' como falso
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Género no proporcionado.");
			valid = false;
		} else {
			// Convertir el string del género a un valor enum Genero
			try {
				genero = Genero.valueOf(generoString.toUpperCase());
			} catch (IllegalArgumentException e) {
				// Si el género no es válido, se envía un error y se marca 'valid' como falso
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Género no válido.");
				valid = false;
			}
		}

		// Obtener otros parámetros del formulario
		String titulo = request.getParameter("titulo");
		String sello = request.getParameter("sello");
		int year = 0;
		// Validar el año
		try {
			year = Integer.parseInt(request.getParameter("year"));
		} catch (NumberFormatException e) {
			// Si el año no es válido, se envía un error y se marca 'valid' como falso
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Año inválido.");
			valid = false;
		}
		String duracion = request.getParameter("duracion");

		// Obtener los archivos de la canción y la foto
		Part archivoCancion = null;
		Part archivoFoto = null;
		if (valid) {
			archivoCancion = request.getPart("archivo");
			archivoFoto = request.getPart("portada");
			if (archivoCancion == null || archivoFoto == null) {
				// Si alguno de los archivos no está presente, se envía un error y se marca 'valid' como falso
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Archivo de canción o foto no proporcionado.");
				valid = false;
			}
		}

		// Obtener los nombres de archivo de la canción y la foto
		String nombreArchivoCancion = "";
		String nombreArchivoFoto = "";
		if (valid) {
			nombreArchivoCancion = obtenerNombreArchivoCancion(archivoCancion);
			nombreArchivoFoto = obtenerNombreArchivoFoto(archivoFoto);
			if (nombreArchivoCancion.isEmpty() || nombreArchivoFoto.isEmpty()) {
				// Si los nombres de los archivos son inválidos, se envía un error y se marca 'valid' como falso
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Nombre de archivo inválido.");
				valid = false;
			}
		}

		// Guardar los archivos en el sistema de archivos
		if (valid) {
			try {
				guardarArchivoCancion(archivoCancion.getInputStream(), nombreArchivoCancion);
				guardarArchivoFoto(archivoFoto.getInputStream(), nombreArchivoFoto);
			} catch (IOException e) {
				// Si ocurre un error al guardar los archivos, se envía un error y se marca 'valid' como falso
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error al guardar archivos.");
				valid = false;
			}
		}

		// Insertar el track en la base de datos
		if (valid) {
			try {
				Track t1 = new Track(genero, titulo, sello, year, duracion, nombreArchivoCancion, nombreArchivoFoto);
				t1.insertarTrack();
				// Si la inserción es exitosa, se redirige a listarTracks.html
				response.sendRedirect("listarTracks.html");
			} catch (SQLException e) {
				// Si ocurre un error durante la inserción, se envía un error
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error al insertar datos en la base de datos.");
			}
		}

		// Esta línea es redundante ya que si la inserción es exitosa se redirige antes, pero se mantiene por claridad
		response.getWriter().println("Datos insertados correctamente en la base de datos");
	}


	/**
	 * Método para obtener el nombre del archivo de la canción desde la parte (Part) de la solicitud.
	 * 
	 * @param part La parte (Part) de la solicitud HTTP que contiene el archivo de la canción
	 * @return El nombre del archivo de la canción
	 */
	private String obtenerNombreArchivoCancion(Part part) {
	    String nombreArchivoCancion = null;
	    // Divide los encabezados de la parte en un array utilizando el delimitador ";"
	    String[] headers = part.getHeader("content-disposition").split(";");
	    int i = 0; // Inicializa el índice para recorrer el array
	    // Utiliza un bucle while para recorrer los encabezados
	    // El bucle se detiene cuando se ha encontrado el nombre del archivo (nombreArchivoCancion != null)
	    // o cuando se han revisado todos los encabezados (i < headers.length)
	    while (i < headers.length && nombreArchivoCancion == null) {
	        String content = headers[i]; // Obtiene el encabezado actual
	        // Verifica si el encabezado actual comienza con "filename"
	        if (content.trim().startsWith("filename")) {
	            // Extrae el nombre del archivo desde el encabezado
	            nombreArchivoCancion = content.substring(content.indexOf('=') + 1).trim().replace("\"", "");
	        }
	        i++; // Incrementa el índice para revisar el siguiente encabezado
	    }
	    // Retorna el nombre del archivo encontrado o null si no se encontró ninguno
	    return nombreArchivoCancion;
	}

	/**
	 * Método para obtener el nombre del archivo de la foto desde la parte (Part) de la solicitud.
	 * 
	 * @param part La parte (Part) de la solicitud HTTP que contiene el archivo de la foto
	 * @return El nombre del archivo de la foto
	 */
	private String obtenerNombreArchivoFoto(Part part) {
		String nombreArchivoFoto = null;
	    // Divide los encabezados de la parte en un array utilizando el delimitador ";"
	    String[] headers = part.getHeader("content-disposition").split(";");
	    int i = 0; // Inicializa el índice para recorrer el array
	    // Utiliza un bucle while para recorrer los encabezados
	    // El bucle se detiene cuando se ha encontrado el nombre del archivo (nombreArchivoCancion != null)
	    // o cuando se han revisado todos los encabezados (i < headers.length)
	    while (i < headers.length && nombreArchivoFoto == null) {
	        String content = headers[i]; // Obtiene el encabezado actual
	        // Verifica si el encabezado actual comienza con "filename"
	        if (content.trim().startsWith("filename")) {
	            // Extrae el nombre del archivo desde el encabezado
	            nombreArchivoFoto = content.substring(content.indexOf('=') + 1).trim().replace("\"", "");
	        }
	        i++; // Incrementa el índice para revisar el siguiente encabezado
	    }
	    // Retorna el nombre del archivo encontrado o null si no se encontró ninguno
	    return nombreArchivoFoto;
	}

	//	private String obtenerNombreArchivoFoto(Part part) {
	//	    // Obtiene el encabezado "content-disposition" y lo divide en partes
	//	    String header = part.getHeader("content-disposition");
	//	    
	//	    // Usa streams para filtrar la parte que comienza con "filename"
	//	    return Arrays.stream(header.split(";"))
	//	        .map(String::trim) // Elimina espacios en blanco al inicio y final
	//	        .filter(content -> content.startsWith("filename")) // Filtra para encontrar el contenido que empieza con "filename"
	//	        .map(content -> content.substring(content.indexOf('=') + 1).trim().replace("\"", "")) // Extrae y limpia el nombre del archivo
	//	        .findFirst() // Obtiene el primer resultado que coincida
	//	        .orElse(null); // Retorna null si no se encuentra
	//	}


	/**
	 * Método para guardar el archivo de la canción en el servidor.
	 * 
	 * @param input InputStream que contiene los datos del archivo de la canción
	 * @param nombreArchivoCancion Nombre del archivo de la canción
	 * @throws IOException Si ocurre un error de entrada o salida durante la operación de copia del archivo
	 */
	private void guardarArchivoCancion(InputStream input, String nombreArchivoCancion) throws IOException {
		// Crear un objeto File para el archivo de la canción en la carpeta de uploads
		File file = new File(uploads, nombreArchivoCancion);

		// Copiar los datos del InputStream al archivo en el sistema de archivos
		Files.copy(input, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
	}

	/**
	 * Método para guardar el archivo de la foto en el servidor.
	 * 
	 * @param input InputStream que contiene los datos del archivo de la foto
	 * @param nombreArchivoFoto Nombre del archivo de la foto
	 * @throws IOException Si ocurre un error de entrada o salida durante la operación de copia del archivo
	 */
	private void guardarArchivoFoto(InputStream input, String nombreArchivoFoto) throws IOException {
		// Crear un objeto File para el archivo de la foto en la carpeta de uploads
		File file = new File(uploads, nombreArchivoFoto);

		// Copiar los datos del InputStream al archivo en el sistema de archivos
		Files.copy(input, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
	}

}
