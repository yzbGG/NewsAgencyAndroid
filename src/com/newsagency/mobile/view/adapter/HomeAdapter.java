/**
 *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *
 * @PROGECT NewsAgency
 * @AUTHOR dkslbw@gmail.com
 * @TIME 2015年3月7日 下午8:54:35
 *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  * 
 */
package com.newsagency.mobile.view.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.newsagency.mobile.R;
import com.newsagency.mobile.model.Project;
import com.newsagency.mobile.util.AppHelper;
import com.newsagency.mobile.util.AppInfoUtil;
import com.newsagency.mobile.util.ImageManager;

/**
 * @DESCRIBE ...
 */
public class HomeAdapter extends BaseAdapter {

	private Context con;
	private List<Project> datas;

	public HomeAdapter(Context con, List<Project> datas) {
		this.con = con;
		this.datas = datas;
	}

	// /**
	// * 刷新数据
	// *
	// * @param news
	// */
	// public void refreshData(List<HashMap<String, Object>> datas) {
	// if (null == datas) {
	// this.datas = new ArrayList<HashMap<String, Object>>();
	// } else {
	// this.datas = datas;
	// }
	// notifyDataSetChanged();
	// }

	@Override
	public int getCount() {
		return 4;
	}

	@Override
	public Object getItem(int arg0) {
		return datas.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(con).inflate(R.layout.list_item_news, parent, false);

			convertView.setTag(holder);
		} else { 
			holder = (ViewHolder) convertView.getTag();
		}

		return convertView;

	}

	class ViewHolder {
		ImageView itemImage;
		TextView itemMoney;
		TextView itemTitle;
		TextView itemInfo;
	}
}
