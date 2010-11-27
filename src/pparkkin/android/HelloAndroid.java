package pparkkin.android;

import java.util.List;

import android.app.Activity;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.GridView;

public class HelloAndroid extends Activity {
    private static final float LOCATION_UPDATE_MIN_DISTANCE = (float) 0.01;
	private static final long LOCATION_UPDATE_MIN_TIME = 0;
	
	//private Gallery gallery;
	private GridView gallery;
	private Location location;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main); // Can't access views before calling setContentView
    }
    
    @Override
    public void onStart() {
    	super.onStart();
    	
        gallery = (GridView) findViewById(R.id.gridview);
        
        /*
        gallery.setOnItemClickListener(new OnItemClickListener() {
			@Override
        	public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
        		Intent i = new Intent();
        		i.setAction(android.content.Intent.ACTION_VIEW);
        		i.setDataAndType((Uri) gallery.getAdapter().getItem(0), "image/jpg");
        		startActivityForResult(i, 0);
        	}
        });
        */

        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

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
        		                               LOCATION_UPDATE_MIN_DISTANCE,
        		                               locationListener);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
        		                               LOCATION_UPDATE_MIN_TIME,
        		                               LOCATION_UPDATE_MIN_DISTANCE,
        		                               locationListener);

        /* Try to see if we can get a current location without
         * having to wait for an update
         */
        List<String> providers = locationManager.getProviders(true);
        Location bestLocation = location;
        for (String p : providers) {
        	Location l = locationManager.getLastKnownLocation(p);
        	if (l == null) continue;
        	if (bestLocation == null) bestLocation = l;
        	if (l.getAccuracy() < bestLocation.getAccuracy())
        		bestLocation = l;
        }
        location = bestLocation;

        if (location != null)
        	setLocation(location);
        
        
        /* Old experiments
        TextView textView = (TextView) findViewById(R.id.textview);
        textView.setText("Hey hey hey!");
        
        Bitmap image = BitmapFactory.decodeResource(getResources(), R.drawable.leticia0);
        ImageView imageView = (ImageView) findViewById(R.id.imageview);
        imageView.setImageBitmap(image);
        */        
    }

	private void setLocation(Location location) {
		new UpdateLocationTask().execute(location);
		/*
		PanoramioImageAdapter a = new PanoramioImageAdapter(this, location);
	    
        gallery.setAdapter(a);
        */
	}
	
	private void setAdapter(PanoramioImageAdapter p) {
		gallery.setAdapter(p);
	}
	
	private class UpdateLocationTask extends AsyncTask<Location, Void, PanoramioImageAdapter> {

		@Override
		protected PanoramioImageAdapter doInBackground(Location... locations) {
			return PanoramioImageAdapter.getAdapter(HelloAndroid.this, locations[0]);
		}
		
		protected void onPostExecute(PanoramioImageAdapter p) {
			HelloAndroid.this.setAdapter(p);
		}
	}
}