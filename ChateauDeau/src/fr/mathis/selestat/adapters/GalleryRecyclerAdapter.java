package fr.mathis.selestat.adapters;

import java.util.ArrayList;
import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import fr.mathis.selestat.R;
import fr.mathis.selestat.models.Photo;
import fr.mathis.selestat.views.DynamicHeightImageView;

public class GalleryRecyclerAdapter extends RecyclerView.Adapter<GalleryRecyclerAdapter.GridViewHolder> {

	private ArrayList<Photo> _items;
	private DisplayImageOptions _options;
	private OnClickListener _listener;

	public GalleryRecyclerAdapter(OnClickListener listener) {
		_listener = listener;
		_items = new ArrayList<Photo>();
		_options = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisk(true).bitmapConfig(Bitmap.Config.RGB_565).imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2).displayer(new FadeInBitmapDisplayer(300)).build();
	}

	@Override
	public int getItemCount() {
		return _items.size();
	}

	@SuppressLint("NewApi") @Override
	public void onBindViewHolder(GridViewHolder vh, int position) {
		vh.ivThumbnail.setHeightRatio(_items.get(position).getRatio());
		vh.flContainer.setBackgroundColor(Color.parseColor(_items.get(position).getColor()));
		vh.ivThumbnail.setImageDrawable(null);
		ImageLoader.getInstance().displayImage(_items.get(position).getThumbnailUrl(), vh.ivThumbnail, _options);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			vh.ivThumbnail.setTransitionName("TransImg" + _items.get(position).getId());
		}
	}

	@Override
	public GridViewHolder onCreateViewHolder(ViewGroup container, int position) {
		View v = LayoutInflater.from(container.getContext()).inflate(R.layout.template_gallery, container, false);
		v.setOnClickListener(_listener);

		return new GridViewHolder(v);
	}

	public void update(ArrayList<Photo> items) {
		_items.clear();
		_items = items;
		notifyDataSetChanged();
	}

	public Photo getItem(int position) {
		return _items.get(position);
	}

	public ArrayList<Photo> getItems() {
		return _items;
	}

	public static class GridViewHolder extends RecyclerView.ViewHolder {
		protected DynamicHeightImageView ivThumbnail;
		protected FrameLayout flContainer;

		public GridViewHolder(View v) {
			super(v);
			ivThumbnail = (DynamicHeightImageView) v.findViewById(R.id.iv_gallery_photo);
			flContainer = (FrameLayout) v;
		}
	}

}
