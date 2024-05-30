package controlador;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;
import modelo.Track;
import modelo.Track.Genero;
import modelo.Usuario.Rol;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.sql.Statement;
import com.google.gson.Gson;

import dao.DaoTracks;

/**
 * @author Ángel Benítez Izquierdo
 * @version 1.0
 * 
 * Servlet implementation class ModificarTracks
 * Este servlet se utiliza para modificar 
 * las pistas de audio, la portada y todos los datos menos el "idTracks" en la base de datos.
 */
//@WebServlet("/ModificarTracks") // Define el mapeo del servlet Pero falla si lo descomento
@MultipartConfig
public class ModificarTracks extends HttpServlet {
	private static final long serialVersionUID = 1L;
	HttpSession sesion;   

	// Ruta de la carpeta donde se guardan los archivos subidos
	private String pathFiles = "C:\\Users\\veyro\\eclipse-workspace\\FullFader\\src\\main\\webapp\\archivos";
	private File uploads = new File(pathFiles);


	/**
	 * Constructor por defecto del servlet ModificarTracks.
	 */
	public ModificarTracks() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * Método GET utilizado para obtener información de una pista de audio por el idTracks.
	 * 
	 * @param request  El objeto HttpServletRequest que contiene la solicitud del cliente.
	 * @param response El objeto HttpServletResponse que contiene la respuesta que se enviará al cliente.
	 * @throws ServletException Si ocurre un error en la ejecución del servlet.
	 * @throws IOException      Si ocurre un error de entrada/salida al procesar la solicitud o generar la respuesta.
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// Obtiene la sesión actual, si no existe, se crea una nueva
		sesion = request.getSession();

		int idSesion = 0; // Variable para almacenar el ID de usuario de la sesión

