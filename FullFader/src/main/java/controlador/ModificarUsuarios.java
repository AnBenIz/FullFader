package controlador;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import modelo.Track;
import modelo.Usuario;
import modelo.Track.Genero;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

import com.google.gson.Gson;

import dao.DaoTracks;
import dao.DaoUsuarios;

/**
 * Servlet implementation class ModificarUsuarios
 */
public class ModificarUsuarios extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ModificarUsuarios() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	System.out.println("Entra por el GetModificar Usuarios");

    	// Objeto PrintWriter para escribir la respuesta
    	PrintWriter out = response.getWriter();

    	// ID de la pista de audio a obtener
    	int idUsuario = Integer.parseInt(request.getParameter("idUsuarios"));

    	// Objeto Gson para convertir el objeto Track a formato JSON
    	Gson gson = new Gson();

    	// Imprime el ID de la pista de audio en la consola
    	System.out.println("id "+idUsuario);

    	// Intenta obtener la pista de audio por su ID
    	Usuario usuario;
    	try {
    		usuario = DaoUsuarios.getInstance().obtenerUsuarioPorId(idUsuario);
    		if (usuario != null) {
    			// Si se encuentra la pista, establece el tipo de contenido de la respuesta como JSON
    			response.setContentType("application/json");
    			// Convierte la pista de audio a formato JSON y la imprime en la respuesta
    			response.getWriter().println(gson.toJson(idUsuario));
    		} else {
    			// Si no se encuentra la pista, establece el código de estado de la respuesta como NOT FOUND
    			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
    		}
    	} catch (SQLException e) {
    		// Si ocurre una excepción de SQL, imprime la traza de la pila
    		e.printStackTrace();
    	}
    }

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
//	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//		 // Indica si la solicitud es válida
//	    boolean valid = true;
//
//	    // ID de la pista de audio a modificar
//	    int idUsuario = Integer.parseInt(request.getParameter("idUsuarios"));
//
//	    // Género de la pista de audio
//	    String generoString = request.getParameter("genero");
//	    Genero genero = null;
//
//	    // Verifica si se proporcionó el género
//	    if (generoString == null || generoString.isEmpty()) {
//	        response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Género no proporcionado.");
//	        valid = false;
//	    } else {
//	        try {
//	            genero = Genero.valueOf(generoString.toUpperCase());
//	        } catch (IllegalArgumentException e) {
//	            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Género no válido.");
//	            valid = false;
//	        }
//	    }
//
//	    // Título de la pista de audio
//	    String titulo = request.getParameter("titulo");
//
//	    // Sello de la pista de audio
//	    String sello = request.getParameter("sello");
//
//	    // Año de la pista de audio
//	    int year = 0;
//	    try {
//	        year = Integer.parseInt(request.getParameter("year"));
//	    } catch (NumberFormatException e) {
//	        response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Año inválido.");
//	        valid = false;
//	    }
//
//	    // Duración de la pista de audio
//	    String duracion = request.getParameter("duracion");
//
//
//
//
//
//	    // Actualiza la pista de audio en la base de datos si la solicitud es válida
//	    if (valid) {
//	        try {
//	            Track t1 = new Track(idTrack, genero, titulo, sello, year, duracion, nombreArchivoCancion, nombreArchivoFoto); // Añadir rol a futuro
//	            DaoTracks.actualizarTrack(t1);                
//	            response.sendRedirect("listarTracks.html"); // Redirección exitosa al completar la inserción
//	        } catch (SQLException e) {
//	            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error al insertar datos en la base de datos.");
//	        }
//	    }
//
//	}
	
	protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	    System.out.println("Entra en doDelete Modificar");
		// Obtiene el ID del track a eliminar desde los parámetros de la solicitud
	    int idTrack = Integer.parseInt(request.getParameter("idTrack"));
	    
	    try {
	        // Crea un objeto Track con el ID obtenido
	        Track track = new Track(idTrack);
	        
	        // Intenta eliminar el track utilizando el DAO
	        boolean eliminado = DaoTracks.getInstance().eliminarTrack(track);
	        
	        if (eliminado) {
	            // Si la eliminación fue exitosa, devuelve un código de estado 204 (No Content)
	            response.setStatus(HttpServletResponse.SC_NO_CONTENT);
	        } else {
	            // Si no se pudo eliminar el track, devuelve un código de estado 500 (Internal Server Error)
	            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "No se pudo eliminar el track.");
	        }
	    } catch (SQLException e) {
	        // Si ocurre alguna excepción SQL, imprime el mensaje de error en la consola
	        e.printStackTrace();
	        // Devuelve un código de estado 500 (Internal Server Error) y el mensaje de error
	        response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error al eliminar el track: " + e.getMessage());
	    }
	}

}
