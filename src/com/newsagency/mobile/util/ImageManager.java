package com.newsagency.mobile.util;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.support.v4.util.LruCache;
import android.widget.ImageView;

import com.newsagency.mobile.R;
import com.newsagency.mobile.view.activity.NewsAgencyApplication;

public class ImageManager {
	private static ImageManager imageManager;
	public LruCache<String, Bitmap> mMemoryCache;
	private static final int DISK_CACHE_SIZE = 1024 * 1024 * 40; // 10MB
	private static final String DISK_CACHE_SUBDIR = "thumbnails";
	public DiskLruCache mDiskCache;

	private static final int maxImageSize = 1024 * 2;// 假设为imageview 显示的最大高度

	/** 图片加载队列，后进先出 */
	private Stack<ImageRef> mImageQueue = new Stack<ImageRef>();

	/** 图片请求队列，先进先出，用于存放已发送的请求。 */
	private Queue<ImageRef> mRequestQueue = new LinkedList<ImageRef>();

	/** 图片加载线程消息处理器 */
	private Handler mImageLoaderHandler;

	/** 图片加载线程是否就绪 */
	private boolean mImageLoaderIdle = true;

	/** 请求图片 */
	private static final int MSG_REQUEST = 1;
	/** 图片加载完成 */
	private static final int MSG_REPLY = 2;
	/** 中止图片加载线程 */
	private static final int MSG_STOP = 3;
	/** 如果图片是从网络加载，则应用渐显动画，如果从缓存读出则不应用动画 */
	private boolean isFromNet = true;

	private File cachefile = null;

	// private Context context;

	private static HttpClient httpClient;

	public static ArrayList<String> getIntentArrayList(ArrayList<String> dataList) {
		ArrayList<String> tDataList = new ArrayList<String>();
		for (String s : dataList) {
			if (!s.contains("default")) {
				tDataList.add(s);
			}
		}
		return tDataList;
	}

	/**
	 * 获取单例，只能在UI线程中使用。
	 * 
	 * @param context
	 * @return
	 */
	public static ImageManager getInstance(Context context) {

		// 如果不在ui线程中，则抛出异常
		if (Looper.myLooper() != Looper.getMainLooper()) {
			throw new RuntimeException("Cannot instantiate outside UI thread.");
		}

		if (imageManager == null) {
			httpClient = createHttpClient();
			imageManager = new ImageManager(context);
		}

		return imageManager;
	}

