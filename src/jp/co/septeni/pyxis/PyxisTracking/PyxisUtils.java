package jp.co.septeni.pyxis.PyxisTracking;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.zip.Deflater;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.AllClientPNames;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.os.Process;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

public class PyxisUtils {

	private String className = this.getClass().getSimpleName();
	private PyxisConst pyxisConst = new PyxisConst();

	/**
	 * 基本パラメータのチェック
	 * 
	 * @param fromContext
	 * @param fromIntent
	 * @param siteId
	 * @param pyxisMv
	 * @return
	 */
	protected boolean checkBaseParams(String siteId, String pyxisMv) {

		/** ################# tracelog 処理開始 ################# */
		PyxisUtils.recordLog(className,
				(new Throwable()).getStackTrace()[0].getMethodName(),
				(new Throwable()).getStackTrace()[0].getLineNumber(),
				"基本パラメータのチェック開始", 1, false);

		Context fromContext = PyxisTracking.context;

		boolean result = true;

		if (fromContext == null) {
			/** ################# errorlog チェック false ################# */
			PyxisUtils.recordLog(className,
					(new Throwable()).getStackTrace()[0].getMethodName(),
					(new Throwable()).getStackTrace()[0].getLineNumber(),
					"コンストラクターを引数に設定してください。", 2, false);
			result = false;
		}
		if (PyxisTracking.intent == null) {
			/** ################# errorlog チェック false ################# */
			PyxisUtils.recordLog(className,
					(new Throwable()).getStackTrace()[0].getMethodName(),
					(new Throwable()).getStackTrace()[0].getLineNumber(),
					"インテントを引数に設定してください。", 2, false);
			result = false;
		}

		if (pyxisMv != null && "".equals(pyxisMv)) {
			/** ################# errorlog チェック false ################# */
			PyxisUtils.recordLog(className,
					(new Throwable()).getStackTrace()[0].getMethodName(),
					(new Throwable()).getStackTrace()[0].getLineNumber(),
					"MVが設定されていません。", 2, false);
			result = false;
		}
		if (TextUtils.isEmpty(siteId)) {
			/** ################# errorlog チェック false ################# */
			PyxisUtils.recordLog(className,
					(new Throwable()).getStackTrace()[0].getMethodName(),
					(new Throwable()).getStackTrace()[0].getLineNumber(),
					"SiteIDを引数に設定してください。 ", 2, false);
			result = false;
		}

		if (fromContext
				.checkCallingOrSelfPermission(android.Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
			/** ################# errorlog チェック false ################# */
			PyxisUtils.recordLog(className,
					(new Throwable()).getStackTrace()[0].getMethodName(),
					(new Throwable()).getStackTrace()[0].getLineNumber(),
					"AndroidManifest.xml に INTERNET のパーミッションを追加してください。 ", 2,
					false);
			result = true;
		}

		if (fromContext
				.checkCallingOrSelfPermission(android.Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED) {
			/** ################# errorlog チェック false ################# */
			PyxisUtils
					.recordLog(
							className,
							(new Throwable()).getStackTrace()[0]
									.getMethodName(),
							(new Throwable()).getStackTrace()[0]
									.getLineNumber(),
							"AndroidManifest.xml に ACCESS_NETWORK_STATE のパーミッションを追加してください。 ",
							2, false);
			result = true;
		}

		/** ################# tracelog 処理終了 ################# */
		PyxisUtils.recordLog(className,
				(new Throwable()).getStackTrace()[0].getMethodName(),
				(new Throwable()).getStackTrace()[0].getLineNumber(),
				"基本パラメータのチェック終了", 1, false);

		return result;

	}

	/**
	 * CPI計測用パラメータのチェック
	 * 
	 * @param fromContext
	 * @param fromIntent
	 * @param pyxisAppScheme
	 * @param pyxisAppHost
	 * @param pyxisMv
	 * @param siteId
	 * @param suid
	 * @return
	 */
	protected boolean checkParamsForCpi() {

		/** ################# tracelog 処理開始 ################# */
		PyxisUtils.recordLog(className,
				(new Throwable()).getStackTrace()[0].getMethodName(),
				(new Throwable()).getStackTrace()[0].getLineNumber(),
				"CPI計測用パラメータのチェック開始", 1, false);

		boolean result = checkBaseParams(PyxisTracking.siteId,
				PyxisTracking.pyxisMv);

		if (!PyxisTracking.cvMode.equals("1")
				&& PyxisTracking.actionPoint.get("browser_up_flg").equals("1")) {
			if (TextUtils.isEmpty(PyxisTracking.pyxisAppScheme)) {
				/** ################# errorlog チェック false ################# */
				PyxisUtils.recordLog(className,
						(new Throwable()).getStackTrace()[0].getMethodName(),
						(new Throwable()).getStackTrace()[0].getLineNumber(),
						"Pyxis用のschmeを引数に設定してください。 ", 2, false);
				result = false;
			}
			if (TextUtils.isEmpty(PyxisTracking.pyxisAppHost)) {
				/** ################# errorlog チェック false ################# */
				PyxisUtils.recordLog(className,
						(new Throwable()).getStackTrace()[0].getMethodName(),
						(new Throwable()).getStackTrace()[0].getLineNumber(),
						"Pyxis用のhostを引数に設定してください。 ", 2, false);
				result = false;
			}
		}

		/** ################# tracelog 処理終了 ################# */
		PyxisUtils.recordLog(className,
				(new Throwable()).getStackTrace()[0].getMethodName(),
				(new Throwable()).getStackTrace()[0].getLineNumber(),
				"CPI計測用パラメータのチェック終了", 1, false);

		return result;

	}

	/**
	 * アプリ内計測情報保存用のパラメータチェック
	 * 
	 * @param fromContext
	 * @param fromIntent
	 * @param mv
	 * @param verify
	 * @param suid
	 * @param siteId
	 * @param saveMax
	 * @param saveMode
	 * @return
	 */
	protected boolean checkParamasForAppSave(String pyxisMv, String siteId,
			Integer saveMax, Integer saveMode) {

		/** ################# tracelog 処理開始 ################# */
		PyxisUtils.recordLog(className,
				(new Throwable()).getStackTrace()[0].getMethodName(),
				(new Throwable()).getStackTrace()[0].getLineNumber(),
				"アプリ内計測用パラメータのチェック開始", 1, false);

		boolean result = checkBaseParams(siteId, pyxisMv);

		/** ################# tracelog 処理終了 ################# */
		PyxisUtils.recordLog(className,
				(new Throwable()).getStackTrace()[0].getMethodName(),
				(new Throwable()).getStackTrace()[0].getLineNumber(),
				"アプリ内計測用パラメータのチェック終了", 1, false);

		return result;

	}

	/**
	 * Android_ID取得
	 * 
	 * @param Context
	 * @return Android Marcket Id
	 */
	protected String getAndroidId() {

		String res = android.provider.Settings.Secure.getString(
				PyxisTracking.context.getContentResolver(),
				android.provider.Settings.Secure.ANDROID_ID);
		if (TextUtils.isEmpty(res)) {
			/** ################# tracelog ################# */
			PyxisUtils.recordLog(className,
					(new Throwable()).getStackTrace()[0].getMethodName(),
					(new Throwable()).getStackTrace()[0].getLineNumber(),
					"AndroidIdなし", 1, false);
			return "";
		}

		return res;
	}

	/**
	 * 端末名取得
	 * 
	 * @return Device Name
	 */
	protected String getPhoneName() {

		if (TextUtils.isEmpty(android.os.Build.MODEL)) {
			/** ################# tracelog ################# */
			PyxisUtils.recordLog(className,
					(new Throwable()).getStackTrace()[0].getMethodName(),
					(new Throwable()).getStackTrace()[0].getLineNumber(),
					"端末名取得失敗", 1, false);
		}

		return android.os.Build.MODEL;
	}

	//
	/**
	 * OSバージョン取得
	 * 
	 * @return OS Version
	 */
	protected String getOsVersion() {
		if (TextUtils.isEmpty(android.os.Build.VERSION.RELEASE)) {
			/** ################# tracelog ################# */
			PyxisUtils.recordLog(className,
					(new Throwable()).getStackTrace()[0].getMethodName(),
					(new Throwable()).getStackTrace()[0].getLineNumber(),
					"OSバージョン取得失敗", 1, false);
		}
		return android.os.Build.VERSION.RELEASE;
	}

	/**
	 * APIバージョン取得
	 * 
	 * @return API Version
	 */
	protected int getApiVersion() {

		int ver = 0;
		try {
			ver = android.os.Build.VERSION.SDK_INT;
		} catch (Exception e) {
			/** ################# errorlog チェック false ################# */
			PyxisUtils.recordErrorLog(className,
					(new Throwable()).getStackTrace()[0].getMethodName(),
					(new Throwable()).getStackTrace()[0].getLineNumber(),
					"APIバージョン取得の取得失敗 ", 2, false, e);
		}

		return ver;
	}

	/**
	 * Pyxis User-Agent作成
	 * 
	 * @param Context
	 * @return User-Agent
	 */
	protected String makeUserAgent() {

		String ua = "";

		try {
			Context context = PyxisTracking.context;
			String packegeName = context.getPackageName();

			PackageInfo packageInfo = context.getPackageManager()
					.getPackageInfo(packegeName, PackageManager.GET_META_DATA);
			ua = context.getPackageName() + "/" + packageInfo.versionName
					+ " Pyxis/" + pyxisConst.SDK_VERSION + " (Android) "
					+ Locale.getDefault().getCountry();
		} catch (NameNotFoundException e) {
			/** ################# errorlog チェック false ################# */
			PyxisUtils.recordErrorLog(className,
					(new Throwable()).getStackTrace()[0].getMethodName(),
					(new Throwable()).getStackTrace()[0].getLineNumber(),
					"SDKバージョン取得の取得失敗 ", 2, false, e);
		}
		return ua;
	}

	/**
	 * アプリのバージョンを取得する
	 * 
	 * @return
	 */
	protected String getAppVersion() {
		String version = "";
		try {
			Context context = PyxisTracking.context;
			PackageManager packageManager = context.getPackageManager();
			PackageInfo packageInfo = packageManager.getPackageInfo(
					context.getPackageName(), PackageManager.GET_ACTIVITIES);
			version = packageInfo.versionName;
		} catch (NameNotFoundException e) {
			/** ################# errorlog チェック false ################# */
			PyxisUtils.recordErrorLog(className,
					(new Throwable()).getStackTrace()[0].getMethodName(),
					(new Throwable()).getStackTrace()[0].getLineNumber(),
					"アプリバージョン取得の取得失敗 ", 2, false, e);
		}
		return version;
	}

	/**
	 * ネットワーク状態取得
	 * 
	 * @param Context
	 * @return bool
	 */
	protected boolean getNetworkStatus() {

		ConnectivityManager cm = (ConnectivityManager) PyxisTracking.context
				.getApplicationContext().getSystemService(
						Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		if (ni == null || cm.getActiveNetworkInfo().isConnected() == false) {
			/** ################# tracelog ################# */
			PyxisUtils.recordLog(className,
					(new Throwable()).getStackTrace()[0].getMethodName(),
					(new Throwable()).getStackTrace()[0].getLineNumber(),
					"ネットワーク状態：未接続", 1, false);
			return false;
		} else {
			/** ################# tracelog ################# */
			PyxisUtils.recordLog(className,
					(new Throwable()).getStackTrace()[0].getMethodName(),
					(new Throwable()).getStackTrace()[0].getLineNumber(),
					"ネットワーク状態：接続中", 1, false);
			return true;
		}

	}

	/**
	 * AES128暗号化
	 * 
	 * @param data
	 *            暗号化の対象データ
	 * @param secret_key
	 *            128ビットの秘密鍵
	 * @return 暗号化されたデータ
	 */
	protected byte[] encodeAES128(byte[] data, byte[] secret_key) {

		/** ################# tracelog ################# */
		PyxisUtils.recordLog(className,
				(new Throwable()).getStackTrace()[0].getMethodName(),
				(new Throwable()).getStackTrace()[0].getLineNumber(),
				"AES128暗号化開始", 1, false);

		byte[] returnByte = null;

		SecretKeySpec sKey = new SecretKeySpec(secret_key, "AES");
		try {
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, sKey);

			returnByte = cipher.doFinal(data);
			/** ################# tracelog ################# */
			PyxisUtils.recordLog(className,
					(new Throwable()).getStackTrace()[0].getMethodName(),
					(new Throwable()).getStackTrace()[0].getLineNumber(),
					"AES128暗号化成功", 1, false);
		} catch (Exception e) {
			/** ################# errorlog チェック false ################# */
			PyxisUtils.recordErrorLog(className,
					(new Throwable()).getStackTrace()[0].getMethodName(),
					(new Throwable()).getStackTrace()[0].getLineNumber(),
					"AES128暗号化失敗 ", 2, false, e);
		}

		/** ################# tracelog ################# */
		PyxisUtils.recordLog(className,
				(new Throwable()).getStackTrace()[0].getMethodName(),
				(new Throwable()).getStackTrace()[0].getLineNumber(),
				"AES128暗号化完了", 1, false);

		return returnByte;
	}

	/**
	 * ZIP圧縮
	 * 
	 * @param val
	 *            圧縮対象のデータ
	 * @return 圧縮されたデータ
	 */
	protected byte[] compressZip(String val) {

		/** ################# tracelog ################# */
		PyxisUtils.recordLog(className,
				(new Throwable()).getStackTrace()[0].getMethodName(),
				(new Throwable()).getStackTrace()[0].getLineNumber(),
				"ZIP圧縮開始", 1, false);

		byte[] value = null;
		try {
			Deflater compresser = new Deflater();
			compresser.setLevel(Deflater.BEST_COMPRESSION);

			value = val.toString().getBytes();

			compresser.setInput(value);
			compresser.finish();

			ByteArrayOutputStream compos = new ByteArrayOutputStream(
					value.length);

			byte[] buf = new byte[1024];
			int count;
			while (!compresser.finished()) {
				count = compresser.deflate(buf);
				compos.write(buf, 0, count);
			}
			/** ################# tracelog ################# */
			PyxisUtils.recordLog(className,
					(new Throwable()).getStackTrace()[0].getMethodName(),
					(new Throwable()).getStackTrace()[0].getLineNumber(),
					"ZIP圧縮成功", 1, false);
		} catch (Exception e) {
			/** ################# errorlog チェック false ################# */
			PyxisUtils.recordErrorLog(className,
					(new Throwable()).getStackTrace()[0].getMethodName(),
					(new Throwable()).getStackTrace()[0].getLineNumber(),
					"ZIP圧縮失敗 ", 2, false, e);
		}

		/** ################# tracelog ################# */
		PyxisUtils.recordLog(className,
				(new Throwable()).getStackTrace()[0].getMethodName(),
				(new Throwable()).getStackTrace()[0].getLineNumber(),
				"ZIP圧縮完了", 1, false);

		return value;
	}

	/**
	 * LZW圧縮
	 * 
	 * @param uncompressed
	 *            圧縮対象のデータ
	 * @return 圧縮されたバイナリデータのバイトコード
	 */
	protected byte[] compressLzw(String uncompressed) {

		/** ################# tracelog ################# */
		PyxisUtils.recordLog(className,
				(new Throwable()).getStackTrace()[0].getMethodName(),
				(new Throwable()).getStackTrace()[0].getLineNumber(),
				"LZW圧縮処理開始", 1, false);

		byte[] lzwByte = null;

		try {

			int dictSize = 256;
			Map<String, Integer> dictionary = new HashMap<String, Integer>();
			for (int i = 0; i < 256; i++) {
				dictionary.put("" + (char) i, i);
			}

			String w = "";
			List<Integer> codes = new ArrayList<Integer>();
			for (char c : uncompressed.toCharArray()) {
				String wc = w + c;
				if (dictionary.containsKey(wc)) {
					w = wc;
				} else {
					codes.add(dictionary.get(w));
					dictionary.put(wc, dictSize++);
					w = "" + c;
				}
			}

			if (!w.equals("")) {
				codes.add(dictionary.get(w));
			}

			int dictionary_count = 256;
			int bits = 8;
			List<Integer> asciiList = new ArrayList<Integer>();
			int rest = 0;
			int rest_length = 0;

			for (Integer code : codes) {
				rest = (rest << bits) + code;
				rest_length += bits;
				dictionary_count++;
				if (dictionary_count > (1 << bits)) {
					bits++;
				}

				while (rest_length > 7) {
					rest_length -= 8;
					asciiList.add((rest >> rest_length));
					rest &= (1 << rest_length) - 1;
				}
			}

			if (rest_length > 0) {
				asciiList.add((rest << (8 - rest_length)));
			}

			byte[] returnByte = new byte[asciiList.size()];
			for (int i = 0; i < asciiList.size(); i++) {
				returnByte[i] = asciiList.get(i).byteValue();
			}

			/** ################# tracelog ################# */
			PyxisUtils.recordLog(className,
					(new Throwable()).getStackTrace()[0].getMethodName(),
					(new Throwable()).getStackTrace()[0].getLineNumber(),
					"LZW圧縮完了", 1, false);

			lzwByte = returnByte;

		} catch (Exception e) {
			/** ################# tracelog ################# */
			PyxisUtils.recordErrorLog(className,
					(new Throwable()).getStackTrace()[0].getMethodName(),
					(new Throwable()).getStackTrace()[0].getLineNumber(),
					"LZW圧縮失敗", 2, false, e);
		}

		/** ################# tracelog ################# */
		PyxisUtils.recordLog(className,
				(new Throwable()).getStackTrace()[0].getMethodName(),
				(new Throwable()).getStackTrace()[0].getLineNumber(),
				"LZW圧縮処理完了", 1, false);

		return lzwByte;

	}

	/** Base64テーブル */
	private static final String _BASE64_TABLE = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";

	/**
	 * Base64 エンコード
	 * 
	 * @param data
	 *            対象データ
	 * @return Base64エンコードされたデータ
	 */
	protected String base64Encode(byte[] data) {

		/** ################# tracelog ################# */
		PyxisUtils.recordLog(className,
				(new Throwable()).getStackTrace()[0].getMethodName(),
				(new Throwable()).getStackTrace()[0].getLineNumber(),
				"Base64 エンコード処理開始", 1, false);

		String returnStr = "";

		try {
			if (data.length == 0) {
				/** ################# tracelog ################# */
				PyxisUtils.recordLog(className,
						(new Throwable()).getStackTrace()[0].getMethodName(),
						(new Throwable()).getStackTrace()[0].getLineNumber(),
						"Base64 エンコード処理終了:空文字", 1, false);
				return "";
			}

			int i;
			int index[] = new int[1 + data.length * 4 / 3];
			int count = 0;
			int limit = data.length - 3;
			int mod;
			int padding;
			StringBuilder result = new StringBuilder("");

			for (i = 0; i < limit; i += 3) {
				index[count++] = (data[i] & 0xfc) >>> 2;
				index[count++] = ((data[i] & 0x03) << 4)
						+ ((data[i + 1] & 0xf0) >>> 4);
				index[count++] = ((data[i + 1] & 0x0f) << 2)
						+ ((data[i + 2] & 0xc0) >>> 6);
				index[count++] = ((data[i + 2]) & 0x3f);
			}

			mod = data.length % 3;
			if (mod == 0) {
				index[count++] = (data[i] & 0xfc) >>> 2;
				index[count++] = ((data[i] & 0x03) << 4)
						+ ((data[i + 1] & 0xf0) >>> 4);
				index[count++] = ((data[i + 1] & 0x0f) << 2)
						+ ((data[i + 2] & 0xc0) >>> 6);
				index[count++] = ((data[i + 2]) & 0x3f);

			} else if (mod == 1) {
				index[count++] = (data[i] & 0xfc) >>> 2;
				index[count++] = (data[i] & 0x03) << 4;

			} else if (mod == 2) {
				index[count++] = (data[i] & 0xfc) >>> 2;
				index[count++] = ((data[i] & 0x03) << 4)
						+ ((data[i + 1] & 0xf0) >>> 4);
				index[count++] = (data[i + 1] & 0x0f) << 2;
			}

			for (i = 0; i < count; i++) {
				result.append(_BASE64_TABLE.charAt(index[i]));
			}

			padding = (4 - result.length() % 4) % 4;
			for (i = 0; i < padding; i++) {
				result.append("=");
			}

			returnStr = result.toString();
		} catch (Exception e) {
			/** ################# tracelog ################# */
			PyxisUtils.recordErrorLog(className,
					(new Throwable()).getStackTrace()[0].getMethodName(),
					(new Throwable()).getStackTrace()[0].getLineNumber(),
					"Base64 エンコード失敗", 2, false, e);
		}

		/** ################# tracelog ################# */
		PyxisUtils.recordLog(className,
				(new Throwable()).getStackTrace()[0].getMethodName(),
				(new Throwable()).getStackTrace()[0].getLineNumber(),
				"Base64 エンコード処理終了", 1, false);

		return returnStr;
	}

	/**
	 * Base64 デコード
	 * 
	 * @param base64
	 *            Base64エンコードされたデータ
	 * @return データ
	 */
	protected byte[] base64Decode(String s) {

		/** ################# tracelog ################# */
		PyxisUtils.recordLog(className,
				(new Throwable()).getStackTrace()[0].getMethodName(),
				(new Throwable()).getStackTrace()[0].getLineNumber(),
				"Base64 デコード処理開始" + s, 1, false);

		int end = 0; // end state
		if (s.endsWith("=")) {
			end++;
		}
		if (s.endsWith("==")) {
			end++;
		}
		int len = (s.length() + 3) / 4 * 3 - end;
		byte[] result = new byte[len];
		int dst = 0;
		try {
			for (int src = 0; src < s.length(); src++) {
				int code = _BASE64_TABLE.indexOf(s.charAt(src));
				if (code == -1) {
					break;
				}
				switch (src % 4) {
				case 0:
					result[dst] = (byte) (code << 2);
					break;
				case 1:
					result[dst++] |= (byte) ((code >> 4) & 0x3);
					result[dst] = (byte) (code << 4);
					break;
				case 2:
					result[dst++] |= (byte) ((code >> 2) & 0xf);
					result[dst] = (byte) (code << 6);
					break;
				case 3:
					result[dst++] |= (byte) (code & 0x3f);
					break;
				}
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			// 正常なので何もしない
			PyxisUtils.recordLog(className,
					(new Throwable()).getStackTrace()[0].getMethodName(),
					(new Throwable()).getStackTrace()[0].getLineNumber(),
					"Base64 デコード処理終了", 1, false);
		} catch (Exception ex) {
			/** ################# tracelog ################# */
			PyxisUtils.recordLog(className,
					(new Throwable()).getStackTrace()[0].getMethodName(),
					(new Throwable()).getStackTrace()[0].getLineNumber(),
					"Base64 デコード処理エラー", 2, false);
		}
		/** ################# tracelog ################# */
		PyxisUtils.recordLog(className,
				(new Throwable()).getStackTrace()[0].getMethodName(),
				(new Throwable()).getStackTrace()[0].getLineNumber(),
				"Base64 デコード処理終了", 1, false);
		return result;
	}

	/**
	 * ソケット通信でUUIDを取得
	 * 
	 * @param context
	 * @return uuid
	 */
	protected String getUUID() {
		/** ################# tracelog ################# */
		PyxisUtils.recordLog(className,
				(new Throwable()).getStackTrace()[0].getMethodName(),
				(new Throwable()).getStackTrace()[0].getLineNumber(),
				"UUID取得処理開始", 1, false);

		String uuid = null;

		try {
			PyxisDBHelper dbHelper;
			dbHelper = new PyxisDBHelper();
			// UUID取得
			HashMap<String, Object> userMap = dbHelper.selUser();
			dbHelper.close();
			if (userMap.get("UUID") != null) {
				// DBにUUIDが入ってたらそれを返す
				return userMap.get("UUID").toString();
			}

			HttpPost httpPost = new HttpPost(pyxisConst.HTTP_PATH_UUID);
			List<NameValuePair> params = new ArrayList<NameValuePair>(1) {
				private static final long serialVersionUID = -2210805557550155321L;
				{
					add(new BasicNameValuePair("mode", "1"));
				}
			};
			httpPost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
			httpPost.setHeader("User-Agent", makeUserAgent());

			HttpParams httpParms = new BasicHttpParams();
			httpParms
					.setIntParameter(AllClientPNames.CONNECTION_TIMEOUT, 15000);
			httpParms.setIntParameter(AllClientPNames.SO_TIMEOUT, 15000);
			DefaultHttpClient client = new DefaultHttpClient(httpParms);
			HttpResponse httpResponse = client.execute(httpPost);

			// TODO Kinfisher Phuoc added and debug
			HttpEntity htentity = httpResponse.getEntity();
			String result = "";
			if (htentity != null) {
				InputStream instream = null;
				try {
					instream = htentity.getContent();
					result = convertStreamToString(instream);
				} catch (IOException e) {
					e.printStackTrace();
				} catch (IllegalStateException e1) {
					e1.printStackTrace();
				}
			}
			Log.i(className,
					(new Throwable()).getStackTrace()[0].getMethodName()
							+ " "
							+ (new Throwable()).getStackTrace()[0]
									.getLineNumber() + ", Post to URL: "
							+ pyxisConst.HTTP_PATH_UUID);
			Log.i(className,
					(new Throwable()).getStackTrace()[0].getMethodName()
							+ " "
							+ (new Throwable()).getStackTrace()[0]
									.getLineNumber() + ", respond: " + result);
			// ==================== END Kingfisher ============================
			// ステータスコードを取得
			int httpStatusCode = httpResponse.getStatusLine().getStatusCode();

			// HTTP Statusが200の場合、正常通信
			if (httpStatusCode == HttpStatus.SC_OK) {
				Log.i(className,
						(new Throwable()).getStackTrace()[0].getMethodName()
								+ (new Throwable()).getStackTrace()[0]
										.getLineNumber() + "Status ok.");
				// レスポンスを取得
				HttpEntity entity = httpResponse.getEntity();
				uuid = EntityUtils.toString(entity);

				// リソースを解放
				entity.consumeContent();

				// クライアントを終了させる
				client.getConnectionManager().shutdown();

				/** ################# tracelog ################# */
				PyxisUtils.recordLog(className,
						(new Throwable()).getStackTrace()[0].getMethodName(),
						(new Throwable()).getStackTrace()[0].getLineNumber(),
						"UUID : " + uuid, 1, false);

			} else {
				Log.i(className,
						(new Throwable()).getStackTrace()[0].getMethodName()
								+ (new Throwable()).getStackTrace()[0]
										.getLineNumber() + "Status error.");
				/** ################# tracelog ################# */
				PyxisUtils.recordLog(className,
						(new Throwable()).getStackTrace()[0].getMethodName(),
						(new Throwable()).getStackTrace()[0].getLineNumber(),
						"UUID未取得:[通信不可]", 1, false);
			}

		} catch (Exception e) {
			/** ################# tracelog ################# */
			PyxisUtils.recordErrorLog(className,
					(new Throwable()).getStackTrace()[0].getMethodName(),
					(new Throwable()).getStackTrace()[0].getLineNumber(),
					"UUID取得失敗", 2, false, e);
		}

		/** ################# tracelog ################# */
		PyxisUtils.recordLog(className,
				(new Throwable()).getStackTrace()[0].getMethodName(),
				(new Throwable()).getStackTrace()[0].getLineNumber(),
				"UUID取得処理終了", 1, false);

		return uuid;

	}

	/**
	 * @param instream
	 *            is the inputStream to be converted.
	 * @return the converted result in format of string.
	 */
	private static String convertStreamToString(InputStream instream) {

		String line = null;
		StringBuilder sb = new StringBuilder();
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					instream, "UTF-8"));

			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				instream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}

	/**
	 * ソケット通信で成果点情報を取得
	 * 
	 * @param context
	 * @return uuid
	 */
	protected HashMap<String, String> getActionPoint(String mv) {

		/** ################# tracelog ################# */
		PyxisUtils.recordLog(className,
				(new Throwable()).getStackTrace()[0].getMethodName(),
				(new Throwable()).getStackTrace()[0].getLineNumber(),
				"成果点情報取得処理開始", 1, false);

		String ret = null;
		HashMap<String, String> actionPointList = new HashMap<String, String>();

		try {
			HttpPost httpPost = new HttpPost(pyxisConst.HTTP_PATH_ACTION_POINT);

			List<NameValuePair> params = new ArrayList<NameValuePair>(2) {
				private static final long serialVersionUID = 7825567028420910212L;
			};
			params.add(new BasicNameValuePair("mv", mv));
			params.add(new BasicNameValuePair("dv", "a"));
			httpPost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
			httpPost.setHeader("User-Agent", makeUserAgent());

			HttpParams httpParms = new BasicHttpParams();
			httpParms.setIntParameter(AllClientPNames.CONNECTION_TIMEOUT, 3000);
			httpParms.setIntParameter(AllClientPNames.SO_TIMEOUT, 3000);
			DefaultHttpClient client = new DefaultHttpClient(httpParms);
			HttpResponse httpResponse = client.execute(httpPost);

			// ステータスコードを取得
			int httpStatusCode = httpResponse.getStatusLine().getStatusCode();

			// HTTP Statusが200の場合、正常通信
			if (httpStatusCode == HttpStatus.SC_OK) {

				// レスポンスを取得
				HttpEntity entity = httpResponse.getEntity();
				ret = EntityUtils.toString(entity);

				// リソースを解放
				entity.consumeContent();

				// クライアントを終了させる
				client.getConnectionManager().shutdown();

			} else {
				/** ################# tracelog ################# */
				PyxisUtils.recordLog(className,
						(new Throwable()).getStackTrace()[0].getMethodName(),
						(new Throwable()).getStackTrace()[0].getLineNumber(),
						"成果点情報未取得:[通信不可]", 1, false);
				return null;
			}

			if (ret.equals("-1")) {
				/** ################# tracelog ################# */
				PyxisUtils.recordErrorLog(className,
						(new Throwable()).getStackTrace()[0].getMethodName(),
						(new Throwable()).getStackTrace()[0].getLineNumber(),
						"成果点情報取得失敗 : エラーレスポンス -1", 2, false, null);
				return null;
			}

			String[] actionPointListArr = ret.split("&pyxis&");
			for (int i = 0; i < actionPointListArr.length; i++) {
				/** ################# tracelog ################# */
				PyxisUtils.recordLog(className,
						(new Throwable()).getStackTrace()[0].getMethodName(),
						(new Throwable()).getStackTrace()[0].getLineNumber(),
						actionPointListArr[i], 1, false);
				String[] tmp = actionPointListArr[i].split("#");

				if (tmp.length == 1) {
					actionPointList.put(tmp[0], null);
				}

				if (tmp.length == 2) {
					if (tmp[0].equalsIgnoreCase("welcome_url")) {
						tmp[1] = new String(base64Decode(tmp[1]), "UTF-8");
					}
					actionPointList.put(tmp[0], tmp[1]);
				}
			}

		} catch (Exception e) {
			/** ################# tracelog ################# */
			PyxisUtils.recordErrorLog(className,
					(new Throwable()).getStackTrace()[0].getMethodName(),
					(new Throwable()).getStackTrace()[0].getLineNumber(),
					"成果点情報取得失敗", 2, false, e);
			return null;
		}

		/** ################# tracelog ################# */
		PyxisUtils.recordLog(className,
				(new Throwable()).getStackTrace()[0].getMethodName(),
				(new Throwable()).getStackTrace()[0].getLineNumber(),
				"成果点情報取得処理終了：" + ret, 1, false);

		return actionPointList;

	}

	/**
	 * ソケット通信(CPI用)
	 * 
	 * @param url
	 *            通信先
	 * @param params
	 *            リクエストパラメータ
	 * @return bool
	 */
	protected boolean sendCpiBySocket(String url,
			ArrayList<NameValuePair> params) {

		/** ################# tracelog ################# */
		PyxisUtils.recordLog(className,
				(new Throwable()).getStackTrace()[0].getMethodName(),
				(new Throwable()).getStackTrace()[0].getLineNumber(),
				"ソケット通信処理開始", 1, false);

		boolean res = false;

		try {
			HttpPost httpPost = new HttpPost(url);
			httpPost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
			httpPost.setHeader("User-Agent", makeUserAgent());

			HttpParams httpParms = new BasicHttpParams();
			httpParms
					.setIntParameter(AllClientPNames.CONNECTION_TIMEOUT, 10000);
			httpParms.setIntParameter(AllClientPNames.SO_TIMEOUT, 10000);
			DefaultHttpClient client = new DefaultHttpClient(httpParms);
			HttpResponse httpResponse = client.execute(httpPost);

			// ステータスコードを取得
			int httpStatusCode = httpResponse.getStatusLine().getStatusCode();

			// HTTP Statusが200の場合、正常通信
			if (httpStatusCode == HttpStatus.SC_OK) {

				// レスポンスを取得
				HttpEntity entity = httpResponse.getEntity();
				String response = EntityUtils.toString(entity);

				// リソースを解放
				entity.consumeContent();

				// クライアントを終了させる
				client.getConnectionManager().shutdown();

				// レスポンスデーターチェック
				if ("1".equals(response)) {
					res = true;
				} else {
					/** ################# errorlog ################# */
					PyxisUtils.recordLog(className, (new Throwable())
							.getStackTrace()[0].getMethodName(),
							(new Throwable()).getStackTrace()[0]
									.getLineNumber(), "ソケット通信処理失敗[エラーレスポンス] : "
									+ response, 2, false);
				}
			} else {
				/** ################# errorlog ################# */
				PyxisUtils.recordLog(className,
						(new Throwable()).getStackTrace()[0].getMethodName(),
						(new Throwable()).getStackTrace()[0].getLineNumber(),
						"ソケット通信処理失敗[HTTPステータスエラー] : " + httpStatusCode, 2,
						false);
			}
		} catch (Exception e) {
			/** ################# errorlog ################# */
			PyxisUtils.recordLog(className,
					(new Throwable()).getStackTrace()[0].getMethodName(),
					(new Throwable()).getStackTrace()[0].getLineNumber(),
					"ソケット通信処理失敗[Exception] : " + e, 2, false);
		}
		/** ################# tracelog ################# */
		PyxisUtils.recordLog(className,
				(new Throwable()).getStackTrace()[0].getMethodName(),
				(new Throwable()).getStackTrace()[0].getLineNumber(),
				"ソケット通信処理終了", 1, false);

		return res;
	}

	private static final String twohyphens = "--";
	private static final String end = "\r\n";
	private static final String dq = "\"";
	private static final String prfx_boundary = "-----------------------------";
	private static final String mBoundary = prfx_boundary
			+ Long.toString(System.currentTimeMillis());

	/**
	 * ソケット通信(アプリ内計測用)
	 * 
	 * @param url
	 *            通信先
	 * @param params
	 *            リクエストパラメータ
	 * @return bool
	 */
	protected boolean sendAppBySocket(String url, Map<String, String> params) {

		final String POST_METHOD = "POST";
		final String CONNECTION = "Connection";
		final String CHARSET = "Charset";
		final String CONTENT_TYPE = "Content-Type";

		final String KEEP_ALIVE = "Keep-Alive";
		final String UTF8 = "UTF-8";
		final String MULTIPART_BOUNDARY = "multipart/form-data; boundary=";

		final String FORM_LBL_OUTENC = "outputencoding";
		final String FORM_LBL_OUTFORMAT = "outputformat";
		final String FORM_LBL_USRFILE = "fs";
		final String FORM_LBL_ELEMENTCLASS = "eclass";
		final String FORM_VAL_FORMAT = "txt";
		final String FORM_VAL_FILE = "pid_" + PyxisTracking.pid; // ファイル名は「pid_[PID]」
		final String FORM_VAL_ELEMENTCLASS = "text_line";

		/** ################# tracelog ################# */
		PyxisUtils.recordLog(className,
				(new Throwable()).getStackTrace()[0].getMethodName(),
				(new Throwable()).getStackTrace()[0].getLineNumber(),
				"ソケット通信処理開始", 1, false);

		boolean res = false;

		try {

			// コネクション開始

			URL conUrl = new URL(url);

			HttpURLConnection conn = (HttpURLConnection) conUrl
					.openConnection();
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setUseCaches(false);
			conn.setConnectTimeout(3000);
			conn.setReadTimeout(3000);

			// ヘッダ設定
			conn.setRequestMethod(POST_METHOD);

			conn.setRequestProperty(CONNECTION, KEEP_ALIVE);
			conn.addRequestProperty("User-Agent", makeUserAgent());
			conn.setRequestProperty(CHARSET, UTF8);
			conn.setRequestProperty(CONTENT_TYPE, MULTIPART_BOUNDARY
					+ mBoundary);

			// 送信データの設定
			DataOutputStream os = new DataOutputStream(conn.getOutputStream());

			// テキストフィールド
			append_string(os, FORM_LBL_OUTENC, UTF8.toLowerCase());
			append_string(os, FORM_LBL_OUTFORMAT, FORM_VAL_FORMAT);
			append_string(os, FORM_LBL_ELEMENTCLASS, FORM_VAL_ELEMENTCLASS);
			append_string(os, "sz", Integer.toString(params.get("fs").length()));

			// ファイルフィールド
			os.writeBytes(twohyphens + mBoundary + end);
			os.writeBytes("Content-Disposition: form-data; name=" + dq
					+ FORM_LBL_USRFILE + dq + "; filename=" + dq
					+ FORM_VAL_FILE + dq + end);
			os.writeBytes("Content-Type: application/octet-stream" + end + end);
			os.write(params.get("fs").getBytes(), 0, params.get("fs").length());
			os.flush();
			os.writeBytes(end);
			os.writeBytes(twohyphens + mBoundary + twohyphens + end);

			// 結果受け取り
			final int responceCode = conn.getResponseCode();

			if (responceCode != HttpURLConnection.HTTP_OK) {
				throw new RuntimeException("invalid responce code, "
						+ responceCode);
			}

			// 受信データ処理
			int httpStatusCode = conn.getResponseCode();

			// HTTP Statusが200の場合、正常通信
			if (httpStatusCode == HttpStatus.SC_OK) {

				// レスポンスを取得
				InputStream is = conn.getInputStream();// POSTした結果を取得
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(is));
				// List<String> response = new ArrayList<String>();
				String resData = reader.readLine();

				// while ((resData = reader.readLine()) != null) {
				// response.add(resData);
				// }
				reader.close();

				// レスポンスデーターチェック
				if ("1".equals(resData)) {
					res = true;
				} else {
					/** ################# errorlog ################# */
					PyxisUtils.recordLog(className, (new Throwable())
							.getStackTrace()[0].getMethodName(),
							(new Throwable()).getStackTrace()[0]
									.getLineNumber(), "ソケット通信処理失敗[エラーレスポンス] : "
									+ resData, 2, false);
				}
			} else {
				/** ################# errorlog ################# */
				PyxisUtils.recordLog(className,
						(new Throwable()).getStackTrace()[0].getMethodName(),
						(new Throwable()).getStackTrace()[0].getLineNumber(),
						"ソケット通信処理失敗[HTTPステータスエラー] : " + httpStatusCode, 2,
						false);
			}
		} catch (Exception e) {
			/** ################# errorlog ################# */
			PyxisUtils.recordErrorLog(className,
					(new Throwable()).getStackTrace()[0].getMethodName(),
					(new Throwable()).getStackTrace()[0].getLineNumber(),
					"ソケット通信処理失敗[Exception] : ", 2, false, e);
		}
		/** ################# tracelog ################# */
		PyxisUtils.recordLog(className,
				(new Throwable()).getStackTrace()[0].getMethodName(),
				(new Throwable()).getStackTrace()[0].getLineNumber(),
				"ソケット通信処理終了", 1, false);

		return res;
	}

