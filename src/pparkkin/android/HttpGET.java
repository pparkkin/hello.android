package pparkkin.android;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.util.Log;

public class HttpGET {
	public static Drawable fetchDrawable(String url) {
		Drawable d = null;
		
		try {
			d = fetchDrawable(new URL(url));
		} catch (MalformedURLException e) {
			Log.e("Hello.Android", "Got url: "+url, e);
		}
		
		return d;
	}
	public static Drawable fetchDrawable(URL url) {
		Drawable d = null;
		
		try {
			InputStream is = fetchStream(url);
			d = Drawable.createFromStream(is, "src");
		} catch (MalformedURLException e) {
			Log.e("Hello.Android", "Got url: "+url, e);
		} catch (IOException e) {
			Log.e("Hello.Android", "Got url: "+url, e);
		}
		
		return d;
	}

	public static Bitmap fetchBitmap(String url) {
		Bitmap b = null;
		
		try {
			b = fetchBitmap(new URL(url));
		} catch (MalformedURLException e) {
			Log.e("Hello.Android", "Got url: "+url, e);
		}
		
		return b;
	}
    public static Bitmap fetchBitmap(URL url)
    {        
        Bitmap bitmap = null;
        InputStream in = null;        
        try {
            in = fetchStream(url);
            bitmap = BitmapFactory.decodeStream(in);
            in.close();
        } catch (IOException e) {
			Log.e("Hello.Android", "Got url: "+url, e);
        }
        return bitmap;                
    }
    
	private static InputStream fetchStream(URL url) throws IOException {
        InputStream in = null;
        int response = -1;

        URLConnection conn = url.openConnection();

        if (!(conn instanceof HttpURLConnection))                     
            throw new IOException("Not an HTTP connection");

        try{
            HttpURLConnection httpConn = (HttpURLConnection) conn;
            httpConn.setAllowUserInteraction(false);
            httpConn.setInstanceFollowRedirects(true);
            httpConn.setRequestMethod("GET");
            httpConn.connect(); 

            response = httpConn.getResponseCode();                 
            if (response == HttpURLConnection.HTTP_OK) {
                in = httpConn.getInputStream();                                 
            }                     
        }
        catch (Exception ex)
        {
            throw new IOException("Error connecting");            
        }
        return in;
        
        /*
		InputStream content = (InputStream) url.getContent();
		return content;
		*/
	}
}
