/**@文件:OffLineDataUtil.java
 * @作者:dkslbw@gmail.com
 * @日期:2014年9月28日 上午11:51:07*/
package com.newsagency.mobile.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.provider.ContactsContract.DeletedContacts;
import android.util.Log;

/**
 * @公司: 南京红松信息技术有限公司
 * @CLASS:OffLineDataUtil
 * @描述:离线数据工具类
 * @作者:dkslbw@gmail.com
 * @版本:v1.0
 * @日期:2014年9月28日 上午11:51:07
 */

public class OffLineDataUtil {
	
	private static OffLineDataUtil offLineDataUtil;
	
	private Context context;
	
	private final String prefix="offline_";
	
	private OffLineDataUtil(Context context)
	{
		this.context=context;
	}
	
	public static OffLineDataUtil shardOffLineDataUtil(Context context)
	{
		if(offLineDataUtil==null)
		{
			offLineDataUtil=new OffLineDataUtil(context);
		}
		return offLineDataUtil;
	}

	/**
	 * @方法描述:填充缓存
	 * @作者:dkslbw@gmail.com
	 *
	 * @param json
	 * @param key
	 */
	public void put(JSONObject json, String key) {
		ILog.d("添加缓存"+key+"\n"+json.toString());
		String string = json.toString();
		try {
			byte[] bytes=string.getBytes("UTF8");
			writeInternalStoragePrivate(prefix+key,bytes,context);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
	}

	/**
	 * @方法描述:获取缓存
	 * @作者:dkslbw@gmail.com
	 *
	 * @param key
	 * @return
	 */
	public JSONObject get(String key) {
		ILog.d("读取缓存"+key);
		byte[] bytes=readInternalStoragePrivate(prefix+key,context);

			String string;
			JSONObject json = null;
			try {
				string = new String( bytes ,"UTF-8");
				json=new JSONObject(string);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			return json;

	}
	
	/**
	 * @方法描述:删除缓存
	 * @作者:dkslbw@gmail.com
	 *
	 * @param key
	 */
	public void delete(String key)
	{
		deleteInternalStoragePrivate(key);
	}
	
	/**
	 * @方法描述:清空所有缓存文件	
	 * @作者:dkslbw@gmail.com
	 *
	 */
	public void deleteAllCache()
	{
		 File file=context.getFilesDir();
		 File[] fileList=file.listFiles(new OffLineFilter());
		 for (File mfile : fileList) {
			 Log.e("DELETE", "删除"+mfile.getName());
			 deleteInternalStoragePrivate(mfile.getName());
		}
	}

	/**
	 * Writes content to internal storage making the content private to the
	 * application. The method can be easily changed to take the MODE as
	 * argument and let the caller dictate the visibility: MODE_PRIVATE,
	 * MODE_WORLD_WRITEABLE, MODE_WORLD_READABLE, etc.
	 * 
	 * @param filename
	 *            - the name of the file to create
	 * @param content
	 *            - the content to write
	 */
	private void writeInternalStoragePrivate(String filename, byte[] content, Context context) {
		try {
			// MODE_PRIVATE creates/replaces a file and makes
			// it private to your application. Other modes:
			// MODE_WORLD_WRITEABLE
			// MODE_WORLD_READABLE
			// MODE_APPEND
			FileOutputStream fos = context.openFileOutput(filename, Context.MODE_PRIVATE);
			fos.write(content);
			fos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Reads a file from internal storage
	 * 
	 * @param filename
	 *            the file to read from
	 * @return the file content
	 */
	private byte[] readInternalStoragePrivate(String key, Context context) {
		int len = 1024;
		byte[] buffer = new byte[len];
		try {
			FileInputStream fis = context.openFileInput(key);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			int nrb = fis.read(buffer, 0, len); // read up to len bytes
			while (nrb != -1) {
				baos.write(buffer, 0, nrb);
				nrb = fis.read(buffer, 0, len);
			}
			buffer = baos.toByteArray();
			fis.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return buffer;
	}

	/**
	 * Delete internal private file
	 * 
	 * @param filename
	 *            - the filename to delete
	 */
	private void deleteInternalStoragePrivate(String filename) {
		File file = context.getFileStreamPath(filename);
		if (file != null) {
			file.delete();
		}
	}

}
