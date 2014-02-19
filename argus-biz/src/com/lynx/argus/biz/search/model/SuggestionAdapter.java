package com.lynx.argus.biz.search.model;

import android.app.Activity;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Toast;
import com.lynx.argus.R;
import com.lynx.lib.core.Const;
import com.lynx.lib.core.LFApplication;
import com.lynx.lib.core.Logger;
import com.lynx.lib.http.HttpService;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhufeng.liu
 * @version 14-2-10 下午6:48
 */
public class SuggestionAdapter extends ArrayAdapter<String> {

	protected static final String Tag = "SuggestionAdapter";
	private static final String BMAP_API_PLACE_SUGGESSTION = "/suggestion";

	private HttpService httpService;
	private List<String> suggestions;

	public SuggestionAdapter(Activity context) {
		super(context, R.layout.layout_autocomplete_item);
		suggestions = new ArrayList<String>();
		httpService = (HttpService) LFApplication.instance().service("http");
	}

	@Override
	public int getCount() {
		return suggestions.size();
	}

	@Override
	public String getItem(int index) {
		return suggestions.get(index);
	}

	@Override
	public Filter getFilter() {
		Filter myFilter = new Filter() {
			@Override
			protected FilterResults performFiltering(CharSequence constraint) {
				FilterResults filterResults = new FilterResults();
				if (constraint != null) {
					List<SuggestionItem> newSuggestions = getSuggestion(constraint.toString(), "上海");
					suggestions.clear();
					for (int i = 0; i < newSuggestions.size(); i++) {
						suggestions.add(newSuggestions.get(i).name());
					}

					// Now assign the values and count to the FilterResults
					// object
					filterResults.values = suggestions;
					filterResults.count = suggestions.size();
				}
				return filterResults;
			}

			@Override
			protected void publishResults(CharSequence contraint, FilterResults results) {
				if (results != null && results.count > 0) {
					notifyDataSetChanged();
				} else {
					notifyDataSetInvalidated();
				}
			}
		};
		return myFilter;
	}

	private List<SuggestionItem> getSuggestion(String query, String city) {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("ak", Const.BMAP_API_KEY));
		params.add(new BasicNameValuePair("output", "json"));
		params.add(new BasicNameValuePair("query", query));
		params.add(new BasicNameValuePair("region", city));
		String param = URLEncodedUtils.format(params, "UTF-8");
		String url = String.format("%s%s?%s", Const.BMAP_API_PLACE, BMAP_API_PLACE_SUGGESSTION,
				param);

		try {
			Object result = httpService.getSync(url);
			JSONObject joResult = new JSONObject(result.toString());
			if (joResult.getInt("status") == 0) {
				JSONArray jaResult = joResult.getJSONArray("result");
				return parseSuggestion(jaResult);
			} else {
				Toast.makeText(getContext(), "刷新失败", Toast.LENGTH_SHORT).show();
			}
		} catch (Exception e) {
			Logger.e(Tag, "get suggestion error", e);
			e.printStackTrace();
		}
		return null;
	}

	private List<SuggestionItem> parseSuggestion(JSONArray data) {
		if (data == null || data.length() == 0) {
			return null;
		}
		List<SuggestionItem> suggestions = new ArrayList<SuggestionItem>();
		for (int i = 0; i < data.length(); ++i) {
			try {
				JSONObject joSuggestion = data.getJSONObject(i);
				String name = joSuggestion.getString("name");
				String district = joSuggestion.getString("district");
				suggestions.add(new SuggestionItem(name, district));
			} catch (Exception e) {

			}
		}
		return suggestions;
	}
}
