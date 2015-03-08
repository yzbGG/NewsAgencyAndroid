/**
 *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *
 * @PROGECT NewsAgency
 * @AUTHOR dkslbw@gmail.com
 * @TIME 2015年3月7日 下午8:32:26
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
public class PhotoFragment extends BaseFragment{
	
	private XListView listView;
	private List<Project> list;
	private HomeAdapter adapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_photo, null);
		initView(inflater, view);
//		initData();
//		initListener();
		return view;
	}

	private void initView(LayoutInflater inflater, View view) {  

//		listView = (XListView) view.findViewById(R.id.xlistview_mirror);
//		listView.setHeaderDividersEnabled(false);
//		listView.setFooterDividersEnabled(false); 
//		list = new ArrayList<Project>();
//		adapter = new HomeAdapter(getActivity(), list);
//		listView.setAdapter(adapter);
//		listView.setPullLoadEnable(false);
//		listView.setPullRefreshEnable(false);
 

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
