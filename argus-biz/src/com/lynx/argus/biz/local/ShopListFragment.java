package com.lynx.argus.biz.local;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.*;
import com.lynx.argus.R;
import com.lynx.argus.app.BizApplication;
import com.lynx.argus.app.BizFragment;
import com.lynx.argus.biz.SysInfoActivity;
import com.lynx.lib.core.Const;
import com.lynx.lib.http.HttpService;
import com.lynx.lib.http.handler.HttpCallback;
import com.lynx.lib.widget.pulltorefresh.PullToRefreshListView;
import com.lynx.lib.geo.GeoService;
import com.lynx.lib.geo.LocationListener;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: chris
 * Date: 13-9-16 上午10:29
 */
public class ShopListFragment extends BizFragment {
    private GeoService geoService;
    private HttpService httpService;

    private List<Map<String, Object>> shops = new ArrayList<Map<String, Object>>();
    private ShopListAdapter adapter;
    private PullToRefreshListView ptrlvShop;

    private static final String BMAP_PLACE = "http://api.map.baidu.com/place/v2/search?&query=%s&location=%s,%s&radius=2000&output=json&ak=%s";

    private Animation animRotate;
    private AnimationDrawable adIndicator;
    private TextView tvLocAddr;
    private ImageView ivLocIndicator, ivLocRefresh;

