package com.lynx.argus.plugin.parenting;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lynx.argus.plugin.parenting.model.ShopInfo;
import com.lynx.lib.misc.AsyncImageLoader;

/**
 *
 * @author zhufeng.liu
 * @version 14-2-24 下午4:13
 */
public final class HotShopFragment extends Fragment {
	private static final String KEY_CONTENT = "HotShopFragment:Content";
	private AsyncImageLoader imgLoader;

	public static HotShopFragment newInstance(ShopInfo shopInfo) {
		HotShopFragment fragment = new HotShopFragment();
		fragment.shopInfo = shopInfo;

		return fragment;
	}

	private ShopInfo shopInfo;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.imgLoader = AsyncImageLoader.instance();

		if ((savedInstanceState != null) && savedInstanceState.containsKey(KEY_CONTENT)) {
			shopInfo = (ShopInfo) savedInstanceState.get(KEY_CONTENT);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.layout_shoplist_item, container, false);
		ImageView ivSnap = (ImageView) view.findViewById(R.id.iv_shoplist_item_snap);

		TextView tvName = (TextView) view.findViewById(R.id.tv_shoplist_item_name);
		TextView tvShop = (TextView) view.findViewById(R.id.tv_shoplist_item_shop);
		TextView tvTime = (TextView) view.findViewById(R.id.tv_shoplist_item_time);
		TextView tvReview = (TextView) view.findViewById(R.id.tv_shoplist_item_review);

		tvName.setText(String.format("%s%s", shopInfo.getStoreName(), shopInfo.getShopName()));
		tvShop.setText("" + shopInfo.getRegion());
		tvTime.setText(shopInfo.getLatlng().toString());
		tvReview.setText("" + shopInfo.getReviewNum());
		imgLoader.showAsyncImage(
				ivSnap,
				String.format("%s/%s", ParentingFragment.LM_API_PARENT_DOMAIN,
						shopInfo.getSnapUrl()));

		return view;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putSerializable(KEY_CONTENT, shopInfo);
	}
}
