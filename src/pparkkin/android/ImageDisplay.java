package pparkkin.android;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class ImageDisplay extends Activity {
	public static final int errorImage = R.drawable.bsod;
	
	public ImageDisplay() {
		super();
	}
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image); // Can't access views before calling setContentView
        
        ImageView i = (ImageView) findViewById(R.id.imageview);
        TextView t = (TextView) findViewById(R.id.textview);
        
        Bundle extras = getIntent().getExtras();
        if (extras == null) {
        	t.setText("nourl");
	        i.setImageResource(errorImage);
        } else {
        	String u = extras.getString("url");
        	t.setText("'"+u+"'");
        	Bitmap b = HttpGET.fetchBitmap(u);
        	if (b == null) {
    	        i.setImageResource(errorImage);
        	} else {
        		i.setImageBitmap(b);
        	}
        	/*
        	Drawable d = HttpGET.fetchDrawable(u);
        	if (d == null) {
    	        i.setImageResource(R.drawable.aitta);
        	} else {
        		i.setImageDrawable(d);
        	}
        	*/
        }
    }
}
