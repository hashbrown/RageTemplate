package org.ragetemplate.contentproviders;

import org.ragetemplate.contentproviders.RageProviderContracts.RageComics;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * This creates/opens the database.
 */
public class ProviderDbHelper extends SQLiteOpenHelper {

	public final String TAG = getClass().getSimpleName();

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
		sqlBuilder.append(RageComics.TITLE + " TEXT, ");
		sqlBuilder.append(RageComics.AUTHOR + " TEXT, ");
		sqlBuilder.append(RageComics.URL + " TEXT, ");
		sqlBuilder.append(RageComics.THUMBNAIL + " TEXT");
		sqlBuilder.append(RageComics.CREATED + " INTEGER");
		sqlBuilder.append(");");
		String sql = sqlBuilder.toString(); // putting this here to make it easier to breakpoint on
		Log.i(TAG, "Creating DB table with string: '" + sql + "'");
		db.execSQL(sql);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
	}

}
