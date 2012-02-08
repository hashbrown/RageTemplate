package org.ragetemplate.contentproviders;

import android.net.Uri;
import android.provider.BaseColumns;


public final class RageProviderContracts {

	public static final String AUTHORITY = "org.ragetemplate.rageprovider";

	// RAGE COMIC TABLE CONTRACT
	public static final class RageComics implements BaseColumns {

		public static final String TABLE_NAME = "ragecomics";

		// URI DEFS
		static final String SCHEME = "content://";
		public static final String URI_PREFIX = SCHEME + AUTHORITY;
		private static final String URI_PATH_COMICS = "/" + TABLE_NAME;
		// Note the slash on the end of this one, as opposed to the URI_PATH_COMICS, which has no slash.
		private static final String URI_PATH_WITH_COMIC_ID = "/" + TABLE_NAME + "/";
		public static final int COMIC_ID_PATH_POSITION = 1;

		// content://org.ragetemplate.rageprovider/rage
		public static final Uri CONTENT_URI = Uri.parse(URI_PREFIX + URI_PATH_COMICS);
		// content://org.ragetemplate.rageprovider/rage/ -- used for content provider insert() call
		public static final Uri CONTENT_ID_URI_BASE = Uri.parse(SCHEME + AUTHORITY + URI_PATH_WITH_COMIC_ID);
		// content://org.ragetemplate.rageprovider/rage/#
		public static final Uri CONTENT_ID_URI_PATTERN = Uri.parse(SCHEME + AUTHORITY + URI_PATH_WITH_COMIC_ID + "#");

		public static final String[] ALL_COLUMNS;

		static {
			ALL_COLUMNS = new String[] { 
					RageComics._ID, 
					RageComics.NAME,
					RageComics.TITLE, 
					RageComics.AUTHOR, 
					RageComics.COMIC_FILENAME, 
					RageComics.THUMBNAIL_FILENAME, 
					RageComics.CREATED,
					RageComics.IS_NSFW					
			};
		}
		
		/**
		 * Column name for the 'name' (the unique ID + 'kind' to refer to a comic with Reddit's JSON API)
		 * <P>
		 * Type: TEXT
		 * </P>
		 */
		public static final String NAME = "name";		

		/**
		 * Column name for the comic title
		 * 
		 * <P>
		 * Type: TEXT
		 * </P>
		 * 
		 */
		public static final String TITLE = "title";

		/**
		 * Column name for the comic author
		 * <P>
		 * Type: TEXT
		 * </P>
		 */
		public static final String AUTHOR = "author";

		/**
		 * Column name for comic file URI
		 * <P>
		 * Type: TEXT
		 * </P>
		 */
		public static final String COMIC_FILENAME = "comic_filename";

		/**
		 * Column name for the comic's thumbnail file URI
		 * <P>
		 * Type: TEXT
		 * </P>
		 */
		public static final String THUMBNAIL_FILENAME = "thumbnail_filename";

		/**
		 * Column name for the creation date
		 * <P>
		 * Type: LONG  (UNIX timestamp)
		 * </P>
		 */
		public static final String CREATED = "timestamp";

		/**
		 * Column name for a boolean indicating if the comic is NSFW
		 * <P>
		 * Type: INTEGER (actually a boolean - 0 for False, 1 for True) - sqlite has no proper booleans
		 * </P>
		 */
		public static final String IS_NSFW = "is_nsfw";		

		// Prevent instantiation of this class
		private RageComics() {
		}
	}

	private RageProviderContracts() {
		// disallow instantiation
	}
}
