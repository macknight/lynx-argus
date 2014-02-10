package com.lynx.argus.biz.more;

import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import com.lynx.argus.R;
import com.lynx.lib.core.LFApplication;
import com.lynx.lib.core.LFFragment;
import com.lynx.lib.geo.GeoService;
import com.lynx.lib.geo.entity.*;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

/**
 * @author chris.liu
 * @addtime 14-1-29 下午9:44
 */
public class SysInfoFragment extends LFFragment {

	private static final List<BasicNameValuePair> SYS_INFO = new ArrayList<BasicNameValuePair>();
	private GeoService geoService;

	static {
		SYS_INFO.add(new BasicNameValuePair("product", android.os.Build.PRODUCT));
		SYS_INFO.add(new BasicNameValuePair("cpu_abi", android.os.Build.CPU_ABI));
		SYS_INFO.add(new BasicNameValuePair("tags", android.os.Build.TAGS));
		SYS_INFO.add(new BasicNameValuePair("version_codes_base",
				android.os.Build.VERSION_CODES.BASE + ""));
		SYS_INFO.add(new BasicNameValuePair("model", android.os.Build.MODEL));
		SYS_INFO.add(new BasicNameValuePair("sdk", android.os.Build.VERSION.SDK_INT + ""));
		SYS_INFO.add(new BasicNameValuePair("version_release", android.os.Build.VERSION.RELEASE));
		SYS_INFO.add(new BasicNameValuePair("device", android.os.Build.DEVICE));
		SYS_INFO.add(new BasicNameValuePair("display", android.os.Build.DISPLAY));
		SYS_INFO.add(new BasicNameValuePair("brand", android.os.Build.BRAND));
		SYS_INFO.add(new BasicNameValuePair("borad", android.os.Build.BOARD));
		SYS_INFO.add(new BasicNameValuePair("finger_print", android.os.Build.FINGERPRINT));
		SYS_INFO.add(new BasicNameValuePair("id", android.os.Build.ID));
		SYS_INFO.add(new BasicNameValuePair("manufacturer", android.os.Build.MANUFACTURER));
		SYS_INFO.add(new BasicNameValuePair("user", android.os.Build.USER));
	}

	public SysInfoFragment() {
		geoService = (GeoService) LFApplication.instance().service("geo");
	}

