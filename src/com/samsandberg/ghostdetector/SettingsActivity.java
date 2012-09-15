
package com.samsandberg.ghostdetector;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;

public class SettingsActivity extends PreferenceActivity implements OnSharedPreferenceChangeListener {

	protected SharedPreferences settings;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	addPreferencesFromResource(R.xml.prefs);
    	settings = PreferenceManager.getDefaultSharedPreferences(this);
    	
    	// TODO: what if uuid is null? Pass them back to RideActivity?
    	uuid = settings.getString("uuid", null);
    }
	
	@Override
	public void onSharedPreferenceChanged(SharedPreferences arg0, String arg1) {
	}
	
}