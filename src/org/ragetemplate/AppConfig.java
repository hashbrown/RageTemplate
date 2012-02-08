package org.ragetemplate;

import java.io.File;

import android.content.Context;

public class AppConfig {

	private final Context ctx;
	
	public AppConfig(Context ctx) {
		this.ctx = ctx;
	}
	
	public File getComicsDir() {
		return new File(this.ctx.getFilesDir(), this.ctx.getString(R.string.rage_comics_folder));
	}

	public File getThumbnailsDir() {
		return new File(this.ctx.getFilesDir(), this.ctx.getString(R.string.rage_thumbnails_folder));
		
	}
}
