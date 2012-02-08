package org.ragetemplate.contentproviders;

import java.io.File;
import java.util.Date;

import org.ragetemplate.AppConfig;
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

	public static RageComic newComicFromCursor(Context ctx, Cursor cursor) {
		// Column numbers here rely on the ARCHIVE_COLUMNS list above
		String name = cursor.getString(cursor.getColumnIndex(RageComics.NAME));
		String title = cursor.getString(cursor.getColumnIndex(RageComics.TITLE));
		String author = cursor.getString(cursor.getColumnIndex(RageComics.AUTHOR));
		long timestamp = cursor.getLong(cursor.getColumnIndex(RageComics.CREATED));
		boolean isNSFW = (cursor.getInt(cursor.getColumnIndex(RageComics.IS_NSFW)) == 1);		

		AppConfig cfg = new AppConfig(ctx);
		String comicFileName = cursor.getString(cursor.getColumnIndex(RageComics.COMIC_FILENAME));
		Uri comicUri = Uri.fromFile(new File(cfg.getComicsDir(), comicFileName));
		String thumbnailFileName = cursor.getString(cursor.getColumnIndex(RageComics.THUMBNAIL_FILENAME));
		Uri thumbnailUri = Uri.fromFile(new File(cfg.getThumbnailsDir(), thumbnailFileName));
		
		// Build the rageComic from the cursor
		RageComic rageComic = new RageComic(name, title, author, comicUri, thumbnailUri, new Date(timestamp), isNSFW);
		return rageComic;
	}

	public RageLoader(Context context) {
		// TODO: add some kind of flag to the loader, something which would make it a different data set (i.e.: affect
		// the query), which would let us show how to use multiple loader IDs
		super(context, RageComics.CONTENT_URI, RageComics.ALL_COLUMNS, null, null, SORT_ORDER);
	}	
}
