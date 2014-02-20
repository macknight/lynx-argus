package com.lynx.argus.biz.widget.satellitemenu;

import android.graphics.drawable.Drawable;
import android.view.animation.Animation;
import android.widget.ImageView;

/**
 * 
 * @author zhufeng.liu
 * 
 * @version 14-2-20 下午6:40
 */
public class SatelliteMenuItem {

	private int id;
	private int imgResId;
	private Drawable drawable;
	private ImageView iv, ivClone;
	private Animation animOut, animIn, animClick;
	private int x, y;

	public SatelliteMenuItem(int id, int imgResId) {
		this.id = id;
		this.imgResId = imgResId;
	}

	public SatelliteMenuItem(int id, Drawable drawable) {
		this.id = id;
		this.drawable = drawable;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getImgResId() {
		return imgResId;
	}

	public void setImgResId(int imgResId) {
		this.imgResId = imgResId;
	}

	public Drawable getDrawable() {
		return drawable;
	}

	public void setDrawable(Drawable drawable) {
		this.drawable = drawable;
	}

	public ImageView getIv() {
		return iv;
	}

	public void setIv(ImageView iv) {
		this.iv = iv;
	}

	public ImageView getIvClone() {
		return ivClone;
	}

	public void setIvClone(ImageView ivClone) {
		this.ivClone = ivClone;
	}

	public Animation getAnimOut() {
		return animOut;
	}

	public void setAnimOut(Animation animOut) {
		this.animOut = animOut;
	}

	public Animation getAnimIn() {
		return animIn;
	}

	public void setAnimIn(Animation animIn) {
		this.animIn = animIn;
	}

	public Animation getAnimClick() {
		return animClick;
	}

	public void setAnimClick(Animation animClick) {
		this.animClick = animClick;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}
}
