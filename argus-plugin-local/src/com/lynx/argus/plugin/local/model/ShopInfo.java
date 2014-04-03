package com.lynx.argus.plugin.local.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * @author chris.liu
 * @version 4/1/14 4:28 PM
 */
public class ShopInfo implements Parcelable {
	@Expose
	public String name;

	@Expose
	public ShopLocation location;

	@Expose
	public String address;

	@Expose
	public String telephone;

	@Expose
	public String uid;

	@Expose
	@SerializedName("detail_info")
	public ShopDetail detailInfo;

    @Expose
    @SerializedName("events")
    public List<TuanEvent> tuanEvents;

	public ShopInfo(Parcel in) {
		name = in.readString();
        uid = in.readString();
		location = in.readParcelable(ShopLocation.class.getClassLoader());
        address = in.readString();
        telephone = in.readString();
        detailInfo = in.readParcelable(ShopDetail.class.getClassLoader());
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel parcel, int flag) {
        parcel.writeString(name);
        parcel.writeString(uid);
        parcel.writeParcelable(location, flag);
        parcel.writeString(address);
        parcel.writeString(telephone);
        parcel.writeParcelable(detailInfo, flag);
	}

	public static final Parcelable.Creator<ShopInfo> CREATOR = new Parcelable.Creator<ShopInfo>() {
		public ShopInfo createFromParcel(Parcel in) {
			return new ShopInfo(in);
		}

		public ShopInfo[] newArray(int size) {
			return new ShopInfo[size];
		}
	};
}
