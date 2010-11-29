package pparkkin.android;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.AdapterView.OnItemClickListener;

public class HelloAndroid extends Activity {
    private static final float LOCATION_UPDATE_MIN_DISTANCE_NETWORK = (float) 0.1;
	private static final float LOCATION_UPDATE_MIN_DISTANCE_GPS = (float) 0.1;
	private static final long LOCATION_UPDATE_MIN_TIME = 60000; // 1min (60000ms)
	
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
        
        gallery.setOnItemClickListener(new OnItemClickListener() {
			@Override
        	public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
				String address = gallery.getAdapter().getItem(position).toString();
				String u = address.replaceAll("square", "medium");
        		
				Intent i = new Intent(HelloAndroid.this, ImageDisplay.class);
				i.putExtra("url", u);
				startActivity(i);
				/*
				String address = gallery.getAdapter().getItem(position).toString();
				Uri u = Uri.parse(address.replaceAll("square", "medium"));
        		//Uri u = (Uri) gallery.getAdapter().getItem(0);
        		if (u == null) return;
        		
        		Intent i = new Intent();
        		i.setAction(android.content.Intent.ACTION_VIEW);
        		i.setData(u);
        		startActivity(i);
        		*/
        	}
        });

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
        		                               LOCATION_UPDATE_MIN_DISTANCE_GPS,
        		                               locationListener);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
        		                               LOCATION_UPDATE_MIN_TIME,
        		                               LOCATION_UPDATE_MIN_DISTANCE_NETWORK,
        		                               locationListener);

        if (gallery.getAdapter() == null) {
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
        } else {
        	gallery.invalidateViews();
        }
        
        
        /* Old experiments
        TextView textView = (TextView) findViewById(R.id.textview);
        textView.setText("Hey hey hey!");
        
        Bitmap image = BitmapFactory.decodeResource(getResources(), R.drawable.leticia0);
        ImageView imageView = (ImageView) findViewById(R.id.imageview);
        imageView.setImageBitmap(image);
        */        
    }

	private void setLocation(Location location) {
		AsyncTask<Location, Void, PanoramioImageAdapter> t = new UpdateLocationTask().execute(location);
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