	/**
	 * 私有构造函数，保证单例模式
	 * 
	 * @param context
	 */
	private ImageManager(Context context) {
		this.cachefile = context.getCacheDir();
		// this.context=context;
		int memClass = ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE)).getMemoryClass();
		memClass = memClass > 32 ? 32 : memClass;
		// 使用可用内存的1/8作为图片缓存
		final int cacheSize = 1024 * 1024 * memClass / 3;

		mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {

			protected int sizeOf(String key, Bitmap bitmap) {
				return bitmap.getRowBytes() * bitmap.getHeight();
			}

		};

		File cacheDir = DiskLruCache.getDiskCacheDir(context, DISK_CACHE_SUBDIR);
		mDiskCache = DiskLruCache.openCache(context, cacheDir, DISK_CACHE_SIZE);

	}

	/**
	 * 存放图片信息
	 */
	class ImageRef {

		/** 图片对应ImageView控件 */
		ImageView imageView;
		/** 图片URL地址 */
		String url;
		/** 图片缓存路径 */
		String filePath;
		/** 默认图资源ID */
		int resId;
		int width = 0;
		int height = 0;
		boolean isLoged = false;

		/**
		 * 构造函数
		 * 
		 * @param imageView
		 * @param url
		 * @param resId
		 * @param filePath
		 */
		ImageRef(ImageView imageView, String url, String filePath, int resId, boolean isLoged) {
			this.imageView = imageView;
			this.url = url;
			this.filePath = filePath;
			this.resId = resId;
			this.isLoged = isLoged;
		}

		ImageRef(ImageView imageView, String url, String filePath, int resId, int width, int height, boolean isLoged) {
			this.imageView = imageView;
			this.url = url;
			this.filePath = filePath;
			this.resId = resId;
			this.width = width;
			this.height = height;
			this.isLoged = isLoged;
		}

	}

	public void getImage(ImageView imageView, String url) {
		getImage(imageView, url, false);
	}

	/**
	 * 显示图片
	 * 
	 * @param imageView
	 * @param url
	 * @param resId
	 */
	public void getImage(ImageView imageView, String url, boolean isLogin) {
//		if (imageView == null) {
//			return;
//		}
//		if (imageView.getTag() != null && imageView.getTag().toString().equals(url)) {
//			return;
//		}
//		imageView.setImageResource(R.color.cornsilk);
//
//		if (url == null || url.equals("")) {
//			return;
//		}
//		// 添加url tag
//		imageView.setTag(url);
//
//		// 读取map缓存
//		Bitmap bitmap = mMemoryCache.get(url);
//
//		if (bitmap != null) {
//			setImageBitmap(imageView, bitmap, false);
//			return;
//		}
//
//		// 生成文件名
//		String filePath = urlToFilePath(url);
//
//		if (filePath == null) {
//			return;
//		}
//
//		queueImage(new ImageRef(imageView, url, filePath, R.color.cornsilk, isLogin));
		
		getImage(imageView, url,R.color.app_bg,isLogin);
	}
	
	
	/**
	 * 显示图片
	 * 
	 * @param imageView
	 * @param url
	 * @param resId
	 */
	public void getImage(ImageView imageView, String url,int defaultImage, boolean isLogin) {
		if (imageView == null) {
			return;
		}
		if (imageView.getTag() != null && imageView.getTag().toString().equals(url)) {
			return;
		}
		imageView.setImageResource(defaultImage);

		if (url == null || url.equals("")) {
			return;
		}
		// 添加url tag
		imageView.setTag(url);

		// 读取map缓存
		Bitmap bitmap = mMemoryCache.get(url);

		if (bitmap != null) {
			setImageBitmap(imageView, bitmap, false);
			return;
		}

		// 生成文件名
		String filePath = urlToFilePath(url);

		if (filePath == null) {
			return;
		}

		queueImage(new ImageRef(imageView, url, filePath, defaultImage, isLogin));
	}
	
	

	/**
	 * 显示图片固定大小图片的缩略图，一般用于显示列表的图片，可以大大减小内存使用
	 * 
	 * @param imageView
	 *            加载图片的控件
	 * @param url
	 *            加载地址
	 * @param resId
	 *            默认图片
	 * @param width
	 *            指定宽度
	 * @param height
	 *            指定高度
	 */
	public void getImage(ImageView imageView, String url, int width, int height, boolean isLogin) {
		if (imageView == null) {
			return;
		}
		imageView.setImageResource(R.color.cornsilk);
		if (url == null || url.equals("")) {
			return;
		}
		// 添加url tag
		imageView.setTag(url);
		// 读取map缓存
		Bitmap bitmap = mMemoryCache.get(url + width + height);
		if (bitmap != null) {
			setImageBitmap(imageView, bitmap, false);
			return;
		}
		// 生成文件名
		String filePath = urlToFilePath(url);

		if (filePath == null) {
			return;
		}
		queueImage(new ImageRef(imageView, url, filePath, R.color.cornsilk, width, height, isLogin));
	}

	/**
	 * 入队，后进先出
	 * 
	 * @param imageRef
	 */
	public void queueImage(ImageRef imageRef) {

		// 删除已有ImageView
		Iterator<ImageRef> iterator = mImageQueue.iterator();
		while (iterator.hasNext()) {
			if (iterator.next().imageView == imageRef.imageView) {
				iterator.remove();
			}
		}

		// 添加请求
		mImageQueue.push(imageRef);
		sendRequest();
	}

	/**
	 * 发送请求
	 */
	private void sendRequest() {

		// 开启图片加载线程
		if (mImageLoaderHandler == null) {
			HandlerThread imageLoader = new HandlerThread("image_loader");
			imageLoader.start();
			mImageLoaderHandler = new ImageLoaderHandler(imageLoader.getLooper());
		}

		// 发送请求
		if (mImageLoaderIdle && mImageQueue.size() > 0) {
			ImageRef imageRef = mImageQueue.pop();
			Message message = mImageLoaderHandler.obtainMessage(MSG_REQUEST, imageRef);
			mImageLoaderHandler.sendMessage(message);
			mImageLoaderIdle = false;
			mRequestQueue.add(imageRef);
		}
	}

	/**
	 * 图片加载线程
	 */
	class ImageLoaderHandler extends Handler {

		public ImageLoaderHandler(Looper looper) {
			super(looper);
		}

		public void handleMessage(Message msg) {
			if (msg == null)
				return;

			switch (msg.what) {

			case MSG_REQUEST: // 收到请求
				Bitmap bitmap = null;
				Bitmap tBitmap = null;
				if (msg.obj != null && msg.obj instanceof ImageRef) {

					ImageRef imageRef = (ImageRef) msg.obj;
					String url = imageRef.url;
					if (url == null)
						return;
					// 如果本地url即读取sd相册图片，则直接读取，不用经过DiskCache
					if (!url.toLowerCase().contains("http:")) {
						tBitmap = null;
						BitmapFactory.Options opt = new BitmapFactory.Options();
						opt.inSampleSize = 1;
						opt.inJustDecodeBounds = true;
						BitmapFactory.decodeFile(url, opt);
						int bitmapSize = opt.outHeight * opt.outWidth * 4;
						opt.inSampleSize = bitmapSize / (1000 * 2000);
						opt.inJustDecodeBounds = false;
						tBitmap = BitmapFactory.decodeFile(url, opt);
						/* 图片旋转 */
						// tBitmap = ImageUtil.rotateBitmap(tBitmap,
						// ImageUtil.readPictureDegree(url));
						if (imageRef.width != 0 && imageRef.height != 0) {
							bitmap = ThumbnailUtils.extractThumbnail(tBitmap,// 图片压缩
									imageRef.width, imageRef.height, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
							isFromNet = true;
						} else {
							bitmap = tBitmap;
							tBitmap = null;
						}

					} else

						bitmap = mDiskCache.get(url);

					if (bitmap != null && !url.equalsIgnoreCase(AppInfoUtil.sharedAppInfoUtil().getImageServerUrl() + "/file/getAvatar")) {
						// ToolUtil.log("从disk缓存读取");
						// 写入map缓存
						if (imageRef.width != 0 && imageRef.height != 0) {
							if (mMemoryCache.get(url + imageRef.width + imageRef.height) == null)
								mMemoryCache.put(url + imageRef.width + imageRef.height, bitmap);
						} else {
							if (mMemoryCache.get(url) == null)
								mMemoryCache.put(url, bitmap);
						}

					} else {
						try {
							byte[] data = loadByteArrayFromNetwork(url, imageRef.isLoged);

							if (data != null) {

								BitmapFactory.Options opt = new BitmapFactory.Options();
								opt.inSampleSize = 1;

								opt.inJustDecodeBounds = true;
								BitmapFactory.decodeByteArray(data, 0, data.length, opt);
								int bitmapSize = opt.outHeight * opt.outWidth * 4;// pixels*3
																					// if
																					// it's
																					// RGB
																					// and
																					// pixels*4
																					// if
																					// it's
																					// ARGB
								if (bitmapSize > 1000 * 1200)
									opt.inSampleSize = 2;
								int maxSize = opt.outHeight > opt.outWidth ? opt.outHeight : opt.outWidth;
								if (maxSize > maxImageSize)// 判断最大的边是否超过4096
								{
									int sample = (int) (maxSize / (maxImageSize * 1.0f) + 0.5f);
									opt.inSampleSize = sample;
								}
								opt.inJustDecodeBounds = false;
								tBitmap = BitmapFactory.decodeByteArray(data, 0, data.length, opt);
								if (imageRef.width != 0 && imageRef.height != 0) {
									bitmap = ThumbnailUtils.extractThumbnail(tBitmap, imageRef.width, imageRef.height,
											ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
								} else {
									bitmap = tBitmap;
									tBitmap = null;
								}

								// if(bitmap!=null&&(url.endsWith("GIF")||url.endsWith("gif")))
								// {
								// BitmapDrawable drawable=(BitmapDrawable)
								// context.getResources().getDrawable(R.drawable.ic_delete);
								// bitmap=AppHelper.createWatermarkBitmap(bitmap,drawable.getBitmap());
								// }

								if (bitmap != null && url != null) {
									// 写入SD卡
									if (imageRef.width != 0 && imageRef.height != 0) {
										mDiskCache.put(url + imageRef.width + imageRef.height, bitmap);
										mMemoryCache.put(url + imageRef.width + imageRef.height, bitmap);
									} else {
										mDiskCache.put(url, bitmap);
										mMemoryCache.put(url, bitmap);
									}
									isFromNet = true;
								}
							}
						} catch (OutOfMemoryError e) {
						}

					}

				}

				if (mImageManagerHandler != null) {
					Message message = mImageManagerHandler.obtainMessage(MSG_REPLY, bitmap);
					mImageManagerHandler.sendMessage(message);
				}
				break;

			case MSG_STOP: // 收到终止指令
				Looper.myLooper().quit();
				break;

			}
		}
	}

	/** UI线程消息处理器 */
	private Handler mImageManagerHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			if (msg != null) {
				switch (msg.what) {

				case MSG_REPLY: // 收到应答

					do {
						ImageRef imageRef = mRequestQueue.remove();

						if (imageRef == null)
							break;

						if (imageRef.imageView == null || imageRef.imageView.getTag() == null || imageRef.url == null)
							break;

						if (!(msg.obj instanceof Bitmap) || msg.obj == null) {
							break;
						}
						Bitmap bitmap = (Bitmap) msg.obj;

						// 非同一ImageView
						if (!(imageRef.url).equals((String) imageRef.imageView.getTag())) {
							break;
						}

						setImageBitmap(imageRef.imageView, bitmap, isFromNet);
						isFromNet = false;

					} while (false);

					break;
				}
			}
			// 设置闲置标志
			mImageLoaderIdle = true;

			// 若服务未关闭，则发送下一个请求。
			if (mImageLoaderHandler != null) {
				sendRequest();
			}
		}
	};

	/**
	 * 添加图片显示渐现动画
	 * 
	 */
	private void setImageBitmap(ImageView imageView, Bitmap bitmap, boolean isTran) {

		imageView.setImageBitmap(bitmap);

	}

	/**
	 * 从网络获取图片字节数组
	 * 
	 * @param url
	 * @return
	 */
	private byte[] loadByteArrayFromNetwork(String url, boolean isLoged) {

		try {

			HttpGet method = new HttpGet(url);
			if (isLoged)
				method.addHeader("Cookie", "JSESSIONID=" + NewsAgencyApplication.SESSION_ID);
			HttpResponse response = httpClient.execute(method);
			HttpEntity entity = response.getEntity();
			return EntityUtils.toByteArray(entity);

		} catch (Exception e) {
			return null;
		}

	}

	/**
	 * 根据url生成缓存文件完整路径名
	 * 
	 * @param url
	 * @return
	 */
	public String urlToFilePath(String url) {

		// 扩展名位置
		int index = url.lastIndexOf('.');
		if (index == -1) {
			return null;
		}

		StringBuilder filePath = new StringBuilder();

		// 图片存取路径
		filePath.append(cachefile.toString()).append('/');

		// 图片文件名
		filePath.append(MD5Util.SharedMD5Util().Md5(url)).append(url.substring(index));

		return filePath.toString();
	}

	/**
	 * Activity#onStop后，ListView不会有残余请求。
	 */
	public void stop() {

		// 清空请求队列
		mImageQueue.clear();

	}

	/**
	 * 获得裁剪后的图片
	 * 
	 * @param bitmap
	 * @return
	 */
	/**
	 * 按正方形裁切图片
	 */
	public Bitmap ImageCrop(Bitmap bitmap, int width, int height) {
		int w = bitmap.getWidth(); // 得到图片的宽，高
		int h = bitmap.getHeight();

		if (w > width) {

		}

		int wh = w > h ? h : w;// 裁切后所取的正方形区域边长
		int retX = w > h ? (w - h) / 2 : 0;// 基于原图，取正方形左上角x坐标
		int retY = w > h ? 0 : (h - w) / 2;

		// 下面这句是关键
		return Bitmap.createBitmap(bitmap, retX, retY, wh, wh, null, false);
	}

	private static HttpClient createHttpClient() {
		HttpParams params = new BasicHttpParams();
		HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
		HttpProtocolParams.setContentCharset(params, HTTP.DEFAULT_CONTENT_CHARSET);
		HttpProtocolParams.setUseExpectContinue(params, true);
		HttpConnectionParams.setConnectionTimeout(params, 20 * 1000);
		HttpConnectionParams.setSoTimeout(params, 20 * 1000);
		HttpConnectionParams.setSocketBufferSize(params, 8192);
		SchemeRegistry schReg = new SchemeRegistry();
		schReg.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
		schReg.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));

		ClientConnectionManager connMgr = new ThreadSafeClientConnManager(params, schReg);

		return new DefaultHttpClient(connMgr, params);
	}

	private void shutdownHttpClient() {
		if (httpClient != null && httpClient.getConnectionManager() != null) {
			httpClient.getConnectionManager().shutdown();
		}
	}

	public HttpClient getHttpClient() {
		return httpClient;
	}

	/**
	 * 从缓存中和磁盘中移除这个文件
	 * 
	 * @param key
	 */
	public void remove(String key) {
		mMemoryCache.remove(key);
		mDiskCache.remove(key);
	}

}
