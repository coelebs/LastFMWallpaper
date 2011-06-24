package nl.vincentkriek.lastfm;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import org.apache.http.util.ByteArrayBuffer;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;


public class LastFM {
	public static final String TAG = "nl.vincentkriek.lastfm.LastFM";
    public static final String baseURL = "http://ws.audioscrobbler.com/2.0/?format=json&api_key=40887e583290b0d8932e3c872ac7aae5";
	
	public static String request(String extra) throws IOException {
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
	
	public static JSONArray getRecentTracks(String user) {
		String json;
		JSONArray jarray = null;
		try {
			json = request("&method=user.getrecenttracks&user=" + user);
			jarray = new JSONObject(new String(json)).getJSONObject("recenttracks").getJSONArray("track");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return jarray;
	}
	
	public static Bitmap getAlbumArt(String mbid) {
		String json;
		Bitmap bitmap = null;
		try {
			json = request("&method=album.getInfo&mbid=" + mbid);
			JSONArray images = new JSONObject(json).getJSONObject("album").getJSONArray("image");
			String albumArtLink = null;
			for(int i = 0; i < images.length(); i++) {
				JSONObject image = images.getJSONObject(i);
				albumArtLink = image.getString("#text");

				if(image.getString("size").equals("mega")) {
					break;
				}				
			}
			
			URL url = new URL(albumArtLink);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setDoInput(true);
			connection.connect();
			InputStream input = connection.getInputStream();
			bitmap = BitmapFactory.decodeStream(input);
		} catch (IOException e) {
			Log.e(TAG, e.getMessage());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return bitmap;
	}
}
