package pparkkin.android;

import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.AdapterView.OnItemClickListener;

public class HelloAndroid extends Activity {
	private static GridView gallery;
	private static LocationManager locationManager;
	
	private static ProgressDialog spinner;
	private Location location;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main); // Can't access views before calling setContentView
    	
        initGallery();
        initLocationManager();
        
        gallery.setOnItemClickListener(new OnItemClickListener() {
			@Override
        	public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
				String address = gallery.getAdapter().getItem(position).toString();
				String u = address.replaceAll("square", "medium");
        		
				Intent i = new Intent(HelloAndroid.this, ImageDisplay.class);
				i.putExtra("url", u);
				startActivity(i);
        	}
        });
    	
    	if (location == null) {
	    	// get a current location
	        List<String> providers = locationManager.getProviders(true);
	        
	        Location bestLocation = null;
	        
	        for (String p : providers) {
	        	Location l = locationManager.getLastKnownLocation(p);
	        	if (l == null) continue;
	        	if (bestLocation == null) bestLocation = l;
	        	if (l.getAccuracy() < bestLocation.getAccuracy())
	        		bestLocation = l;
	        }
	
	        if (bestLocation != null)
	        	setLocation(bestLocation);
    	}

    }
    
	private void initLocationManager() {
		if (locationManager == null)
        	locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
	}

	@SuppressWarnings("unchecked")
	private void initGallery() {
		if (gallery == null)
			gallery = (GridView) findViewById(R.id.gridview);
		
		final Object data = getLastNonConfigurationInstance();
		
		if (data == null) return;
		
		final Map<Uri, Drawable> images = (Map<Uri, Drawable>) data;
		PanoramioImageAdapter p = new PanoramioImageAdapter(this, images);
		setAdapter(p);
	}

	@Override
	public Object onRetainNonConfigurationInstance() {
		final Map<Uri, Drawable> iM = ((PanoramioImageAdapter) gallery.getAdapter()).getImages();
		return iM;
	}
	
	/* Location update using AsyncTask
	 * I don't like this. It feels to me like there's a big chance of
	 * a race condition if two updates are executing at the same time.
	 */
	private void setLocation(Location location) {
		new UpdateLocationTask().execute(location);
		this.location = location;
	}
	
	private class UpdateLocationTask extends AsyncTask<Location, Void, PanoramioImageAdapter> {
		protected void onPreExecute() {
			HelloAndroid.this.showSpinner();
		}

		@Override
		protected PanoramioImageAdapter doInBackground(Location... locations) {
			return new PanoramioImageAdapter(HelloAndroid.this, locations[0]);
		}
		
		protected void onPostExecute(PanoramioImageAdapter p) {
			HelloAndroid.this.setAdapter(p);
			HelloAndroid.this.dismissSpinner();
		}
	}
	
	private void setAdapter(PanoramioImageAdapter p) {
		gallery.setAdapter(p);
	}

	synchronized public void showSpinner() {
			if (spinner == null || !spinner.isShowing())
				spinner = ProgressDialog.show(this, "",
						"Loading Images. Please wait...", true);
	}

	synchronized public void dismissSpinner() {
			if (spinner != null && spinner.isShowing())
				spinner.dismiss();
	}
}