package be.ugent.oomo.labo_2.utilities;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;
import be.ugent.oomo.labo_2.R;
import be.ugent.oomo.labo_2.contentprovider.MessageContentProvider;
import be.ugent.oomo.labo_2.database.DatabaseContract;

public final class ChatServerUtilities {
	
	private static final String TAG = ChatServerUtilities.class.getName();
	
	/**
     * Default lifespan (7 days) of the {@link #isRegisteredOnServer(Context)}
     * flag until it is considered expired.
     */
    private static final long DEFAULT_ON_SERVER_LIFESPAN_MS = 1000 * 3600 * 24 * 7;
    
	private static final int MAX_ATTEMPTS = 1;
    private static final int BACKOFF_MILLI_SECONDS = 2000;
    private static final Random random = new Random();
    
    public static final String PROPERTY_USER = "userOnServer";
    private static final String PROPERTY_ON_SERVER = "onServer";
    private static final String PROPERTY_ON_SERVER_EXPIRATION_TIME = "onServerExpirationTime";
    private static final String PROPERTY_ON_SERVER_LIFESPAN = "onServerLifeSpan";
    
    public static class ServerException extends Exception {
    	private static final long serialVersionUID = -3963924290239629732L;
    	public final int errorCode;
		
    	public ServerException(String error, int errorCode) {
    		this(error,null,errorCode);
    	}
    	
    	public ServerException(String error, Exception ex, int errorCode) {
    		super(error, ex);
    		this.errorCode=errorCode;
    	}
    	
		public ServerException(String error)  {
			this(error, null, -1);
		}
		
		public ServerException(String error, Exception ex) {
			this(error, ex, -1);
		}
    }

    /**
     * Checks whether the device was successfully registered in the server side,
     * as set by {@link #setRegisteredOnServer(Context, boolean)}.
     *
     * <p>To avoid the scenario where the device sends the registration to the
     * server but the server loses it, this flag has an expiration date, which
     * is {@link #DEFAULT_ON_SERVER_LIFESPAN_MS} by default (but can be changed
     * by {@link #setRegisterOnServerLifespan(Context, long)}).
     */
	public static boolean isRegisteredOnServer(final Context context) {
		final SharedPreferences prefs = CommonUtilities.getGcmPreferences(context);
        boolean isRegistered = prefs.getBoolean(PROPERTY_ON_SERVER, false);
        String savedUser = prefs.getString(PROPERTY_USER,null);
        isRegistered = (isRegistered && savedUser != null);
        Log.v(TAG, String.format("Is registered on server: %b", isRegistered));
        if (isRegistered) {
            // checks if the information is not stale
            long expirationTime = prefs.getLong(PROPERTY_ON_SERVER_EXPIRATION_TIME, -1);
            if (System.currentTimeMillis() > expirationTime) {
            	Log.v(TAG, String.format("flag expired on: %s", new Timestamp(expirationTime)));
                return false;
            }
        }
        return isRegistered;
	}
	
	/**
     * Sends the registration ID to your server over HTTP, so it can use GCM/HTTP or CCS to send
     * messages to your app.
	 * @throws InterruptedException 
     *
     */
	public static void registerOnServer(final Context context, final String regId, String user) throws ServerException {
		Log.i(TAG, "registering device (regId = " + regId + ")");
        String serverUrl = CommonUtilities.SERVER_URL + "/register";
        Map<String, String> params = new HashMap<String, String>();
        params.put("regId", regId);
        params.put("user", user);
        long backoff = BACKOFF_MILLI_SECONDS + random.nextInt(1000);
        // Once GCM returns a registration id, we need to register it in the
        // demo server. As the server might be down, we will retry it a couple
        // times.
        Exception error = null;
        for (int i = 1; i <= MAX_ATTEMPTS; i++) {
            Log.d(TAG, "Attempt #" + i + " to register");
            try {
                post(serverUrl, params);
                setRegisteredOnServer(context, true);
                setUserOnServer(context, user);
                Log.i(TAG, context.getString(R.string.server_registered));
                return;
            } catch (IOException e) {
            	error = e;
                // Here we are simplifying and retrying on any error; in a real
                // application, it should retry only on unrecoverable errors
                // (like HTTP error code 503).
                Log.e(TAG, "Failed to register on attempt " + i + ":" + e);
                if (i == MAX_ATTEMPTS) {
                    break;
                }
                try {
                    Log.d(TAG, "Sleeping for " + backoff + " ms before retry");
                    Thread.sleep(backoff);
                } catch (InterruptedException e1) {
                    // Activity finished before we complete - exit.
                    Log.d(TAG, "Thread interrupted: abort remaining retries!");
                    Thread.currentThread().interrupt();
                    throw new ServerException("Thread interrupted: abort remaining retries!", e1);
                }
                // increase backoff exponentially
                backoff *= 2;
            }
        }
        throw new ServerException("Failed registering on server.", error);
	}
	
