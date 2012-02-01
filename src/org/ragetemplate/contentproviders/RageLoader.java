package org.ragetemplate.contentproviders;

import java.util.ArrayList;
import java.util.List;

import org.ragetemplate.contentproviders.RageProviderContracts.RageComics;
import org.ragetemplate.data.RageComic;

import android.content.Context;
import android.content.CursorLoader;
import android.database.Cursor;

public class RageLoader extends CursorLoader {

	public static final String TAG = "RageLoader";

	// our 'projection' of columns we want to receive
	static final String SORT_ORDER = RageComics.CREATED + " ASC";

	public static RageComic getRageComicFromCursor(Cursor cursor) {
		// Column numbers here rely on the ARCHIVE_COLUMNS list above
		String title = cursor.getString(cursor.getColumnIndex(RageComics.TITLE));
		String author = cursor.getString(cursor.getColumnIndex(RageComics.AUTHOR));
		String url = cursor.getString(cursor.getColumnIndex(RageComics.URL));
		String thumbnail = cursor.getString(cursor.getColumnIndex(RageComics.THUMBNAIL));
		long timestamp = cursor.getLong(cursor.getColumnIndex(RageComics.CREATED));
		
		// Build the rageComic from the cursor
		RageComic rageComic = new RageComic(title, author, url, thumbnail, timestamp);
		return rageComic;
	}

	public RageLoader(Context context) {
		super(context, RageComics.CONTENT_URI, RageComics.ALL_COLUMNS, null, null, SORT_ORDER);
	}
}
