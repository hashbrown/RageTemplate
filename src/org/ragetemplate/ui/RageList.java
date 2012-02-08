package org.ragetemplate.ui;

import org.ragetemplate.contentproviders.RageCursorAdapter;
import org.ragetemplate.contentproviders.RageLoader;
import org.ragetemplate.data.RageComic;

import android.app.ListFragment;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ListView;


public class RageList extends ListFragment {

	private final String TAG = getClass().getSimpleName();
	
	RageCursorAdapter adapter;
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		this.setEmptyText("(No comics loaded!)");
		this.setHasOptionsMenu(true);

		adapter = new RageCursorAdapter(this.getActivity());
		setListAdapter(adapter);
//		this.setListShown(false); // Start out with a progress indicator.
	}
	
	protected RageComic getComicData(int index) {
		try {
			Object thing = this.adapter.getItem(index);
			if (thing == null) {
				return null;
			}
			return RageLoader.newComicFromCursor((Cursor) thing);
		} catch (IndexOutOfBoundsException ioob) {
			Log.w(TAG, "getComicData() FAIL: IndexOutOfBoundsException for index " + index, ioob);
			return null;
		}
	}

	@Override
	public void onListItemClick(ListView lv, View v, int position, long id) {
		RageComic c = this.getComicData(position);
		Intent viewIntent = new Intent(Intent.ACTION_VIEW);
		viewIntent.setDataAndType(c.getImageUri(), this.getMimeType(c.getImageUri()));
		this.startActivity(viewIntent);
	}

	
	// LOADER CALLBACKS
	public Loader<Cursor> onCreateLoader(int loaderId, Bundle args) {
		// The loader ID and args can be used to pass args into your Loader subclass, but we aren't taking advantage
		// of it here.
		Log.i(TAG, "Entering onCreateLoader()");
		return new RageLoader(this.getActivity());
	}

	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		// Swap in the new cursor. (The framework will take care of closing the old cursor once we return.)
		Log.i(TAG, "Entering onLoadFinished()");
		adapter.swapCursor(data);
	}

	public void onLoaderReset(Loader<Cursor> loader) {
		// This is called when the last Cursor provided to onLoadFinished() above is about to be closed. We need to
		// make sure we are no longer using it.
		Log.i(TAG, "Entering onLoaderReset()");
		this.adapter.swapCursor(null);
	}
	// END: LOADER CALLBACKS

	public String getMimeType(Uri fileUri) {
		String extension = MimeTypeMap.getFileExtensionFromUrl(fileUri.toString());
		String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
		return mimeType;
	}

}
