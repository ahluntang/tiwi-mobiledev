package be.ugent.oomo.labo_2;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import be.ugent.oomo.labo_2.contentprovider.MessageContentProvider;
import be.ugent.oomo.labo_2.database.DatabaseContract;
import be.ugent.oomo.labo_2.database.DatabaseContract.Message;
import be.ugent.oomo.labo_2.utilities.ChatServerUtilities;

/**
 * A fragment representing a single ChatContact detail screen. This fragment is
 * either contained in a {@link ChatMessageListActivity} in two-pane mode (on
 * tablets) or a {@link ChatMessageDetailActivity} on handsets.
 */
public class ChatMessageDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

	private String contact = null;
	private CursorAdapter listAdapter;
	
	private EditText replyMessageEditView;
	private TextView titleTextView;
	private ListView messagesListView;

	private EditText toEditText;
	private Button sendButton;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        listAdapter = new MessagesAdapter(getActivity(), null);
        Bundle arg = getArguments();
        if (arg != null) {
	    	contact = arg.getString(DatabaseContract.Message.COLUMN_NAME_CONTACT);
	    	update();
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_chatmessage_detail, container, false);
		titleTextView = ((TextView) rootView.findViewById(R.id.title));
		toEditText = ((EditText) rootView.findViewById(R.id.to));
		messagesListView = ((ListView) rootView.findViewById(R.id.messages));
		messagesListView.setAdapter(listAdapter);
		replyMessageEditView = ((EditText) rootView.findViewById(R.id.reply_message));
		sendButton = ((Button) rootView.findViewById(R.id.reply));
		sendButton.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
						imm.hideSoftInputFromWindow(replyMessageEditView.getWindowToken(), 0);
						contact = toEditText.getText().toString();
						ChatServerUtilities.sendMessageAsync(getActivity()
								, replyMessageEditView.getText().toString()
								, contact);
						replyMessageEditView.setText("");
						editableContact(false);
						update();
					}
				});
		editableContact(contact == null);
		return rootView;
	}
	
	private void update() {
		if (contact != null) {
			getLoaderManager().initLoader(0, null, this);
		}
	}
	
	private void editableContact(boolean editable) {
		if (editable) {
			titleTextView.setText(getActivity().getString(R.string.new_message));
			toEditText.setText(null);
			toEditText.setVisibility(View.VISIBLE);
			sendButton.setText(R.string.send);
		} else {
			titleTextView.setText(contact);
			toEditText.setText(contact);
			toEditText.setVisibility(View.GONE);
			sendButton.setText(R.string.reply);
		}
	}
	
	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		String[] projection = {
                Message._ID,
                Message.COLUMN_NAME_DATE,
                Message.COLUMN_NAME_MESSAGE,
                Message.COLUMN_NAME_SEND_FROM_APP,
                };
        String orderBy = Message.COLUMN_NAME_DATE + " ASC";
        String selection = Message.COLUMN_NAME_CONTACT + "=?";
        String[] selectionArgs = new String[]{
        		contact,
        };
        return new CursorLoader(getActivity(), MessageContentProvider.CONTENT_MESSAGE_URI, projection, selection, selectionArgs, orderBy);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> arg0, Cursor data) {
		listAdapter.swapCursor(data);
		messagesListView.post(new Runnable() {
	        @Override
	        public void run() {
	        	messagesListView.setSelection(listAdapter.getCount() - 1);
	        }
	    });
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		listAdapter.swapCursor(null);
	}
}
