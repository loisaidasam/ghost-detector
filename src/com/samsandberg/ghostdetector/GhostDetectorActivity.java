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
	
	protected float CHANGE_SENSITIVITY_LOW = (float)0.05;
	protected float CHANGE_SENSITIVITY_MED = (float)0.1;
	protected float CHANGE_SENSITIVITY_HIGH = (float)0.2;
	
	protected VuMeter view;
	protected int mStrength = -1;
	protected float mSensitivity = CHANGE_SENSITIVITY_MED;

	private int convertToAsu(int dbm) {
		return Math.round(((float)dbm + 113f) / 2f);
	}
	
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
				
				// Default?
				else {
					strength = 0;
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
		}
		
		// This method is deprecated (but maybe sometimes used?)
		public void onSignalStrengthChanged(int asu) {
			Log.d(TAG, "#2. " + String.valueOf(asu)); 
			if (mStrength != asu){
				int diff = Math.abs(asu - mStrength);
				if (diff > 0) {
    				view.indicateChange(diff); 
    				mStrength = asu;
				}
			}  
		}
	};
	
	protected float getSensitivity() {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        switch (Integer.parseInt(settings.getString("sensitivity", "1"))) {
	        case 0:
	        	return CHANGE_SENSITIVITY_LOW;
	        case 2:
	        	return CHANGE_SENSITIVITY_HIGH;
	        case 1:
	        default:
	        	return CHANGE_SENSITIVITY_MED;
        }
	}
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		
		mSensitivity = getSensitivity();
        view = new VuMeter(this, mSensitivity);
        setContentView(view);
    }
    
    @Override
    public void onResume() {
    	super.onResume();

		float newSensitivity = getSensitivity();
		if (newSensitivity != mSensitivity) {
			mSensitivity = newSensitivity;
			view.modifyChangeSensitivity(mSensitivity);
			Log.d(TAG, "mSensitivity=" + mSensitivity);
		}
	
		TelephonyManager mTelManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE); 
		mTelManager.listen(mSignalListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
    }
    
    @Override
    public void onPause() {
    	super.onPause();
    	
		TelephonyManager mTelManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE); 
		mTelManager.listen(mSignalListener, PhoneStateListener.LISTEN_NONE);
    }
    
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
	        	startActivity(new Intent(this, AboutActivity.class));
	            return true;
	        case R.id.menu_settings:
	        	startActivity(new Intent(this, SettingsActivity.class));
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
        }
    }
}