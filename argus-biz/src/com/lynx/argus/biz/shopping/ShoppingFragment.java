package com.lynx.argus.biz.shopping;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import com.lynx.argus.R;
import com.lynx.argus.app.BizFragment;
import com.lynx.lib.core.LFDexActivity;

/**
 * Created with IntelliJ IDEA.
 * User: chris
 * Date: 13-9-12 下午6:20
 */
public class ShoppingFragment extends BizFragment {

	public static final String Tag = "shopping";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.layout_shopping, container, false);

		ImageButton ib = (ImageButton) v.findViewById(R.id.ib_local_dex_laoder);
		ib.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent i = new Intent(getActivity(), LFDexActivity.class);
				i.putExtra("module", "local");
				startActivity(i);
			}
		});

		return v;
	}

}