	private void append_string(DataOutputStream os, String name, String value)
			throws IOException {
		os.writeBytes(twohyphens + mBoundary + end);
		os.writeBytes("Content-Disposition: form-data; name=" + dq + name + dq
				+ end + end);
		os.writeBytes(value + end);
	}

	/**
	 * ブラウザからの起動かチェックする
	 * 
	 * @return true : ブラウザから起動 / false : それ以外
	 */
	protected boolean isStartupFromBrowser() {

		/** ################# tracelog 処理開始 ################# */
		PyxisUtils.recordLog(className,
				(new Throwable()).getStackTrace()[0].getMethodName(),
				(new Throwable()).getStackTrace()[0].getLineNumber(),
				"ブラウザ起動チェック処理開始", 1, false);

		boolean isTrue = false;

		try {

			Intent fromIntent = PyxisTracking.intent;

			// ブラウザーから起動された場合
			String action = fromIntent.getAction();
			if (Intent.ACTION_VIEW.equals(action)) {

				/** ################# tracelog ################# */
				PyxisUtils.recordLog(className,
						(new Throwable()).getStackTrace()[0].getMethodName(),
						(new Throwable()).getStackTrace()[0].getLineNumber(),
						"ブラウザ起動チェック[ブラウザから起動]", 1, false);

				Uri uri = fromIntent.getData();
				String q_sceme = "";
				String q_host = "";
				if (uri.getScheme() != null) {
					q_sceme = uri.getScheme();
				}
				if (uri.getHost() != null) {
					q_host = uri.getHost();
				}

				// schemeチェック
				if (PyxisTracking.scheme.equals(q_sceme)
						&& PyxisTracking.appHost.equals(q_host)) {
					isTrue = true;
					/** ################# tracelog ################# */
					PyxisUtils.recordLog(className, (new Throwable())
							.getStackTrace()[0].getMethodName(),
							(new Throwable()).getStackTrace()[0]
									.getLineNumber(), "ブラウザ起動チェック[ブラウザから起動]",
							1, false);
				}
			} else {
				/** ################# tracelog ################# */
				PyxisUtils.recordLog(className,
						(new Throwable()).getStackTrace()[0].getMethodName(),
						(new Throwable()).getStackTrace()[0].getLineNumber(),
						"ブラウザ起動チェック[通常起動]", 1, false);
			}
		} catch (Exception e) {
			isTrue = false;
			/** ################# errorlog 処理失敗 ################# */
			PyxisUtils.recordErrorLog(className,
					(new Throwable()).getStackTrace()[0].getMethodName(),
					(new Throwable()).getStackTrace()[0].getLineNumber(),
					"ブラウザ起動チェック失敗 : ", 2, false, e);
		}

		/** ################# tracelog 処理終了 ################# */
		PyxisUtils.recordLog(className,
				(new Throwable()).getStackTrace()[0].getMethodName(),
				(new Throwable()).getStackTrace()[0].getLineNumber(),
				"ブラウザ起動チェック処理終了", 1, false);

		return isTrue;
	}

