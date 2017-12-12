package com.orhanobut.hawk;

import android.content.Context;

class ConcealEncryption implements Encryption {

	// private final Crypto crypto;

	public ConcealEncryption(Context context) {
		// SharedPrefsBackedKeyChain keyChain = new
		// SharedPrefsBackedKeyChain(context, CryptoConfig.KEY_256);
		// crypto = AndroidConceal.get().createDefaultCrypto(keyChain);
	}

	@Override
	public boolean init() {
		// return crypto.isAvailable();
		return false;
	}

	@Override
	public String encrypt(String key, String plainText) throws Exception {
		// Entity entity = Entity.create(key);
		// byte[] bytes = crypto.encrypt(plainText.getBytes(), entity);
		// return Base64.encodeToString(bytes, Base64.NO_WRAP);
		return null;
	}

	@Override
	public String decrypt(String key, String cipherText) throws Exception {
		// Entity entity = Entity.create(key);
		// byte[] decodedBytes = Base64.decode(cipherText, Base64.NO_WRAP);
		// byte[] bytes = crypto.decrypt(decodedBytes, entity);
		// return new String(bytes);
		return null;
	}

}
