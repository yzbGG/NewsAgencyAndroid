package com.newsagency.mobile.util;

import android.util.Log;

public class ILog {
	
	private static int level;
	
	public static void e(String msg)
	{
		Log.e("ILog", msg);
	}
	public static void d(String msg)
	{
		Log.d("ILog", msg);
	}
	
	public static void i(String msg)
	{
		Log.i("ILog", msg);
	}
	
	public static void v(String msg)
	{
		Log.e("ILog", msg);
	}
	
	public static void w(String msg)
	{
		Log.w("ILog", msg);
	}

}
