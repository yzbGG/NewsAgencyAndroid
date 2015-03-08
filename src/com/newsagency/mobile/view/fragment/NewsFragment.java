/**
 *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *
 * @PROGECT IYIMING
 * @AUTHOR dkslbw@gmail.com
 * @TIME 2015年3月3日 下午11:04:37
 *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  * 
 */
package com.newsagency.mobile.view.fragment;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.newsagency.mobile.R;
import com.newsagency.mobile.model.Project;
import com.newsagency.mobile.view.adapter.HomeAdapter;
import com.newsagency.mobile.view.widget.XListView;


/**
 * @DESCRIBE ...
 */
public class NewsFragment extends BaseFragment{
	
	private XListView listView;
	private List<Project> list;
	private HomeAdapter adapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_news, null);
		initView(inflater, view);
//		initData();
//		initListener();
		return view;
	}

	private void initView(LayoutInflater inflater, View view) {

		listView = (XListView) view.findViewById(R.id.xlistview_home);
		listView.setHeaderDividersEnabled(false);
		listView.setFooterDividersEnabled(false);
//		listView.setOnItemClickListener(this);
//		this.initHeadView(inflater);
//		listView.addHeaderView(this.headView);
		list = new ArrayList<Project>();
		adapter = new HomeAdapter(getActivity(), list);
		listView.setAdapter(adapter);
		listView.setPullLoadEnable(false);
		listView.setPullRefreshEnable(false);
//
//		homeYiming = (LinearLayout) view.findViewById(R.id.home_yiming);
//		homeLiuxue = (LinearLayout) view.findViewById(R.id.home_liuxue);
//		homeQianzheng = (LinearLayout) view.findViewById(R.id.home_qianzheng);
//		homeShengzi = (LinearLayout) view.findViewById(R.id.home_shengzi);
//		homeFangchan = (LinearLayout) view.findViewById(R.id.home_fangchan);
//		homeShuiwu = (LinearLayout) view.findViewById(R.id.home_shuiwu);
//		homeShangye = (LinearLayout) view.findViewById(R.id.home_shangye);
//		homeYinhang = (LinearLayout) view.findViewById(R.id.home_yinhang);
//
//		homeTouzi = (LinearLayout) view.findViewById(R.id.home_touzi);
//		homeJiangzuo = (LinearLayout) view.findViewById(R.id.home_jiangzuo);
//		homeYouji = (LinearLayout) view.findViewById(R.id.home_youji);

	}

	@Override
	public int getFragmentTitleResourceId() {
		return 0;
	}

	@Override
	public boolean isNeedRemove() {
		return false;
	}




}
