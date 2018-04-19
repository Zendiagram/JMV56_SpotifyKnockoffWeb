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
 * Servlet implementation class get_songs
 */
@WebServlet("/api/get_songs")
public class get_songs extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public get_songs() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("application/json");
		HttpSession session = request.getSession(true);
		
		
		String title = "", artist = "", album = "", songYear = "";
		String sql = "";
		
		if(session.getAttribute("SRCH_RSLTS") == null) {
		
			if(request.getParameter("title") != null) {
				title = request.getParameter("title");
				
				if(!title.equals("")) {
					sql = "SELECT * FROM song WHERE title LIKE '%" + title + "%';";
				}
			}
			
			else if(request.getParameter("artist") != null) {
				artist = request.getParameter("artist");
				
				if(! artist.equals("")) {
					sql = "SELECT * FROM song JOIN song_artist ON song_id = fk_song_id JOIN artist ON fk_artist_id = artist_id ";
					sql += "WHERE artist.band_name LIKE '%" + artist + "%' " +
							"OR artist.first_name LIKE '%" + artist + "%' " +
							"OR artist.last_name LIKE '%" + artist + "%'";
				}
			}
			
			else if(request.getParameter("songYear") != null) {
				songYear = request.getParameter("songYear");
				
				if(!songYear.equals("")) {
					sql = "SELECT * FROM song;"; //JOIN song_artist ON song_id = fk_song_id JOIN artist ON fk_artist_id = artist_id "
					sql +="WHERE release_date LIKE '%" + songYear + "%' ";
				}
			}
			
			else if(request.getParameter("album") != null) {
				album = request.getParameter("album");
				
				if(!album.equals("")) {
					sql = "SELECT * FROM song JOIN album_song ON song_id = fk_song_id JOIN album ON fk_album_id = album_id ";
					sql +="WHERE album.title LIKE '%" + album + "%' ";
				}
			}
			
			else if(sql.equals("")) {
				sql = "SELECT * FROM song;";
			}
			
			//response.getWriter().write(sql);
			
			JSONArray songList = new JSONArray();
			
			DbUtilities db = new DbUtilities();
			ResultSet rs;
			try {
				rs = db.getResultSet(sql);
				
				while(rs.next()) {
					JSONObject song = new JSONObject();
					
					song.put("song_id", rs.getString("song_id"));
					song.put("title", rs.getString("title"));
					song.put("release_date", rs.getString("release_date"));
					song.put("record_date", rs.getString("record_date"));
					song.put("length", rs.getInt("length"));
					songList.put(song);
				}
				
				response.getWriter().write(songList.toString());
				session.setAttribute("SEARCH_TERM", songList.toString());
				response.getWriter().write(songList.toString());
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		}
			
		else {
			response.getWriter().write(session.getAttribute("SRCH_RSLTS").toString());
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
