package com.thirdparty.entry;

import java.util.ArrayList;

import android.text.TextUtils;

public class UploadRequest {
	public ArrayList<MyUploadTask> mUploadTasks = new ArrayList<MyUploadTask>();

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		StringBuilder sb = new StringBuilder();
		sb.append("tasks size:"+mUploadTasks.size()+"=");
		for (int i = 0; i < mUploadTasks.size(); i++) {
			MyUploadTask uploadTask = mUploadTasks.get(i);
			// if (uploadTask != null) {
			// if (!TextUtils.isEmpty(uploadTask.mMd5) &&
			// !TextUtils.isEmpty(uploadTask.mPath)) {
			// if (i == mUploadTasks.size() - 1) {
			// sb.append(uploadTask.mMd5);
			// } else {
			// sb.append(uploadTask.mMd5).append("#");
			// }
			// }
			// }
			sb.append(uploadTask.toString());
		}
		return sb.toString();
	}
}
