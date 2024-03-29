package org.ragetemplate.contentproviders;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;

import org.ragetemplate.AppConfig;
import org.ragetemplate.contentproviders.RageProviderContracts.RageComics;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.util.Log;

public class RageProvider extends ContentProvider {

	public static final String TAG = "RageProvider";

	// "projection" map of all the ragecomics table columns
	private static HashMap<String, String> RageProjectionMap;
	// URI matcher ID for the main rage comics URI pattern
	private static final int MATCHER_COMICS = 1;
	// URI matcher ID for the single rage comic ID pattern
	private static final int MATCHER_COMIC_ID = 2;
	// URI matcher for validating URIs
	private static final UriMatcher uriMatcher;
	// Handle to our ProviderDbHelper.
	private ProviderDbHelper dbHelper;
	
	// static 'setup' block
	static {
		// Build up URI matcher
		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		// Add a pattern to route URIs terminated with just "rageComics"
		uriMatcher.addURI(RageProviderContracts.AUTHORITY, RageComics.TABLE_NAME, MATCHER_COMICS);
		// Add a pattern to route URIs terminated with comic IDs
		uriMatcher.addURI(RageProviderContracts.AUTHORITY, RageComics.TABLE_NAME + "/#", MATCHER_COMIC_ID);

		// Create and initialize a projection map that returns all columns,
		// This map returns a column name for a given string. The two are usually equal, but we need this structure
		// later, down in .query()
		RageProjectionMap = new HashMap<String, String>();
		for (String column : RageComics.ALL_COLUMNS) {
			RageProjectionMap.put(column, column);
		}
	}

	@Override
	public boolean onCreate() {
		this.dbHelper = new ProviderDbHelper(this.getContext());
		return true; // if there are any issues, they'll be reported as exceptions
	}

	@Override
	public int delete(Uri uri, String whereClause, String[] whereValues) {
		SQLiteDatabase db = this.dbHelper.getWritableDatabase();
		String finalWhere;
		int deletedRowsCount;

		// Perform the delete based on URI pattern
		db.beginTransaction();
		try {
			switch (uriMatcher.match(uri)) {
			case MATCHER_COMICS:
				// Delete all the rage comics matching the where column/value pairs
				deletedRowsCount = db.delete(RageComics.TABLE_NAME, whereClause, whereValues);
				break;

			case MATCHER_COMIC_ID:
				//Delete the comic with the given ID
				String comicId = uri.getPathSegments().get(RageComics.COMIC_ID_PATH_POSITION);
				finalWhere = RageComics._ID + " = " + comicId;
				if (whereClause != null) {
					finalWhere = finalWhere + " AND " + whereClause;
				}

				// Perform the delete.
				deletedRowsCount = db.delete(RageComics.TABLE_NAME, finalWhere, whereValues);
				break;

			// If the incoming URI is invalid, throws an exception.
			default:
				throw new IllegalArgumentException("Unknown URI " + uri);
			}
		} finally {
			db.endTransaction();
		}

		// Notify observers of the the change
		getContext().getContentResolver().notifyChange(uri, null);

		// Returns the number of rows deleted.
		return deletedRowsCount;
	}

	@Override
	public Uri insert(Uri uri, ContentValues initialValues) {
		// Validate the incoming URI.
		if (uriMatcher.match(uri) != MATCHER_COMICS) {
			throw new IllegalArgumentException("Unknown URI " + uri);
		}

		ContentValues values;
		if (initialValues != null) {
			values = new ContentValues(initialValues);
		} else {
			throw new SQLException("ContentValues arg for .insert() is null, cannot insert row.");
		}

		long newRowId = this.dbHelper.getWritableDatabase().insert(RageComics.TABLE_NAME, null, values);

		if (newRowId > 0) { // if rowID is -1, it means the insert failed
			// Build a new RageComic URI with the new comic's ID appended to it.
			Uri comicUri = ContentUris.withAppendedId(RageComics.CONTENT_ID_URI_BASE, newRowId);
			// Notify observers that our data changed.
			getContext().getContentResolver().notifyChange(comicUri, null);
			return comicUri;
		}

		throw new SQLException("Failed to insert row into " + uri); // Insert failed: halt and catch fire.
	}

