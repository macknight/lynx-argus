package com.lynx.lib.core;

/**
 * 
 * @author zhufeng.liu
 * 
 * @addtime 13-10-6 下午8:26
 */
public class Const {

	// 线上环境域名
	public static String LM_API_DOMAIN = "http://58.210.101.202:59102/lynx-web";

	public static String BMAP_API_KEY = "EAaacd071ed10ccc5653a49b9fbd2923";
	public static String BMAP_API_PLACE = "http://api.map.baidu.com/place/v2";
	public static String BMAP_API_DIRECT = "http://api.map.baidu.com/direction/v1";
	public static String BMAP_API_TELEMATIC = "http://api.map.baidu.com/telematics/v3";

	private Const() {
		throw new AssertionError("this class shouldn't be instanced");
	}

}
