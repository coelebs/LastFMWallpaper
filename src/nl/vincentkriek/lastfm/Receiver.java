package nl.vincentkriek.lastfm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

public class Receiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
		Editor edit = pref.edit();
		if(!pref.getBoolean("wallpaperchanged", false)) {
			context.stopService(new Intent(context, WallpaperService.class));
		}
		
		edit.putBoolean("wallpaperchanged", false);
		edit.commit();
	}

}