	/**
	 * SDカードにsqliteファイルをコピー
	 * 
	 * @param fromContext
	 * @param intent
	 * @param srcFile
	 *            コピー元ファイル
	 * @param dstFile
	 *            コピー先ディレクトリ
	 */
	protected boolean copyDBtoExternalStorage() {

		// // PCでマウント中だったら強制的にマウント解除
		// if(Environment.MEDIA_SHARED.equals(Environment.getExternalStorageState())){
		// IMountService mountService =
		// IMountService.Stub.asInterface(fromContext.getSystemService(fromContext.));
		//
		// }

		boolean isSucceed = false;
		try {

			if (Environment.MEDIA_MOUNTED.equals(Environment
					.getExternalStorageState())) {

				String packageName = PyxisTracking.intent.getComponent()
						.getPackageName();

				String sqlitePath = "/data/data/" + packageName + "/databases/"
						+ PyxisConst.DB_NAME;

				File saveDir = new File(Environment
						.getExternalStorageDirectory().getPath()
						+ "/"
						+ packageName);
				saveDir.mkdirs();
				File saveFile = new File(saveDir.getPath() + "/"
						+ PyxisConst.DB_NAME);
				if (saveFile.exists()) {
					// 存在する場合は消去
					saveFile.delete();
				}
				saveFile.createNewFile();

				InputStream input = null;
				OutputStream output = null;
				input = new FileInputStream(new File(sqlitePath));
				output = new FileOutputStream(saveFile, true);

				int DEFAULT_BUFFER_SIZE = 1024 * 4;
				byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
				int n = 0;
				while (-1 != (n = input.read(buffer))) {
					output.write(buffer, 0, n);
				}
				input.close();
				output.close();

				isSucceed = true;
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return isSucceed;
	}

	/**
	 * sesidをファイルに書き込み
	 * 
	 * @param sesid
	 * @return 書き込みできたらtrue
	 */
	protected static boolean writeSesid(Context context, String sesid) {
		/** ################# tracelog 処理開始 ################# */
		PyxisUtils.recordLog(new PyxisUtils().getClass().getSimpleName(),
				(new Throwable()).getStackTrace()[0].getMethodName(),
				(new Throwable()).getStackTrace()[0].getLineNumber(),
				"sesid書き込み開始 : " + sesid, 1, false);

		try {
			FileOutputStream fileOutputStream = context.openFileOutput(
					"sesid.txt", Context.MODE_PRIVATE);
			fileOutputStream.write(sesid.getBytes());
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * sesidをファイルから読み込み
	 * 
	 * @return context
	 */
	protected static String readSesid(Context context) {
		try {
			FileInputStream fileInputStream;
			fileInputStream = context.openFileInput("sesid.txt");
			byte[] readBytes = new byte[fileInputStream.available()];
			fileInputStream.read(readBytes);
			String readString = new String(readBytes);
			/** ################# tracelog 処理開始 ################# */
			PyxisUtils.recordLog(new PyxisUtils().getClass().getSimpleName(),
					(new Throwable()).getStackTrace()[0].getMethodName(),
					(new Throwable()).getStackTrace()[0].getLineNumber(),
					"sesid読み込み : " + readString, 1, false);
			return readString;
		} catch (Exception e) {
			/** ################# errorlog ################# */
			PyxisUtils.recordErrorLog(new PyxisUtils().getClass()
					.getSimpleName(), (new Throwable()).getStackTrace()[0]
					.getMethodName(), (new Throwable()).getStackTrace()[0]
					.getLineNumber(), "sesid読み込み失敗 : ", 2, false, e);
			return null;
		}
	}

	/**
	 * referrer情報をファイルに書き込み
	 * 
	 * @param sesid
	 * @param verify
	 * @return 書き込みできたらtrue
	 */
	protected static boolean writeReferrer(Context context, String sesid,
			String verify) {
		/** ################# tracelog 処理開始 ################# */
		PyxisUtils
				.recordLog(new PyxisUtils().getClass().getSimpleName(),
						(new Throwable()).getStackTrace()[0].getMethodName(),
						(new Throwable()).getStackTrace()[0].getLineNumber(),
						"referrer情報書き込み開始 : sesid = " + sesid + " / verify = "
								+ verify, 1, false);

		try {
			FileOutputStream fileOutputStream = context.openFileOutput(
					"referrer.txt", Context.MODE_PRIVATE);
			fileOutputStream.write(sesid.getBytes());
			fileOutputStream.write("&".getBytes());
			fileOutputStream.write(verify.getBytes());
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * referrerをファイルから読み込み
	 * 
	 * @return String[sesid][verify]
	 */
	protected static String[] readReferrer(Context context) {
		try {
			FileInputStream fileInputStream;
			fileInputStream = context.openFileInput("referrer.txt");
			byte[] readBytes = new byte[fileInputStream.available()];
			fileInputStream.read(readBytes);
			String readString = new String(readBytes);
			/** ################# tracelog 処理開始 ################# */
			PyxisUtils.recordLog(new PyxisUtils().getClass().getSimpleName(),
					(new Throwable()).getStackTrace()[0].getMethodName(),
					(new Throwable()).getStackTrace()[0].getLineNumber(),
					"referrer情報読み込み : " + readString, 1, false);
			// 取得したreferrer情報をsesidとverifyに分割
			String[] referrerArr = readString.split("&");
			return referrerArr;
		} catch (Exception e) {
			/** ################# errorlog ################# */
			PyxisUtils.recordErrorLog(new PyxisUtils().getClass()
					.getSimpleName(), (new Throwable()).getStackTrace()[0]
					.getMethodName(), (new Throwable()).getStackTrace()[0]
					.getLineNumber(), "verify読み込み失敗 : ", 2, false, e);
			return null;
		}
	}

	/**
	 * エラーログを記録
	 * 
	 * @param className
	 * @param methodName
	 * @param raw
	 * @param message
	 * @param mode
	 * @param isInit
	 * @param e
	 */
	protected static void recordErrorLog(String className, String methodName,
			int raw, String message, int mode, boolean isInit, Exception e) {

		PyxisConst pyxisConst = new PyxisConst();

		StringBuilder exception = new StringBuilder();
		if (e != null && e.getStackTrace().length > 0) {

			exception.append(pyxisConst.CODE_LF).append("[StackTraceSection]")
					.append(pyxisConst.CODE_LF);

			for (int i = 0; i < e.getStackTrace().length; i++) {
				exception.append(e.getStackTrace()[i].toString()).append(
						pyxisConst.CODE_LF);
			}
		}
		recordLog(className, methodName, raw, message + exception, mode, isInit);
		if (e != null) {
			e.printStackTrace();
		}
	}

	/**
	 * ログを記録
	 * 
	 * @param fileName
	 * @param className
	 * @param methodName
	 * @param raw
	 * @param message
	 * @param mode
	 *            1:traceLog / 1以外:errorLog
	 * @param isInit
	 *            初期化モード
	 * @param mode
	 *            1:traceLog / 1以外:errorLog
	 */
	protected static void recordLog(String className, String methodName,
			int raw, String message, int mode, boolean isInit) {

		PyxisConst pyxisConst = new PyxisConst();

		// ログモードが無効な場合は記録しない
		if (PyxisTracking.logMode != null && !PyxisTracking.logMode) {
			return;
		}

		StringBuilder sb = new StringBuilder(512);

		sb.append(className).append(" ");
		sb.append(methodName).append(" ");
		sb.append(Integer.toString(raw)).append(" ");
		sb.append(message);

		if (sb.length() > 0) {
			// LogCat
			if (mode == 0) {
				Log.d(pyxisConst.LOG_SUCCESS_TAG, sb.toString());
			} else if (mode == 1) {
				Log.d(pyxisConst.LOG_TRACE_TAG, sb.toString());
			} else if (mode == 2) {
				Log.e(pyxisConst.LOG_ERROR_TAG, sb.toString());
			}

			// SDカードが有効な場合はログ出力
			if (PyxisTracking.enableSDCard != null
					&& PyxisTracking.enableSDCard) {
				if (!logWriter(sb.toString(), mode, isInit)) {
					Log.d(pyxisConst.LOG_TRACE_TAG, "SDカードへの出力に失敗しました。");
				}
			}

		}
	}

	/**
	 * ログをSDカードに保存
	 * 
	 * @param log
	 * @param mode
	 *            1:traceLog / 1以外:errorLog
	 * @param isInit
	 *            初期化モード
	 * @return true:writed / false:can't writed
	 */
	private static boolean logWriter(String log, int mode, boolean isInit) {

		PyxisConst pyxisConst = new PyxisConst();

		boolean isSucceed = false;

		try {

			if (Environment.MEDIA_MOUNTED.equals(Environment
					.getExternalStorageState())) {

				String packageName = PyxisTracking.intent.getComponent()
						.getPackageName();

				File saveDir = new File(Environment
						.getExternalStorageDirectory().getPath()
						+ "/"
						+ packageName);
				saveDir.mkdirs();
				File saveFile = new File(saveDir.getPath() + "/"
						+ pyxisConst.LOG_FILE_NAME);

				StringBuilder sb = new StringBuilder();

				// 初期化フラグがtrueの場合は初期化
				if (isInit) {
					saveFile.delete();
				}

				// ファイルが存在しない場合
				if (!saveFile.exists()) {

					// ファイル新規作成
					saveFile.createNewFile();

					// ヘッダ出力
					// header
					sb.append("[head]").append(pyxisConst.CODE_LF);
					sb.append("SDK Version : " + pyxisConst.SDK_VERSION)
							.append(pyxisConst.CODE_LF);
					sb.append(pyxisConst.CODE_LF);
				}

				BufferedWriter bufferedWriterObj = null;
				// ファイル出力ストリームの作成
				bufferedWriterObj = new BufferedWriter(new OutputStreamWriter(
						new FileOutputStream(saveFile, true), "UTF-8"));

				SimpleDateFormat dateFormat = new SimpleDateFormat(
						"yyyy/MM/dd HH:mm:ss.SSS");
				String dateStr = dateFormat.format(System.currentTimeMillis());

				if (!TextUtils.isEmpty(log)) {

					if (mode == 0) {
						// successlog
						sb.append("[success] : ");
					} else if (mode == 1) {
						// tracelog
						sb.append("[trace] : ");
					} else if (mode == 2) {
						// errorlog
						sb.append("[error] : ");
					}
					sb.append(dateStr).append(pyxisConst.CODE_TAB);
					sb.append(log).append(pyxisConst.CODE_LF);
				}

				String logStr = null;
				if (sb.length() > 0) {
					logStr = sb.toString();
				}

				bufferedWriterObj.write(logStr);
				bufferedWriterObj.flush();
				bufferedWriterObj.close();

				isSucceed = true;
			} else {
				Log.d(pyxisConst.LOG_ERROR_TAG,
						"SDカードへの出力が出来ません。理由：SDカードが存在しない。又はSDカードがPCにマウントされている等");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return isSucceed;
	}

	/**
	 * 必要な処理を行ってからプロセスを落とす
	 * 
	 */
	protected static void processKiller() {

		try {

			// 原因追求のため可能な場合はsqliteファイルをSDカードにコピー
			PyxisUtils pyxisUtils = new PyxisUtils();
			pyxisUtils.copyDBtoExternalStorage();

			/** ################# errorlog ################# */
			PyxisUtils.recordLog(new PyxisUtils().getClass().getSimpleName(),
					(new Throwable()).getStackTrace()[0].getMethodName(),
					(new Throwable()).getStackTrace()[0].getLineNumber(),
					"エラーによりプロセス終了 ", 2, false);
		} catch (Exception e) {

		} finally {
			Process.killProcess(Process.myPid());
		}
	}

	/**
	 * UUIDの生成を行う
	 */
	protected static String generateUuid() {

		// UUID生成
		String uuid = UUID.randomUUID().toString();

		// MD5ハッシュ
		return md5(uuid);
	}

	/**
	 * 文字列をMD5でハッシュする
	 * 
	 * @param str
	 */
	protected static String md5(String str) {

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
					sb.append("0"
							+ Integer.toHexString((0xff & messageDigest[i])));
				} else {
					sb.append(Integer.toHexString((0xff & messageDigest[i])));
				}
			}

			return sb.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			/** ################# errorlog ################# */
			PyxisUtils.recordLog(new PyxisUtils().getClass().getSimpleName(),
					(new Throwable()).getStackTrace()[0].getMethodName(),
					(new Throwable()).getStackTrace()[0].getLineNumber(),
					"MD5ハッシュ失敗 ", 2, false);
		}

		return "";
	}

	/**
	 * インストール計測、アプリ計測で送信するパラメータfidを生成する
	 * 
	 * @param aes_key
	 */
	protected String generateFid(String aes_key) {
		String fidStr = "";

		// Manifestファイルで端末ID取得設定がされている場合のみfid生成
		if (PyxisTracking.aidGet) {
			// アンドロイドIDを取得して、[aid=XXXXXXX]の形式にする
			fidStr += "aid=" + getAndroidId() + "&";
		}
		// App成果送信の制限モードを追記
		fidStr += "lm="
				+ base64Encode(String.valueOf(
						PyxisTracking.appLimitMode.getVal()).getBytes()) + "&";
		// FBイベント送信パラメータ
		PyxisFbEventParameterConfig.validate();
		fidStr += "fb=" + PyxisFbEventParameterConfig.createParamStr();
		// AES128で暗号化したものをBase64エンコードして返す
		return base64Encode(encodeAES128(fidStr.getBytes(), aes_key.getBytes()));
	}

	/**
	 * オプトアウト設定
	 * 
	 * @param optOutFlg
	 *            trueの場合、オプトアウトする falseの場合、オプトアウトしない
	 */
	protected static void setOptOut(boolean optOutFlg) {

		// boolean→Stringへ変換
		String optOut = boolOptOut2Str(optOutFlg);

		// userテーブルを更新
		PyxisDBHelper dbHelper = new PyxisDBHelper();
		dbHelper.updUser(null, null, null, null, optOut);
		dbHelper.close();

		/** ################# tracelog 処理終了 ################# */
		recordLog(new PyxisUtils().getClass().getSimpleName(),
				(new Throwable()).getStackTrace()[0].getMethodName(),
				(new Throwable()).getStackTrace()[0].getLineNumber(),
				"[オプトアウト設定変更処理完了] application_tracking_enabledを" + optOut
						+ "に更新。", 1, false);

	}

	/**
	 * オプトアウト情報参照
	 */
	private static String getOptOut() {
		// DBからユーザー情報を取得
		PyxisDBHelper dbHelper = new PyxisDBHelper();
		HashMap<String, Object> userMap = dbHelper.selUser();
		dbHelper.close();

		/** ################# tracelog 処理終了 ################# */
		recordLog(
				new PyxisUtils().getClass().getSimpleName(),
				(new Throwable()).getStackTrace()[0].getMethodName(),
				(new Throwable()).getStackTrace()[0].getLineNumber(),
				"[オプトアウト設定参照処理完了] application_tracking_enabledは "
						+ String.valueOf(userMap.get("OPTOUT")), 1, false);

		// 取得した値に対応するstring値を返す
		return (userMap.get("OPTOUT") == null) ? "" : String.valueOf(userMap
				.get("OPTOUT"));
	}

	/**
	 * オプトアウト値を取得し、String型で返す
	 * 
	 * @return String 現在のオプトアウト設定値
	 */
	protected static String getStrOptOut() {
		return getOptOut();
	}

	/**
	 * オプトアウト値を取得し、boolean型で返す
	 * 
	 * @return boolean 現在のオプトアウト設定値
	 */
	protected static boolean getBoolOptOut() {
		return stringOptOut2bool(getOptOut());
	}

	/**
	 * booleanのオプトアウト値を、DBに格納する際のStringに変換する。
	 */
	private static String boolOptOut2Str(boolean optOut) {
		return (optOut) ? "0" : "1";
	}

	/**
	 * stringのオプトアウト値を、booleanに変換する。
	 */
	private static boolean stringOptOut2bool(String optOut) {
		return optOut.equals("0");
	}

	/**
	 * referrer計測時に使用するパラメータir_idを生成する
	 * 
	 * @return ir_id:ir + UUID
	 */
	protected static String generateIrId() {

		// UUID生成
		String irId = "ir" + UUID.randomUUID().toString();

		/** ################# tracelog 処理終了 ################# */
		recordLog(new PyxisUtils().getClass().getSimpleName(),
				(new Throwable()).getStackTrace()[0].getMethodName(),
				(new Throwable()).getStackTrace()[0].getLineNumber(),
				"ir_id生成完了。ir_id: " + irId, 1, false);

		return irId;
	}

	/**
	 * ir_idをSharedPreferenceから取得。 存在しない場合は生成する。
	 * 
	 * @param アプリケーションコンテキスト
	 * @return ir_id
	 */
	protected static String getIrId(Context context) {

		// SharedPreferenceからir_id取得
		SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(context);
		String irId = sharedPreferences.getString(PyxisConst.PREF_PYXIS_IR_ID,
				"");

		if (irId.isEmpty()) {
			// SharedPreferenceに存在しない場合は、生成する
			irId = generateIrId();

			// 生成したir_idをSharedPreferenceに保存しておく
			Editor editor = sharedPreferences.edit();
			editor.putString(PyxisConst.PREF_PYXIS_IR_ID, irId);
			editor.commit();
		}

		/** ################# tracelog 処理終了 ################# */
		recordLog(new PyxisUtils().getClass().getSimpleName(),
				(new Throwable()).getStackTrace()[0].getMethodName(),
				(new Throwable()).getStackTrace()[0].getLineNumber(),
				"ir_id取得完了。ir_id: " + irId, 1, false);

		return irId;
	}

	protected static String utf8Encode(String src) {

		String result = "";

		if (src != null && !src.isEmpty()) {
			try {
				result = new String(src.getBytes("UTF-8"), "UTF-8");
			} catch (UnsupportedEncodingException e) {
				// 文字コードべた書きなのでおこらない
				e.printStackTrace();
			}
		}
		/** ################# tracelog 処理終了 ################# */
		recordLog(new PyxisUtils().getClass().getSimpleName(),
				(new Throwable()).getStackTrace()[0].getMethodName(),
				(new Throwable()).getStackTrace()[0].getLineNumber(),
				"文字コードをUTF-8に変換:" + result, 1, false);
		return result;
	}

	/**
	 * getSHA256
	 * 
	 * @param data
	 * @return String
	 */
	protected static String getSHA256(String data) {

		if (TextUtils.isEmpty(data)) {
			return "";
		}

		final String HASH_TYPE = "SHA-256";

		SecretKeySpec secretKeySpec = new SecretKeySpec(data.getBytes(),
				HASH_TYPE);

		try {

			MessageDigest md = MessageDigest.getInstance(HASH_TYPE);
			md.update(data.getBytes());
			byte[] bytes = md.digest();

			StringBuilder sb = new StringBuilder();
			for (byte b : bytes) {
				String hex = String.format("%02x", b);
				sb.append(hex);
			}
			return sb.toString();

		} catch (NoSuchAlgorithmException e) {

			String message = "SHA-256ハッシュ失敗. 理由[指定アルゴリズム不正]. trace = "
					+ e.getStackTrace().toString();

			/** ################# errorlog ################# */
			recordLog(new PyxisUtils().getClass().getSimpleName(),
					(new Throwable()).getStackTrace()[0].getMethodName(),
					(new Throwable()).getStackTrace()[0].getLineNumber(),
					message, 2, false);

		}

		return "";
	}

	/**
	 * 配列の文字列結合
	 * 
	 * @param arry
	 * @param with
	 * @return
	 */
	public static String join(String[] arry, String with) {
		StringBuffer buf = new StringBuffer();
		for (String s : arry) {
			if (buf.length() > 0) {
				buf.append(with);
			}
			buf.append(s);
		}
		return buf.toString();
	}

}
