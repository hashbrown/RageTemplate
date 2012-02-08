package org.ragetemplate.contentproviders;

import java.util.Date;

import org.ragetemplate.contentproviders.RageProviderContracts.RageComics;
import org.ragetemplate.data.RageComic;

import android.content.Context;
import android.content.CursorLoader;
import android.database.Cursor;
import android.net.Uri;

public class RageLoader extends CursorLoader {

	public static final String TAG = "RageLoader";

	// our 'projection' of columns we want to receive
	static final String SORT_ORDER = RageComics.CREATED + " ASC";

	public static RageComic newComicFromCursor(Cursor cursor) {
		// Column numbers here rely on the ARCHIVE_COLUMNS list above
		String name = cursor.getString(cursor.getColumnIndex(RageComics.NAME));
		String title = cursor.getString(cursor.getColumnIndex(RageComics.TITLE));
		String author = cursor.getString(cursor.getColumnIndex(RageComics.AUTHOR));
		Uri imageUri = Uri.parse(cursor.getString(cursor.getColumnIndex(RageComics.IMAGE_URI)));
		Uri thumbUri = Uri.parse(cursor.getString(cursor.getColumnIndex(RageComics.THUMBNAIL_URI)));
		long timestamp = cursor.getLong(cursor.getColumnIndex(RageComics.CREATED));
		boolean isNSFW = (cursor.getInt(cursor.getColumnIndex(RageComics.IS_NSFW)) == 1);		
		// Build the rageComic from the cursor
		RageComic rageComic = new RageComic(name, title, author, imageUri, thumbUri, new Date(timestamp), isNSFW);
		return rageComic;
	}

	public RageLoader(Context context) {
		// TODO: add some kind of flag to the loader, something which would make it a different data set (i.e.: affect
		// the query), which would let us show how to use multiple loader IDs
		super(context, RageComics.CONTENT_URI, RageComics.ALL_COLUMNS, null, null, SORT_ORDER);
	}	
}
