/**
 *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *
 * @PROGECT IYIMING
 * @AUTHOR dkslbw@gmail.com
 * @TIME 2015年3月7日 下午3:21:25
 *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  * 
 */
package com.newsagency.mobile.view.activity;

import java.util.HashMap;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.newsagency.mobile.R;
import com.newsagency.mobile.util.AppHelper;
import com.newsagency.mobile.view.fragment.BaseFragment;
import com.newsagency.mobile.view.fragment.EventFragment;
import com.newsagency.mobile.view.fragment.NewsFragment;
import com.newsagency.mobile.view.fragment.PhotoFragment;

/**
 * @DESCRIBE ...
 */
public class MyActivity extends BaseActivity implements OnClickListener {

	private TextView textNews, textEvent, textPhoto;
	private ImageView imageNews, imageEvent, imagePhoto;
	private LinearLayout lineNews, lineEvent, linePhoto;
	private LinearLayout tabNews, tabEvent, tabPhoto;

	/**
	 * 上一个显示的fragment，需要隐藏的
	 */
	private BaseFragment lastFragment;

	/**
	 * 当前的fragment
	 */
	private BaseFragment currentFragment;

	private int tabIndex = 0;

	/**
	 * 存放不需要重新创建的fragment
	 */
	private HashMap<Integer, BaseFragment> maps = new HashMap<Integer, BaseFragment>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my);
		initView();
		initData();
		initListener();
		setTab(0);
	}

	private void initView() {
		textNews = (TextView) findViewById(R.id.text_news);
		textEvent = (TextView) findViewById(R.id.text_event);
		textPhoto = (TextView) findViewById(R.id.text_photo);

		imageNews = (ImageView) findViewById(R.id.pic_news);
		imageEvent = (ImageView) findViewById(R.id.pic_event);
		imagePhoto = (ImageView) findViewById(R.id.pic_photo);

		lineNews = (LinearLayout) findViewById(R.id.line_news);
		lineEvent = (LinearLayout) findViewById(R.id.line_event);
		linePhoto = (LinearLayout) findViewById(R.id.line_photo);

		tabNews = (LinearLayout) findViewById(R.id.tab_news);
		tabEvent = (LinearLayout) findViewById(R.id.tab_event);
		tabPhoto = (LinearLayout) findViewById(R.id.tab_photo);
	}

	private void initData() {

	}

	private void initListener() {
		tabNews.setOnClickListener(this);
		tabEvent.setOnClickListener(this);
		tabPhoto.setOnClickListener(this);
	}

	private void setTab(int index) {
		textNews.setTextColor(0xff222222);
		textEvent.setTextColor(0xff222222);
		textPhoto.setTextColor(0xff222222);
		imageNews.setImageResource(R.drawable.icon_news_down);
		imageEvent.setImageResource(R.drawable.icon_event_down);
		imagePhoto.setImageResource(R.drawable.icon_photo_down);
		lineNews.setBackgroundColor(0x0001122c);
		lineEvent.setBackgroundColor(0x0001122c);
		linePhoto.setBackgroundColor(0x0001122c);
		tabNews.setBackgroundColor(0x00058b80);
		tabEvent.setBackgroundColor(0x00058b80);
		tabPhoto.setBackgroundColor(0x00058b80);
		if (index == 0) {
			textNews.setTextColor(0xffffffff);
			imageNews.setImageResource(R.drawable.icon_news);
			lineNews.setBackgroundColor(0xff01122c);
			tabNews.setBackgroundColor(0xff058b80);
		} else if (index == 1) {
			textEvent.setTextColor(0xffffffff);
			imageEvent.setImageResource(R.drawable.icon_event);
			lineEvent.setBackgroundColor(0xff01122c);
			tabEvent.setBackgroundColor(0xff058b80);
		} else if (index == 2) {
			textPhoto.setTextColor(0xffffffff);
			imagePhoto.setImageResource(R.drawable.icon_photo);
			linePhoto.setBackgroundColor(0xff01122c);
			tabPhoto.setBackgroundColor(0xff058b80);
		}
		showFragment(index);
	}

	@Override
	public void onClick(View v) {
		if (v == tabNews) {
			setTab(0);
		} else if (v == tabEvent) {
			setTab(1);
		} else if (v == tabPhoto) {
			setTab(2);
		}
	}

	/**
	 * 显示fragment
	 */
	public BaseFragment showFragment(int checkId) {
		FragmentTransaction transaction = getFragmentManager().beginTransaction();
		if (AppHelper.getSDKVersionNumber() >= 18) {
			transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
		}
		BaseFragment fragment = maps.get(checkId);
		if (fragment == null) {
			fragment = generateFragment(checkId);
			if (fragment == null) {
				return null;
			}
			maps.put(checkId, fragment);
			if (lastFragment != null) {
				if (lastFragment.isNeedRemove()) {
					maps.remove(lastFragment.getBindIngd());

					transaction.remove(lastFragment);
				} else {
					transaction.hide(lastFragment);
				}
			}
			transaction.add(R.id.container, fragment);
			lastFragment = fragment;

		} else {
			if (lastFragment != null) {
				if (lastFragment == fragment) {
					return fragment;
				}
				if (lastFragment.isNeedRemove()) {
					maps.remove(lastFragment.getBindIngd());
					transaction.remove(lastFragment);
					lastFragment = null;
				} else {
					transaction.hide(lastFragment);
				}
			}

			transaction.show(fragment);
			lastFragment = fragment;
		}

		// 更换右边图片
		changeFragmentTitle(fragment);

		transaction.commit();

		return fragment;
	}

	/**
	 * 更改activity的标题
	 * 
	 * @param fragment
	 */
	private void changeFragmentTitle(BaseFragment fragment) {

	}

	/**
	 * 新增的fragment 在此跟按钮id绑定
	 * 
	 * @param checkId
	 * @return
	 */
	private BaseFragment generateFragment(int checkId) {
		BaseFragment fragment = null;

		switch (checkId) {
		case 0:
			fragment = new NewsFragment();
			break;
		case 1:
			fragment = new EventFragment();
			break;
		case 2:
			fragment = new PhotoFragment();
			break;
		default:
			fragment = new NewsFragment();
			break;
		}
		if (fragment != null) {
			fragment.setBindIngd(checkId);
		}
		return fragment;
	}

}
