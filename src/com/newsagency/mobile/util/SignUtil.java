/**
 *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *
 * @COMPANY IFXME.COM
 * @AUTHOR dkslbw@gmail.com
 * @TIME 2014年8月8日 上午1:08:14
 *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  * 
 */
package com.newsagency.mobile.util;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @DESCRIBE 签名工具类
 */
public class SignUtil {

	/**
	 * 获取增加安全验证的map
	 * 
	 * @param params
	 * @param isloged
	 * @return
	 */
	public static final Map<String, String> getSignedParam(Map<String, String> params, boolean isloged) {
		params.put("timestamp", getDateTimePoint());
		if (AppHelper.appVersion != null && AppHelper.appVersion.length() != 0) {
			params.put("v", AppHelper.appVersion);
		} else {
			params.put("v", "unknown");
		}
		// params.put("app_key", AppInfoUtil.sharedAppInfoUtil().getAppKey());
		if (isloged) {
			// params.put("session_id",
			// UserData.SharedUserData().getUSESSION());
		}
		params.put("app_secret", AppInfoUtil.sharedAppInfoUtil().getAppSecret());
		Object[] keys = params.keySet().toArray();
		Arrays.sort(keys);
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < keys.length; i++) {
			builder.append(keys[i].toString() + params.get(keys[i].toString()));
		}
		String sign = MD5Util.SharedMD5Util().Md5(builder.toString());
		params.remove("app_secret");
		Map<String, String> requestParams = new HashMap<String, String>();
		Iterator<String> it = params.keySet().iterator();
		while (it.hasNext()) {
			String key = it.next();
			requestParams.put(key, params.get(key));
		}
		requestParams.put("sign", sign);
		return requestParams;
	}

	/**
	 * 获取增加安全验证的map
	 * 
	 * @param params
	 * @param isloged
	 * @return
	 */
	public static final String getSignedString(Map<String, String> params, boolean isloged) {
		params.put("timestamp", getDateTimePoint());
		params.put("v", "1.0");
		// params.put("app_key", AppInfoUtil.sharedAppInfoUtil().getAppKey());
		if (isloged) {
			// params.put("session_id",
			// UserData.SharedUserData().getUSESSION());
		}
		params.put("app_secret", AppInfoUtil.sharedAppInfoUtil().getAppSecret());
		Object[] keys = params.keySet().toArray();
		Arrays.sort(keys);
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < keys.length; i++) {
			builder.append(keys[i].toString() + params.get(keys[i].toString()));
		}
		String sign = MD5Util.SharedMD5Util().Md5(builder.toString());
		params.remove("app_secret");
		// Map<String, String> requestParams = new HashMap<String, String>();
		String url = "";
		Iterator<String> it = params.keySet().iterator();
		while (it.hasNext()) {
			String key = it.next();
			// requestParams.put(key, params.get(key));
			url += key + "=" + params.get(key) + "&";
		}
		url += "sign=" + sign;
		// requestParams.put("sign", sign);
		return url;
	}

	/**
	 * 获取当前的时间戳
	 * 
	 * @return 日期时间点字符串.
	 */
	private static String getDateTimePoint() {
		long timestamp = System.currentTimeMillis();
		Date date = new Date(timestamp);
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return formatter.format(date);
	}

}
