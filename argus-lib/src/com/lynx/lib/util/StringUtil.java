package com.lynx.lib.util;

import android.text.TextUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;

/**
 * 
 * @author zhufeng.liu
 * 
 * @addtime 13-8-30 下午1:33
 */
public class StringUtil {

	private final static String[] hexDigits = { "0", "1", "2", "3", "4", "5",
			"6", "7", "8", "9", "a", "b", "c", "d", "e", "f" };

	private StringUtil() {
		throw new AssertionError("this class shouldn't be instanced");
	}

	public static String byteArrayToHexString(byte[] b) {
		StringBuilder resultSb = new StringBuilder();
		for (int i = 0; i < b.length; i++) {
			resultSb.append(byteToHexString(b[i]));
		}
		return resultSb.toString();
	}

	private static String byteToHexString(byte b) {
		int n = b;
		if (n < 0)
			n = 0x100 + n;
		int d1 = n >> 4;
		int d2 = n & 0xF;
		return hexDigits[d1] + hexDigits[d2];
	}

	private static final String[] P_0 = new String[] { "", "0", "00", "000",
			"0000", "00000", "000000", "0000000", "00000000" };

	public static String fixHex(long l, int fixLen) {
		String h = Long.toHexString(l);
		int lack = fixLen - h.length();
		if (lack > 0) {
			if (lack < P_0.length) {
				h = P_0[lack] + h;
			} else {
				StringBuffer sb = new StringBuffer(16);
				for (int i = 0; i < lack; i++) {
					sb.append('0');
				}
				sb.append(h);
				h = sb.toString();
			}
		}
		return h;
	}

	public static String MD5Encode(String origin) {
		String resultString = null;
		try {
			resultString = new String(origin);
			MessageDigest md = MessageDigest.getInstance("MD5");
			resultString = byteArrayToHexString(md.digest(resultString
					.getBytes()));
		} catch (Exception ex) {
		}
		return resultString;
	}

	public static String join(long[] ls, String split) {
		switch (ls.length) {
		case 0:
			return "";
		case 1:
			return String.valueOf(ls[0]);
		case 2:
			return String.valueOf(ls[0]) + split + String.valueOf(ls[1]);
		case 3:
			return String.valueOf(ls[0]) + split + String.valueOf(ls[1])
					+ split + String.valueOf(ls[2]);
		case 4:
			return String.valueOf(ls[0]) + split + String.valueOf(ls[1])
					+ split + String.valueOf(ls[2]) + split
					+ String.valueOf(ls[3]);
		case 5:
			return String.valueOf(ls[0]) + split + String.valueOf(ls[1])
					+ split + String.valueOf(ls[2]) + split
					+ String.valueOf(ls[3]) + split + String.valueOf(ls[4]);
		default:
			StringBuffer sb = new StringBuffer(20 * ls.length);
			sb.append(ls[0]);
			for (int i = 1, n = ls.length; i < n; i++) {
				sb.append(split);
				sb.append(ls[i]);
			}
			return sb.toString();
		}
	}

	public static long parseLong(String str) {
		try {
			return Long.parseLong(str);
		} catch (NumberFormatException e) {
			return new BigInteger(str).longValue();
		}
	}

	public static final String LESS_THAN_ENTITY = "&lt;";
	public static final String GREATER_THAN_ENTITY = "&gt;";
	public static final String AMPERSAND_ENTITY = "&amp;";
	public static final String APOSTROPHE_ENTITY = "&apos;";
	public static final String QUOTE_ENTITY = "&quot;";

	public static String xmlEscapeBody(Object value) {
		StringBuffer buffer = new StringBuffer(value.toString());
		for (int i = 0, size = buffer.length(); i < size; i++) {
			switch (buffer.charAt(i)) {
			case '<':
				buffer.replace(i, i + 1, LESS_THAN_ENTITY);
				size += 3;
				i += 3;
				break;
			case '>':
				buffer.replace(i, i + 1, GREATER_THAN_ENTITY);
				size += 3;
				i += 3;
				break;
			case '&':
				buffer.replace(i, i + 1, AMPERSAND_ENTITY);
				size += 4;
				i += 4;
				break;
			}
		}
		return buffer.toString();
	}

	public static String xmlEscapeAttr(Object value) {
		StringBuffer buffer = new StringBuffer(value.toString());
		for (int i = 0, size = buffer.length(); i < size; i++) {
			switch (buffer.charAt(i)) {
			case '<':
				buffer.replace(i, i + 1, LESS_THAN_ENTITY);
				size += 3;
				i += 3;
				break;
			case '>':
				buffer.replace(i, i + 1, GREATER_THAN_ENTITY);
				size += 3;
				i += 3;
				break;
			case '&':
				buffer.replace(i, i + 1, AMPERSAND_ENTITY);
				size += 4;
				i += 4;
				break;
			case '\'':
				buffer.replace(i, i + 1, APOSTROPHE_ENTITY);
				size += 5;
				i += 5;
				break;
			case '\"':
				buffer.replace(i, i + 1, QUOTE_ENTITY);
				size += 5;
				i += 5;
				break;
			}
		}
		return buffer.toString();
	}

	public static boolean validateLength(String str, int min, int max) {
		if (str == null)
			str = "";
		if (str.length() > max)
			return false;
		try {
			byte[] bytes = str.getBytes("utf-8");
			return bytes.length <= max && bytes.length >= min;
		} catch (UnsupportedEncodingException e) {
			return false;
		}
	}

	public static boolean validateLength(String str, int max) {
		return validateLength(str, 0, max);
	}

	public static boolean isAllPunctuation(String source) {
		String s = source;
		if (TextUtils.isEmpty(source)) {
			return true;
		} else {
			s = source.trim().replaceAll("\\p{Punct}|\\$|￥|~|～", "").trim();
			if (s == null || s.length() == 0) {
				return true;
			}
			return false;
		}
	}

	public static String stream2string(InputStream instream, String encoding)
			throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		int i = -1;
		while ((i = instream.read()) != -1) {
			baos.write(i);
		}
		return new String(baos.toByteArray(), encoding);
	}
}
