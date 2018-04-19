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
 * Servlet implementation class get_artists
 */
@WebServlet("/get_artists")
public class get_artists extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public get_artists() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("application/json");
		HttpSession session = request.getSession(true);
		
		String name = "", album = "", song = "";
		String sql = "";
		//response.getWriter().append("Served at: ").append(request.getContextPath());
		
		if(session.getAttribute("SEARCH_TERM") == null) {
		
			if(request.getParameter("name") != null) {
				name = request.getParameter("name");
				
				if(!name.equals("")) {
					sql = "SELECT * FROM artist WHERE first_name LIKE '%" + name + "%' " + 
							"OR last_name LIKE '%" + name + "%' " +
							"OR band_name LIKE '%" + name + "%';";
				}
			}
			
			else if(request.getParameter("album") != null) {
				album = request.getParameter("album");
				
				if(!album.equals("")) {
					sql = "SELECT * FROM artist JOIN song_artist ON artist_id = fk_artist_id " + 
							"JOIN song ON song_id = song_artist.fk_song_id " + 
							"JOIN album_song ON song_id = album_song.fk_song_id " + 
							"JOIN album ON fk_album_id = album_id ";
					sql += "WHERE album.title LIKE '%" + album + "%';";						
				}
			}
			
			else if(request.getParameter("song") != null) {
				song = request.getParameter("song");
				
				if(!song.equals("")) {
					sql = "SELECT * FROM artist JOIN song_aritst ON artist_id = fk_artist_id JOIN song ON fk_song_id = song_id ";
					sql +="WHERE song.title LIKE '%" + song + "%' ";
				}
			}
			
			else if(sql.equals("")) {
				sql = "SELECT * FROM artist;";
			}
			
			
			JSONArray artistList = new JSONArray();
			
			DbUtilities db = new DbUtilities();
			ResultSet rs;
			try {
				rs = db.getResultSet(sql);
				
				while(rs.next()) {
					JSONObject artist = new JSONObject();
					
					artist.put("artist_id", rs.getString("artist_id"));
					artist.put("first_name", rs.getString("first_name"));
					artist.put("last_name", rs.getString("last_name"));
					artist.put("band_name", rs.getString("band_name"));
					artist.put("bio", rs.getString("bio"));
					artistList.put(artist);
				}
				
				response.getWriter().write(artistList.toString());
				session.setAttribute("SEARCH_TERM", artistList.toString());
				response.getWriter().write(artistList.toString());
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		}
		
		else{
			response.getWriter().write(session.getAttribute("SEARCH_TERM").toString());
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
