package pparkkin.android;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

public class ImageAdapter extends BaseAdapter {

	private Context context;
	private Integer[] thumbs = {
			R.drawable.loading,
			R.drawable.loading,
			R.drawable.loading,
			R.drawable.loading,
			R.drawable.loading,
			R.drawable.loading,
			R.drawable.loading,
			R.drawable.loading,
			R.drawable.loading,
			R.drawable.loading,
			R.drawable.loading,
			R.drawable.loading
	};

	public ImageAdapter(Context context) {
		this.context = context;
	}

	@Override
	public int getCount() {
		return thumbs.length;
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ImageView i;
		if (convertView == null) {
			i = new ImageView(context);
			i.setLayoutParams(new GridView.LayoutParams(85, 85));
			i.setScaleType(ImageView.ScaleType.CENTER_CROP);
			i.setPadding(8, 8, 8, 8);
		} else {
			i = (ImageView) convertView;
		}
		
		i.setImageResource(thumbs[position]);
		return i;
	}

}
