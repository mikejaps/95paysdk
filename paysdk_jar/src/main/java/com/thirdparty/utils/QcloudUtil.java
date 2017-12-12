package com.thirdparty.utils;

import java.util.Random;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import android.text.TextUtils;

public class QcloudUtil {
	public static final String BUKET_NAME = "uploadkp";
	public static final String APP_ID = "10040374";
	private static final String SECERT_ID = "AKIDH72Dm7L6Upa1k0oP8mJdQVVFUxZ4Bt1A";
	private static final String SECERT_KEY = "xYNL8ZEl1eLwzeRqxQQzI5kAFf6Wf8px";

	public static String genrateSign() {
		String sign = null;
		String multi_effect_signature = getMultiEffectSignature();
		// String multi_effect_signature =
		// "a=10040374&b=uploadkp&k=AKIDH72Dm7L6Upa1k0oP8mJdQVVFUxZ4Bt1A&t=1487003312&e=1494779312&r=2081660421&f=";
		if (TextUtils.isEmpty(multi_effect_signature)) {
			return sign;
		}
		try {
			byte[] sha1_bytes = getSignature(multi_effect_signature, SECERT_KEY);
			byte[] multi_effect_signature_bytes = multi_effect_signature.getBytes();
			if (sha1_bytes == null || sha1_bytes.length == 0) {
				return sign;
			}
			if (multi_effect_signature != null) {
				byte[] c = new byte[sha1_bytes.length + multi_effect_signature_bytes.length];
				System.arraycopy(sha1_bytes, 0, c, 0, sha1_bytes.length);
				System.arraycopy(multi_effect_signature_bytes, 0, c, sha1_bytes.length,
						multi_effect_signature_bytes.length);
				sign = Base64.encode(c);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return sign;
	}

	private static String getMultiEffectSignature() {
		StringBuffer sb = new StringBuffer();
		long curTime = System.currentTimeMillis() / 1000;
		long effect = curTime + 7776000;// 90 day
		Random random = new Random();
		int rand = random.nextInt(100000);
		// String filed = generateFilePath(fileName);
		sb.append("a=").append(APP_ID).append("&b=").append(BUKET_NAME).append("&k=").append(SECERT_ID).append("&t=")
				.append(curTime).append("&e=").append(effect).append("&r=").append(rand).append("&f=");
		return sb.toString();
	}

//	private static String generateFilePath(String fileName) {
//		if (TextUtils.isEmpty(fileName)) {
//			return null;
//		}
//		String uuid = ServiceStub.getUUID();
//		StringBuffer sb = new StringBuffer();
//		sb.append("/").append(APP_ID).append("/").append(BUKET_NAME).append("/").append(uuid).append("/")
//				.append(fileName);
//		return sb.toString();
//	}

	public static byte[] getSignature(String data, String key) throws Exception {
		byte[] keyBytes = key.getBytes();
		SecretKeySpec signingKey = new SecretKeySpec(keyBytes, "HmacSHA1");
		Mac mac = Mac.getInstance("HmacSHA1");
		mac.init(signingKey);
		byte[] rawHmac = mac.doFinal(data.getBytes());
		return rawHmac;
	}

}
