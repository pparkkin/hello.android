package pparkkin.android;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
	private static final String BASE_URL = "http://www.panoramio.com/map/get_panoramas.php";
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
	
	public PanoramioImageAdapter(Context c, Location l) {
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

	private void setLocation(Location l) {
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

	private void loadImages() {
		imageUris.clear();
		images.clear();
		
		double lng = location.getLongitude();
		double lat = location.getLatitude();
		
		String q = buildQuery(lng-LNG_RANGE, lat-LAT_RANGE,
				              lng+LNG_RANGE, lat+LAT_RANGE);
		List<String> urls = fetchImageURLs(q);

		for (String url : urls) {
			imageUris.add(Uri.parse(url));
			try {
				URL u = new URL(url);
				images.add(HttpGET.fetchDrawable(u));
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
		}
	}
	
	private List<String> fetchImageURLs(String q) {
		JSONArray photos;
		try {
			InputStream is = (InputStream) fetchURL(q);
			String jsonString = convertStreamToString(is);
			JSONObject result = new JSONObject(jsonString);
			photos = (JSONArray) result.get("photos");
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		
		if (photos == null)
			return null;
		
		List<String> imageURLs = new ArrayList<String>();
		for (int i = 0; i < photos.length(); i++) {
			try {
				JSONObject p = photos.getJSONObject(i);
				imageURLs.add((String) p.get("photo_file_url"));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		
		return imageURLs;
	}

	private String buildQuery(double  minx,
                              double  miny,
                              double  maxx,
                              double  maxy) {
		return buildQuery(DEFAULT_SET, DEFAULT_SIZE,
				          minx, miny, maxx, maxy,
				          DEFAULT_FROM, DEFAULT_TO,
				          false);
	}

	private String buildQuery(String  set,
			                  String  size,
			                  double  minx,
			                  double  miny,
			                  double  maxx,
			                  double  maxy,
			                  int     from,
			                  int     to,
			                  boolean mapFilter) {
		StringBuilder sb = new StringBuilder(BASE_URL+"?");
		List<String> arguments = new ArrayList<String>();
		
		if (set != null)
			arguments.add("set="+set);
		if (size != null)
			arguments.add("size="+size);
		arguments.add("minx="+minx);
		arguments.add("miny="+miny);
		arguments.add("maxx="+maxx);
		arguments.add("maxy="+maxy);
		arguments.add("from="+from);
		arguments.add("to="+to);
		if (mapFilter)
			arguments.add("mapfilter=true");
		
		sb.append(join(arguments, "&"));
		
		return sb.toString();
	}

	private Object fetchURL(String address) throws IOException {
		URL url = new URL(address);
		Object content = url.getContent();
		return content;
	}
	
	private String join(Collection<String> s, String delimiter) {
        StringBuffer buffer = new StringBuffer();
        Iterator<String> iter = s.iterator();
        while (iter.hasNext()) {
            buffer.append(iter.next());
            if (iter.hasNext()) {
                buffer.append(delimiter);
            }
        }
        return buffer.toString();
    }
	
    private String convertStreamToString(InputStream is) {
        /*
         * To convert the InputStream to String we use the BufferedReader.readLine()
         * method. We iterate until the BufferedReader return null which means
         * there's no more data to read. Each line will appended to a StringBuilder
         * and returned as String.
         */
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
 
        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

	public Map<Uri, Drawable> getImages() {
		Map<Uri, Drawable> map = new HashMap<Uri, Drawable>(imageUris.size());
		
		for (int i = 0; i < imageUris.size(); ++i) {
			map.put(imageUris.get(i), images.get(i));
		}
		
		return map;
	}
}
