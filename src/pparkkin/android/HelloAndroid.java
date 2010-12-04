package pparkkin.android;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.AdapterView.OnItemClickListener;

public class HelloAndroid extends Activity {
    private static final float LOCATION_UPDATE_MIN_DISTANCE_NETWORK = (float) 0;
	private static final float LOCATION_UPDATE_MIN_DISTANCE_GPS = (float) 0;
	private static final long LOCATION_UPDATE_MIN_TIME = 60000; // 1min (60000ms)
	
	private static GridView gallery;
	private static LocationManager locationManager;
	
	private static ProgressDialog spinner;
	
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

        /* Set up listener to listen to location updates */
        LocationListener locationListener = new LocationListener() {
        	public void onLocationChanged(Location location) {
        		// Called when a new location is found by the network location provider.
                HelloAndroid.this.setLocation(location);
        	}

			public void onStatusChanged(String provider, int status, Bundle extras) {}

        	public void onProviderEnabled(String provider) {}

        	public void onProviderDisabled(String provider) {}
        };
        
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
        		                               LOCATION_UPDATE_MIN_TIME,
        		                               LOCATION_UPDATE_MIN_DISTANCE_GPS,
        		                               locationListener);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
        		                               LOCATION_UPDATE_MIN_TIME,
        		                               LOCATION_UPDATE_MIN_DISTANCE_NETWORK,
        		                               locationListener);
    }

	private void initLocationManager() {
		if (locationManager == null)
        	locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
	}

	private void initGallery() {
		if (gallery == null)
			gallery = (GridView) findViewById(R.id.gridview);
	}

	/* Location update using AsyncTask
	 * I don't like this. It feels to me like there's a big chance of
	 * a race condition if two updates are executing at the same time.
	 */
	private void setLocation(Location location) {
		new UpdateLocationTask().execute(location);
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

	public void showSpinner() {
		spinner = ProgressDialog.show(HelloAndroid.this, "",
				  "Loading Images. Please wait...", true);
	}

	public void dismissSpinner() {
		spinner.dismiss();
	}
}