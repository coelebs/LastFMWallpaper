package nl.vincentkriek.lastfm;

import java.io.IOException;

import org.json.JSONException;

import android.app.WallpaperManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.preference.Preference.OnPreferenceClickListener;
import android.util.Log;

public class Settings extends PreferenceActivity {
    public static final String TAG = "nl.vincentkriek.lastfm";

	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.settings);
		
		Preference setNow = findPreference("set_now");
		setNow.setOnPreferenceClickListener(setNowListener);
		
		CheckBoxPreference enableTimer = (CheckBoxPreference)findPreference("refresh");
		enableTimer.setOnPreferenceChangeListener(enableTimerListener);
    }
    
    private OnPreferenceClickListener setNowListener = new OnPreferenceClickListener() {

		public boolean onPreferenceClick(Preference preference) {
			setWallpaperTask.run();
			return true;
		}

	};
	
	private OnPreferenceChangeListener enableTimerListener = new OnPreferenceChangeListener() {
		
		@Override
		public boolean onPreferenceChange(Preference preference, Object newValue) {
			Boolean isChecked = (Boolean)newValue;
			((CheckBoxPreference)preference).setChecked(isChecked);
			if(isChecked) {
				startService(new Intent(Settings.this, WallpaperService.class));
			} else {
				stopService(new Intent(Settings.this, WallpaperService.class));
			}
			
			return false;
		}
	};
    
    private Handler handler = new Handler();
    
    private Runnable setWallpaperTask = new Runnable() {
    	   public void run() {
 	   		   	SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		    	String user = pref.getString("username", "");
		    	Bitmap background;
    	    	try {
    				background = LastFM.getAlbumArtByUser(user);
    				WallpaperManager.getInstance(getApplicationContext()).setBitmap(background);
    			} catch (JSONException e) {
    				Log.e(TAG, e.getMessage());
    			} catch (IOException e) {
    				Log.e(TAG, e.getMessage());
    			}
    			
    			EditTextPreference intervalPreference = (EditTextPreference)findPreference("interval");
    			String interval_string = intervalPreference.getText();
    			int interval = Integer.parseInt(interval_string);
    			
    			handler.postDelayed(setWallpaperTask, (interval * 60 * 60) * 1000);
    	   }
    	};
  
}