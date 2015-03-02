/**
 *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *
 * @COMPANY IFXME.COM
 * @AUTHOR dkslbw@gmail.com
 * @TIME 2014年8月6日 下午11:23:03
 *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  * 
 */
package com.newsagency.mobile.net;

import java.util.Iterator;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.newsagency.mobile.util.AppHelper;
import com.newsagency.mobile.util.ILog;
import com.newsagency.mobile.util.MD5Util;
import com.newsagency.mobile.util.OffLineDataUtil;
import com.newsagency.mobile.view.activity.BaseActivity;
import com.newsagency.mobile.view.activity.NewsAgencyApplication;
import com.newsagency.mobile.view.fragment.BaseFragment;

/**
 * @DESCRIBE ...
 */
public class Net {
	private final static String TAG = "Net";

	private NetResponseListener listener;
	private RequestQueue requestQueue;

	private Context context;

	public Net(BaseActivity context) {
		this.context = context;
		FakeX509TrustManager.allowAllSSL();
		requestQueue = Volley.newRequestQueue(context);
		this.listener = context;
	}

	public Net(BaseFragment context) {
		this.context = context.getActivity();
		FakeX509TrustManager.allowAllSSL();
		requestQueue = Volley.newRequestQueue(context.getActivity());
		this.listener = context;
	}

