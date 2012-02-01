package org.ragetemplate.data;

public class RageComic {

	private String title;
	private String author;
	private String url;
	private String thumbnailUrl;
	private long created;
	
	public RageComic(String title, String author, String url, String thumbnailUrl, long timestamp) {
		super();
		this.title = title;
		this.author = author;
		this.url = url;
		this.thumbnailUrl = thumbnailUrl;
		this.created = timestamp;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getThumbnailUrl() {
		return thumbnailUrl;
	}

	public void setThumbnailUrl(String thumbnailUrl) {
		this.thumbnailUrl = thumbnailUrl;
	}

	public long getCreated() {
		return created;
	}

	public void setCreated(long created) {
		this.created = created;
	}
}
