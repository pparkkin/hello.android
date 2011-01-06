package pparkkin.android;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.util.Log;

public class HttpGET {
	public static Drawable fetchDrawable(String url) throws MalformedURLException, IOException {
		Drawable d = null;
		
		d = fetchDrawable(URI.create(url));
		
		return d;
	}
	
	public static Drawable fetchDrawable(URI uri) throws IOException {
		Drawable d = null;
		
		InputStream is = fetchStream(uri);
		d = Drawable.createFromStream(is, "src");
		if (d == null)
			throw new IOException("Unable to decode stream");
		
		is.close();
		
		return d;
	}

	public static Bitmap fetchBitmap(String url) throws MalformedURLException, IOException {
		Bitmap b = null;
		
		b = fetchBitmap(URI.create(url));
		
		return b;
	}
    public static Bitmap fetchBitmap(URI uri) throws IOException
    {        
        Bitmap bitmap = null;
        InputStream in = null;        

        in = fetchStream(uri);
        bitmap = BitmapFactory.decodeStream(in);
        if (bitmap == null)
        	throw new IOException("Unable to decode stream");
        
        in.close();
            
        return bitmap;                
    }
    
    public static JSONObject fetchJSONObject(String url) throws IOException, JSONException
    {
    	return fetchJSONObject(URI.create(url));
    }
    
    public static JSONObject fetchJSONObject(URI uri) throws IOException, JSONException
    {
		JSONObject result;
		
		InputStream is = fetchStream(uri);
		String jsonString = convertStreamToString(is);
		result = new JSONObject(jsonString);
		
		return result;
    }
    
    private static String convertStreamToString(InputStream is) {
        // To convert the InputStream to String we use the BufferedReader.readLine()
        // method. We iterate until the BufferedReader return null which means
        // there's no more data to read. Each line will appended to a StringBuilder
        // and returned as String.
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
 
        String line = null;
        
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            Log.e("Hello.Android", e.getMessage());
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                Log.e("Hello.Android", e.getMessage());
            }
        }
        return sb.toString();
    }

	private static InputStream fetchStream(URI uri) throws IOException {
		// fixed
		// thanks to
		//  http://stackoverflow.com/questions/4414839/bitmapfactory-decodestream-returns-null-without-exception
	    HttpGet httpRequest = new HttpGet(uri);
	    HttpClient httpclient = new DefaultHttpClient();
	    HttpResponse response = (HttpResponse) httpclient.execute(httpRequest);
	    HttpEntity entity = response.getEntity();
	    BufferedHttpEntity bufHttpEntity = new BufferedHttpEntity(entity);
	    InputStream instream = bufHttpEntity.getContent();
	    return instream;
	}
}