    private String query = "美食";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            httpService = (HttpService) BizApplication.instance().service("http");
        } catch (Exception e) {
            e.printStackTrace();
        }

        query = getArguments().getString("query");

        adapter = new ShopListAdapter(tabActivity, shops);

        animRotate = new RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        animRotate.setDuration(1500);
        animRotate.setRepeatCount(-1);
        animRotate.setRepeatMode(Animation.RESTART);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.layout_shop_list, container, false);

        initLocationModule(v);

        ptrlvShop = (PullToRefreshListView) v.findViewById(R.id.prlv_shop_list);
        ptrlvShop.getRefreshableView().setAdapter(adapter);
        Drawable drawable = getResources().getDrawable(R.drawable.ptr_refresh);
        ptrlvShop.setLoadingDrawable(drawable);

        ptrlvShop.setOnRefreshListener(new PullToRefreshListView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (geoService == null) {
                    Toast.makeText(tabActivity, "定位模块不可用", Toast.LENGTH_SHORT).show();
                    return;
                } else if (geoService.coord() == null) {
                    geoService.locate(false);
                    return;
                } else {
                    getLocalShop();
                }
            }
        });

        ptrlvShop.getRefreshableView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Map<String, Object> shop = shops.get(position - 1);
                ShopDetailFragment sdf = new ShopDetailFragment();
                Bundle bundle = new Bundle();
                bundle.putString("uid", shop.get("uid").toString());
                sdf.setArguments(bundle);
                tabActivity.pushFragment(LocalFragment.Tag, sdf, true, true);
            }
        });

        ImageView ivIndicator = (ImageView) v.findViewById(R.id.iv_loc_indicator);
        ivIndicator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(tabActivity, SysInfoActivity.class);
                startActivity(intent);
            }
        });

        return v;
    }

    private HttpCallback httpCallback = new HttpCallback<Object>() {
        @Override
        public void onSuccess(Object o) {
            ptrlvShop.onRefreshComplete();
            super.onSuccess(o);
            try {
                JSONObject joResult = new JSONObject(o.toString());
                if (joResult.getInt("status") == 0) {
                    JSONArray jaResult = joResult.getJSONArray("results");
                    if (jaResult == null || jaResult.length() == 0) {
                        return;
                    }
                    shops.clear();
                    for (int i = 0; i < jaResult.length(); ++i) {
                        Map<String, Object> shopInfo = new HashMap<String, Object>();
                        JSONObject joShop = jaResult.getJSONObject(i);

                        String name = "";
                        try {
                            name = joShop.getString("name");
                        } catch (Exception e) {
                        }
                        shopInfo.put("name", name);

                        String addr = "";
                        try {
                            addr = joShop.getString("address");
                        } catch (Exception e) {
                        }
                        shopInfo.put("addr", addr);

                        String tele = "";
                        try {
                            tele = joShop.getString("telephone");
                        } catch (Exception e) {
                        }
                        shopInfo.put("tele", tele);

                        String uid = "";
                        try {
                            uid = joShop.getString("uid");
                        } catch (Exception e) {
                        }
                        shopInfo.put("uid", uid);
                        shops.add(shopInfo);
                    }
                    adapter.setData(shops);
                } else {
                    Toast.makeText(tabActivity, "刷新失败", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onFailure(Throwable t, String strMsg) {
            super.onFailure(t, strMsg);
            ptrlvShop.onRefreshComplete();
            Toast.makeText(tabActivity, "刷新失败", Toast.LENGTH_SHORT).show();
        }
    };

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    ivLocIndicator.setBackgroundResource(R.anim.anim_indicator);
                    adIndicator = (AnimationDrawable) ivLocIndicator.getBackground();
                    adIndicator.setOneShot(false);
                    if (adIndicator.isRunning()) {
                        adIndicator.stop();
                    }
                    adIndicator.start();
                    animRotate.startNow();
                    tvLocAddr.setText("正在定位...");
                    break;
                case 1:  // 定位成功
                    if (adIndicator != null) {
                        adIndicator.stop();
                    }
                    if (animRotate != null) {
                        animRotate.cancel();
                    }
                    ivLocIndicator.setBackgroundResource(R.drawable.green_point);
                    tvLocAddr.setText(geoService.address());
                    getLocalShop();
                    break;
                case 2:
                    if (adIndicator != null) {
                        adIndicator.stop();
                    }
                    if (animRotate != null) {
                        animRotate.cancel();
                    }
                    ivLocIndicator.setBackgroundResource(R.drawable.gray_point);
                    tvLocAddr.setText("定位失败");
                    if (ptrlvShop.isRefreshing()) {
                        ptrlvShop.onRefreshComplete();
                    }
                    break;
            }
        }
    };

    /**
     * 初始化定位相关模块
     */
    private void initLocationModule(View v) {
        geoService = (GeoService) BizApplication.instance().service("geo");

        ivLocIndicator = (ImageView) v.findViewById(R.id.iv_loc_indicator);
        tvLocAddr = (TextView) v.findViewById(R.id.tv_loc_addr);
        ivLocRefresh = (ImageView) v.findViewById(R.id.iv_loc_refresh);
        ivLocRefresh.setAnimation(animRotate);
        animRotate.cancel();


        if (geoService == null) {
            Toast.makeText(this.getActivity(), "定位模块不可用", Toast.LENGTH_SHORT).show();
            return;
        }

        if (geoService.address() != null) {
            tvLocAddr.setText(geoService.address());
            ivLocIndicator.setBackgroundResource(R.drawable.green_point);
        }

        ivLocRefresh.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                geoService.locate(false);
            }
        });

        geoService.addListener(new LocationListener() {
            @Override
            public void onLocationChanged(GeoService.LocationStatus status) {
                switch (status) {
                    case ONGOING:
                        handler.sendEmptyMessage(0);
                        break;
                    case SUCCESS:
                        handler.sendEmptyMessage(1);
                        break;
                    case FAIL:
                        handler.sendEmptyMessage(2);
                        break;
                }
            }
        });
    }

    private void getLocalShop() {
        try {
            if (geoService == null) {
                Toast.makeText(tabActivity, "定位模块不可用", Toast.LENGTH_SHORT).show();
                return;
            } else if (geoService.coord() == null) {
                geoService.locate(false);
                return;
            } else {
                double lat = geoService.coord().getLat();
                double lng = geoService.coord().getLng();
                String url = String.format(BMAP_PLACE, URLEncoder.encode(query, "utf-8"),
                        lat, lng, Const.BMAP_API_KEY);
                httpService.get(url, null, httpCallback);

                ptrlvShop.setRefreshing();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class ShopListAdapter extends BaseAdapter {
        private LayoutInflater inflater;
        private List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();


        private ShopListAdapter(Context context, List<Map<String, Object>> data) {
            this.inflater = LayoutInflater.from(context);
            this.data = data;
        }

        public void setData(List<Map<String, Object>> data) {
            this.data = data;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public Object getItem(int position) {
            return data.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.layout_shop_list_item, null, false);
            }
            //得到条目中的子组件
//            ImageView tvIcon = (ImageView) convertView.findViewById(R.id.iv_shop_list_item_icon);
            TextView tvName = (TextView) convertView.findViewById(R.id.tv_shop_list_item_name);
            TextView tvAddr = (TextView) convertView.findViewById(R.id.tv_shop_list_item_addr);
            TextView tvTel = (TextView) convertView.findViewById(R.id.tv_shop_list_item_tele);
            //从list对象中为子组件赋值
//            tvIcon.setBackgroundResource(Integer.parseInt(data.get(position).get("icon").toString()));
            tvName.setText(data.get(position).get("name").toString());
            tvAddr.setText(data.get(position).get("addr").toString());
            tvTel.setText(data.get(position).get("tele").toString());
            return convertView;
        }
    }
}