package org.ragetemplate;

import org.ragetemplate.downloader.RageDownloader;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;


public class RageTemplateActivity extends Activity {
	
	RageDownloader rageDownloader;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.rage_activity);
		this.setProgressBarIndeterminate(true);
		this.setProgressBarIndeterminateVisibility(false);
        
		this.rageDownloader = new RageDownloader(this);
    }
    
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = this.getMenuInflater();
		inflater.inflate(R.menu.rage_activity, menu);
		return true;
	}
    
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
			case R.id.get_more_rage:
				this.setProgressBarIndeterminateVisibility(true);
				this.rageDownloader.getMoreRage(10);
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	
	
}