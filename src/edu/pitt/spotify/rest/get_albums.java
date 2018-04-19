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

/**
 * Servlet implementation class get_albums
 */
@WebServlet("/api/get_albums")
public class get_albums extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public get_albums() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("application/json");
		HttpSession session = request.getSession(true);
		
		
		String title = "", artist = "", song = "", releaseYear = "";
		String sql = "";
		
		if(session.getAttribute("SEARCH_TERM") == null) {
		
			if(request.getParameter("title") != null) {
				title = request.getParameter("title");
				
				if(!title.equals("")) {
					sql = "SELECT * FROM album WHERE title LIKE '%" + title + "%';";
				}
			}
			
			else if(request.getParameter("artist") != null) {
				artist = request.getParameter("artist");
				
				if(! artist.equals("")) {
					sql = "SELECT * FROM album JOIN album_song ON album_id = fk_album_id " + 
							"JOIN song ON song_id = album_song.fk_song_id " + 
							"JOIN song_artist ON song_id = song_artist.fk_song_id " + 
							"JOIN artist ON fk_artist_id = artist_id ";
					sql += "WHERE artist.band_name LIKE '%" + artist + "%' " +
							"OR artist.first_name LIKE '%" + artist + "%' " +
							"OR artist.last_name LIKE '%" + artist + "%'";
				}
			}
			
			else if(request.getParameter("releaseYear") != null) {
				releaseYear = request.getParameter("songYear");
				
				if(!releaseYear.equals("")) {
					sql = "SELECT * FROM album;"; //JOIN song_artist ON song_id = fk_song_id JOIN artist ON fk_artist_id = artist_id "
					sql +="WHERE release_date LIKE '%" + releaseYear + "%' ";
				}
			}
			
			else if(request.getParameter("song") != null) {
				song = request.getParameter("song");
				
				if(!song.equals("")) {
					sql = "SELECT * FROM album JOIN album_song ON album_id = fk_album_id JOIN song ON fk_song_id = song_id ";
					sql +="WHERE song.title LIKE '%" + song + "%' ";
				}
			}
			
			else if(sql.equals("")) {
				sql = "SELECT * FROM album;";
			}
			
			
			JSONArray albumList = new JSONArray();
			
			DbUtilities db = new DbUtilities();
			ResultSet rs;
			try {
				rs = db.getResultSet(sql);
				
				while(rs.next()) {
					JSONObject album = new JSONObject();
					
					album.put("album_id", rs.getString("album_id"));
					album.put("title", rs.getString("title"));
					album.put("release_date", rs.getString("release_date"));
					album.put("PMRC_rating", rs.getString("PMRC_rating"));
					album.put("length", rs.getInt("length"));
					albumList.put(album);
				}
				
				response.getWriter().write(albumList.toString());
				session.setAttribute("SEARCH_TERM", albumList.toString());
				response.getWriter().write(albumList.toString());
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		}
		
		else {
			response.getWriter().write(session.getAttribute("SEARCH_TERM").toString());
		}
			
		//response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
