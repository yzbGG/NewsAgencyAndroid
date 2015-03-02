/**
 * 
 */
package com.newsagency.mobile.view.activity;

import android.app.Application;
import android.os.Environment;

/**
 * 
* @ClassName: IYiMingApplication 
* @Description: TODO(这里用一句话描述这个类的作用) 
* @author dkslbw@gmail.com
* @date 2014年12月11日 下午1:55:57 
*
 */
public class NewsAgencyApplication extends Application {

	/**
	 * 存储session中的id
	 */
	public static String SESSION_ID = "";
	public boolean isLoged = false;
	/**
	 * 是否有更新
	 */
	public boolean hasUpdate=false;
	/**
	 * 是否处于离线模式
	 */
	public static boolean isInOfflineState=false;

	/** 手机SD卡路径 */
	private final String SDCardPath = Environment.getExternalStorageDirectory().toString();
	/** 本软件缓存照片的目录 ,和系统存储目录相同 */
	private final String SysDefaultImageDir = SDCardPath + "/DCIM/camera/";
	/** 本软件使用的存储目录 */
	private final String BaseSDCardPath = SDCardPath + "/IYIMING/";
	/** 本软件上传图片的缓存目录 */
	private final String UploadImagePath = BaseSDCardPath + "uploadImage/";



}
