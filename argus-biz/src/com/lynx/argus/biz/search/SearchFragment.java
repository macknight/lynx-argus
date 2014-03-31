package com.lynx.argus.biz.search;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;

import com.lynx.argus.R;
import com.lynx.argus.biz.search.model.SuggestionAdapter;
import com.lynx.lib.core.LFFragment;

/**
 * 
 * @author zhufeng.liu
 * @version 13-10-27 下午9:29
 */
public class SearchFragment extends LFFragment {
	public static final String Tag = "Search";

	public SearchFragment() {

	}

	@Override
	public View onLoadView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
			throws Exception {
		View view = inflater.inflate(R.layout.layout_search, container, false);
		if (view == null) {
			throw new Exception("页面初始化错误");
		}

		AutoCompleteTextView actvSearch = (AutoCompleteTextView) view
				.findViewById(R.id.actv_search_keyword);
		String keyword = actvSearch.getText().toString();
		SuggestionAdapter suggestionAdapter = new SuggestionAdapter(tabActivity);
		actvSearch.setAdapter(suggestionAdapter);
		return view;
	}

}
