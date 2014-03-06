package com.septeni.polaris.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.UUID;

import jp.co.septeni.pyxis.PyxisTracking.PyxisDBHelper;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

public class Utility {
	/**
	 * Get key hash to use with facebook.
	 * 
	 * @param context
	 */
	public static void getKeyHash(Context context) {
		// Add code to print out the key hash
		try {
			PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(),
					PackageManager.GET_SIGNATURES);
			for (Signature signature : info.signatures) {
				MessageDigest md = MessageDigest.getInstance("SHA");
				md.update(signature.toByteArray());
				Log.e("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
			}
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}

	public static void updUser(PyxisDBHelper pyxisDBHelper, String uuid) {

		for (int i = 0; i < 10; i++) {
			try {
				uuid = replaceChar(uuid);

				ContentValues val = new ContentValues();
				if (!TextUtils.isEmpty(uuid)) {
					val.put("UUID", uuid);
				}

				SQLiteDatabase db = pyxisDBHelper.getWritableDatabase();
				db.update("USER", val, null, null);

				break;
			} catch (Exception e) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				continue;
			}
		}
	}

	public static HashMap<String, Object> selUser(PyxisDBHelper pyxisDBHelper) {
		Cursor cursor = null;
		HashMap<String, Object> resHashMap = new HashMap<String, Object>();
		try {
			SQLiteDatabase db = pyxisDBHelper.getReadableDatabase();
			cursor = db.query("USER", new String[] { "SESID", "FB_SESID", "UUID", "OPTOUT" }, null, null, null, null,
					null);

			if (cursor != null && cursor.getCount() > 0) {
				cursor.moveToFirst();
				String sesid = cursor.getString(cursor.getColumnIndex("SESID"));
				String fbSesid = cursor.getString(cursor.getColumnIndex("FB_SESID"));
				String uuid = cursor.getString(cursor.getColumnIndex("UUID"));
				String optOut = cursor.getString(cursor.getColumnIndex("OPTOUT"));

				resHashMap.put("SESID", sesid);
				resHashMap.put("FB_SESID", fbSesid);
				resHashMap.put("UUID", uuid);
				resHashMap.put("OPTOUT", optOut);
			}

			if (cursor != null) cursor.close();

		} catch (Exception e) {
			e.printStackTrace();
			LogUtils.logError(e.getMessage());
		}

		return resHashMap;
	}

	public static HashMap<String, Object> selInstall(PyxisDBHelper pyxisDBHelper) {

		Cursor cursor = null;
		HashMap<String, Object> installMap = new HashMap<String, Object>();
		try {
			SQLiteDatabase db = pyxisDBHelper.getReadableDatabase();
			cursor = db.query("INSTALL",
					new String[] { "START_FLAG", "FB_START_FLAG", "START_DATE", "IS_BROWSER_UPED", }, null, null, null,
					null, null);

			cursor.moveToFirst();
			if (cursor.getCount() > 0) {

				Integer startFlg = cursor.getInt(cursor.getColumnIndex("START_FLAG"));
				Integer fbStartFlg = cursor.getInt(cursor.getColumnIndex("FB_START_FLAG"));
				String startDate = cursor.getString(cursor.getColumnIndex("START_DATE"));
				Integer isBrowserUped = cursor.getInt(cursor.getColumnIndex("IS_BROWSER_UPED"));

				installMap.put("START_FLAG", startFlg);
				installMap.put("FB_START_FLAG", fbStartFlg);
				installMap.put("START_DATE", startDate);
				installMap.put("IS_BROWSER_UPED", isBrowserUped);
			}
		} catch (Exception e) {
			e.printStackTrace();
			LogUtils.logError(e.getMessage());
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}

		return installMap;
	}

	/**
	 * Generate SDK verify.
	 * 
	 * @return verify in MD5.
	 */
	public static String generateUuid() {
		// UUID random.
		String uuid = UUID.randomUUID().toString();

		// MD5 encode
		return md5(uuid);
	}

	/**
	 * Encode Uuid in MD5.
	 * 
	 * @param str
	 */
	private static String md5(String str) {

		try {
			// MD5ハッシュ値をバイト配列で取得
			MessageDigest digest = MessageDigest.getInstance("MD5");
			digest.reset();
			digest.update(str.getBytes());
			byte[] messageDigest = digest.digest();

			// 16進数に変換
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < messageDigest.length; i++) {
				if ((0xff & messageDigest[i]) < 0x10) {
					// 16進数で9未満(一桁の場合)は、先頭に0をつける
					sb.append("0" + Integer.toHexString((0xff & messageDigest[i])));
				} else {
					sb.append(Integer.toHexString((0xff & messageDigest[i])));
				}
			}

			return sb.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			LogUtils.logError(e.getMessage());
		}

		return "";
	}

	private static String replaceChar(String param) {
		if (TextUtils.isEmpty(param)) {
			return param;
		}
		String res = param.replaceAll("\n", "");
		res = res.replaceAll("\r", "");
		res = res.replaceAll("\t", "");
		return res;
	}
}
