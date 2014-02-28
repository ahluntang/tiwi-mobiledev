package be.ugent.oomo.labo_2;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;
import be.ugent.oomo.labo_2.contentprovider.MessageContentProvider;
import be.ugent.oomo.labo_2.database.DatabaseContract;
import be.ugent.oomo.labo_2.database.DatabaseContract.Message;

/**
 * A list fragment representing a list of ChatMessages. This fragment
 * also supports tablet devices by allowing list items to be given an
 * 'activated' state upon selection. This helps indicate which item is
 * currently being viewed in a {@link ChatMessageDetailFragment}.
 * <p>
 * Activities containing this fragment MUST implement the {@link Callbacks}
 * interface.
 */
public class ChatMessageListFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor>{
	
	private static final String TAG = ChatMessageListFragment.class.getName();

    /**
     * The serialization (saved instance state) Bundle key representing the
     * activated item position. Only used on tablets.
     */
    private static final String STATE_ACTIVATED_POSITION = "activated_position";

    /**
     * The fragment's current callback object, which is notified of list item
     * clicks.
     */
    private Callbacks mCallbacks = sDummyCallbacks;

    /**
     * The current activated item position. Only used on tablets.
     */
    private int mActivatedPosition = ListView.INVALID_POSITION;

	private SimpleCursorAdapter listAdapter;

    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selections.
     */
    public interface Callbacks {
        /**
         * Callback for when an item has been selected.
         */
        public void onItemSelected(String contact);
    }

    /**
     * A dummy implementation of the {@link Callbacks} interface that does
     * nothing. Used only when this fragment is not attached to an activity.
     */
    private static Callbacks sDummyCallbacks = new Callbacks() {
        @Override
        public void onItemSelected(String contact) {
        }
    };

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ChatMessageListFragment() {
    }
    
    @Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    	inflater.inflate(R.menu.main, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
        case R.id.action_new_message:
        	setActivatedPosition(ListView.INVALID_POSITION);
        	mCallbacks.onItemSelected(null); //
            return true;
        case R.id.action_test:
        	Intent intent = new Intent();
        	intent.setAction("com.google.android.c2dm.intent.RECEIVE");
        	intent.putExtra(DatabaseContract.Message.COLUMN_NAME_CONTACT, "SENDER");
        	intent.putExtra(DatabaseContract.Message.COLUMN_NAME_MESSAGE, "TEST MESSAGE. Can't be replied to reply and should show error message.");
        	intent.addCategory(getActivity().getPackageName());
        	if (hasBroadcastReceiver(intent)) {
        		getActivity().sendOrderedBroadcast(intent, "com.google.android.c2dm.permission.RECEIVE");
        	} else {
        		Toast.makeText(getActivity(), "No broadcast receiver for intent. Action=" + intent.getAction() + ", Permission=com.google.android.c2dm.permission.RECEIVE" , Toast.LENGTH_LONG).show();
        	}
        	return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }
    
    private boolean hasBroadcastReceiver(Intent intent) {
    	
    	return getActivity().getPackageManager()
        		.queryBroadcastReceivers(intent, 0)
        		.size() > 0;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setHasOptionsMenu(true);
        
        getLoaderManager().initLoader(0, null, this);
        // Fields from the database (projection)
        String[] from = new String[] {
        		Message._ID, // hacked -> this is contact name (see onCreateLoader projection)
                Message.COLUMN_NAME_MESSAGE,
                };
        // Fields on the UI to which we map
        int[] to = new int[] { android.R.id.text1, android.R.id.text2 };
        listAdapter = new SimpleCursorAdapter(getActivity(), android.R.layout.simple_list_item_activated_2, null, from, to, 0);
        setListAdapter(listAdapter);
    }
    
    @Override
    public void onResume() {
    	super.onResume();
    	String contact = getActivity().getIntent().getStringExtra(Message.COLUMN_NAME_CONTACT);
        Log.d(TAG, "Selected contact: " + contact);
        if (contact != null) {
        	setActivatedPosition(ListView.INVALID_POSITION);
        	mCallbacks.onItemSelected(contact);
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Restore the previously serialized activated item position.
        if (savedInstanceState != null
                && savedInstanceState.containsKey(STATE_ACTIVATED_POSITION)) {
            setActivatedPosition(savedInstanceState.getInt(STATE_ACTIVATED_POSITION));
        }
        this.setEmptyText(getString(R.string.contact_list_empty));
        
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // Activities containing this fragment must implement its callbacks.
        if (!(activity instanceof Callbacks)) {
            throw new IllegalStateException("Activity must implement fragment's callbacks.");
        }

        mCallbacks = (Callbacks) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();

        // Reset the active callbacks interface to the dummy implementation.
        mCallbacks = sDummyCallbacks;
    }

    @Override
    public void onListItemClick(ListView listView, View view, int position, long id) {
        super.onListItemClick(listView, view, position, id);

        // Notify the active callbacks interface (the activity, if the
        // fragment is attached to one) that an item has been selected.
        Cursor cursor = listAdapter.getCursor();
        cursor.moveToPosition(position);
        mCallbacks.onItemSelected(cursor.getString(cursor.getColumnIndex(DatabaseContract.Message._ID))); // see hack projection
        setActivatedPosition(position);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mActivatedPosition != ListView.INVALID_POSITION) {
            // Serialize and persist the activated item position.
            outState.putInt(STATE_ACTIVATED_POSITION, mActivatedPosition);
        }
    }

    /**
     * Turns on activate-on-click mode. When this mode is on, list items will be
     * given the 'activated' state when touched.
     */
    public void setActivateOnItemClick(boolean activateOnItemClick) {
        // When setting CHOICE_MODE_SINGLE, ListView will automatically
        // give items the 'activated' state when touched.
        getListView().setChoiceMode(activateOnItemClick
                ? ListView.CHOICE_MODE_SINGLE
                : ListView.CHOICE_MODE_NONE);
    }

    private void setActivatedPosition(int position) {
        if (position == ListView.INVALID_POSITION) {
            getListView().setItemChecked(mActivatedPosition, false);
        } else {
            getListView().setItemChecked(position, true);
        }
        mActivatedPosition = position;
    }

	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		String[] projection = {
                Message.COLUMN_NAME_CONTACT + " AS " + Message._ID, // Hack so we can use group by in content provider
                Message.COLUMN_NAME_MESSAGE,
                };
        String orderBy = Message.COLUMN_NAME_DATE + " DESC";
        String selection = null;
        String[] selectionArgs = new String[]{};
        CursorLoader cursorLoader = new CursorLoader(getActivity(), MessageContentProvider.CONTENT_MESSAGE_URI, projection, selection, selectionArgs, orderBy);
        return cursorLoader;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> arg0, Cursor c) {
		listAdapter.swapCursor(c);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		listAdapter.swapCursor(null);
	}
}
