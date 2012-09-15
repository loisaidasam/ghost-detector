package com.samsandberg.ghostdetector;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class GhostDetectorActivity extends Activity {
	
	protected final String TAG = "GhostDetectorActivity";
	
	VuMeter view;
	int mStrength = -1;
	float CHANGE_SENSITIVITY_LOW = (float)0.05;
	float CHANGE_SENSITIVITY_MED = (float)0.1;
	float CHANGE_SENSITIVITY_HIGH = (float)0.2;
	
	private int convertToAsu(int dbm) {
		return Math.round(((float)dbm + 113f) / 2f);
	}
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        float changeSensitivity = settings.getFloat("change_sensitivity", CHANGE_SENSITIVITY_MED);
        view = new VuMeter(this, changeSensitivity);
        setContentView(view);
        
    	PhoneStateListener mSignalListener = new PhoneStateListener() {
    		
    		// We expect this method to be called
    		public void onSignalStrengthsChanged(SignalStrength signalStrength) {
    			int strength = mStrength;
    			
    			if (signalStrength.isGsm()) {
    				// Possible values are 0-31 or 99
    				strength = signalStrength.getGsmSignalStrength();
    				Log.d(TAG, "#1. getGsmSignalStrength() " + strength);
    				
    				// We don't care about 99 
    				if (strength == 99) {
    					strength = mStrength;
    				}
    			} else {
    				if (signalStrength.getEvdoDbm() < 0) {
    					strength = convertToAsu(signalStrength.getEvdoDbm());
    					Log.d(TAG, "#1. getEvdoDbm() " + strength);
    				} 
    				
    				else if (signalStrength.getCdmaDbm() < 0) { 
    					strength = convertToAsu(signalStrength.getCdmaDbm());
    					Log.d(TAG, "#1. getCdmaDbm() " + strength); 
    				}
    			}
    			
    			if (mStrength == -1) {
    				mStrength = strength;
    			}
    			Log.d(TAG, "mStrength=" + mStrength + " strength=" + strength);

    			int diff = Math.abs(mStrength - strength);
    			if (diff > 0) {
    				view.indicateChange(diff);
    				mStrength = strength;
    			}
    			
    			super.onSignalStrengthsChanged(signalStrength); 
    		}
    		
    		// This method is deprecated (but maybe sometimes used?)
    		public void onSignalStrengthChanged(int asu) {
    			Log.d(TAG, "#2. " + String.valueOf(asu)); 
    			if (mStrength != asu){
    				view.indicateChange(Math.abs(asu - mStrength)); 
    				mStrength = asu;
    			} 
    			super.onSignalStrengthChanged(asu); 
    		}
    	};
	
		TelephonyManager mTelManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE); 
		mTelManager.listen(mSignalListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
    }
    

    // Initiating Menu XML file (menu.xml)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);
        return true;
    }
 
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
	        case R.id.menu_about:
	        	Intent intent = new Intent(this, AboutActivity.class);
	        	startActivity(intent);
	            return true;
	        case R.id.menu_settings:
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
        }
    }
}