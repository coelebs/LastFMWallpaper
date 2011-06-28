package nl.vincentkriek.lastfm;

import java.io.IOException;

import org.json.JSONException;

import android.app.ActivityManager;
import android.app.Service;
import android.app.WallpaperManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

public class WallpaperService extends Service {
	public static final String TAG = "nl.vincentkriek.lastfm";

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	public void onCreate() {
		super.onCreate();
	}
	
	public void onDestroy() {
		super.onDestroy();
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		if(intent.hasExtra("setnow")) {
			setWallpaperTask.run();
		} else {
			startWallpaperTask();
		}

		return super.onStartCommand(intent, flags, startId);
	}

	private void startWallpaperTask() {
		handler.removeCallbacks(setWallpaperTask);
		handler.postDelayed(setWallpaperTask, 100);		
	}
	
    private Handler handler = new Handler();
    
    public Runnable setWallpaperTask = new Runnable() {
    	   public void run() {
    		   	SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
    	    	String user = pref.getString("username", "");
    	    	Bitmap background;
    	    	try {
    				background = LastFM.getAlbumArtByUser(user);
    				if(background != null) {
    					Editor edit = pref.edit();
    					
    					edit.putBoolean("wallpaperchanged", true);
    					edit.commit();
    					WallpaperManager.getInstance(getApplicationContext()).setBitmap(background);
    				}
    			} catch (JSONException e) {
    				Log.e(TAG, e.getMessage());
    			} catch (IOException e) {
    				Log.e(TAG, e.getMessage());
    			}
    			
    			int interval = Integer.parseInt(pref.getString("interval", "24"));
    			
    			handler.postDelayed(setWallpaperTask, (interval * 60 * 60) * 1000);
    	   }
    	};
    	
	public static boolean isServiceRunning(Context context) {
	    ActivityManager manager = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
	    String servicename = WallpaperService.class.getName();
	    
	    for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
	        if (servicename.equals(service.service.getClassName())) {
	            return true;
	        }
	    }
	    return false;
	}
}
