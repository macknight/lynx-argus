package com.lynx.lib.core;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.telephony.TelephonyManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

/**
 * 
 * @author chris.liu
 * 
 * @version 14-1-14 上午10:32
 */
public class LFEnvironment {

	private static boolean sourceInited = false;
	private static String imei;
	private static String did;
	private static String ua;
	private static String source;
	private static PackageInfo packageInfo;

	private LFEnvironment() {
		throw new AssertionError("this class shouldn't be instanced");
	}

	/**
	 * user agent
	 * 
	 * @return
	 */
	public String userAgent() {

		if (ua == null) {
			StringBuilder sb = new StringBuilder("MApi 1.0 (");

			try {
				Context c = LFApplication.instance();
				PackageInfo packageInfo = c.getPackageManager().getPackageInfo(
						c.getPackageName(), 0);

				sb.append(packageInfo.packageName);
				sb.append(" ").append(packageInfo.versionName);
			} catch (Exception e) {
				sb.append("com.dianping.v1 5.6");
			}

			// 只是保护一下
			try {
				String source = source();
				if (source != null)
					sb.append(" ").append(source);
				else
					sb.append(" null");

				sb.append(" Android ");
				sb.append(Build.VERSION.RELEASE);
				sb.append(")");
				ua = sb.toString();
			} catch (Exception e) {
				ua = "MApi 1.0 (com.dianping.v1 5.6 null null; Android "
						+ Build.VERSION.RELEASE + ")";
			}
		}
		return ua;
	}

	/**
	 * 设备唯一标识码，由wifi mac地址转义而成
	 * 
	 * @return
	 */
	public static String imei() {
		if (imei == null) {
			// update cached imei when identity changed. including brand, model,
			// radio and system version
			String deviceIdentity = Build.VERSION.RELEASE + ";" + Build.MODEL
					+ ";" + Build.BRAND;
			if (deviceIdentity.length() > 64) {
				deviceIdentity = deviceIdentity.substring(0, 64);
			}
			if (deviceIdentity.indexOf('\n') >= 0) {
				deviceIdentity = deviceIdentity.replace('\n', ' ');
			}

			String cachedIdentity = null;
			String cachedImei = null;
			try {
				// do not use file storage, use cached instead
				File path = new File(LFApplication.instance().getCacheDir(),
						"cached_imei");
				FileInputStream fis = new FileInputStream(path);
				byte[] buf = new byte[1024];
				int l = fis.read(buf);
				fis.close();
				String str = new String(buf, 0, l, "UTF-8");
				int a = str.indexOf('\n');
				cachedIdentity = str.substring(0, a);
				int b = str.indexOf('\n', a + 1);
				cachedImei = str.substring(a + 1, b);
			} catch (Exception e) {
			}

			if (deviceIdentity.equals(cachedIdentity)) {
				imei = cachedImei;
			} else {
				imei = null;
			}

			// cache fail, read from telephony manager
			if (imei == null) {
				try {
					TelephonyManager tel = (TelephonyManager) LFApplication
							.instance().getSystemService(
									Context.TELEPHONY_SERVICE);
					imei = tel.getDeviceId();
					if (imei != null) {
						if (imei.length() < 8) {
							imei = null;
						} else {
							char c0 = imei.charAt(0);
							boolean allSame = true;
							for (int i = 0, n = imei.length(); i < n; i++) {
								if (c0 != imei.charAt(i)) {
									allSame = false;
									break;
								}
							}
							if (allSame)
								imei = null;
						}
					}
				} catch (Exception e) {
				}
				if (imei != null) {
					try {
						File path = new File(LFApplication.instance()
								.getCacheDir(), "cached_imei");
						FileOutputStream fos = new FileOutputStream(path);
						String str = deviceIdentity + "\n" + imei + "\n";
						fos.write(str.getBytes("UTF-8"));
						fos.close();
					} catch (Exception e) {
					}
				} else {
					File path = new File(
							LFApplication.instance().getCacheDir(),
							"cached_imei");
					path.delete();
				}
			}

			if (imei == null) {
				imei = "00000000000000";
			}
		}
		return imei;
	}

	public static boolean isDebug() {
		return Logger.level() != Logger.AppLevel.PRODUCT;
	}

	private static PackageInfo pkgInfo() {
		if (packageInfo == null) {
			try {
				packageInfo = LFApplication
						.instance()
						.getPackageManager()
						.getPackageInfo(
								LFApplication.instance().getPackageName(), 0);
			} catch (PackageManager.NameNotFoundException e) {

			}
		}

		return packageInfo;
	}

	private static String source() {
		if (!sourceInited) {
			try {
				InputStream ins = LFApplication.instance.getAssets().open(
						"source.txt");
				byte[] bytes = new byte[0x100];
				int l = ins.read(bytes);
				if (l > 0) {
					String str = new String(bytes, 0, l);
					source = escapeSource(str);
				}
			} catch (Exception ignored) {

			}
			sourceInited = true;
		}
		return source;
	}

	private static String escapeSource(String src) {
		StringBuilder sb = new StringBuilder();
		for (char c : src.toCharArray()) {
			if (c >= 'a' && c <= 'z') {
				sb.append(c);
			} else if (c >= 'A' && c <= 'Z') {
				sb.append(c);
			} else if (c >= '0' && c <= '9') {
				sb.append(c);
			} else if (c == '.' || c == '_' || c == '-' || c == '/') {
				sb.append(c);
			} else if (c == ' ') {
				sb.append('_');
			}
		}
		return sb.toString();
	}

}