	@Override
	protected View onLoadView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) throws Exception {
		View view = inflater.inflate(R.layout.layout_sys_info, container, false);
		addCellInfo(view);
		addWifiInfo(view);
		addCoordInfo(view);
		addSysInfo(view);
		addServiceInfo(view);
		return view;
	}

	private void addCellInfo(View parent) {
		TableLayout tlCellInfo = (TableLayout) parent.findViewById(R.id.tl_sys_info_cell);
		TextView tvCellTitle = (TextView) parent.findViewById(R.id.tv_sys_info_cell_title);
		List<Cell> cells = geoService.cells();
		float[] layoutWeights = { 0.5f, 1.0f, 1.0f, 1.0f, 0.5f };
		if (cells != null && cells.size() > 0) {
			tvCellTitle.setText(tvCellTitle.getText() + cells.get(0).type().name());
			Cell firstCell = cells.get(0);
			List<String> data = new ArrayList<String>();
			if (firstCell instanceof GSMCell) {
				data.add(tabActivity.getString(R.string.tv_cell_mcc_tip));
				data.add(tabActivity.getString(R.string.tv_cell_mnc_tip));
				data.add(tabActivity.getString(R.string.tv_cell_lac_tip));
				data.add(tabActivity.getString(R.string.tv_cell_cid_tip));
				data.add(tabActivity.getString(R.string.tv_cell_asu_tip));
			} else {
				data = new ArrayList<String>();
				data.add(tabActivity.getString(R.string.tv_cell_mcc_tip));
				data.add(tabActivity.getString(R.string.tv_cell_sid_tip));
				data.add(tabActivity.getString(R.string.tv_cell_nid_tip));
				data.add(tabActivity.getString(R.string.tv_cell_bid_tip));
				data.add(tabActivity.getString(R.string.tv_cell_asu_tip));
			}
			addRow(tlCellInfo, data, false, 0, layoutWeights);

			for (int i = 0; i < cells.size(); ++i) {
				data = new ArrayList<String>();
				Cell cell = cells.get(i);
				if (cell instanceof GSMCell) {
					GSMCell gsmCell = (GSMCell) cell;
					data.add(gsmCell.mcc() + "");
					data.add(gsmCell.mnc() + "");
					data.add(gsmCell.lac() + "");
					data.add(gsmCell.cid() + "");
					data.add(gsmCell.asu() + "");
					addRow(tlCellInfo, data, i == cells.size() - 1, i + 1, layoutWeights);
				} else if (cell instanceof CDMACell) {
					CDMACell cdmaCell = (CDMACell) cell;
					data.add(cdmaCell.mcc() + "");
					data.add(cdmaCell.sid() + "");
					data.add(cdmaCell.nid() + "");
					data.add(cdmaCell.bid() + "");
					data.add("0");
					addRow(tlCellInfo, data, i == cells.size() - 1, i + 1, layoutWeights);
				}
			}
		}
	}

	private void addWifiInfo(View parent) {
		TableLayout tlWifiInfo = (TableLayout) parent.findViewById(R.id.tl_sys_info_wifi);
		List<Wifi> wifis = geoService.wifis();
		float[] wifi_weights = { 0.5f, 1.0f, 0.3f };
		if (wifis != null && wifis.size() > 0) {
			for (int i = 0; i < wifis.size(); ++i) {
				List<String> data = new ArrayList<String>();
				Wifi wifi = wifis.get(i);
				data.add(wifi.ssid());
				data.add(wifi.mac());
				data.add(wifi.dBm() + "");
				addRow(tlWifiInfo, data, i == wifis.size() - 1, i, wifi_weights);
			}
		}
	}

	private void addCoordInfo(View parent) {
		TableLayout tlWifiInfo = (TableLayout) parent.findViewById(R.id.tl_sys_info_wifi);
		List<Wifi> wifis = geoService.wifis();
		float[] wifi_weights = { 0.5f, 1.0f, 0.3f };
		if (wifis != null && wifis.size() > 0) {
			for (int i = 0; i < wifis.size(); ++i) {
				List<String> data = new ArrayList<String>();
				Wifi wifi = wifis.get(i);
				data.add(wifi.ssid());
				data.add(wifi.mac());
				data.add(wifi.dBm() + "");
				addRow(tlWifiInfo, data, i == wifis.size() - 1, i, wifi_weights);
			}
		}
	}

	private void addSysInfo(View parent) {
		TableLayout tlCoordInfo = (TableLayout) parent.findViewById(R.id.tl_sys_info_coord);
		List<Coord> coords = geoService.coords();
		float[] coord_weights = { 1.0f, 1.0f, 1.0f, 0.7f, 1.0f };
		if (coords != null && coords.size() > 0) {
			for (int i = 0; i < coords.size(); ++i) {
				List<String> data = new ArrayList<String>();
				Coord coord = coords.get(i);
				data.add(coord.source().name());
				data.add(coord.lat() + "");
				data.add(coord.lng() + "");
				data.add(coord.acc() + "");
				data.add(coord.elapse() + "");
				addRow(tlCoordInfo, data, i == coords.size() - 1, i, coord_weights);
			}
		}
	}

	private void addServiceInfo(View parent) {
		TableLayout tlSysInfo = (TableLayout) parent.findViewById(R.id.tl_sys_info_os);

		float[] sys_weights = { 0.3f, 0.6f };
		for (int i = 0; i < SYS_INFO.size(); ++i) {
			List<String> data = new ArrayList<String>();
			data.add(SYS_INFO.get(i).getName());
			data.add(SYS_INFO.get(i).getValue());
			addRow(tlSysInfo, data, i == SYS_INFO.size() - 1, i, sys_weights);
		}
	}

	private void addRow(TableLayout parent, List<String> data, boolean islst, int index,
			float[] weights) {
		int line_color = Color.rgb(0, 160, 160);
		int text_size = 12;
		int text_color = Color.BLACK;
		int text_bg_color_0 = Color.rgb(220, 220, 220);
		int text_bg_color_1 = Color.rgb(239, 228, 176);
		int text_bg_color = text_bg_color_0;
		View line = null;

		if (index % 2 == 0) {
			text_bg_color = text_bg_color_1;
		} else {
			text_bg_color = text_bg_color_0;
		}

		TableRow.LayoutParams params;

		TableRow tr = new TableRow(tabActivity);
		tr.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT));
		tr.setBackgroundColor(text_bg_color);

		for (int i = 0; i < data.size(); ++i) {
			String str_value = data.get(i);
			TextView tv_value = new TextView(tabActivity);
			params = new TableRow.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, weights[i]);
			tv_value.setLayoutParams(params);
			tv_value.setEllipsize(TextUtils.TruncateAt.END);
			tv_value.setSingleLine(true);
			tv_value.setGravity(Gravity.CENTER);
			tv_value.setTextSize(text_size);
			tv_value.setTextColor(text_color);
			tv_value.setText(str_value);
			tv_value.setBackgroundColor(Color.TRANSPARENT);
			tr.addView(tv_value);

			if (i < data.size() - 1) {
				line = new View(tabActivity);
				params = new TableRow.LayoutParams(1, ViewGroup.LayoutParams.MATCH_PARENT);
				line.setLayoutParams(params);
				line.setBackgroundColor(line_color);
				tr.addView(line);
			}
		}
		parent.addView(tr, new TableLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.MATCH_PARENT));
	}

}
