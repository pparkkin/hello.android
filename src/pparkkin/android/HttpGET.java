package pparkkin.android;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.util.Log;

public class HttpGET {
	public static Drawable fetchDrawable(String url) throws MalformedURLException, IOException {
		Drawable d = null;
		
		d = fetchDrawable(new URL(url));
		
		return d;
	}
	public static Drawable fetchDrawable(URL url) throws IOException {
		Drawable d = null;
		
		InputStream is = fetchStream(url);
		d = Drawable.createFromStream(is, "src");
		
		return d;
	}

	public static Bitmap fetchBitmap(String url) throws MalformedURLException, IOException {
		Bitmap b = null;
		
		b = fetchBitmap(new URL(url));
		
		return b;
	}
    public static Bitmap fetchBitmap(URL url) throws IOException
    {        
        Bitmap bitmap = null;
        InputStream in = null;        

        in = fetchStream(url);
        bitmap = BitmapFactory.decodeStream(in);
        in.close();
            
        return bitmap;                
    }
    
    public static JSONObject fetchJSONObject(String url) throws IOException, JSONException
    {
    	return fetchJSONObject(new URL(url));
    }
    
    public static JSONObject fetchJSONObject(URL url) throws IOException, JSONException
    {
		JSONObject result;
		
		InputStream is = fetchStream(url);
		String jsonString = convertStreamToString(is);
		result = new JSONObject(jsonString);
		
		return result;
    }
    
    private static String convertStreamToString(InputStream is) {
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

	private static InputStream fetchStream(URL url) throws IOException {
        InputStream in = null;
        int response = -1;

        URLConnection conn = url.openConnection();

        if (!(conn instanceof HttpURLConnection))                     
            throw new IOException("Not an HTTP connection");

        HttpURLConnection httpConn = (HttpURLConnection) conn;
        httpConn.setAllowUserInteraction(false);
        httpConn.setInstanceFollowRedirects(true);
        httpConn.setRequestMethod("GET");
        httpConn.connect(); 
        
        response = httpConn.getResponseCode();                 
        if (response == HttpURLConnection.HTTP_OK) {
        	in = httpConn.getInputStream();                                 
        }
            
        return in;
	}
}
