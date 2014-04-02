package com.lynx.argus.plugin.local.model;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @author chris.liu
 * @version 4/2/14 6:43 PM
 */
public class TuanEvent implements Parcelable {
	@Expose
	@SerializedName("cn_name")
	public String name;
    @Expose
    @SerializedName("groupon_title")
    public String title;
	@Expose
	@SerializedName("groupon_price")
	public String price;
    @Expose
    @SerializedName("regular_price")
    public String regularPrice;
	@Expose
	@SerializedName("groupon_rebate")
	public String rebate;
    @Expose
    @SerializedName("groupon_start")
    public String startTime;
    @Expose
    @SerializedName("groupon_end")
    public String endTime;
    @Expose
    @SerializedName("groupon_image")
    public String image;
    @Expose
    @SerializedName("groupon_num")
    public String num;
	@Expose
	@SerializedName("groupon_site")
	public String site;


	public TuanEvent(Parcel in) {
        name = in.readString();
        title = in.readString();
        price = in.readString();
        regularPrice = in.readString();
        rebate = in.readString();
        startTime = in.readString();
        endTime = in.readString();
        image = in.readString();
        num = in.readString();
        site = in.readString();
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(title);
        parcel.writeString(price);
        parcel.writeString(regularPrice);
        parcel.writeString(rebate);
        parcel.writeString(startTime);
        parcel.writeString(endTime);
        parcel.writeString(image);
        parcel.writeString(num);
        parcel.writeString(site);
	}

	public static final Parcelable.Creator<TuanEvent> CREATOR = new Parcelable.Creator<TuanEvent>() {
		public TuanEvent createFromParcel(Parcel in) {
			return new TuanEvent(in);
		}

		public TuanEvent[] newArray(int size) {
			return new TuanEvent[size];
		}
	};
}
