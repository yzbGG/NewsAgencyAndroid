/**
 *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *
 * @COMPANY IFXME.COM
 * @AUTHOR dkslbw@gmail.com
 * @TIME 2014年8月10日 上午10:37:43
 *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  * 
 */
package com.newsagency.mobile.util;

import java.util.ResourceBundle;

/**
 * @DESCRIBE 解析接口url 和 参数的工具类
 */
public class UrlUtil {

	private ResourceBundle rb;

	private UrlUtil() {
		rb = ResourceBundle.getBundle("urls");
	}

	private static UrlUtil util;

	public static UrlUtil sharedUrlUtil() {
		if (util == null) {
			util = new UrlUtil();
		}
		return util;
	}

	/**
	 * 获取key对应的值
	 * 
	 * @param key
	 * @return
	 */
	private String get(String key) {
		String result = rb.getString(key);
		return result;
	}

	/**
	 * 获取url
	 * @param key
	 * @return
	 */
	public String getUrl(String key) {
		String urlString = get(key);
		if (urlString.indexOf("|") > 0) {
			return urlString.split("\\|")[0];// 取出url

		} else// url没有参数的情况
		{
			return urlString;

		}
	}

	public UrlBean getUrlBean(String key) {
		UrlBean urlBean = new UrlBean();
		String urlString = get(key);
		if (urlString.indexOf("|") > 0) {
			urlBean.setUrl(urlString.split("\\|")[0]);// 取出url
			urlBean.setParams(urlString.split("\\|")[1].split(","));// 取出参数
		} else// url没有参数的情况
		{
			urlBean.setUrl(urlString);
			urlBean.setParams(null);
		}
		return urlBean;
	}

	/**
	 * @DESCRIBE 存储url的bean
	 */
	public class UrlBean {
		private String url;
		private String[] params;

		/**
		 * @return the url
		 */
		public String getUrl() {
			return url;
		}

		/**
		 * @param url
		 *            the url to set
		 */
		public void setUrl(String url) {
			this.url = url;
		}

		/**
		 * @return the params
		 */
		public String[] getParams() {
			return params;
		}

		/**
		 * @param params
		 *            the params to set
		 */
		public void setParams(String[] params) {
			this.params = params;
		}
	}

}
