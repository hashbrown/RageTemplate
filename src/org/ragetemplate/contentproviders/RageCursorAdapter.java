package org.ragetemplate.contentproviders;

import org.ragetemplate.R;
import org.ragetemplate.data.RageComic;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class RageCursorAdapter extends CursorAdapter {

	public RageCursorAdapter(Context context) {
		super(context, null);
	}

	@Override
	public void bindView(View row, final Context context, final Cursor cursor) {
		RageComic c = RageLoader.newComicFromCursor(context, cursor);
		TextView titleView = (TextView) row.findViewById(R.id.title);
		titleView.setText(c.getTitle());
		ImageView thumbView = (ImageView) row.findViewById(R.id.thumbnail);
		thumbView.setImageURI(c.getThumbnailUri());
	}
	
	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		return LayoutInflater.from(context).inflate(R.layout.rage_row, parent, false);
	}
}
