package pparkkin.android;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import android.graphics.drawable.Drawable;

public class HttpGET {
	public static Drawable fetchDrawable(URL url) {
		Drawable d = null;
		
		try {
			InputStream is = fetchStream(url);
			d = Drawable.createFromStream(is, "src");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return d;
	}

	private static InputStream fetchStream(URL url) throws IOException {
		InputStream content = (InputStream) url.getContent();
		return content;
	}
}
