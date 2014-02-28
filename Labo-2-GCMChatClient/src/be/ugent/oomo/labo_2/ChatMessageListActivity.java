package be.ugent.oomo.labo_2;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;
import be.ugent.oomo.labo_2.database.DatabaseContract;
import be.ugent.oomo.labo_2.utilities.ChatServerUtilities;
import be.ugent.oomo.labo_2.utilities.GcmRegistrar;


/**
 * An activity representing a list of ChatMessages. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link ChatMessageDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 * <p>
 * The activity makes heavy use of fragments. The list of items is a
 * {@link ChatMessageListFragment} and the item details
 * (if present) is a {@link ChatMessageDetailFragment}.
 * <p>
 * This activity also implements the required
 * {@link ChatMessageListFragment.Callbacks} interface
 * to listen for item selections.
 */
public class ChatMessageListActivity extends FragmentActivity
        implements ChatMessageListFragment.Callbacks {

	private static final String TAG = ChatMessageListActivity.class.getName();
	
    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Crashlytics.start(this);
		setContentView(R.layout.activity_chatmessage_list);

        if (findViewById(R.id.chatmessage_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-large and
            // res/values-sw600dp). If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;

            // In two-pane mode, list items should be given the
            // 'activated' state when touched.
            ((ChatMessageListFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.chatmessage_list))
                    .setActivateOnItemClick(true);
            getSupportFragmentManager().beginTransaction()
            		.replace(R.id.chatmessage_detail_container, new EmptySelectionFragment())
            		.commit();
        }
    }
    
    
    
    @Override
	protected void onResume() {
		super.onResume();
		// Check device for Play Services APK. If check succeeds, proceed with GCM registration.
        if (!checkPlayServices()) {
        	Log.e(TAG, "No valid Google Play Services APK found.");
        	Toast.makeText(this, "No valid Google Play Services APK found.", Toast.LENGTH_SHORT).show();
        } else if (!GcmRegistrar.isRegistered(this)
        		|| !ChatServerUtilities.isRegisteredOnServer(this)) {
        	// start login dialog.
        	DialogFragment dialog = new LoginDialogFragment();
    		dialog.show(getSupportFragmentManager(), "Login");
    		dialog.setCancelable(false);    		
        }
	}
    
    @Override
	protected void onNewIntent(Intent intent) {
    	super.onNewIntent(intent);
		this.setIntent(intent);
	}
    
    static final int REQUEST_CODE_RECOVER_PLAY_SERVICES = 1001;

	/**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
    	  int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
    	  if (status != ConnectionResult.SUCCESS) {
    	    if (GooglePlayServicesUtil.isUserRecoverableError(status)) {
    	      return false;
    	    } else {
    	      Toast.makeText(this, "This device is not supported.", 
    	          Toast.LENGTH_LONG).show();
    	      finish();
    	    }
    	    return false;
    	  }
    	  return true;
    }
    
    void showErrorDialog(int code) {
    	  GooglePlayServicesUtil.getErrorDialog(code, this, 
    	      REQUEST_CODE_RECOVER_PLAY_SERVICES).show();
	}
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
      switch (requestCode) {
        case REQUEST_CODE_RECOVER_PLAY_SERVICES:
          if (resultCode == RESULT_CANCELED) {
            Toast.makeText(this, "Google Play Services must be installed.",
                Toast.LENGTH_SHORT).show();
            finish();
          }
          return;
      }
      super.onActivityResult(requestCode, resultCode, data);
    }

	/**
     * Callback method from {@link ChatMessageListFragment.Callbacks}
     * indicating that the item with the given ID was selected.
     */
    @Override
    public void onItemSelected(String contact) {
        if (mTwoPane) {
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putString(DatabaseContract.Message.COLUMN_NAME_CONTACT, contact);
            ChatMessageDetailFragment fragment = new ChatMessageDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.chatmessage_detail_container, fragment)
                    .commit();

        } else {
            // In single-pane mode, simply start the detail activity
            // for the selected item ID.
            Intent detailIntent = new Intent(this, ChatMessageDetailActivity.class);
            detailIntent.putExtra(DatabaseContract.Message.COLUMN_NAME_CONTACT, contact);
            startActivity(detailIntent);
        }
    }
}
