package fr.mathis.selestat.adapters;

import java.util.ArrayList;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import fr.mathis.selestat.R;
import fr.mathis.selestat.models.Category;

public class HomeSpinnerAdapter extends BaseAdapter {

	ArrayList<Category> _categories;
	LayoutInflater _inflater;
	int _currentCategoryId = -1;

	public HomeSpinnerAdapter(LayoutInflater inflater, ArrayList<Category> categories, int currentCategoryId) {
		_categories = categories;
		_inflater = inflater;
		_currentCategoryId = currentCategoryId;
	}

	@Override
	public int getCount() {
		return _categories.size();
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		if (v == null) {
			v = _inflater.inflate(R.layout.template_spinner_view, parent, false);
		}

		TextView title = (TextView) v.findViewById(R.id.tv_spinner_title);
		title.setText(_categories.get(position).getTitle());

		return v;
	}

	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		if (v == null) {
			v = _inflater.inflate(R.layout.template_spinner_dropdownview, parent, false);
		}

		Category c = _categories.get(position);

		TextView title = (TextView) v.findViewById(R.id.tv_spinner_title);
		title.setText(c.getTitle());

		TextView count = (TextView) v.findViewById(R.id.tv_spinner_count);
		count.setText(c.getCount() + "");

		View vIndicator = v.findViewById(R.id.v_spinner_indicator);
		vIndicator.setVisibility(_currentCategoryId == c.getId() ? View.VISIBLE : View.INVISIBLE);

		return v;
	}

	public void UpdateIndicator(int currentCategoryId) {
		_currentCategoryId = currentCategoryId;
		notifyDataSetChanged();
	}

	public void UpdateData(ArrayList<Category> categories) {
		_categories = categories;
	}
}
