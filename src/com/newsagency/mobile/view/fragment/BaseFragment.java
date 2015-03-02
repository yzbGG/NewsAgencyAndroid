/**
 *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *
 * @COMPANY IFXME.COM
 * @AUTHOR dkslbw@gmail.com
 * @TIME 2014年8月6日 下午11:24:10
 *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  * 
 */
package com.newsagency.mobile.view.fragment;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

import com.android.volley.VolleyError;
import com.newsagency.mobile.net.Net;
import com.newsagency.mobile.net.Net.NetResponseListener;
import com.newsagency.mobile.util.AppHelper;
import com.newsagency.mobile.util.AppInfoUtil;
import com.newsagency.mobile.util.ImageManager;
import com.newsagency.mobile.util.SignUtil;
import com.newsagency.mobile.util.UrlUtil;
import com.newsagency.mobile.util.UrlUtil.UrlBean;
import com.newsagency.mobile.view.activity.BaseActivity;
import com.newsagency.mobile.view.activity.NewsAgencyApplication;
import com.newsagency.mobile.view.widget.PopBox;

/**
 * @DESCRIBE 所有的网络请求activity基类
 */
public abstract class BaseFragment extends Fragment implements NetResponseListener {

	private Net net;
	protected NewsAgencyApplication application;
	
	private final String RET="status";
	private final String SUCCESS_TAG="000";
	private final String SESSION_TIMEOUT_TAG="113";
	private final String MSG="memo";
	protected EditText searchText;
	
	
	/**
	 * 顶部菜单右边按钮点击事件
	 * 
	 * @param v
	 *            被点击的view
	 */
	public abstract void rightTitleClick(View v, BaseActivity context);
	
	public abstract void leftTitleClick(View v, BaseActivity context);

	/**
	 * 顶部菜单右边按钮将要更换的图片资源id（必须为图片资源id）
	 * 
	 * @return
	 */
	public abstract int getRightTitle();
	
	
	
	public abstract int getLeftTitle();
	
	
	/**
	 * 左边按钮是否需要隐藏
	 * @return
	 */
	public abstract boolean isLeftTitleHide();
	
	/**
	 * 右边按钮是否需要隐藏
	 * @return
	 */
	public abstract boolean isRightTitleHide();
	
	/**
	 * 标题是否需要隐藏
	 * @return
	 */
	public abstract boolean isNavBarHide();
	
	/**
	 * 此fragment的名字，会显示在顶部导航栏中间
	 * 
	 * @return
	 */
	public abstract int getFragmentTitleResourceId();

	/**
	 * 如果返回true 表示此fragment不显示的时候会被移除，false表示仅仅隐藏起来
	 * 
	 * @return
	 */
	public abstract boolean isNeedRemove();
	
	
	public void getEdit(EditText text)
	{
		this.searchText=text; 
	}

	/**
	 * 点击的按钮id绑定到fragment
	 */
	private int bindIngd;

	public int getBindIngd() {
		return bindIngd;
	}

	public void setBindIngd(int bindIngd) {
		this.bindIngd = bindIngd;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onDetach() {
		super.onDetach();
	}

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		application = (NewsAgencyApplication) activity.getApplication();
		net = new Net(this);
	}
	
	
	public void post(String key, Object[] params,boolean isLoged,String Tag) {
		post(key,params,isLoged,Tag,false);
	}
	
	/**
	 * 
	 * @param key
	 * @param params
	 * @param isLoged
	 * @param Tag 唯一标识
	 */
	public void post(String key, Object[] params,boolean isLoged,String Tag,boolean isCache) {

		Map<String, String> map = getParamMap(key, params);
		Map<String, String> headers=new HashMap<String, String>();
		if(NewsAgencyApplication.SESSION_ID!=null&&NewsAgencyApplication.SESSION_ID.length()!=0)
		{
			headers.put("Cookie", "JSESSIONID="+NewsAgencyApplication.SESSION_ID);
		}
			// 安全性加密过的参数
		net.postString(AppInfoUtil.sharedAppInfoUtil().getServerUrl() + UrlUtil.sharedUrlUtil().getUrl(key), SignUtil.getSignedParam(map, isLoged),headers, Tag,isCache);
	}
	
	
	
	

	protected void get(String url, String tag) {
		net.getString(AppInfoUtil.sharedAppInfoUtil().getServerUrl() + url, tag);
	}


	/**
	 * 将不定参数转化为object 数组
	 * 
	 * @param args
	 * @return
	 */
	protected Object[] addParam(Object... args) {
		return args;
	}

