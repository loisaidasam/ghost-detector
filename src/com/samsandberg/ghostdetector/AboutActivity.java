
package com.samsandberg.ghostdetector;

import java.io.InputStream;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.TextView;

public class AboutActivity extends Activity {
	

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        
        setContentView(R.layout.about);

        TextView tv = (TextView) findViewById(R.id.about_content);
        
    	try {
            Resources res = getResources();
            InputStream in_s = res.openRawResource(R.raw.about);

            byte[] b = new byte[in_s.available()];
            in_s.read(b);
            tv.setText(new String(b));
        } catch (Exception e) {
            e.printStackTrace();
            String defaultText = "This Ghost Detector app looks for abnormal changes in phone signal state to detect nearby ghosts. Use with care!";
            tv.setText(defaultText);
        }
    }
}