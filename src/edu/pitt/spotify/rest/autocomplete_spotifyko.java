package edu.pitt.spotify.rest;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import edu.pitt.spotify.utils.DbUtilities;
import edu.pitt.spotify.utils.ErrorLogger;

/**
 * Servlet implementation class autocomplete_spotifyko
 * 
 * This servlet defines methods and provides structure used to return data for the autocomplete function of the application.
 * A separate servlet is need because the SQL queries are missing a wildcard compared to the servlet that populates the main table of the application.
 * 
 * @author James Van Poolen
 * @version 1.0
 */
@WebServlet("/api/autocomplete_spotifyko")
public class autocomplete_spotifyko extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public autocomplete_spotifyko() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("application/json");
		String searchTerm;
		String sql = "";
		JSONObject searchResults = new JSONObject();
		final int RESULTS_LIMIT = 40;
		
		HttpSession session = request.getSession(true);
		//session.setAttribute("SEARCH_RESULTS", "");
		
		/*if (session.getAttribute("SEARCH_RESULTS") == null){*/
		
			if(request.getParameter("searchTerm") != null) {
				searchTerm = request.getParameter("searchTerm");
				if(!searchTerm.equals("")) {
					
					try {
						//Autocomplete functionality against the song table
						sql = "SELECT * FROM song WHERE title LIKE '" + searchTerm + "%' LIMIT " + RESULTS_LIMIT + ";";
						//response.getWriter().write(sql);
						JSONArray songList = new JSONArray();
	
						DbUtilities db = new DbUtilities();
						ResultSet rs = db.getResultSet(sql);
						while(rs.next()){
							JSONObject song = new JSONObject();
							song.put("song_id", rs.getString("song_id"));
							song.put("title", rs.getString("title"));
							song.put("release_date", rs.getString("release_date"));
							song.put("record_date", rs.getString("record_date"));
							song.put("length", rs.getInt("length"));
							song.put("file_path", rs.getString("file_path"));
							songList.put(song);
						}
						
						searchResults.put("songs", songList);
						
						
						
						//Autocomplete functionality against the album table
						sql = "SELECT * FROM album WHERE title LIKE '" + searchTerm + "%' LIMIT " + RESULTS_LIMIT + ";";
						//response.getWriter().write(sql);
						JSONArray albumList = new JSONArray();
	
						
						rs = db.getResultSet(sql);
						while(rs.next()){
							JSONObject album = new JSONObject();
							album.put("album_id", rs.getString("album_id"));
							album.put("title", rs.getString("title"));
							album.put("release_date", rs.getString("release_date"));
							album.put("cover_image_path", rs.getString("cover_image_path"));
							album.put("recording_company_name", rs.getString("recording_company_name"));
							album.put("number_of_tracks", rs.getInt("number_of_tracks"));
							album.put("PMRC_rating", rs.getString("PMRC_rating"));
							album.put("length", rs.getInt("length"));
							albumList.put(album);
						}
						
						searchResults.put("albums", albumList);
						
						
						
						
						//Autocomplete functionality against the artist table
						sql = "SELECT * FROM artist "
								+ "WHERE first_name LIKE '" + searchTerm + "%' "
								+ "OR last_name LIKE '" + searchTerm + "%' "
								+ "OR band_name LIKE '" + searchTerm + "%';";
						//response.getWriter().write(sql);
						JSONArray artistList = new JSONArray();
	
						rs = db.getResultSet(sql);
						while(rs.next()){
							JSONObject artist = new JSONObject();
							artist.put("artist_id", rs.getString("artist_id"));
							artist.put("first_name", rs.getString("first_name"));
							artist.put("last_name", rs.getString("last_name"));
							artist.put("band_name", rs.getString("band_name"));
							artist.put("bio", rs.getString("bio"));
							
							artistList.put(artist);
						}
	
						// Store album list in searchResults JSON object
						searchResults.put("artists", artistList);
						
						session.setAttribute("SEARCH_RESULTS", searchResults.toString());
						response.getWriter().write(searchResults.toString());
					} catch (SQLException e) {
						response.getWriter().write("SQL Error");
						ErrorLogger.log(e.getMessage());
						e.printStackTrace();
					} catch (JSONException e) {
						response.getWriter().write("JSON Error");
						ErrorLogger.log(e.getMessage());
						e.printStackTrace();
					}
					
					
				}  // end of check for empty searchTerm parameter
				
			}  // end of check for null searchTerm parameter
				
		/*}  // end of null SEARCH_TERM in session
			
		else {
			response.getWriter().write(session.getAttribute("SEARCH_RESULTS").toString());
		}*/
	}		

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
