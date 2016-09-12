package fr.mathis.selestat.adapters;

import java.util.ArrayList;

import android.graphics.Bitmap;
import android.graphics.Color;
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
import fr.mathis.selestat.views.DynamicWidthImageView;

public class AddRecyclerAdapter extends RecyclerView.Adapter<AddRecyclerAdapter.GridViewHolder> {

	private ArrayList<Photo> _items;
	private DisplayImageOptions _options;
	private OnClickListener _listener;

	public AddRecyclerAdapter(OnClickListener listener) {
		_listener = listener;
		_items = new ArrayList<Photo>();
		_options = new DisplayImageOptions.Builder().cacheInMemory(false).cacheOnDisk(false).bitmapConfig(Bitmap.Config.RGB_565).imageScaleType(ImageScaleType.EXACTLY).displayer(new FadeInBitmapDisplayer(300)).build();
	}

	@Override
	public int getItemCount() {
		return _items.size();
	}

	@Override
	public void onBindViewHolder(GridViewHolder vh, int position) {
		vh.ivThumbnail.setHeightRatio(_items.get(position).getRatio());
		vh.ivThumbnail.setImageDrawable(null);
		vh.ivThumbnail.setBackgroundColor(Color.WHITE);
		ImageLoader.getInstance().displayImage("file://" + _items.get(position).getThumbnailUrl(), vh.ivThumbnail, _options);
	}

	@Override
	public GridViewHolder onCreateViewHolder(ViewGroup container, int position) {
		View v = LayoutInflater.from(container.getContext()).inflate(R.layout.template_add, container, false);
		v.setOnClickListener(_listener);

		return new GridViewHolder(v);
	}

	public void add(Photo p) {
		_items.add(p);
		notifyItemInserted(_items.size() - 1);
	}

	public Photo getItem(int position) {
		return _items.get(position);
	}

	public ArrayList<Photo> getItems() {
		return _items;
	}

	public static class GridViewHolder extends RecyclerView.ViewHolder {
		protected DynamicWidthImageView ivThumbnail;
		protected FrameLayout flContainer;

		public GridViewHolder(View v) {
			super(v);
			ivThumbnail = (DynamicWidthImageView) v.findViewById(R.id.iv_gallery_photo);
			flContainer = (FrameLayout) v;
		}
	}

}