		// Verifica si existe el atributo "idUsuarios" en la sesión
		if (sesion.getAttribute("idUsuarios") != null) {
			// Obtiene el ID de usuario de la sesión
			idSesion = (int) sesion.getAttribute("idUsuarios");

			// Obtiene el rol del usuario de la sesión
			Rol sesionRol = (Rol) sesion.getAttribute("rol");

			// Verifica si el rol del usuario es ADMINISTRADOR
			if (sesionRol == Rol.ADMINISTRADOR) {
				// Imprime un mensaje en la consola indicando que ha entrado en la sección de modificación
				System.out.println("Entra por el GetModificar");

				// Objeto PrintWriter para escribir la respuesta
				PrintWriter out = response.getWriter();

				// Obtiene el parámetro "idTrack" de la solicitud y lo convierte a entero
				int idTrack = Integer.parseInt(request.getParameter("idTrack"));

				// Objeto Gson para convertir objetos Java a formato JSON
				Gson gson = new Gson();

				// Imprime el ID de la pista de audio en la consola
				System.out.println("id " + idTrack);

				// Objeto Track para almacenar la pista de audio obtenida
				Track track;
				try {
					// Intenta obtener la pista de audio por su ID utilizando el método obtenerTrackPorId de DaoTracks
					track = DaoTracks.getInstance().obtenerTrackPorId(idTrack);

					if (track != null) {
						// Si se encuentra la pista, establece el tipo de contenido de la respuesta como JSON
						response.setContentType("application/json");

						// Convierte la pista de audio a formato JSON y la escribe en la respuesta
						response.getWriter().println(gson.toJson(track));
					} else {
						// Si no se encuentra la pista, establece el código de estado de la respuesta como NOT FOUND (404)
						response.setStatus(HttpServletResponse.SC_NOT_FOUND);
					}
				} catch (SQLException e) {
					// Si ocurre una excepción de SQL, imprime la traza de la pila
					e.printStackTrace();
				}

			}

		} else {
			// Si no existe el atributo "idUsuarios" en la sesión, redirige al usuario a la página de login
			response.sendRedirect("login.html");
		}
	}

	/**
	 * Método POST utilizado para procesar la solicitud de modificación de una pista de audio.
	 * 
	 * @param request  El objeto HttpServletRequest que contiene la solicitud del cliente.
	 * @param response El objeto HttpServletResponse que contiene la respuesta que se enviará al cliente.
	 * @throws ServletException Si ocurre un error en la ejecución del servlet.
	 * @throws IOException      Si ocurre un error de entrada/salida al procesar la solicitud o generar la respuesta.
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// Obtiene el ID de la pista de audio a modificar desde los parámetros de la solicitud y lo convierte a entero
		int idTrack = Integer.parseInt(request.getParameter("idTrack"));

		// Declaración de la pista de audio actual fuera del bloque try-catch
		Track trackAntiguo = null;

		try {
			// Intenta obtener la pista de audio actual por su ID utilizando el método obtenerTrackPorId de DaoTracks
			trackAntiguo = DaoTracks.getInstance().obtenerTrackPorId(idTrack);
		} catch (SQLException e) {
			// Si ocurre una excepción de SQL, envía un error de estado 500 (Internal Server Error) y un mensaje de error
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error al obtener datos de la base de datos.");
			return; // Sale del método
		}

		// Variable para verificar si se actualizó algún campo de la pista
		boolean updated = false;

		// Obtiene el parámetro "genero" de la solicitud
		String generoString = request.getParameter("genero");
		if (generoString != null && !generoString.isEmpty()) {
			// Si el parámetro "genero" no está vacío, intenta convertirlo al tipo Enum Genero
			Genero genero = null;
			try {
				genero = Genero.valueOf(generoString.toUpperCase());
				// Actualiza el género de la pista y establece la variable updated a true
				trackAntiguo.setGenero(genero);
				updated = true;
			} catch (IllegalArgumentException e) {
				// Si ocurre una excepción de argumento ilegal, envía un error de estado 400 (Bad Request) y un mensaje de error
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Género no válido.");
				return; // Sale del método
			}
		}

		// Obtiene el parámetro "titulo" de la solicitud
		String titulo = request.getParameter("titulo");
		if (titulo != null && !titulo.isEmpty()) {
			// Si el parámetro "titulo" no está vacío, actualiza el título de la pista y establece la variable updated a true
			trackAntiguo.setTitulo(titulo);
			updated = true;
		}

		// Obtiene el parámetro "sello" de la solicitud
		String sello = request.getParameter("sello");
		if (sello != null && !sello.isEmpty()) {
			// Si el parámetro "sello" no está vacío, actualiza el sello de la pista y establece la variable updated a true
			trackAntiguo.setSello(sello);
			updated = true;
		}

		// Obtiene el parámetro "year" de la solicitud
		String yearString = request.getParameter("year");
		if (yearString != null && !yearString.isEmpty()) {
			// Si el parámetro "year" no está vacío, intenta convertirlo a entero
			try {
				int year = Integer.parseInt(yearString);
				// Actualiza el año de la pista y establece la variable updated a true
				trackAntiguo.setYear(year);
				updated = true;
			} catch (NumberFormatException e) {
				// Si ocurre una excepción de formato de número, envía un error de estado 400 (Bad Request) y un mensaje de error
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Año inválido.");
				return; // Sale del método
			}
		}

		// Obtiene el parámetro "duracion" de la solicitud
		String duracion = request.getParameter("duracion");
		if (duracion != null && !duracion.isEmpty()) {
			// Si el parámetro "duracion" no está vacío, actualiza la duración de la pista y establece la variable updated a true
			trackAntiguo.setDuracion(duracion);
			updated = true;
		}

		// Obtiene el archivo de la canción de la solicitud
		Part archivoCancion = request.getPart("archivo");
		if (archivoCancion != null && archivoCancion.getSize() > 0) {
			// Si el archivo de la canción no está vacío, obtiene el nombre del archivo
			String nombreArchivoCancion = obtenerNombreArchivoCancion(archivoCancion);
			if (!nombreArchivoCancion.isEmpty()) {
				try {
					// Guarda el archivo de la canción en el servidor y actualiza el nombre del archivo en la pista
					guardarArchivoCancion(archivoCancion.getInputStream(), nombreArchivoCancion);
					trackAntiguo.setArchivo(nombreArchivoCancion);
					updated = true;
				} catch (IOException e) {
					// Si ocurre una excepción de E/S, envía un error de estado 500 (Internal Server Error) y un mensaje de error
					response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error al guardar archivo de canción.");
					return; // Sale del método
				}
			} else {
				// Si el nombre del archivo de la canción es inválido, envía un error de estado 400 (Bad Request) y un mensaje de error
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Nombre de archivo de canción inválido.");
				return; // Sale del método
			}
		}

		// Obtiene el archivo de la foto de la solicitud
		Part archivoFoto = request.getPart("portada");
		if (archivoFoto != null && archivoFoto.getSize() > 0) {
			// Si el archivo de la foto no está vacío, obtiene el nombre del archivo
			String nombreArchivoFoto = obtenerNombreArchivoFoto(archivoFoto);
			if (!nombreArchivoFoto.isEmpty()) {
				try {
					// Guarda el archivo de la foto en el servidor y actualiza el nombre del archivo en la pista
					guardarArchivoFoto(archivoFoto.getInputStream(), nombreArchivoFoto);
					trackAntiguo.setPortada(nombreArchivoFoto);
					updated = true;
				} catch (IOException e) {
					// Si ocurre una excepción de E/S, envía un error de estado 500 (Internal Server Error) y un mensaje de error
					response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error al guardar archivo de foto.");
					return; // Sale del método
				}
			} else {
				// Si el nombre del archivo de la foto es inválido, envía un error de estado 400 (Bad Request) y un mensaje de error
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Nombre de archivo de foto inválido.");
				return; // Sale del método
			}
		}

		// Si se actualizó algún campo, guardar los cambios en la base de datos
		if (updated) {
			try {
				// Intenta actualizar la pista de audio en la base de datos utilizando el método actualizarTrack de DaoTracks
				DaoTracks.actualizarTrack(trackAntiguo);
				// Redirige al usuario a la página listarTracks.html después de la actualización exitosa
				response.sendRedirect("listarTracks.html");
			} catch (SQLException e) {
				// Si ocurre una excepción de SQL, envía un error de estado 500 (Internal Server Error) y un mensaje de error
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error al actualizar datos en la base de datos.");
			}
		} else {
			// Si no se realizó ninguna actualización, escribe un mensaje en la respuesta
			response.getWriter().println("No se realizó ninguna actualización.");
		}
	}

	/**
	 * Maneja las solicitudes HTTP DELETE para eliminar una pista de la base de datos.
	 * 
	 * Este método procesa una solicitud DELETE para eliminar una pista específica identificada por su ID.
	 * Recupera el ID de la pista de los parámetros de la solicitud, intenta eliminar la pista utilizando 
	 * el objeto de acceso a datos DaoTracks, y establece el estado HTTP de la respuesta apropiado según el resultado.
	 * 
	 * @param request  el objeto HttpServletRequest que contiene la solicitud realizada por el cliente al servlet
	 * @param response el objeto HttpServletResponse que contiene la respuesta que el servlet envía al cliente
	 * @throws ServletException si se detecta un error de entrada o salida cuando el servlet maneja la solicitud
	 * @throws IOException      si no se puede manejar la solicitud DELETE
	 */
	@Override
	protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// Imprime un mensaje en la consola indicando que se ha entrado en el método doDelete
		System.out.println("Entra en doDelete Modificar");

		// Obtiene el ID del track a eliminar desde los parámetros de la solicitud y lo convierte a entero
		int idTrack = Integer.parseInt(request.getParameter("idTrack"));

		try {
			// Crea un objeto Track con el ID obtenido
			Track track = new Track(idTrack);

			// Intenta eliminar el track utilizando el método eliminarTrack del DAO
			boolean eliminado = DaoTracks.getInstance().eliminarTrack(track);

			if (eliminado) {
				// Si la eliminación fue exitosa, establece el código de estado de la respuesta a 204 (No Content)
				response.setStatus(HttpServletResponse.SC_NO_CONTENT);
			} else {
				// Si no se pudo eliminar el track, establece el código de estado de la respuesta a 500 (Internal Server Error)
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "No se pudo eliminar el track.");
			}
		} catch (SQLException e) {
			// Si ocurre alguna excepción SQL, imprime el mensaje de error en la consola
			e.printStackTrace();
			// Establece el código de estado de la respuesta a 500 (Internal Server Error) y envía el mensaje de error
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error al eliminar el track: " + e.getMessage());
		}
	}


	/**
	 * Obtiene el nombre del archivo de la canción a partir del objeto Part proporcionado.
	 * 
	 * @param part El objeto Part que contiene la información del archivo de la canción.
	 * @return El nombre del archivo de la canción.
	 */
	private String obtenerNombreArchivoCancion(Part part) {
	    String nombreArchivoCancion = null;
	    // Itera sobre las cabeceras del objeto Part para encontrar el nombre del archivo
	    for (String content : part.getHeader("content-disposition").split(";")) {
	        if (content.trim().startsWith("filename")) {
	            // Extrae el nombre del archivo de la cabecera y lo limpia de comillas
	            nombreArchivoCancion = content.substring(content.indexOf('=') + 1).trim().replace("\"", "");
	            break;
	        }
	    }
	    return nombreArchivoCancion;
	}

	/**
	 * Obtiene el nombre del archivo de la foto a partir del objeto Part proporcionado.
	 * 
	 * @param part El objeto Part que contiene la información del archivo de la foto.
	 * @return El nombre del archivo de la foto.
	 */
	private String obtenerNombreArchivoFoto(Part part) {
	    String nombreArchivoFoto = null;
	    // Itera sobre las cabeceras del objeto Part para encontrar el nombre del archivo
	    for (String content : part.getHeader("content-disposition").split(";")) {
	        if (content.trim().startsWith("filename")) {
	            // Extrae el nombre del archivo de la cabecera y lo limpia de comillas
	            nombreArchivoFoto = content.substring(content.indexOf('=') + 1).trim().replace("\"", "");
	            break;
	        }
	    }
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
	 * Guarda el archivo de la canción en el servidor.
	 * 
	 * @param input              El flujo de entrada que contiene los datos del archivo de la canción.
	 * @param nombreArchivoCancion El nombre del archivo de la canción.
	 * @throws IOException Si ocurre un error de entrada/salida al intentar guardar el archivo.
	 */
	private void guardarArchivoCancion(InputStream input, String nombreArchivoCancion) throws IOException {
	    // Crea un objeto File con la ubicación y el nombre del archivo de la canción
	    File file = new File(uploads, nombreArchivoCancion);
	    // Copia los datos del flujo de entrada al archivo en el servidor, reemplazando si ya existe un archivo con el mismo nombre
	    Files.copy(input, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
	}

	/**
	 * Guarda el archivo de la foto en el servidor.
	 * 
	 * @param input            El flujo de entrada que contiene los datos del archivo de la foto.
	 * @param nombreArchivoFoto El nombre del archivo de la foto.
	 * @throws IOException Si ocurre un error de entrada/salida al intentar guardar el archivo.
	 */
	private void guardarArchivoFoto(InputStream input, String nombreArchivoFoto) throws IOException {
	    // Crea un objeto File con la ubicación y el nombre del archivo de la foto
	    File file = new File(uploads, nombreArchivoFoto);
	    // Copia los datos del flujo de entrada al archivo en el servidor, reemplazando si ya existe un archivo con el mismo nombre
	    Files.copy(input, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
	}


}
