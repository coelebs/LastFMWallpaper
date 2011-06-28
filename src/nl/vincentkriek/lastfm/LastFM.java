package nl.vincentkriek.lastfm;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import org.apache.http.util.ByteArrayBuffer;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;


public class LastFM {
	private static final String TAG = "nl.vincentkriek.lastfm";
    private static final String baseURL = "http://ws.audioscrobbler.com/2.0/?format=json&api_key=40887e583290b0d8932e3c872ac7aae5";
	
	public static Bitmap getAlbumArtByUser(String user) {
		Bitmap bitmap = null;
		try {
			JSONObject json = getRecentTrack(user);
			String mbid = json.getJSONObject("album").getString("mbid");
			String request = null;
			if(mbid.length() > 0)
				request = "&method=album.getInfo&mbid=" + mbid;
			else {
				String artist = json.getJSONObject("artist").getString("#text");     
				String album = json.getJSONObject("album").getString("#text"); 
				request = "&method=album.getInfo&artist=" 
					+ URLEncoder.encode(artist)	+ "&album=" + URLEncoder.encode(album);
			}		

			bitmap = getAlbumArt(request(request));
		} catch (IOException e) {
			Log.e(TAG, e.getMessage());
		} catch (JSONException e) {
			Log.e(TAG, e.getMessage());
		}
		
		return bitmap;
	}

	public static Bitmap getArtistArtByUser(String user) {
		Bitmap bitmap = null;
		try {
			JSONObject json = getRecentTrack(user);
			String mbid = json.getJSONObject("artist").getString("mbid");
			String request = null;
			if(mbid.length() > 0)
				request = "&method=artist.getImages&mbid=" + mbid;
			else {
				String artist = json.getJSONObject("artist").getString("#text");     
				String album = json.getJSONObject("album").getString("#text"); 
				request = "&method=artist.getImages&artist=" 
					+ URLEncoder.encode(artist)	+ "&album=" + URLEncoder.encode(album);
			}		

			bitmap = getArtistArt(request(request));
		} catch (IOException e) {
			Log.e(TAG, e.getMessage());
		} catch (JSONException e) {
			Log.e(TAG, e.getMessage());
		}
		
		return bitmap;
	}
    
    private static Bitmap getBitmap(String link) throws IOException {
		URL url = new URL(link);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setDoInput(true);
		connection.connect();
		InputStream input = connection.getInputStream();
		return BitmapFactory.decodeStream(input);
    }
    
	private static String request(String extra) throws IOException {
		URL url = new URL(baseURL + extra);
 		URLConnection conn = url.openConnection();
		InputStream is = conn.getInputStream();
		BufferedInputStream bis = new BufferedInputStream(is);
		ByteArrayBuffer bab = new ByteArrayBuffer(64);
		int current = 0;
		
		while((current = bis.read()) != -1)
 		{
		         bab.append((byte)current);
		}
		
		return new String(bab.toByteArray());
	}
	
	private static JSONObject getRecentTrack(String user) {
		String json;
		JSONObject jObject = null;
		JSONArray recent = null;
		try {
			json = request("&method=user.getrecenttracks&user=" + user);
			recent = new JSONObject(new String(json)).getJSONObject("recenttracks").getJSONArray("track");
			for(int i = 0; i < recent.length(); i++) {
				jObject = recent.getJSONObject(i);
				if((!jObject.getJSONObject("album").getString("#text").equals("") ||
				   !jObject.getJSONObject("album").getString("mbid").equals("")) &&
				   (!jObject.getJSONObject("artist").getString("#text").equals("") ||
				   !jObject.getJSONObject("artist").getString("mbid").equals(""))) {
					break;
				}
			}
		} catch (IOException e) {
			Log.e(TAG, e.getMessage());
		} catch (JSONException e) {
			Log.e(TAG, e.getMessage());
		}
		return jObject;
	}

	private static Bitmap getArtistArt(String json) throws JSONException, IOException {
		JSONArray images = new JSONObject(json).getJSONObject("images").getJSONArray("image").getJSONObject(0).getJSONObject("sizes").getJSONArray("size");
		String albumArtLink = null;
		for(int i = 0; i < images.length(); i++) {
			JSONObject image = images.getJSONObject(i);
			albumArtLink = image.getString("#text");

			if(image.getString("name").equals("original")) {
				break;
			}				
		}
		
		return getBitmap(albumArtLink);
	}

	private static Bitmap getAlbumArt(String request) throws JSONException, IOException {
		JSONArray images = new JSONObject(request).getJSONObject("album").getJSONArray("image");
		String albumArtLink = null;
		for(int i = 0; i < images.length(); i++) {
			JSONObject image = images.getJSONObject(i);
			albumArtLink = image.getString("#text");

			if(image.getString("size").equals("mega")) {
				break;
			}				
		}
		
		return getBitmap(albumArtLink);
	}
}
