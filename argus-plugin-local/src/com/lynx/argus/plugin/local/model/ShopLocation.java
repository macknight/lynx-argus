package com.lynx.argus.plugin.local.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;

/**
 * @author chris.liu
 * @version 4/1/14 5:51 PM
 */
public class ShopLocation implements Parcelable {
	@Expose
	public double lat;
	@Expose
	public double lng;

	public ShopLocation(Parcel in) {
		lat = in.readDouble();
		lng = in.readDouble();
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel parcel, int flag) {
		parcel.writeDouble(lat);
		parcel.writeDouble(lng);
	}

	public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
		public ShopLocation createFromParcel(Parcel in) {
			return new ShopLocation(in);
		}

		public ShopLocation[] newArray(int size) {
			return new ShopLocation[size];
		}
	};
}
