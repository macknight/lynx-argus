package com.lynx.service.geo.impl1v1;

import com.lynx.lib.util.StringUtils;
import com.lynx.service.geo.entity.Address;
import com.lynx.service.geo.entity.GeoPoint;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.URLEncoder;

/**
 * Created with IntelliJ IDEA.
 * User: chris
 * Date: 13-9-10 下午1:38
 */
public class BMapApiUtil {
    private static final String BMAP_API_KEY = "EAaacd071ed10ccc5653a49b9fbd2923";
    private static final String BMAP_API_GEOCODING_URL = "http://api.map.baidu.com/geocoder/v2/?address=%s&output=json&ak=%s";
    private static final String BMAP_API_REVGEOCODING_URL = "http://api.map.baidu.com/geocoder/v2/?ak=%s&callback=renderReverse&geo=%s,%s&output=json&pois=1";


    public static Address revGeoCoding(double lat, double lng) {
        String url = String.format(BMAP_API_REVGEOCODING_URL, BMAP_API_KEY, lat, lng);
        HttpClient httpClient = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(url);
        InputStream instream = null;
        try {
            HttpResponse httpResp = httpClient.execute(httpGet);
            if (httpResp.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                instream = httpResp.getEntity().getContent();
                String tmp = StringUtils.stream2string(instream, "UTF-8");
                tmp = tmp.substring(29, tmp.length() - 1);
                JSONObject jo = new JSONObject(tmp);
                if (jo.getInt("status") == 0) {
                    JSONObject joAddr = jo.getJSONObject("result").getJSONObject("addressComponent");
                    String province = joAddr.getString("province");
                    String city = joAddr.getString("city");
                    String region = joAddr.getString("region");
                    String street = joAddr.getString("street");
                    String number = joAddr.getString("number");
                    Address addr = new Address(province, city, region, street, number);
                    return addr;
                }
            }
        } catch (Exception e) {

        } finally {
            if (instream != null) {
                try {
                    instream.close();
                    instream = null;
                } catch (Exception e) {

                }
            }
            if (httpGet != null) {
                httpGet.abort();
            }
            if (httpClient != null) {
                httpClient.getConnectionManager().shutdown();
            }
            httpGet = null;
            httpClient = null;
            instream = null;
        }
        return null;
    }

    public static GeoPoint geoCoding(String address) {
        try {
            String tmp = URLEncoder.encode(address, "UTF-8");
            String url = String.format(BMAP_API_GEOCODING_URL, tmp, BMAP_API_KEY);
            HttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(url);
            InputStream instream = null;
            try {
                HttpResponse httpResp = httpClient.execute(httpGet);
                if (httpResp.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                    instream = httpResp.getEntity().getContent();
                    tmp = StringUtils.stream2string(instream, "UTF-8");
                    JSONObject jo = new JSONObject(tmp);
                    if (jo.getInt("status") == 0) {
                        JSONObject joLoc = jo.getJSONObject("result").getJSONObject("geo");
                        double lat = joLoc.getDouble("lat");
                        double lng = joLoc.getDouble("lng");
                        return new GeoPoint(lat, lng);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (instream != null) {
                    try {
                        instream.close();
                    } catch (Exception e) {

                    }
                }
                if (httpGet != null) {
                    httpGet.abort();
                }
                if (httpClient != null) {
                    httpClient.getConnectionManager().shutdown();
                }
                httpGet = null;
                httpClient = null;
                instream = null;
            }
        } catch (Exception e) {

        }
        return null;
    }
}
