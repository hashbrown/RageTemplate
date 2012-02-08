package org.ragetemplate.data;

import java.util.Date;

import org.ragetemplate.contentproviders.RageProviderContracts.RageComics;

import android.content.ContentValues;
import android.net.Uri;


public class RageComic {

	private final String name;
	private final String title;
	private final String author;
	private final Uri imageUri;
	private Uri thumbnailUri;
	private final Date created;
	private final boolean NSFW;
	
	public RageComic(String name, String title, String author, Uri imageUri, Uri thumbnailUri, Date created, boolean NSFW) {
		super();
		this.name = name;
		this.title = title;
		this.author = author;
		this.imageUri = imageUri;
		this.thumbnailUri = thumbnailUri;
		this.created = created;
		this.NSFW = NSFW;
	}

	public String getName() {
		return name;
	}

	public String getTitle() {
		return title;
	}

	public String getAuthor() {
		return author;
	}

	public Uri getImageUri() {
		return imageUri;
	}

	public Uri getThumbnailUri() {
		return thumbnailUri;
	}

	public Date getCreated() {
		return created;
	}

	public boolean isNSFW() {
		return NSFW;
	}

	public ContentValues toContentValues() {
		ContentValues rowData = new ContentValues();
		rowData.put(RageComics.NAME, this.getName());
		rowData.put(RageComics.TITLE, this.getTitle());
		rowData.put(RageComics.AUTHOR, this.getAuthor());
		rowData.put(RageComics.COMIC_FILENAME, this.getImageUri().getLastPathSegment());
		rowData.put(RageComics.THUMBNAIL_FILENAME, this.getThumbnailUri().getLastPathSegment());
		rowData.put(RageComics.CREATED, this.created.getTime());
		rowData.put(RageComics.IS_NSFW, this.NSFW);		
		return rowData;
	}
}
