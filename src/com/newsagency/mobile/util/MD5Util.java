/**
 *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *
 * @COMPANY IFXME.COM
 * @AUTHOR dkslbw@gmail.com
 * @TIME 2014年8月8日 上午1:25:00
 *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  * 
 */
package com.newsagency.mobile.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @DESCRIBE md5加密的工具类
 */
public class MD5Util {
	
	private static MD5Util md5Util;
	
	protected static char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9','a', 'b', 'c', 'd', 'e', 'f' };
	protected static MessageDigest messagedigest = null;
	
	private MD5Util(){
		
	}
	
	public static final MD5Util SharedMD5Util() {
		if (md5Util == null) {
			try {
				md5Util = new MD5Util();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return md5Util;
	}
	

	public String Md5(String plainText) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			try {
				md.update(plainText.getBytes("utf-8"));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			byte b[] = md.digest();

			int i;

			StringBuffer buf = new StringBuffer("");
			for (int offset = 0; offset < b.length; offset++) {
				i = b[offset];
				if (i < 0)
					i += 256;
				if (i < 16)
					buf.append("0");
				buf.append(Integer.toHexString(i));
			}

			//System.out.println("result: " + buf.toString());// 32位的加密

			//System.out.println("result: " + buf.toString().substring(8, 24));// 16位的加密
			
			return buf.toString();

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			
			return null;
		}
	}
	
	
	public String Md5_16(String plainText) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			try {
				md.update(plainText.getBytes("utf-8"));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			byte b[] = md.digest();

			int i;

			StringBuffer buf = new StringBuffer("");
			for (int offset = 0; offset < b.length; offset++) {
				i = b[offset];
				if (i < 0)
					i += 256;
				if (i < 16)
					buf.append("0");
				buf.append(Integer.toHexString(i));
			}

			//System.out.println("result: " + buf.toString());// 32位的加密

			//System.out.println("result: " + buf.toString().substring(8, 24));// 16位的加密
			
			return buf.toString().substring(8, 24);//大写

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			
			return null;
		}
	}
	
	
	//实例
	/*File big = new File("E:/adt-bundle-windows-x86_64-20130729.zip");
	try {
		String s=MD5Util.SharedMD5Util().fileMD5(big);
		System.out.println(s);
		return;
	} catch (Exception e) {
		e.printStackTrace();
	}*/
	/**
	 * 获取文件的md5值
	 * @param file
	 * @return
	 * @throws Exception</br> 
	 * 实例</br>
	 * File big = new File("E:/adt-bundle-windows-x86_64-20130729.zip");</br>
	try {</br>
		String s=MD5Util.SharedMD5Util().fileMD5(big);</br>
		System.out.println(s);</br>
		return;</br>
	} catch (Exception e) {</br>
		e.printStackTrace();</br>
	}
	 */
	public String fileMD5(File  file) throws Exception
	{
		MessageDigest messagedigest = MessageDigest.getInstance("MD5");
		   FileInputStream in = new FileInputStream(file);
		   FileChannel ch = in.getChannel();
		   MappedByteBuffer byteBuffer = ch.map(FileChannel.MapMode.READ_ONLY, 0,in.available());
		   messagedigest.update(byteBuffer);
		   in.close();
		   return bufferToHex(messagedigest.digest());
	}
	
	
	 public String md5sum(File file) {
	        InputStream fis;
	        byte[] buffer = new byte[1024];
	        int numRead = 0;
	        MessageDigest md5;
	        try{
	            fis = new FileInputStream(file);
	            md5 = MessageDigest.getInstance("MD5");
	            while((numRead=fis.read(buffer)) > 0) {
	                md5.update(buffer,0,numRead);
	            }
	            fis.close();
	            return bufferToHex(md5.digest());   
	        } catch (Exception e) {
	            System.out.println("error");
	            return null;
	        }
	    }
	
	
	
	private static String bufferToHex(byte bytes[]) {
		   return bufferToHex(bytes, 0, bytes.length);
		}

	
	private static String bufferToHex(byte bytes[], int m, int n) {
		   StringBuffer stringbuffer = new StringBuffer(2 * n);
		   int k = m + n;
		   for (int l = m; l < k; l++) {
		    appendHexPair(bytes[l], stringbuffer);
		   }
		   return stringbuffer.toString();
		}


		private static void appendHexPair(byte bt, StringBuffer stringbuffer) {
		   char c0 = hexDigits[(bt & 0xf0) >> 4];
		   char c1 = hexDigits[bt & 0xf];
		   stringbuffer.append(c0);
		   stringbuffer.append(c1);
		}
}