	/**
     * Unregister this account/device pair within the server.
     */
	public static void unregisterOnServer(final Context context, final String regId) throws ServerException {
		Log.i(TAG, "unregistering device (regId = " + regId + ")");
        String serverUrl = CommonUtilities.SERVER_URL + "/unregister";
        Map<String, String> params = new HashMap<String, String>();
        params.put("regId", regId);
        try {
            post(serverUrl, params);
            setRegisteredOnServer(context, false);
            setUserOnServer(context, null);
            Log.i(TAG, context.getString(R.string.server_unregistered));
        } catch (IOException e) {
        	Log.e(TAG, String.format(context.getString(R.string.server_unregister_error, CommonUtilities.SERVER_URL, e.getMessage())));
            // At this point the device is unregistered from GCM, but still
            // registered in the server.
            // We could try to unregister again, but it is not necessary:
            // if the server tries to send a message to the device, it will get
            // a "NotRegistered" error message and should unregister the device.
            throw new ServerException(context.getString(R.string.server_unregister_error, CommonUtilities.SERVER_URL, e.getMessage(), e));
        }
	}
	
	public static void sendMessageAsync(final Context context, final String my_message, final String receiver) {
		new AsyncTask<Void, Void, Boolean>() {
			Exception error = null;
			@Override
			protected Boolean doInBackground(Void... params) {
				try {
					sendMessage(context, my_message, receiver);
					saveMessage(context, my_message, receiver, 1);
				} catch (ServerException e) {
					error = e;
					return false;
				}
				return true;
			}

			@Override
			protected void onPostExecute(final Boolean success) {
				if (success) {
					message(Log.VERBOSE, "message send");
				} else if (error != null){
					message(Log.ERROR, error.getMessage());
					error.printStackTrace();
				} else {
					message(Log.ERROR, "message send failed. (Unknown)");
				}
				super.onPostExecute(success);
			}
			
			private void message(int level, String text) {
				Toast.makeText(context, text, Toast.LENGTH_LONG).show();
				Log.println(level, TAG, text);
			}
			
			
		}.execute();
	}
	
	private static void sendMessage(final Context context, final String my_message, final String receiver) throws ServerException {
		// Send post message to SERVER_URL + sendChatMessage
		// post parameters: sender, receiver, message
		Map<String, String> params = new HashMap<String,String>();
		final SharedPreferences prefs = CommonUtilities.getGcmPreferences(context);
		params.put("sender", prefs.getString(PROPERTY_USER, null));
		params.put("receiver", receiver);
		params.put("message", my_message);
		try {
			post(CommonUtilities.SERVER_URL + "/sendChatMessage", params);
		} catch (IOException e) {
        	Log.e(TAG, String.format(context.getString(R.string.server_unregister_error, CommonUtilities.SERVER_URL, e.getMessage())));
            throw new ServerException(context.getString(R.string.server_unregister_error, CommonUtilities.SERVER_URL, e.getMessage(), e));
        }
	}
	
	protected static long saveMessage(final Context context, final String message, final String contact) { 
		return saveMessage(context, message, contact, 0);
	}
	
	private static long saveMessage(final Context context, final String message, final String contact, int sendFromApp) {
		ContentValues value = new ContentValues();
		value.put(DatabaseContract.Message.COLUMN_NAME_CONTACT, contact);
		value.put(DatabaseContract.Message.COLUMN_NAME_MESSAGE, message);
		value.put(DatabaseContract.Message.COLUMN_NAME_SEND_FROM_APP, sendFromApp);
		Uri uri = context.getContentResolver().insert(MessageContentProvider.CONTENT_MESSAGE_URI, value);
		return ContentUris.parseId(uri);
	}
	
