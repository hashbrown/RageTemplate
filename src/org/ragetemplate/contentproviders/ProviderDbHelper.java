package org.ragetemplate.contentproviders;

import org.ragetemplate.contentproviders.RageProviderContracts.RageComics;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * This creates, updates, and opens the database.  Opening is handled by the superclass, we handle 
 * the create & upgrade steps
 */
public class ProviderDbHelper extends SQLiteOpenHelper {

	public final String TAG = getClass().getSimpleName();

	//Name of the database file
	private static final String DATABASE_NAME = "rage.db";
	private static final int DATABASE_VERSION = 1;

	public ProviderDbHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		StringBuilder sqlBuilder = new StringBuilder();
		sqlBuilder.append("CREATE TABLE " + RageComics.TABLE_NAME + " (");
		sqlBuilder.append(RageComics._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, ");
		sqlBuilder.append(RageComics.NAME + " TEXT, ");
		sqlBuilder.append(RageComics.TITLE + " TEXT, ");
		sqlBuilder.append(RageComics.AUTHOR + " TEXT, ");
		sqlBuilder.append(RageComics.COMIC_FILENAME + " TEXT, ");
		sqlBuilder.append(RageComics.THUMBNAIL_FILENAME + " TEXT, ");
		sqlBuilder.append(RageComics.CREATED + " INTEGER, ");
		sqlBuilder.append(RageComics.IS_NSFW + " INTEGER");
		sqlBuilder.append(");");
		String sql = sqlBuilder.toString();
		Log.i(TAG, "Creating DB table with string: '" + sql + "'");
		db.execSQL(sql);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		//Gets called when the database is upgraded, i.e. the version number changes
	}

}
