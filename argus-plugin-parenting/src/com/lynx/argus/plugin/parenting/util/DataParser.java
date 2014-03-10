package com.lynx.argus.plugin.parenting.util;

import com.lynx.argus.plugin.parenting.model.CampaignInfo;
import com.lynx.argus.plugin.parenting.model.ShopInfo;
import com.lynx.lib.geo.entity.GeoPoint;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhufeng.liu
 * @version 14-2-17 下午7:17
 */
public class DataParser {

	public static List<CampaignInfo> parseCampaign(String data) {
		List<CampaignInfo> campaigns = new ArrayList<CampaignInfo>();
		try {

			JSONObject joResult = new JSONObject(data);
			JSONArray jaResult = joResult.getJSONArray("data");
			if (jaResult == null || jaResult.length() == 0) {
				return campaigns;
			}
			campaigns.clear();
			for (int i = 0; i < jaResult.length(); ++i) {
				try {
					JSONObject joShop = jaResult.getJSONObject(i);
					CampaignInfo campaignInfo = new CampaignInfo();
					campaignInfo.setId(joShop.getString("goods_id"));
					campaignInfo.setName(joShop.getString("goods_name"));
					campaignInfo.setStoreId(joShop.getString("store_id"));
					campaignInfo.setStoreName(joShop.getString("store_name"));
					campaignInfo.setMarketPrice(joShop.getString("market_price"));
					campaignInfo.setSnapUrl(joShop.getString("default_image"));
					campaignInfo.setStartTime(joShop.getString("start_time"));
					campaignInfo.setEndTime(joShop.getString("end_time"));
					campaignInfo.setPlace(joShop.getString("place"));
					campaignInfo.setRegion(joShop.getString("regions"));
					campaigns.add(campaignInfo);
				} catch (Exception e) {

				}
			}

			JSONObject joPage = joResult.getJSONObject("page");
			int curPage = joPage.getInt("curr_page");
			int pageSize = joPage.getInt("page_count");

		} catch (Exception e) {

		}
		return campaigns;
	}

	public static List<ShopInfo> parseShops(String data) {
		List<ShopInfo> shopInfos = new ArrayList<ShopInfo>();
		try {
			JSONObject joResult = new JSONObject(data);
			JSONArray jaResult = joResult.getJSONArray("data");
			if (jaResult == null || jaResult.length() == 0) {
				return shopInfos;
			}
			shopInfos.clear();
			for (int i = 0; i < jaResult.length(); ++i) {
				ShopInfo shopInfo = new ShopInfo();
				try {
					JSONObject joShop = jaResult.getJSONObject(i);
					shopInfo.setStoreName(joShop.getString("store_name"));
					shopInfo.setStoreId(joShop.getString("store_id"));
					shopInfo.setShopName(joShop.getString("shop_name"));
					shopInfo.setStoreId(joShop.getString("store_id"));
					shopInfo.setRegion(joShop.getString("region_name"));
					shopInfo.setSnapUrl(joShop.getString("default_image"));

					String lng = joShop.getString("map_lng");
					String lat = joShop.getString("map_lat");
					GeoPoint latlng = new GeoPoint(Double.parseDouble(lat), Double.parseDouble(lng));
					shopInfo.setLatlng(latlng);
					int reviewNum = Integer.parseInt(joShop.getString("reviews"));
					shopInfo.setReviewNum(reviewNum);

					shopInfos.add(shopInfo);
				} catch (Exception e) {

				}
			}

			JSONObject joPage = joResult.getJSONObject("page");
			int curPage = joPage.getInt("curr_page");
			int pageSize = joPage.getInt("page_count");
		} catch (Exception e) {

		}
		return shopInfos;
	}
}
