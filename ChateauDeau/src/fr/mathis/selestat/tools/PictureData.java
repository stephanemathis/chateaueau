package fr.mathis.selestat.tools;

import android.graphics.Bitmap;

public class PictureData {

	public int resourceId;
	public Bitmap thumbnail;

	public PictureData(int resourceId, Bitmap thumbnail) {
		this.resourceId = resourceId;
		this.thumbnail = thumbnail;
	}

}
