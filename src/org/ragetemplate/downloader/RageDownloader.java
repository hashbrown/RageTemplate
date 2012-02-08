package org.ragetemplate.downloader;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ragetemplate.R;
import org.ragetemplate.RageTemplateActivity;
import org.ragetemplate.contentproviders.RageProviderContracts.RageComics;
import org.ragetemplate.data.RageComic;
import org.ragetemplate.util.Py;

import android.content.ContentValues;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;


public class RageDownloader {

	private static final String TAG = "RageDownloader";
	
	private final RageTemplateActivity activity;
	private final File imagesDir;
	private final File thumbnailsDir;

	private String lastLoadedComicName;
	
	
	public RageDownloader(RageTemplateActivity activity) {
		this.activity = activity;
		
		this.imagesDir = new File(activity.getFilesDir(), activity.getString(R.string.rage_comics_folder));
		this.thumbnailsDir = new File(activity.getFilesDir(), activity.getString(R.string.rage_thumbnails_folder));

		// make sure the image dirs exist
		for (File f : Py.typedList(this.imagesDir, this.thumbnailsDir)) {
			if (!f.exists()) {
				f.mkdir();
			}
		}
	}

	public void getMoreRage(int numberToGet) {
		// this will be null to start with and will be auto-set to the right thing later.  wheeee!
		this.getMoreRage(numberToGet, this.lastLoadedComicName);
	}
	
	public void getMoreRage(final int numberToGet, final String startAfterName) {
		new AsyncTask<Void, Void, Void>() {
			
			@Override protected void onPreExecute() {
				// super ghetto, but I don't have time to do this right
				RageDownloader.this.activity.setProgressBarIndeterminateVisibility(true);				
			}
			
			@Override protected Void doInBackground(Void... params) {
				RageDownloader.this.blockingGetRage(numberToGet, startAfterName);
				return null;
			}
			
			@Override
			protected void onPostExecute(Void v) {
				// super ghetto, but I don't have time to do this right
				RageDownloader.this.activity.setProgressBarIndeterminateVisibility(false);
			}
			
		}.execute(new Void[] {});
	}

	
	void blockingGetRage(int numberToGet, String startAfterName) {
		JSONArray jsonComics = this.getRageComicsJSON(numberToGet, startAfterName);
		List<RageComic> comics = this.buildComicsList(jsonComics);
		for (RageComic comic : comics) {
			this.downloadComicImages(comic);
		}

		if (comics.size() > 0) {
			RageComic lastItem = comics.get(comics.size() - 1);
			this.lastLoadedComicName = lastItem.getName();
			this.performInserts(comics);
		} else {
			Log.e(TAG, "Comics list is empty, nothing to insert!");
		}

		Log.i(TAG, "Rage comic downloads complete.");
	}

	void downloadComicImages(RageComic c) {
		try {
			URL comicUrl = new URL(c.getImageUri().toString());
			File comicFile = new File(this.imagesDir, c.getImageUri().getLastPathSegment());
			this.saveImageAndCloseStream(comicFile, this.buildImageStream(comicUrl));

			if (!c.isNSFW()) {
				File thumbFile = new File(this.thumbnailsDir, c.getThumbnailUri().getLastPathSegment());
				URL thumbUrl = new URL(c.getThumbnailUri().toString());
				this.saveImageAndCloseStream(thumbFile, this.buildImageStream(thumbUrl));				
			}
			
		} catch (MalformedURLException e) {
			Log.e(TAG, "YOU FAIL");
		}
	}

	InputStream buildImageStream(URL url) {
		try {
			HttpURLConnection urlConnection = null;
			urlConnection = (HttpURLConnection) url.openConnection();
			InputStream in = new BufferedInputStream(urlConnection.getInputStream());
			return in;
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}

	List<RageComic> buildComicsList(JSONArray jsonComics) {
		List<RageComic> comics = new ArrayList<RageComic>();
		
		for (int i = 0; i < jsonComics.length(); i++) {
			RageComic rc;
			try {
				rc = this.buildComicFromJSON(jsonComics.getJSONObject(i));
				comics.add(rc);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				Log.e(TAG, "JSON FAIL.", e);
			}
		}
		
		return comics;
	}

	JSONArray getRageComicsJSON(int numberToGet, String startAfterName) {
		Uri jsonUri = Uri.parse("http://www.reddit.com/r/fffffffuuuuuuuuuuuu/top/.json?count=" + numberToGet);
		if (startAfterName != null) {
			Uri.Builder b = jsonUri.buildUpon();
			b.appendQueryParameter("after", startAfterName);
			jsonUri = b.build();
		}

		HttpURLConnection urlConnection = null;
		try {
			urlConnection = (HttpURLConnection) new URL(jsonUri.toString()).openConnection();
			InputStream in = new BufferedInputStream(urlConnection.getInputStream());
			String jsonStr = IOUtils.toString(in, "UTF-8");
			JSONObject topLevel = new JSONObject(jsonStr);
			return topLevel.getJSONObject("data").getJSONArray("children");

		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		} finally {
			if (urlConnection != null) {
				urlConnection.disconnect();
			}
		}
		
		// Something Bad(TM) happened.
		return null;
	}

	RageComic buildComicFromJSON(JSONObject obj) {
		// Example snippet of the JSON objects we get back:
			//      "name":"t3_pcgwc",
			//      "title":"How I watch the Super Bowl",
			//      "author":"Repair_Manmanmanman",
			//      "thumbnail":"http://c.thumbs.redditmedia.com/XVQ7jjUtcvxhhEeP.jpg",
			//      "over_18":false,
			//      "url":"http://imgur.com/ZkMNe",  // add '.png' for the file name
			//       "created_utc":1328487484,
		
		RageComic r = null;
		
		try {
			JSONObject data = obj.getJSONObject("data");
			String name = data.getString("name");
			String title = data.getString("title");
			String author = data.getString("author");
			
			Uri thumbnailUri = null;
			try {
				thumbnailUri = Uri.parse(data.getString("thumbnail"));
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			}
			boolean NSFW = data.getBoolean("over_18");
			Date createDate = new Date(data.getLong("created_utc"));
			String imageUriStr = data.getString("url");
			if (!imageUriStr.endsWith(".png")) {
				imageUriStr = imageUriStr + ".png";
			}
			Uri imageUri = Uri.parse(imageUriStr);
			
			r = new RageComic(name, title, author, imageUri, thumbnailUri, createDate, NSFW);                
			return r;

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return r;
	}

	void saveImageAndCloseStream(File outFile, InputStream imageStream) {
		BufferedOutputStream out = null;
			try {
				out = new BufferedOutputStream(new FileOutputStream(outFile));
				IOUtils.copy(imageStream, out);
				out.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.wtf(TAG, "FAIL when trying to write out a comic image??  File: " + outFile);
			}		
	}

	int performInserts(List<RageComic> comics) {
		Set<ContentValues> insertsValues = new HashSet<ContentValues>();

		for (RageComic comic : comics) {
			insertsValues.add(comic.toContentValues());
		}

		ContentValues[] values = insertsValues.toArray(new ContentValues[insertsValues.size()]);
		int insertedCount = this.activity.getContentResolver().bulkInsert(RageComics.CONTENT_URI, values);
		Log.i(TAG, String.format("Inserted %d nodes records into RageProvider", insertedCount));

		return insertedCount;
	}	
}
