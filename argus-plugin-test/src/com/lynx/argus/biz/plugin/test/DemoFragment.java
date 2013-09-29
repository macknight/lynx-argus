package com.lynx.argus.biz.plugin.test;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class DemoFragment extends Fragment {
    Button btn;
    int counter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.main, container, false);
        btn = (Button) v.findViewById(R.id.btn);
        btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                btn.setText(String.valueOf(++counter));
            }
        });
        if (savedInstanceState != null) {
            counter = savedInstanceState.getInt("counter");
        }

        btn.setText(String.valueOf(counter));
        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("counter", counter);
    }
}