	/**
	 * 获取请求参数数组
	 * 
	 * @param key
	 * @param args
	 * @return
	 */
	public HashMap<String, String> getParamMap(String key, Object[] args) {
		HashMap<String, String> map = new HashMap<String, String>();
		UrlBean bean = UrlUtil.sharedUrlUtil().getUrlBean(key);
		String[] params = bean.getParams();
		if (args.length != params.length)// 参数个数出错
		{
			Log.e("BaseActivity", "请求参数不完整");
			return null;
		} else if (args.length == 0) {
			return map;
		} else// 参数个数正确
		{
			for (int i = 0; i < args.length; i++) {// 插入参数和参数值
				if(args[i]!=null)//空值不参拼接
				{
					map.put(params[i], (String) args[i]);
				}
			}
			return map;
		}

	}

	@Override
	public boolean onResponseOK(Object response, String tag) {
		JSONObject json;
		try {
			json = new JSONObject((String) response);
			if(!json.getString(RET).equals(SUCCESS_TAG))// 返回成功信息
			{
				if(json.getString(RET).equals(SESSION_TIMEOUT_TAG))
				{
					final PopBox popBox=new PopBox(getActivity());
					popBox.showTitle("提示");
					popBox.showContent("登录已经过期，请重新登录");
					popBox.showBtnOk("好的");
					popBox.setCanDismiss(false);
					popBox.setOKClickListener(new OnClickListener() {
						@Override
						public void onClick(View arg0) {
							//退出登录
							popBox.cancel();
							logOut();
//							Intent intent=new Intent();
//							intent.setClass(getActivity(),LoginActivty.class);
//							startActivity(intent);
						}
					});
					popBox.showDialog();
				}
				else{
					showToast(json.getString(MSG));
				}
				return false;
			} else {
				return true;
			}
		} catch (JSONException e) {
			e.printStackTrace();
			showToast("返回数据格式错误");
			return false;
		}
	}

	@Override
	public void onResponseError(VolleyError arg0, String tag) {

		JSONObject json;
		byte[] bytes=arg0.networkResponse.data;
		String strRead = new String(bytes);
		if(arg0.networkResponse.statusCode==400)
		{
			try {
				json = new JSONObject(strRead);
				if(json.getString(RET).equals(SESSION_TIMEOUT_TAG))
				{
					final PopBox popBox=new PopBox(getActivity());
					popBox.showTitle("提示");
					popBox.showContent("登录已经过期，请重新登录");
					popBox.setCanDismiss(false);
					popBox.showBtnOk("好的");
					popBox.setOKClickListener(new OnClickListener() {
						@Override
						public void onClick(View arg0) {
							//退出登录
							popBox.cancel();
							logOut();
//							Intent intent=new Intent();
//							intent.setClass(getActivity(),LoginActivty.class);
//							startActivity(intent);
						}
					});
					popBox.showDialog();
				}
				else{
					showToast(json.getString(MSG));
				}
				
			} catch (JSONException e) {
				e.printStackTrace();
				showToast(getExceptionMessage(arg0.toString()));
			}
		}
		else
		{
			showToast(getExceptionMessage(arg0.toString()));
		}
		
		

	}

	/**
	 * 打开toast 提示
	 * 
	 * @param content
	 *            提示内容
	 */
	public void showToast(String content) {
		AppHelper.showToast(getActivity(), content);
		Log.d("BaseActivity", content);
	}

	/**
	 * 获取异常的消息
	 */
	public String getExceptionMessage(String msg) {
		if (msg.startsWith("java.net.UnknownHostException"))// 未知的地址
		{
			return "连接失败，请稍后再试";
		} else if (msg.startsWith("com.android.volley.TimeoutError")) {
			return "连接超时";
		} else if (msg.startsWith("com.android.volley.ServerError")) {
			return "服务器内部错误";
		}
		return "连接失败";
	}

	/**
	 * 获取返回值中的data   
	 * 
	 * @param response
	 * @return
	 */
	protected String getDataString(Object response) {
		JSONObject json;
		try {
			json = new JSONObject((String) response);
			if (json.getInt(RET) != 0)// 返回成功信息
			{
				return null;
			} else {
				return json.get("data").toString();
			}
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
	private void logOut()
	{
//		ImageManager.getInstance(getActivity()).mMemoryCache.remove(AppInfoUtil.sharedAppInfoUtil().getImageServerUrl() + application.user.getImageUrl());
//		ImageManager.getInstance(getActivity()).mDiskCache.remove(AppInfoUtil.sharedAppInfoUtil().getImageServerUrl() + application.user.getImageUrl());
//		application.isLoged = false;
//		application.user = null;
//		IYiMingApplication.SESSION_ID =null;
//		// 清除用户数据
//		application.deleteUser();
	}
	
}
