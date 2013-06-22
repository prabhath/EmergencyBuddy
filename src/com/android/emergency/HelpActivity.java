package com.android.emergency;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

/*
 *to show the help screen
 */

public class HelpActivity extends Activity{		
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.help_screen);
        
        
        // Read raw file into string and populate TextView
        InputStream iFile = getResources().openRawResource(R.raw.help_overview);
        try {
            TextView helpText = (TextView) findViewById(R.id.textView_help_overview);
            String strFile = inputStreamToString(iFile);
            helpText.setText(strFile);
        } catch (Exception e) {
            Log.e("Emergency Buddy", "InputStreamToString failure", e);
        }
        
    }
	
	
	public String inputStreamToString(InputStream is) throws IOException {
        StringBuffer sBuffer = new StringBuffer();
        DataInputStream dataIO = new DataInputStream(is);
        String strLine = null;

        while ((strLine = dataIO.readLine()) != null) {
            sBuffer.append(strLine + "\n");
        }

        dataIO.close();
        is.close();

        return sBuffer.toString();
    }
}
