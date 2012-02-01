package org.ragetemplate.contentproviders;

import java.util.HashMap;

import org.ragetemplate.contentproviders.RageProviderContracts.RageComics;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

public class RageProvider extends ContentProvider {

	public static final String TAG = "RageProvider";

	// "projection" map of all the RageComic columns
	private static HashMap<String, String> RageProjectionMap;

	// URI MATCHER-RELATED
	// URI matcher ID for the main rage comics URI pattern
	private static final int MATCHER_DEVICES = 1;
	// URI matcher ID for the single device ID pattern
	private static final int MATCHER_DEVICE_ID = 2;
	private static final UriMatcher uriMatcher;

	// Handle to a new ProviderDbHelper.
	private ProviderDbHelper dbHelper;

	// static 'setup' block
	static {
		// Build up URI matcher
		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		// Add a pattern to route URIs terminated with just "rage comics"
		uriMatcher.addURI(RageProviderContracts.AUTHORITY, RageComics.TABLE_NAME, MATCHER_DEVICES);
		// Add a pattern to route URIs terminated with device IDs
		uriMatcher.addURI(RageProviderContracts.AUTHORITY, RageComics.TABLE_NAME + "/#", MATCHER_DEVICE_ID);

		// Create and initializes a projection map that returns all columns,
		// This map returns a column name for a given string. The two are usually equal, but we need this structure
		// later, down in .query()
		RageProjectionMap = new HashMap<String, String>();

		// Apologies for the formatting here, but the autoformatter forces it this way
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

		switch (uriMatcher.match(uri)) { // Perform the delete based on URI pattern
			case MATCHER_DEVICES:
				// Delete all the rage comics matching the where column/value pairs
				deletedRowsCount = db.delete(RageComics.TABLE_NAME, whereClause, whereValues);
				break;

			case MATCHER_DEVICE_ID:
				// Modify the where clause to only delete the device with the given ID
				String deviceId = uri.getPathSegments().get(RageComics.DEVICE_ID_PATH_POSITION);
				finalWhere = RageComics._ID + " = " + deviceId;
				if (whereClause != null) {
					finalWhere = finalWhere + " AND " + whereClause;
				}

				// Performs the delete.
				deletedRowsCount = db.delete(RageComics.TABLE_NAME, finalWhere, whereValues);
				break;

			// If the incoming pattern is invalid, throws an exception.
			default:
				throw new IllegalArgumentException("Unknown URI " + uri);
		}

		// Notify observers of the the change
		getContext().getContentResolver().notifyChange(uri, null);

		// Returns the number of rows deleted.
		return deletedRowsCount;
	}

	@Override
	public Uri insert(Uri uri, ContentValues initialValues) {
		// Validate the incoming URI.
		if (uriMatcher.match(uri) != MATCHER_DEVICES) {
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
			// Build a new RageComic URI with the new device's ID appended to it.
			Uri deviceUri = ContentUris.withAppendedId(RageComics.CONTENT_ID_URI_BASE, newRowId);
			// Notify observers that our data changed.
			getContext().getContentResolver().notifyChange(deviceUri, null);
			return deviceUri;
		}

		throw new SQLException("Failed to insert row into " + uri); // Insert failed: halt and catch fire.
	}

	@Override
	public Cursor query(Uri uri, String[] selectedColumns, String whereClause, String[] whereValues, String sortOrder) {
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		qb.setTables(RageComics.TABLE_NAME);

		// Choose the projection and adjust the "where" clause based on URI pattern-matching.
		switch (uriMatcher.match(uri)) {
			case MATCHER_DEVICES:
				qb.setProjectionMap(RageProjectionMap);
				break;

			// asking for a single device - use the rage comics projection, but add a where clause to only return the one
			// device
			case MATCHER_DEVICE_ID:
				qb.setProjectionMap(RageProjectionMap);
				// Find the device ID itself in the incoming URI
				String id = uri.getPathSegments().get(RageComics.DEVICE_ID_PATH_POSITION);
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

		// Perform the update based on the incoming URI's pattern
		switch (uriMatcher.match(uri)) {

			case MATCHER_DEVICES:
				// Perform the update and return the number of rows updated.
				updatedRowsCount = db.update(RageComics.TABLE_NAME, updateValues, whereClause, whereValues);
				break;

			case MATCHER_DEVICE_ID:
				String id = uri.getPathSegments().get(RageComics.DEVICE_ID_PATH_POSITION);
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

		/*
		 * Gets a handle to the content resolver object for the current context, and notifies it that the incoming URI
		 * changed. The object passes this along to the resolver framework, and observers that have registered
		 * themselves for the provider are notified.
		 */
		getContext().getContentResolver().notifyChange(uri, null);

		// Returns the number of rows updated.
		return updatedRowsCount;
	}

	@Override
	public String getType(Uri uri) {
		// TODO Auto-generated method stub
		return null;
	}

}
