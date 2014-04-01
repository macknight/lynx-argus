package com.lynx.argus.plugin.local.model;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @author chris.liu
 * @version 4/1/14 5:51 PM
 */
public class ShopDetail implements Parcelable {
	@Expose
	public String tag; // 标签

	@Expose
	public String price; // 人均

	@Expose
	@SerializedName("overall_rating")
	public float overallRating; // 综合分

	@Expose
	@SerializedName("taste_rating")
	public float tasteRating; // 口味分

	@Expose
	@SerializedName("service_rating")
	public float serviceRating; // 服务分

	@Expose
	@SerializedName("environment_rating")
	public float envRating; // 环境分

	@Expose
	@SerializedName("image_num")
	public int imageNum; // 图片数

	@Expose
	@SerializedName("comment_num")
	public int commentNum; // 点评数

	@Expose
	@SerializedName("shop_hours")
	public String shopHours; // 营业时间

	public ShopDetail(Parcel in) {
		tag = in.readString();
		price = in.readString();
		overallRating = in.readFloat();
		tasteRating = in.readFloat();
		serviceRating = in.readFloat();
		envRating = in.readFloat();
		imageNum = in.readInt();
		commentNum = in.readInt();
		shopHours = in.readString();
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel parcel, int flag) {
		parcel.writeString(tag);
		parcel.writeString(price);
		parcel.writeFloat(overallRating);
		parcel.writeFloat(tasteRating);
		parcel.writeFloat(serviceRating);
		parcel.writeFloat(envRating);
		parcel.writeInt(imageNum);
		parcel.writeInt(commentNum);
		parcel.writeString(shopHours);
	}

	public static final Parcelable.Creator<ShopDetail> CREATOR = new Parcelable.Creator<ShopDetail>() {
		public ShopDetail createFromParcel(Parcel in) {
			return new ShopDetail(in);
		}

		public ShopDetail[] newArray(int size) {
			return new ShopDetail[size];
		}
	};
}
