/**
 * @PROGECT IYIMING
 * @AUTHOR dkslbw@gmail.com
 * @Date 2013年11月7日  上午10:04:39
 * @Version V1.0
 */
package com.newsagency.mobile.net;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Handler;
import android.util.Log;

import com.newsagency.mobile.util.ILog;
import com.newsagency.mobile.util.SignUtil;
import com.newsagency.mobile.util.UrlUtil;
import com.newsagency.mobile.util.UrlUtil.UrlBean;
import com.newsagency.mobile.view.activity.NewsAgencyApplication;

;
/**
 * @author Slbws 文件上传的操作类
 */
public class FileUploadUtil {

	/** 文件上传服务器地址 */
	// private static final String SERVER_ADDRESS =
	// "http://192.168.1.101:9999/file/upload";

	/**
	     * 通过拼接的方式构造请求内容，实现参数传输以及文件传输
	     * 
	     * @param actionUrl 访问的服务器URL
	     * @param params 普通参数
	     * @param files 文件参数
	     * @return
	     * @throws IOException
	     */
	    public static void post( String filaPath,String key,Object[] mparams,Handler handler) throws IOException
	    {
	    	Map<String, File> files=new HashMap<String, File>();
	    	files.put("avatar", new File(filaPath));
	    	String actionUrl="https://aiyimin.com.cn/rest";
	    	;
	    	Map<String,String> params =SignUtil.getSignedParam(getParamMap(key,mparams), true);

	        String BOUNDARY = java.util.UUID.randomUUID().toString();
	        String PREFIX = "--", LINEND = "\r\n";
	        String MULTIPART_FROM_DATA = "multipart/form-data";
	        String CHARSET = "UTF-8";

	        URL uri = new URL(actionUrl);
	        HttpURLConnection conn = (HttpURLConnection) uri.openConnection();
	        conn.setReadTimeout(5 * 1000); // 缓存的最长时间
	        conn.setDoInput(true);// 允许输入
	        conn.setDoOutput(true);// 允许输出
	        conn.setUseCaches(false); // 不允许使用缓存
	        conn.setRequestMethod("POST");
	        conn.setRequestProperty("connection", "keep-alive");
	        conn.setRequestProperty("Charsert", "UTF-8");
	        conn.setRequestProperty("Cookie", "JSESSIONID="+NewsAgencyApplication.SESSION_ID);
	        conn.setRequestProperty("Content-Type", MULTIPART_FROM_DATA + ";boundary=" + BOUNDARY);

	        // 首先组拼文本类型的参数
	        StringBuilder sb = new StringBuilder();
	        for (Map.Entry<String, String> entry : params.entrySet())
	        {
	            sb.append(PREFIX);
	            sb.append(BOUNDARY);
	            sb.append(LINEND);
	            sb.append("Content-Disposition: form-data; name=\"" + entry.getKey() + "\"" + LINEND);
	            sb.append("Content-Type: text/plain; charset=" + CHARSET + LINEND);
	            sb.append("Content-Transfer-Encoding: 8bit" + LINEND);
	            sb.append(LINEND);
	            sb.append(entry.getValue());
	            sb.append(LINEND);
	        }

	        DataOutputStream outStream = new DataOutputStream(conn.getOutputStream());
	        outStream.write(sb.toString().getBytes());
	        InputStream in = null;
	        // 发送文件数据
	        if (files != null)
	        {
	            for (Map.Entry<String, File> file : files.entrySet())
	            {
	                StringBuilder sb1 = new StringBuilder();
	                sb1.append(PREFIX);
	                sb1.append(BOUNDARY);
	                sb1.append(LINEND);
	                // name是post中传参的键 filename是文件的名称
	                sb1.append("Content-Disposition: form-data; name=\"file\"; filename=\"" + file.getKey() + "\"" + LINEND);
	                sb1.append("Content-Type: application/octet-stream; charset=" + CHARSET + LINEND);
	                sb1.append(LINEND);
	                outStream.write(sb1.toString().getBytes());

	                InputStream is = new FileInputStream(file.getValue());
	                byte[] buffer = new byte[1024];
	                int len = 0;
	                while ((len = is.read(buffer)) != -1)
	                {
	                    outStream.write(buffer, 0, len);
	                }

	                is.close();
	                outStream.write(LINEND.getBytes());
	            }

	            // 请求结束标志
	            byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINEND).getBytes();
	            outStream.write(end_data);
	            outStream.flush();
	            // 得到响应码
	            int res = conn.getResponseCode();
	            if (res == 200)
	            {
	                in = conn.getInputStream();
	                int ch;
	                StringBuilder sb2 = new StringBuilder();
	                while ((ch = in.read()) != -1)
	                {
	                    sb2.append((char) ch);
	                }
	                ILog.e(sb2.toString());
	                try {
						JSONObject json=new JSONObject(sb2.toString());
						if(json.getString("status").equals("000"))
						{
							handler.sendEmptyMessage(1);
						}
						else
						{
							handler.sendEmptyMessage(2);
						}
					} catch (JSONException e) {
						e.printStackTrace();
						handler.sendEmptyMessage(2);
					}
	            }
	            outStream.close();
	            conn.disconnect();
	        }
	        // return in.toString();
	    }
	
	
	
	/**
	 * 获取请求参数数组
	 * 
	 * @param key
	 * @param args
	 * @return
	 */
	public static HashMap<String, String> getParamMap(String key, Object[] args) {
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
	
	
}
