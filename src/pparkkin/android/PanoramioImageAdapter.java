package pparkkin.android;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.AsyncTask;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

class WebImage {
	public String URL;
	public Drawable thumbnail;
	
	public WebImage(String URL) {
		this.URL = URL;
	}
	
	public void loadThumbnail() {
		if (thumbnail != null) return;
		
		try {
			InputStream is = (InputStream) fetchURL();
			Drawable d = Drawable.createFromStream(is, "src");
			thumbnail = d;
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private Object fetchURL() throws IOException {
		URL url = new URL(this.URL);
		Object content = url.getContent();
		return content;
	}
	
}

public class PanoramioImageAdapter extends BaseAdapter {// implements SpinnerAdapter {
	private static final String BASE_URL = "http://www.panoramio.com/map/get_panoramas.php";
	private static final String DEFAULT_SET = "public";
	private static final String DEFAULT_SIZE = "square";
	private static final int DEFAULT_FROM = 0;
	private static final int DEFAULT_TO = 18;
	private static final double LNG_RANGE = 0.1;
	private static final double LAT_RANGE = 0.1;
	
	int mGalleryItemBackground;
	private Context mContext;
	
	private List<WebImage> images = new ArrayList<WebImage>();
	private Location location;
	
	public PanoramioImageAdapter(Context c, Location l) {
		if (c == null)
			return;
		mContext = c;
		
		if (l == null)
			return;
		this.location = l;
		
		loadImages();
		//new LoadImagesTask().execute(new Void[0]);
	}

	@Override
	public int getCount() {
		return images.size();
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ImageView i = new ImageView(mContext);
		Drawable thumbnail = images.get(position).thumbnail;
		i.setImageDrawable(thumbnail);
		int size = Math.min(thumbnail.getIntrinsicHeight(),
		                    thumbnail.getIntrinsicWidth());
		i.setLayoutParams(new GridView.LayoutParams(size, size));
		//i.setAdjustViewBounds(true);
		i.setScaleType(ImageView.ScaleType.CENTER_CROP);
		i.setPadding(0, 0, 0, 0);
		
		return i;
	}

	private void loadImages() {
		images.clear();
		
		double lng = location.getLongitude();
		double lat = location.getLatitude();
		
		String q = buildQuery(lng-LNG_RANGE, lat-LAT_RANGE,
				              lng+LNG_RANGE, lat+LAT_RANGE);
		List<String> urls = fetchImageURLs(q);

		for (String url : urls) {
			WebImage i = new WebImage(url);
			i.loadThumbnail();
			images.add(i);
		}
		
	}

	private class LoadImagesTask extends AsyncTask<Void, Void, Void> {
		@Override
		protected Void doInBackground(Void... params) {
			images.clear();
			
			double lng = location.getLongitude();
			double lat = location.getLatitude();
			
			String q = buildQuery(lng-0.01, lat-0.01,
					              lng+0.01, lat+0.01);
			List<String> urls = fetchImageURLs(q);

			for (String url : urls) {
				WebImage i = new WebImage(url);
				i.loadThumbnail();
				images.add(i);
			}
			
			return null;
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
}
