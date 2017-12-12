package com.channel.di.utils;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.widget.Toast;

/**
 * Created by Administrator on 2016/7/18.
 */
public class ToastUtils {
	private static Handler mHandler;

	public static void show(Activity activty, String txt) {
		// mHandler = new Handler(Looper.getMainLooper()); //返回到主线程
		// mHandler.post(new Runnable() {
		// @Override
		// public void run() {
		// Toast.makeText(context, txt,
		// android.widget.Toast.LENGTH_SHORT).show();
		// }
		// });
		activty.runOnUiThread(new ToastThread(activty, txt));
	}

	private static class ToastThread implements Runnable {
		private Context mContext;
		private String mTxt;

		public ToastThread(Context context, String txt) {
			this.mContext = context;
			this.mTxt = txt;
		}

		@Override
		public void run() {
			Toast.makeText(mContext, mTxt, Toast.LENGTH_SHORT).show();
		}
	}
}
