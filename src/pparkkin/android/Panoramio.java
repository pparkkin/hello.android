package pparkkin.android;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Panoramio {
	private static final String BASE_URL = "http://www.panoramio.com/map/get_panoramas.php";
	
	public static final String SET_PUBLIC = "public";
	public static final String SET_FULL = "full";
	
	public static final String SIZE_ORIGINAL = "original";
	public static final String SIZE_MEDIUM = "medium";
	public static final String SIZE_SMALL = "small";
	public static final String SIZE_THUMBNAIL = "thumbnail";
	public static final String SIZE_SQUARE = "square";
	public static final String SIZE_MINI_SQUARE = "mini_square";
	
	public static class Panorama {
		public final String photoId;
		public final String title;
		public final String photoUrl;
		public final String photoFileUrl;
		public final double longitude;
		public final double latitude;
		public final int width;
		public final int height;
		public final String ownerId;
		public final String ownerName;
		public final String ownerUrl;
		public Panorama(String photoId,
				String title,
				String photoUrl,
				String photoFileUrl,
				double longitude,
				double latitude,
				int width,
				int height,
				String ownerId,
				String ownerName,
				String ownerUrl)
		{
			this.photoId      = photoId;
			this.title        = title;
			this.photoUrl     = photoUrl;
			this.photoFileUrl = photoFileUrl;
			this.longitude    = longitude;
			this.latitude     = latitude;
			this.width        = width;
			this.height       = height;
			this.ownerId      = ownerId;
			this.ownerName    = ownerName;
			this.ownerUrl     = ownerUrl;
			
		}
	}

	public static List<Panorama> getPanoramas(String set, String size, double minx, double miny,
			double maxx, double maxy, int from, int to, boolean mapFilter) throws IOException, JSONException
	{
		String query = buildQuery(set, size, minx, miny, maxx, maxy, from, to, mapFilter);
		List<Panorama> ps = fetchPanoramas(query);
		return ps;
	}

	private static List<Panorama> fetchPanoramas(String q) throws IOException, JSONException {
		JSONArray photos;
		JSONObject result = HttpGET.fetchJSONObject(q);
		photos = (JSONArray) result.get("photos");
		
		if (photos == null)
			return null;
		
		List<Panorama> panoramas = new ArrayList<Panorama>();
		for (int i = 0; i < photos.length(); i++) {
			try {
				JSONObject j = photos.getJSONObject(i);
				Panorama p = new Panorama(j.getString("photo_id"),
						j.getString("title"),
						j.getString("photo_url"),
						j.getString("photo_file_url"),
						j.getDouble("longitude"),
						j.getDouble("latitude"),
						j.getInt("width"),
						j.getInt("height"),
						j.getString("owner_id"),
						j.getString("owner_name"),
						j.getString("owner_url"));
				
				panoramas.add(p);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		
		return panoramas;
	}

	private static String buildQuery(String  set,
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

	private static String join(Collection<String> s, String delimiter) {
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
	
}