	@Override
	public Cursor query(Uri uri, String[] selectedColumns, String whereClause, String[] whereValues, String sortOrder) {
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		qb.setTables(RageComics.TABLE_NAME);

		// Choose the projection and adjust the "where" clause based on URI pattern-matching.
		switch (uriMatcher.match(uri)) {
		case MATCHER_COMICS:
			qb.setProjectionMap(RageProjectionMap);
			break;

		// asking for a single comic - use the rage comics projection, but add a where clause to only return the one
		// comic
		case MATCHER_COMIC_ID:
			qb.setProjectionMap(RageProjectionMap);
			// Find the comic ID itself in the incoming URI
			String id = uri.getPathSegments().get(RageComics.COMIC_ID_PATH_POSITION);
			qb.appendWhere(RageComics._ID + "=" + id);
			break;

		default:
			// If the URI doesn't match any of the known patterns, throw an exception.
			throw new IllegalArgumentException("Unknown URI " + uri);
		}

		SQLiteDatabase db = this.dbHelper.getReadableDatabase();
		// the two nulls here are 'grouping' and 'filtering by group'
		Cursor cursor = qb.query(db, selectedColumns, whereClause, whereValues, null, null, sortOrder);

		// Tell the Cursor about the URI to watch, so it knows when its source data changes
		cursor.setNotificationUri(getContext().getContentResolver(), uri);
		return cursor;
	}

	@Override
	public int update(Uri uri, ContentValues updateValues, String whereClause, String[] whereValues) {
		SQLiteDatabase db = this.dbHelper.getWritableDatabase();
		int updatedRowsCount;
		String finalWhere;

		db.beginTransaction();
		// Perform the update based on the incoming URI's pattern
		try {
			switch (uriMatcher.match(uri)) {

			case MATCHER_COMICS:
				// Perform the update and return the number of rows updated.
				updatedRowsCount = db.update(RageComics.TABLE_NAME, updateValues, whereClause, whereValues);
				break;

			case MATCHER_COMIC_ID:
				String id = uri.getPathSegments().get(RageComics.COMIC_ID_PATH_POSITION);
				finalWhere = RageComics._ID + " = " + id;

				// if we were passed a 'where' arg, add that to our 'finalWhere'
				if (whereClause != null) {
					finalWhere = finalWhere + " AND " + whereClause;
				}
				updatedRowsCount = db.update(RageComics.TABLE_NAME, updateValues, finalWhere, whereValues);
				break;

			default:
				// Incoming URI pattern is invalid: halt & catch fire.
				throw new IllegalArgumentException("Unknown URI " + uri);
			}
		} finally {
			db.endTransaction();
		}

		/*
		 * Gets a handle to the content resolver object for the current context,
		 * and notifies it that the incoming URI changed. The object passes this
		 * along to the resolver framework, and observers that have registered
		 * themselves for the provider are notified.
		 */
		if (updatedRowsCount > 0) {
			getContext().getContentResolver().notifyChange(uri, null);
		}

		// Returns the number of rows updated.
		return updatedRowsCount;
	}

	//Default bulkInsert is terrible.  Make it better!
	@Override
	public int bulkInsert(Uri uri, ContentValues[] values) {
		this.validateOrThrow(uri);
		SQLiteDatabase db = this.dbHelper.getWritableDatabase();
		db.beginTransaction();
		int insertedCount = 0;
		long newRowId = -1;
		try {
			for (ContentValues cv : values) {
				newRowId = this.insert(uri, cv, db);
				insertedCount++;
			}
			db.setTransactionSuccessful();
			// Build a new Node URI appended with the row ID of the last node to get inserted in the batch
			Uri nodeUri = ContentUris.withAppendedId(RageComics.CONTENT_ID_URI_BASE, newRowId);
			// Notify observers that our data changed.
			getContext().getContentResolver().notifyChange(nodeUri, null);
			return insertedCount;

		} finally {
			db.endTransaction();
		}
	}

	//Used by our implementation of builkInsert
	private long insert(Uri uri, ContentValues initialValues, SQLiteDatabase writableDb) {
		// NOTE: this method does not initiate a transaction - this is up to the caller!
		ContentValues values;
		if (initialValues != null) {
			values = new ContentValues(initialValues);
		} else {
			throw new SQLException("ContentValues arg for .insert() is null, cannot insert row.");
		}

		long newRowId = writableDb.insert(RageComics.TABLE_NAME, null, values);
		if (newRowId == -1) { // if rowID is -1, it means the insert failed
			throw new SQLException("Failed to insert row into " + uri); // Insert failed: halt and catch fire.
		}
		return newRowId;
	}

	private void validateOrThrow(Uri uri) {
		// Validate the incoming URI.
		if (uriMatcher.match(uri) != MATCHER_COMICS) {
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
	}

	@Override
	public String getType(Uri uri) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ParcelFileDescriptor openFile(Uri uri, String mode) throws FileNotFoundException {
		File file = new File(this.getContext().getFilesDir(), uri.getPath());
		ParcelFileDescriptor parcel = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY);
		return parcel;
	}

//	public AssetFileDescriptor openTypedAssetFile(Uri uri, String mimeTypeFilter, Bundle opts) {
//		try {
//			return super.openTypedAssetFile(uri, mimeTypeFilter, opts);
//		} catch (FileNotFoundException e) {
//			Log.e(TAG, "OH NOES!  Can't find file: " + uri);
//		}
//		return null;
//	}
}
