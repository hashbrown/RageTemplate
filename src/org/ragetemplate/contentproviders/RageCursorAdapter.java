package org.ragetemplate.contentproviders;

import org.ragetemplate.R;
import org.ragetemplate.contentproviders.RageProviderContracts.RageComics;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class RageCursorAdapter extends CursorAdapter {


	public RageCursorAdapter(Context context) {
		super(context, null, 0);
	}

	@Override
	public void bindView(View row, final Context context, final Cursor cursor) {
		
		Uri thumbUri = Uri.parse(cursor.getString(cursor.getColumnIndex(RageComics.THUMBNAIL_URI)));
		String title = cursor.getString(cursor.getColumnIndex(RageComics.TITLE));

		TextView titleView = (TextView) row.findViewById(R.id.title);
		titleView.setText(title);
		ImageView thumbView = (ImageView) row.findViewById(R.id.thumbnail);
		thumbView.setImageURI(thumbUri);
	}
	
	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		return LayoutInflater.from(context).inflate(R.layout.rage_row, parent, false);
	}
}
