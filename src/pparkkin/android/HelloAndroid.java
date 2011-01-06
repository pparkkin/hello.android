package pparkkin.android;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.json.JSONException;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class HelloAndroid extends Activity {
	private static TextView textView;
	private static GridView gallery;
	private static LocationManager locationManager;
	
	private static ProgressDialog spinner;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main); // Can't access views before calling setContentView

        textView = (TextView) findViewById(R.id.main_textview);
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
    	
    	updateLocation();

    }

	private void updateLocation() {
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	MenuInflater mI = getMenuInflater();
    	mI.inflate(R.menu.main_options, menu);
    	return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
        case R.id.refresh:
            updateLocation();
            return true;
        default:
            return super.onOptionsItemSelected(item);
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
	}
	
	private class UpdateLocationTask extends AsyncTask<Location, Void, PanoramioImageAdapter> {
		private Exception error;
		
		protected void onPreExecute() {
			HelloAndroid.this.showSpinner();
		}

		@Override
		protected PanoramioImageAdapter doInBackground(Location... locations) {
			PanoramioImageAdapter p = null;
			
			try {
				p = new PanoramioImageAdapter(HelloAndroid.this, locations[0]);
			} catch (IOException e) {
				error = e;
				Log.e("Hello.Android", e.getMessage());
			} catch (JSONException e) {
				error = e;
				Log.e("Hello.Android", e.getMessage());
			}
			
			return p;
		}
		
		protected void onPostExecute(PanoramioImageAdapter p) {
			if (error != null)
				HelloAndroid.this.setText(error.getMessage());
			HelloAndroid.this.setAdapter(p);
			HelloAndroid.this.dismissSpinner();
		}
	}
	
	private void setAdapter(PanoramioImageAdapter p) {
		gallery.setAdapter(p);
	}

	public void setText(String message) {
		textView.setText(message);
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