	/**
     * Gets how long (in milliseconds) the {@link #isRegistered(Context)}
     * property is valid.
     *
     * @return value set by {@link #setRegisteredOnServer(Context, boolean)} or
     *      {@link #DEFAULT_ON_SERVER_LIFESPAN_MS} if not set.
     */
    private static long getRegisterOnServerLifespan(final Context context) {
        final SharedPreferences prefs = CommonUtilities.getGcmPreferences(context);
        long lifespan = prefs.getLong(PROPERTY_ON_SERVER_LIFESPAN, DEFAULT_ON_SERVER_LIFESPAN_MS);
        return lifespan;
    }

    /**
     * Sets how long (in milliseconds) the {@link #isRegistered(Context)}
     * flag is valid.
     */
    public static void setRegisterOnServerLifespan(final Context context, final long lifespan) {
        final SharedPreferences prefs = CommonUtilities.getGcmPreferences(context);
        Editor editor = prefs.edit();
        editor.putLong(PROPERTY_ON_SERVER_LIFESPAN, lifespan);
        editor.commit();
    }
	
	/**
     * Sets whether the device was successfully registered in the server side.
     */
	private static void setRegisteredOnServer(final Context context, final Boolean registered) {
		// set the flag's expiration date
        long lifespan = getRegisterOnServerLifespan(context);
        long expirationTime = System.currentTimeMillis() + lifespan;
        final SharedPreferences prefs = CommonUtilities.getGcmPreferences(context);
        Editor editor = prefs.edit();
        if (registered != null) {
            editor.putBoolean(PROPERTY_ON_SERVER, registered);
            Log.v(TAG, String.format("Setting registeredOnServer flag as %b until %s", registered, new Timestamp(expirationTime)));
        } else {
        	Log.v(TAG, String.format("Setting registeredOnServer expiration to %s",new Timestamp(expirationTime)));
        }
        editor.putLong(PROPERTY_ON_SERVER_EXPIRATION_TIME, expirationTime);
        editor.commit();
    }
	
	private static void setUserOnServer(final Context context, final String user) {
		final SharedPreferences prefs = CommonUtilities.getGcmPreferences(context);
        Editor editor = prefs.edit();
        if (user != null) {
            editor.putString(PROPERTY_USER, user);
            Log.v(TAG, String.format("Setting userOnServer flag as %s", user));
        } else {
        	editor.remove(PROPERTY_USER);
        	Log.v(TAG, "Unsetting userOnServer flag");
        }
        editor.commit();
	}
	
	private static String getUserOnServer(final Context context) {
		final SharedPreferences prefs = CommonUtilities.getGcmPreferences(context);
		return prefs.getString(PROPERTY_USER, null);
	}
    
	/**
     * Issue a POST request to the server.
     *
     * @param endpoint POST address.
     * @param params request parameters.
     *
     * @throws IOException propagated from POST.
	 * @throws ServerException 
     */
    private static void post(String endpoint, Map<String, String> params) throws ServerException, IOException {
    	
    	// Create querystring
    	StringBuilder querybuilder = new StringBuilder();
    	for (String key : params.keySet()) {
            querybuilder.append('&');
            querybuilder.append(key);
            querybuilder.append("=");
            querybuilder.append(params.get(key));
        }
    	querybuilder.deleteCharAt(0); // removing fist &
    	String parameters = querybuilder.toString();
    	Log.i(TAG, parameters);
    	OutputStream os = null;
        try {
	        URL url = new URL(endpoint);
	        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	        //conn.setReadTimeout(10000 /* milliseconds */);
	        //conn.setConnectTimeout(15000 /* milliseconds */);
	        conn.setDoOutput(true);
	        conn.setUseCaches(false);
	        conn.setRequestMethod("POST"); 
	        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
	        //conn.setFixedLengthStreamingMode(parameters.getBytes().length);
	        //conn.connect();
	        os = conn.getOutputStream();
	        os.write(parameters.getBytes());
	        int response = conn.getResponseCode();
	        if (response != 200) {
		        Log.i(TAG, "post failed: " + conn.getErrorStream() +"\n\n" + conn.getResponseMessage());
	        	throw new ServerException(conn.getErrorStream().toString());
	        } else {
		        Log.i(TAG, "posted");
	        }
        } catch (IOException ex){
	        Log.i(TAG, "post failed: " + ex.getLocalizedMessage());
        	throw new ServerException(ex.getMessage());
        } finally {
	        //cleanup outputstream
	        if (os != null) {
	        	os.close(); 
	        } 
	    }
    }
}
