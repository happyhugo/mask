/* Copyright 2011 Ionut Ursuleanu
 
This file is part of pttdroid.
 
pttdroid is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.
 
pttdroid is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.
 
You should have received a copy of the GNU General Public License
along with pttdroid.  If not, see <http://www.gnu.org/licenses/>. */

package ro.ui.pttdroid.settings;

import ro.ui.pttdroids.R;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

public class AudioSettings extends PreferenceActivity {
	
	private static boolean useSpeex;
	private static int speexQuality;
	private static boolean echoState;

	public static final boolean USE_SPEEX = false;
	public static final boolean DONT_USE_SPEEX = false;	
	public static final boolean ECHO_ON = true;
	public static final boolean ECHO_OFF = false;	
	
//	public static final String IP = "ec2-54-214-94-1.us-west-2.compute.amazonaws.com";
//	public static final String IP = "192.168.2.124";
//	public static final String IP = "115.28.39.5";
	
	public static final String IP = "192.168.2.180";
	public static final int PROT = 1234;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.settings_audio);		
	}	
	
	/**
	 * Update cache settings
	 * @param context
	 */
	public static void getSettings(Context context) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		Resources res = context.getResources();
		
    	useSpeex = prefs.getBoolean(
    			"use_speex",
    			USE_SPEEX);    		    		
    	speexQuality = Integer.parseInt(prefs.getString(
    			"speex_quality", 
    			res.getStringArray(R.array.speex_quality_values)[0]));
    	echoState = prefs.getBoolean(
    			"echo",
    			ECHO_OFF);    		
	}
	
	public static boolean useSpeex() {
		return useSpeex;
	}	

	public static int getSpeexQuality() {
		return speexQuality;
	}
	
	public static boolean getEchoState() {
		return echoState;
	}		

}
