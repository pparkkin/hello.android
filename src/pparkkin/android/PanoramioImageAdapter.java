package pparkkin.android;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;

import pparkkin.android.Panoramio.Panorama;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

public class PanoramioImageAdapter extends BaseAdapter {// implements SpinnerAdapter {
	private static final String DEFAULT_SET = "public";
	private static final String DEFAULT_SIZE = "square";
	private static final int DEFAULT_FROM = 0;
	private static final int DEFAULT_TO = 20;
	private static final double LNG_RANGE = 0.01;
	private static final double LAT_RANGE = 0.01;
	
	private Context context;
	private Location location;

	private List<Uri> imageUris = new ArrayList<Uri>(DEFAULT_TO);
	private List<Drawable> images = new ArrayList<Drawable>(DEFAULT_TO);
	
	public PanoramioImageAdapter(Context c, Location l) throws IOException, JSONException {
		super();
		
		setContext(c);
		setLocation(l);
	}

	public PanoramioImageAdapter(Context c, Map<Uri, Drawable> images) {
		setContext(c);
		
		for (Uri key : images.keySet()) {
			imageUris.add(key);
			this.images.add(images.get(key));
		}
	}

	private void setContext(Context c) {
		if (c == null)
			throw new IllegalArgumentException("Context is null");
		context = c;
	}

	private void setLocation(Location l) throws IOException, JSONException {
		if (l == null)
			throw new IllegalArgumentException("Location is null");
		this.location = l;
		
		loadImages();
	}

	@Override
	public int getCount() {
		return imageUris.size();
	}

	@Override
	public Object getItem(int position) {
		return imageUris.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Drawable d = images.get(position);
		if (d == null)
			return null;
		
		ImageView i;
		
		if (convertView == null) {
			i = new ImageView(context);
			int size = Math.min(d.getIntrinsicHeight(),
			                    d.getIntrinsicWidth());
			i.setLayoutParams(new GridView.LayoutParams(size, size));
			//i.setAdjustViewBounds(true);
			i.setScaleType(ImageView.ScaleType.CENTER_CROP);
			i.setPadding(0, 0, 0, 0);
		} else {
			i = (ImageView) convertView;
		}
		
		i.setImageDrawable(d);

		return i;
	}

	private void loadImages() throws IOException, JSONException {
		imageUris.clear();
		images.clear();
		
		double lng = location.getLongitude();
		double lat = location.getLatitude();
		
		List<String> urls = getImageUrls(lng, lat);

		for (String url : urls) {
			imageUris.add(Uri.parse(url));
			try {
				images.add(HttpGET.fetchDrawable(url));
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
		}
	}

	private List<String> getImageUrls(double lng, double lat) throws IOException, JSONException {
		List<String> urls = new ArrayList<String>();
		double minx = lng-LNG_RANGE;
		double miny = lat-LAT_RANGE;
		double maxx = lng+LNG_RANGE;
		double maxy = lat+LAT_RANGE;
		
		List<Panorama> ps = Panoramio.getPanoramas(DEFAULT_SET, DEFAULT_SIZE, minx, miny, maxx, maxy, DEFAULT_FROM, DEFAULT_TO, false); 
		
		for (Panorama p : ps)
		{
			urls.add(p.photoFileUrl);
		}
		
		return urls;
	}
	
	public Map<Uri, Drawable> getImages() {
		Map<Uri, Drawable> map = new HashMap<Uri, Drawable>(imageUris.size());
		
		for (int i = 0; i < imageUris.size(); ++i) {
			map.put(imageUris.get(i), images.get(i));
		}
		
		return map;
	}
}
