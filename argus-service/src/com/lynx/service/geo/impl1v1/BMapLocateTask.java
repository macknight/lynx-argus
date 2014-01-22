package com.lynx.service.geo.impl1v1;

import android.util.Log;
import com.lynx.lib.core.Logger;
import com.lynx.lib.geo.entity.*;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author zhufeng.liu
 * 
 * @version 13-11-8 下午5:01
 */
public class BMapLocateTask {
	private static final String Tag = "BMapLocateTask";
	private static final String B_LOC_URL = "http://loc.map.baidu.com/sdk.php";
	private List<Cell> cells;
	private List<Wifi> wifis;

	public BMapLocateTask(List<Cell> cells, List<Wifi> wifis) {
		this.cells = cells;
		this.wifis = wifis;
	}

	public void locate() {
		new Thread() {
			@Override
			public void run() {
				long elapse = System.currentTimeMillis();
				HttpClient httpClient = null;
				HttpPost httpPost = null;
				try {
					String tmp = formatRequest();
					httpClient = new DefaultHttpClient();

					List<BasicNameValuePair> params;
					httpPost = new HttpPost(B_LOC_URL);
					params = new ArrayList<BasicNameValuePair>();
					params.add(new BasicNameValuePair("bloc", tmp));
					UrlEncodedFormEntity urlencodedformentity = new UrlEncodedFormEntity(
							params, "utf-8");
					httpPost.setEntity(urlencodedformentity);

					HttpResponse http_resp = httpClient.execute(httpPost);
					elapse = System.currentTimeMillis() - elapse;
					if (http_resp.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
						String result = (EntityUtils.toString(
								http_resp.getEntity(), "utf-8"));
						JSONObject json = new JSONObject(result);
						int status_code = json.getJSONObject("result").getInt(
								"error");
						if (status_code != 167) {
							double lat = json.getJSONObject("content")
									.getJSONObject("point").getDouble("y");
							lat = LocationUtil.format(lat, 5);
							double lng = json.getJSONObject("content")
									.getJSONObject("point").getDouble("x");
							lng = LocationUtil.format(lng, 5);
							int acc = (int) json.getJSONObject("content")
									.getDouble("radius");
							Coord coord = new Coord(Coord.CoordSource.BMAP,
									lat, lng, acc, elapse);
							Logger.i(Tag, String.format(
									"get coord form Baidu(%f, %f, %d)",
									coord.lat(), coord.lng(), coord.acc()));
							// coords3thPart.add(coord);
						}
					}
				} catch (Exception e) {
					Log.e(Tag, "gao de locate request error", e);
				} finally {
					if (httpPost != null) {
						httpPost.abort();
					}
					if (httpClient != null) {
						httpClient.getConnectionManager().shutdown();
					}
					Log.d(Tag, "get coord(%s) from Baidu done!");
				}
			}
		}.start();
	}

	public void stop() {

	}

	public String formatRequest() {
		String imei = "";
		try {
			Class<?> clazz = Class.forName("com.dianping.app.Environment");
			if (clazz != null) {
				// imei = Environment.imei();
			}
		} catch (Exception e) {

		}

		String tmp = "&cl=";
		if (cells == null || cells.size() == 0) {
			tmp += "0|0|-1|-1";
		} else {
			Cell cell = cells.get(0);
			switch (cell.type()) {
			case CDMA:
				CDMACell cdmaCell = (CDMACell) cell;
				tmp += cdmaCell.mcc() + "|" + cdmaCell.sid() + "|"
						+ cdmaCell.nid() + "|" + cdmaCell.bid() + "&clt=";
				break;
			case GSM:
				GSMCell gsmCell = (GSMCell) cell;
				tmp += gsmCell.mcc() + "|" + gsmCell.mnc() + "|"
						+ gsmCell.lac() + "|" + gsmCell.cid() + "&clt=";
				for (Cell theCell : cells) {
					gsmCell = (GSMCell) theCell;
					tmp += gsmCell.mcc() + "|" + gsmCell.mnc() + "|"
							+ gsmCell.lac() + "|" + gsmCell.cid() + "|1;";
				}
				break;
			}
			tmp += "10";
		}

		if (wifis != null && wifis.size() != 0) {
			tmp += "&wf=";
			for (int i = 0; i < wifis.size(); ++i) {
				tmp += wifis.get(i).mac().replaceAll(":", "") + ";"
						+ Math.abs(wifis.get(i).dBm()) + ";|";
			}
			tmp = tmp.substring(0, tmp.length() - 1);
		}
		tmp += "&addr=detail&coor=gcj02&os=android&prod=default&im=";
		tmp += imei;
		tmp = BMapDigester.digest(tmp) + "|tp=2";

		return tmp;
	}
}
