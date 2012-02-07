package org.ragetemplate.contentproviders;

import android.net.Uri;
import android.provider.BaseColumns;

public final class RageProviderContracts {

	public static final String AUTHORITY = "org.ragetemplate.rageprovider";

	// RAGE COMIC TABLE CONTRACT
	public static final class RageComics implements BaseColumns {

		public static final String TABLE_NAME = "ragecomics";
		private static final String SCHEME = "content://";
		private static final String URI_PATH_COMICS = "/rage";
		// Note the slash on the end of this one, as opposed to the URI_PATH_COMICS, which has no slash.
		private static final String URI_PATH_WITH_COMIC_ID = "/rage/";
		public static final int COMIC_ID_PATH_POSITION = 1;

		// content://org.ragetemplate.rageprovider/rage
		public static final Uri CONTENT_URI = Uri.parse(SCHEME + AUTHORITY + URI_PATH_COMICS);
		// content://org.ragetemplate.rageprovider/rage/ -- used for content provider insert() call
		public static final Uri CONTENT_ID_URI_BASE = Uri.parse(SCHEME + AUTHORITY + URI_PATH_WITH_COMIC_ID);
		// content://org.ragetemplate.rageprovider/rage/#
		public static final Uri CONTENT_ID_URI_PATTERN = Uri.parse(SCHEME + AUTHORITY + URI_PATH_WITH_COMIC_ID + "/#");

		public static final String[] ALL_COLUMNS;

		static {
			ALL_COLUMNS = new String[] { RageComics._ID, RageComics.TITLE, RageComics.AUTHOR, RageComics.URL, RageComics.THUMBNAIL, RageComics.CREATED };
		}
		
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
		 * Column name for comic URL
		 * <P>
		 * Type: TEXT
		 * </P>
		 */
		public static final String URL = "url";

		/**
		 * Column name for the comic thumbnail URL
		 * <P>
		 * Type: TEXT
		 * </P>
		 */
		public static final String THUMBNAIL = "thumbnail";

		/**
		 * Column name for the creation date
		 * <P>
		 * Type: LONG
		 * </P>
		 */
		public static final String CREATED = "timestamp";

		// Prevent instantiation of this class
		private RageComics() {
		}
	}

	private RageProviderContracts() {
		// disallow instantiation
	}
}
