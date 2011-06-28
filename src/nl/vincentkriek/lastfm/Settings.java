package nl.vincentkriek.lastfm;

import android.content.Intent;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;

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
    
    @Override
    protected void onResume() {
    	super.onResume();
    	
		CheckBoxPreference refresh = (CheckBoxPreference)findPreference("refresh");
		refresh.setChecked(WallpaperService.isServiceRunning(getApplicationContext()));
    }
    
    private OnPreferenceClickListener setNowListener = new OnPreferenceClickListener() {

		public boolean onPreferenceClick(Preference preference) {
			Intent intent = new Intent(Settings.this, WallpaperService.class);
			intent.putExtra("setnow", true);
			startService(intent);
			
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
	

    
}