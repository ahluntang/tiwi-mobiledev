package be.ugent.oomo.labo_2.utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;

public final class CommonUtilities {
	
	static {
		checkSetup();
	}
	
	private CommonUtilities() {
		throw new UnsupportedOperationException();
	}
	
	private static final String TAG = CommonUtilities.class.getName();

	/**
     * Base URL of the Demo Server (such as http://my_host:8080/gcm-demo)
     */
    //public static final String SERVER_URL = "http://secret-sphinx-425.appspot.com";
    //public static final String SERVER_URL = "http://192.168.0.180:8888";
    public static final String SERVER_URL = "http://192.168.16.36:8888";

    /**
     * Google API project id registered to use GCM.
     */
    public static final String SENDER_ID = "646489753422";
    
    
    /**
     * @return Application's version code from the {@code PackageManager}.
     */
    protected static final int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }
    
    /**
     * @return Application's {@code SharedPreferences}.
     */
    protected static SharedPreferences getGcmPreferences(Context context) {
        // This sample app persists the registration ID in shared preferences, but
        // how you store the regID in your app is up to you.
        return context.getSharedPreferences(Class.class.getSimpleName(), Context.MODE_PRIVATE);
    }
    
    private static void checkSetup() {
    	Log.i(TAG, "Checking project setup ...");
    	checkNotNull(SERVER_URL, "SERVER_URL");
        checkNotNull(SENDER_ID, "SENDER_ID");
        Log.i(TAG, "Project correctly setup.");
    }
    
    private static void checkNotNull(Object reference, String name) {
        if (reference == null) {
            throw new NullPointerException(String.format("Please set the %1$s constant and recompile the app.", name));
        }
    }
}
