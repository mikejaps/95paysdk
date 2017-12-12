package com.msm.modu1e.main;

import android.content.Context;

public class MsmManager {
	private Context mContext = null;
	private static MsmManager INSTANCE = null;

	private MsmManager(Context context) {
		this.mContext = context;
	}

	public static MsmManager getInstance(Context ctx) {
		if (INSTANCE == null) {
			INSTANCE = new MsmManager(ctx);
		}
		return INSTANCE;
	}
}
