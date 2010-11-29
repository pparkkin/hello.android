package pparkkin.android;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.ImageView;

public class ImageDisplay extends Activity {
	
	public ImageDisplay() {
		super();
	}
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image); // Can't access views before calling setContentView
        ImageView i = (ImageView) findViewById(R.id.imageview);
        
        Bundle extras = getIntent().getExtras();
        if (extras == null) {
	        i.setImageResource(R.drawable.aitta);
        } else {
        	String u = extras.getString("url");
        	Drawable d = HttpGET.fetchDrawable(u);
        	if (d == null) {
    	        i.setImageResource(R.drawable.aitta);
        	} else {
        		i.setImageDrawable(d);
        	}
        }
    }
}
