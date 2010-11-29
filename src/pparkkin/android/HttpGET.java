package pparkkin.android;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import android.graphics.drawable.Drawable;

public class HttpGET {
	public static Drawable fetchDrawable(String url) {
		Drawable d = null;
		
		try {
			d = fetchDrawable(new URL(url));
		} catch (MalformedURLException e) {
			System.err.println("Got url: "+url);
			e.printStackTrace();
		}
		
		return d;
	}
	public static Drawable fetchDrawable(URL url) {
		Drawable d = null;
		
		try {
			InputStream is = fetchStream(url);
			d = Drawable.createFromStream(is, "src");
		} catch (MalformedURLException e) {
			System.err.println("Got url: "+url);
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("Got url: "+url);
			e.printStackTrace();
		}
		
		return d;
	}

	private static InputStream fetchStream(URL url) throws IOException {
		InputStream content = (InputStream) url.getContent();
		return content;
	}
}
