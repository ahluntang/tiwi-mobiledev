package be.ugent.oomo.labo_2;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.ResourceCursorAdapter;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import be.ugent.oomo.labo_2.database.DatabaseContract;

public class MessagesAdapter extends ResourceCursorAdapter {

	private static class ViewHolder {
		int dateIndex;
		int messageIndex;
		int sendFromAppIndex;
		TextView date;
		TextView message;
	}

	public MessagesAdapter(Context context, Cursor c) {
		super(context, R.layout.chatmessage_listitem, c, 0);
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		ViewHolder holder = (ViewHolder) view.getTag();
		holder.date.setText(cursor.getString(holder.dateIndex));
		holder.message.setText(cursor.getString(holder.messageIndex));
		boolean left = (cursor.getInt(holder.sendFromAppIndex) == 0);
		holder.message.setBackgroundResource(left ? R.drawable.bubble_yellow : R.drawable.bubble_green);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(holder.message.getLayoutParams());
		params.gravity = left ? Gravity.LEFT : Gravity.RIGHT;
		holder.message.setLayoutParams(params);
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		View view = super.newView(context, cursor, parent);
		ViewHolder holder = new ViewHolder();
		holder.date = (TextView) view.findViewById(R.id.date);
		holder.message = (TextView) view.findViewById(R.id.message);
		holder.dateIndex = cursor.getColumnIndexOrThrow(DatabaseContract.Message.COLUMN_NAME_DATE);
		holder.messageIndex = cursor.getColumnIndexOrThrow(DatabaseContract.Message.COLUMN_NAME_MESSAGE);
		holder.sendFromAppIndex = cursor.getColumnIndexOrThrow(DatabaseContract.Message.COLUMN_NAME_SEND_FROM_APP);
		view.setTag(holder);
		return view;
	}
}