	/**
	 * 使用post 方法获取数据
	 */
	public void postJSON(String JSONDataUrl, final Map<String, String> param, final String tag) {

		JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Method.POST, JSONDataUrl, new JSONObject(param),
				new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						Log.v(TAG, "[服务器返回] [" + tag + "]=" + response);
						listener.onResponseOK(response, tag);
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError arg0) {
						printStackTrace(tag, arg0);
						listener.onResponseError(arg0, tag);
					}
				});
		jsonObjectRequest.setTag(tag);
		requestQueue.add(jsonObjectRequest);
	}

	/**
	 * 使用get 方法获取数据
	 */
	public void getJSON(String JSONDataUrl, final String tag) {
		JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Method.GET, JSONDataUrl, null, new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				Log.v(TAG, "[服务器返回] [" + tag + "]=" + response);
				listener.onResponseOK(response, tag);
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError arg0) {
				printStackTrace(tag, arg0);
				listener.onResponseError(arg0, tag);
			}
		});
		jsonObjectRequest.setTag(tag);
		requestQueue.add(jsonObjectRequest);
	}

	/**
	 * 使用get 方法获取字符串数据
	 * 
	 * @param url
	 * @param tag
	 */
	public void getString(String url, final String tag) {
		StringRequest stringRequest = new StringRequest(Method.GET, url, new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {
				Log.v(TAG, "[服务器返回] [" + tag + "]=" + response);
				listener.onResponseOK(response, tag);
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				printStackTrace(tag, error);
				listener.onResponseError(error, tag);
			}
		}) {

			@Override
			public Map<String, String> getHeaders() throws AuthFailureError {
				Map<String, String> map = super.getHeaders();
				ILog.e(map.toString());
				return map;
			}
		};
		stringRequest.setTag(tag);
		requestQueue.add(stringRequest);
	}

	public void postString(String url, final Map<String, String> param, final Map<String, String> mheaders, final String tag) {
		postString(url, param, mheaders, tag, false);
	}

	public void postString(final String url, final Map<String, String> param, final Map<String, String> mheaders, final String tag,
			final boolean isCache) {
		printRequest(tag, Method.POST, url, param);
		StringRequest stringRequest = new StringRequest(Method.POST, url, new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {
				Log.v(TAG, "[服务器返回] [" + tag + "]=" + response);
				listener.onResponseOK(response, tag);
				NewsAgencyApplication.isInOfflineState=false;
				if (isCache) {// 如果需要缓存
					JSONObject json;
					try {
						json = new JSONObject(response);
						if (json.getString("status").equals("000")) {
							OffLineDataUtil.shardOffLineDataUtil(context).put(json, getStringParam(url, param));
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				printStackTrace(tag, error);
				if (isCache) {
					if(NewsAgencyApplication.isInOfflineState==false)
					{
						NewsAgencyApplication.isInOfflineState=true;
						AppHelper.showViewToast(context, "网络目前不可用，您正处于离线浏览模式");
					}
					JSONObject jsonObject = OffLineDataUtil.shardOffLineDataUtil(context).get(getStringParam(url, param));
					if (jsonObject != null) {
						listener.onResponseOK(jsonObject.toString(), tag);
					} else {
						listener.onResponseError(error, tag);
					}
				} else {
					listener.onResponseError(error, tag);
				}
			}
		}) {
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				return param;
			}

			@Override
			protected Response<String> parseNetworkResponse(NetworkResponse response) {

				ILog.e("[服务器返回]" + response.headers.toString());
				if (response.headers.containsKey("Set-Cookie")) {
					// Set-Cookie=JSESSIONID=7660D948DC2300F555CC0C9C2680AAA9;
					// Path=/IYiMing/; HttpOnly
					// 11-18 22:34:56.547: E/ILog(28189):
					// {X-Android-Selected-Transport=http/1.1,
					// Transfer-Encoding=chunked, Date=Tue, 18 Nov 2014 14:33:48
					// GMT, X-Android-Received-Millis=1416321296548,
					// Set-Cookie=JSESSIONID=4B3CEEDB417428D39BB021724917DDDF;
					// Path=/IYiMing/; HttpOnly,
					// Content-Type=application/json;charset=UTF-8,
					// X-Android-Response-Source=NETWORK 200,
					// Server=Apache-Coyote/1.1,
					// X-Android-Sent-Millis=1416321296519}

					String cookies = response.headers.get("Set-Cookie");
					String temp = cookies.split("=")[1];
					temp = temp.split("; ")[0];
					// 如果当前没有sessionid 则请求服务器的id
					if (NewsAgencyApplication.SESSION_ID == null)
						NewsAgencyApplication.SESSION_ID = temp;
				}

				return super.parseNetworkResponse(response);
			}

			@Override
			public Map<String, String> getHeaders() throws AuthFailureError {
				ILog.e("[请求]" + mheaders.toString());
				return mheaders;

			}

		};
		stringRequest.setTag(tag);
		requestQueue.add(stringRequest);
	}

	/**
	 * 取消指定的网络队列
	 * 
	 * @param tag
	 */
	public void cancel(String tag) {
		requestQueue.cancelAll(tag);
	}

	public void cancelUpload(String tag) {
		requestQueue.cancelAll(tag);
	}

	/**
	 * 答打印错误堆栈
	 * 
	 * @param tag
	 * @param e
	 */
	private void printStackTrace(String tag, VolleyError e) {
		Log.e("[ifxme]" + tag, e.toString());
	}

	/**
	 * 打印请求连接和参数
	 * 
	 * @param method
	 * @param url
	 * @param param
	 */
	private void printRequest(String tag, int method, String url, Map<String, String> param) {
		if (method == Method.POST) {
			Log.v(tag, "[POST]:" + url + "\n" + param.toString());
		} else {
			Log.v(tag, "[GET]:" + url + "\n");
		}
	}

	/**
	 * 网络请求状态监听
	 * 
	 * @author Slbw
	 * 
	 */
	public interface NetResponseListener {
		public boolean onResponseOK(Object response, String tag);

		public void onResponseError(VolleyError arg0, String tag);
	}

	public void setNetResponseListener(NetResponseListener listener) {
		this.listener = listener;
	}

	/**
	 * 获取参数的拼接字符串
	 * 
	 * @param url
	 * @param params
	 * @return
	 */
	private String getStringParam(String url, Map<String, String> params) {

		String string = url + "_";
		params.remove("timestamp");
		params.remove("sign");
		params.remove("v");
		System.out.println(params.toString());
		Iterator<String> it = params.keySet().iterator();
		while (it.hasNext()) {
			String key = it.next();
			if (params.get(key) != null) {
				string += params.get(key) + "_" + params.get(key) + "_";
			}
		}
		return MD5Util.SharedMD5Util().Md5_16(string);
		// return "test";
	}

}
