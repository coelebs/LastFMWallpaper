package nl.vincentkriek.lastfm;

import java.io.IOException;

import org.json.JSONException;

import android.app.WallpaperManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.Preference.OnPreferenceClickListener;
import android.util.Log;

public class LastFMTest extends PreferenceActivity {
    public static final String TAG = "nl.vincentkriek.lastfm.LastFMTest";

	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.settings);
		Preference setNow = findPreference("set_now");
		setNow.setOnPreferenceClickListener(new OnPreferenceClickListener() {

			public boolean onPreferenceClick(Preference preference) {
				setWallpaper();
				return true;
			}

		});
    }
    
    public void setWallpaper() {
    	EditTextPreference user = (EditTextPreference)findPreference("username");
    	Bitmap background;
    	try {
			background = LastFM.getAlbumArtByUser(user.getText().toString());
			WallpaperManager.getInstance(getApplicationContext()).setBitmap(background);
		} catch (JSONException e) {
			Log.e(TAG, e.getMessage());
		} catch (IOException e) {
			Log.e(TAG, e.getMessage());
		}

    }
  
}