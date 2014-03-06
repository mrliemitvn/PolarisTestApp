package jp.co.septeni.pyxis.PyxisTracking;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Build.VERSION;

public class PyxisReceiver extends BroadcastReceiver {
	static String TAG = PyxisReceiver.class.getSimpleName();
	private static String className = new PyxisReceiver().getClass()
			.getSimpleName();
	PyxisUtils pyxisUtils = new PyxisUtils();

	@Override
	public void onReceive(Context context, Intent intent) {

		// Processing Install_Referrer event
		handleEvent(context, intent);

		// Pass event to other sdk and call onReceive event to process event
		try {
			ArrayList<String> classNames = getSdkClassesInManifestFile(context);

			if (classNames != null && classNames.size() > 0) {
				for (String className : classNames) {
					((BroadcastReceiver) Class.forName(className).newInstance())
							.onReceive(context, intent);
				}
			}
		} catch (Exception e) {
			PyxisUtils.recordLog(className,
					(new Throwable()).getStackTrace()[0].getMethodName(),
					(new Throwable()).getStackTrace()[0].getLineNumber(),
					"Error in invoking others sdk", 2, false);
		}
	}

	/*
	 * Get SDK list in Manifest file (other sdks putted in <meta-data> tag of
	 * <receiver>)
	 *
	 * @param Context: context of BroadcastReceiver
	 *
	 * @return ArrayList<String> : array of class handling event of other sdk
	 */
	private ArrayList<String> getSdkClassesInManifestFile(Context context) {
		ArrayList<String> classNames = new ArrayList<String>();
		ComponentName receiverComp = new ComponentName(context,
				PyxisReceiver.class);

		try {
			// Get meta data
			ActivityInfo ai = context.getPackageManager().getReceiverInfo(
					receiverComp, PackageManager.GET_META_DATA);

			// extract meda-data
			Bundle bundle = ai.metaData;
			if (bundle == null) {
				return null;
			}
			Set<String> keys = bundle.keySet();

			// iterate through all metadata tags
			Iterator<String> it = keys.iterator();
			String key;
			String className;

			while (it.hasNext()) {
				key = it.next();
				className = bundle.getString(key);
				classNames.add(className);
			}
		} catch (Exception e) {
			PyxisUtils.recordLog(className,
					(new Throwable()).getStackTrace()[0].getMethodName(),
					(new Throwable()).getStackTrace()[0].getLineNumber(),
					"Error in geting SDK list", 2, false);
		}
		return classNames;
	}

	/*
	 * Handle event Install_Referrer of Pyxis SDK (including write session_id to
	 * file and send install tracking)
	 *
	 * @param Context: context of BroadcastReceiver
	 *
	 * @param Intent: intent of BroadcastReceiver
	 */
	private void handleEvent(Context context, Intent intent) {

		// referrerを取得
		String referrerStr = null;
		String referrer = null;
		try {
			referrer = intent.getStringExtra("referrer");
			if (referrer != null && !referrer.isEmpty()) {
				referrerStr = URLDecoder.decode(
						intent.getStringExtra("referrer"), "UTF-8");
				referrerStr = new String(pyxisUtils.base64Decode(referrerStr),
						"UTF-8");
			}

			// referrerを取得できた場合のみ、ファイルに書き込み
			if (referrerStr != null && !referrerStr.isEmpty()) {
				// 取得したreferrerをパースしてMapに格納
				String[] referrerListArr = referrerStr.split("&");
				Map<String, String> referrerMap = new HashMap<String, String>();
				for (String str : referrerListArr) {
					String[] reffererArr = str.split("=");
					referrerMap.put(reffererArr[0], reffererArr[1]);
				}

				String sesid = referrerMap.get("sesid");
				String verify = referrerMap.get("verify");

				PyxisUtils.recordLog(className,
						(new Throwable()).getStackTrace()[0].getMethodName(),
						(new Throwable()).getStackTrace()[0].getLineNumber(),
						"sesid = " + sesid + "verify = " + verify, 1, false);
				PyxisUtils.writeReferrer(context, sesid, verify);
			}

			// 3.1以上の場合はReceive経由で計測
			// 3.1未満はsesidだけ保存し、計測はonCreateのtrackInstallから
			if (Build.VERSION.SDK_INT >= 12) {
				// CPI計測
				PyxisTracking.init(context, intent, null, null);
				PyxisTracking.trackInstall(PyxisTracking.pyxisMv,
						PyxisTracking.pyxisSuid, PyxisTracking.pyxisSales,
						PyxisTracking.pyxisVolume, PyxisTracking.pyxisProfit,
						PyxisTracking.pyxisOthers,
						true);
			}
		} catch (Exception e) {
			PyxisUtils.recordLog(className,
					(new Throwable()).getStackTrace()[0].getMethodName(),
					(new Throwable()).getStackTrace()[0].getLineNumber(),
					"インストールリファラーの取得に失敗。rerrefer：" + referrer, 2, false);
			e.printStackTrace();
		}
	}
}
