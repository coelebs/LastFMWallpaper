package nl.vincentkriek.lastfm;

import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.app.WallpaperManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class LastFMTest extends Activity {
    public static final String TAG = "nl.vincentkriek.lastfm.LastFMTest";

	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        ((Button)findViewById(R.id.button)).setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				String user = ((EditText)findViewById(R.id.user)).getText().toString();
				JSONArray json = LastFM.getRecentTracks(user);
				try {
					String mbid = json.getJSONObject(0).getJSONObject("album").getString("mbid");
					
					WallpaperManager wm = WallpaperManager.getInstance(getApplicationContext());
					wm.setBitmap(LastFM.getAlbumArt(mbid));
				} catch (JSONException e) {
					Log.e(TAG, e.getMessage());
				} catch (IOException e) {
					Log.e(TAG, e.getMessage());
				}
			}
		});
    }
}