package controlador;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import modelo.Track;
import modelo.Track.Genero;

import java.io.IOException;
import java.sql.SQLException;

import dao.DaoTracks;

/**
 * Servlet implementation class ActualizarTracks
 */
@MultipartConfig
public class ActualizarTracks extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ActualizarTracks() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	    // Obtener los parámetros de la solicitud
	    String idTrackParam = request.getParameter("idTrack");
	    String generoParam = request.getParameter("genero");
	    String tituloParam = request.getParameter("titulo");
	    String selloParam = request.getParameter("sello");
	    String yearParam = request.getParameter("year");
	    String duracionParam = request.getParameter("duracion");
	    String archivoParam = request.getParameter("archivo");
	    String portadaParam = request.getParameter("portada");

	    // Convertir los parámetros a los tipos adecuados
	    Integer idTrack = idTrackParam != null ? Integer.valueOf(idTrackParam) : null;
	    Genero genero = Genero.valueOf(generoParam.toUpperCase());
	    int year = Integer.parseInt(yearParam);

	    // Crear un objeto Track con los nuevos datos
	    Track trackActualizado = new Track(idTrack, genero, tituloParam, selloParam, year, duracionParam, archivoParam, portadaParam);

	    // Llamar al método actualizarTrack de DaoTracks
	    DaoTracks daoTracks;
	    try {
	        daoTracks = new DaoTracks();
	        daoTracks.actualizarTrack(trackActualizado);
	        response.sendRedirect("listarTracks.html");
	    } catch (SQLException e) {
	        e.printStackTrace();
	        // Manejar el error de actualización
	        // Redirigir a una página de error o mostrar un mensaje al usuario
	    }
	}




}
