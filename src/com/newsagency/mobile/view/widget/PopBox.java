/**@文件:PopBox.java
 * @作者:dkslbw@gmail.com
 * @日期:2014年7月10日 下午12:54:05*/
package com.newsagency.mobile.view.widget;

import android.app.Dialog;
import android.content.Context;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.newsagency.mobile.R;
import com.newsagency.mobile.util.DensityUtil;

/**
 * @CLASS:PopBox
 * @描述:
 * @作者:dkslbw@gmail.com
 * @版本:v1.0
 * @日期:2014年7月10日 下午12:54:05
 */

public class PopBox extends Dialog { 

	private Button btn_OK;
	private Button btn_Cancel;
	private ProgressBar pb;
	private TextView tvContent, tvTitle;
	private Window window = null;
	private boolean backDismiss = true;

	private LinearLayout bottomLayout;

	public PopBox(Context context) {
		super(context, R.style.dialogSodino);
		setContentView(R.layout.pop_dialog);  
		tvTitle = (TextView) findViewById(R.id.title);
		// tvTitle.setShadowLayer(2f, 0f, 0f, Color.BLACK);
		tvContent = (TextView) findViewById(R.id.content);
		pb = (ProgressBar) findViewById(R.id.pb1);
		// tvContent.setShadowLayer(2f, 0f, 0f, Color.BLACK); 
		btn_OK = (Button) findViewById(R.id.btn_ok);
		// btn_OK.setShadowLayer(2f, 0f, 0f, Color.BLACK);
		LayoutParams lp = (LayoutParams) btn_OK.getLayoutParams();
		lp.width = DensityUtil.dip2px(getContext(), 140);
		btn_OK.setLayoutParams(lp);
		btn_OK.setFocusable(true);
		// btn_OK.setFocusableInTouchMode(true);
		// btn_OK.requestFocus();
		btn_Cancel = (Button) findViewById(R.id.btn_cancel);
		// btn_Cancel.setShadowLayer(2f, 0f, 0f, Color.BLACK);
		btn_Cancel.setFocusable(true);

		bottomLayout = (LinearLayout) findViewById(R.id.bottom);
	}

	public PopBox(Context context, int theme) {
		super(context, theme);
	}

	/**
	 * 调用此方法显示dialog
	 */
	public void showDialog() {
		// 设置触摸对话框意外的地方取消对话框
//		this.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
		setCanceledOnTouchOutside(backDismiss);
		windowDeploy();
		if (getContext() != null)
			show();
	}

	// 设置窗口显示
	public void windowDeploy() {
		window = getWindow();
		WindowManager.LayoutParams lp = window.getAttributes();
		lp.dimAmount = 0.5f;
		window.setAttributes(lp);
		// window.addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND);

	}

	/**
	 * 设置是否手动关闭dialog
	 * 
	 * @param bool
	 */
	public void setCanDismiss(boolean bool) {
		backDismiss = bool;
	}

	/**
	 * 添加标题
	 * 
	 * @param s
	 */
	public void showTitle(String s) {
		tvTitle.setVisibility(View.VISIBLE);
		tvTitle.setText(s);
	}

	/**
	 * 添加内容
	 * 
	 * @param s
	 */
	public void showContent(String s) {
		tvContent.setVisibility(View.VISIBLE);
		tvContent.setText(s);
	}

	/**
	 * 添加确定按钮
	 * 
	 * @param text
	 */
	public void showBtnOk(String text) {
		btn_OK.setVisibility(View.VISIBLE);
		btn_OK.setText(text);
	}

	/**
	 * 添加取消按钮
	 * 
	 * @param text
	 */
	public void showBtnCancel(String text) {
		btn_Cancel.setVisibility(View.VISIBLE);
		btn_Cancel.setText(text);
		LayoutParams lp = (LayoutParams) btn_OK.getLayoutParams();
		lp.width = ((LayoutParams) btn_Cancel.getLayoutParams()).width;
		btn_OK.setLayoutParams(lp);
	}

	/**
	 * 添加圆形进度条
	 */
	public void showProcessBar() {
		pb.setVisibility(View.VISIBLE);
		bottomLayout.setVisibility(View.GONE);
	}

	/**
	 * 添加确定按钮点击事件
	 * 
	 * @param listener
	 */
	public void setOKClickListener(android.view.View.OnClickListener listener) {
		btn_OK.setOnClickListener(listener);
	}

	/**
	 * 添加取消按钮点击事件
	 * 
	 * @param listener
	 */
	public void setCancelClickListener(
			android.view.View.OnClickListener listener) {
		btn_Cancel.setOnClickListener(listener);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (!backDismiss) {
			if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}
}
