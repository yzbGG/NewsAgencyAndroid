/**
 *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *
 * @COMPANY IFXME.COM
 * @AUTHOR dkslbw@gmail.com
 * @TIME 2014年8月8日 上午1:13:43
 *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  * 
 */
package com.newsagency.mobile.util;

import java.util.ResourceBundle;

/**
 * @DESCRIBE 读取应用程序信息的工具类
 */
public class AppInfoUtil {

	private ResourceBundle rb;

	private AppInfoUtil() {
		rb = ResourceBundle.getBundle("appinfo");
	}

	private static AppInfoUtil util;

	public static AppInfoUtil sharedAppInfoUtil() {
		if (util == null) {
			util = new AppInfoUtil();
		}
		return util;
	}

	/**
	 * 获取key对应的值
	 * @param key
	 * @return
	 */
	private String get(String key) {
		String result = rb.getString(key);
		return result;

	}
	/**
	 * 获取app_key
	 * @return
	 */
	public String getAppKey()
	{
		return get("app_key");
	}
	/**
	 * 获取app_secret
	 */
	public String getAppSecret()
	{
		return get("app_secret");
	}
	
	/**
	 * 获取服务器url
	 */
	public String getServerUrl()
	{
		return get("server_url");
	}
	
	/**
	 * 获取序列化文件的路径
	 * @return
	 */
	public String getSerializeDir()
	{
		return get("serialize_dir");
	}
	
	/**
	 * 获取图片服务器url
	 * @return
	 */
	public String getImageServerUrl()
	{
		return get("image_server_url");
	}
	
	/**
	 * 获取应从程序更新的url
	 * @return
	 */
	public String getAppUpdateUrl()
	{
		return get("app_update_url");
	}
	
}