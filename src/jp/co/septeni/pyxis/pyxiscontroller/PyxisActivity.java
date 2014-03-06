package jp.co.septeni.pyxis.pyxiscontroller;

import android.os.Bundle;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;
import com.unity3d.player.UnityPlayerActivity;
import jp.co.septeni.pyxis.PyxisTracking.PyxisTracking;

/**
 * PyxisActivityはPyxisライブラリをUnity3.5.xで動作させるためのアクティビティクラスです。
 * UnityPlayerActivityから派生させています。
 * @author 株式会社セプテーニ
 * @version 1.0.0
 * @see UnityPlayerActivity
 */
public class PyxisActivity extends UnityPlayerActivity {

	///////////////////////////////////////////////////////////////////////////
	// Debug utility Part
	///////////////////////////////////////////////////////////////////////////

	/**
	 * デバッグモード判定用フラグです。
	 * 真の場合にのみデバッグメッセージを表示します。
	 * @since 1.0
	 * @see CheckDebugFg
	 * @see i
	 * @see d
	 * @see w
	 */
	private static boolean isDebug = true;

	/**
	 * AndroidManifest.xml上のdebuggableを見て表示コントロールを決定するメソッドです。
	 * onCreateで呼び出すことにより、その後の動作でデバッグ表示を制御可能になります。
	 * @since 1.0
	 * @see AndroidManifest.xml
	 * @see isDebug
	 */
	private void CheckDebugFg() {
		// AndroidManifest.xmlに定義された情報を取り出します。
		ApplicationInfo appliInfo = null;
		try {
			appliInfo =
				getPackageManager().getApplicationInfo(
										getPackageName(),
										0);

			// <application android:debuggable="true" android:icon="@drawable/icon" android:label="@string/app_name">
			// 上記タグのdebuggableを見て判定します。
			PyxisActivity.isDebug = ((appliInfo.flags & ApplicationInfo.FLAG_DEBUGGABLE) == ApplicationInfo.FLAG_DEBUGGABLE);
		}
		catch (NameNotFoundException e) {
			// 情報が無い場合は強制的にリリースモードとみなします。
			PyxisActivity.isDebug = false;
		}
	}

	/**
	 * Log.iをデバッグ制御するようにしたメソッドです。
	 * @since 1.0
	 * @param tag
	 * @param msg
	 * @return iの戻り値をそのまま戻します。
	 */
	protected static int i(final String tag, final String msg) {
		if (isDebug) {
			return Log.i(tag, msg);
		}
		return 0;
	}

	/**
	 * Log.dをデバッグ制御するようにしたメソッドです。
	 * @since 1.0
	 * @param tag
	 * @param msg
	 * @return dの戻り値をそのまま戻します。
	 */
	protected static int d(final String tag, final String msg) {
		if (isDebug) {
			return Log.d(tag, msg);
		}
		return 0;
	}

	/**
	 * Log.wをデバッグ制御するようにしたメソッドです。
	 * @since 1.0
	 * @param tag
	 * @param msg
	 * @return wの戻り値をそのまま戻します。
	 */
	protected static int w(final String tag, final String msg) {
		if (isDebug) {
			return Log.w(tag, msg);
		}
		return 0;
	}

	/**
	 * Log.eをデバッグ制御するようにしたメソッドです。
	 * @since 1.0
	 * @param tag
	 * @param msg
	 * @return eの戻り値をそのまま戻します。
	 */
	protected static int e(final String tag, final String msg) {
		if (isDebug) {
			return Log.e(tag, msg);
		}
		return 0;
	}

	///////////////////////////////////////////////////////////////////////////
	// Pyxis Library Part
	///////////////////////////////////////////////////////////////////////////

	/**
	 * Pyxisの初期化メソッドが呼ばれたかどうかを記憶します。
	 * 真の場合は初期化メソッドが（内部的にエラーかどうかは別として）呼ばれたことを意味します。
	 * 呼ばれていない場合、このクラスで追加されたPyxis制御メソッド呼び出しの内部処理を抑制します。
	 * @since 1.0
	 * @see onCreate
	 */
	private static boolean isInitialized;

	/**
	 * UnityPlayerActivityのonCreateオーバーライド実装です。
	 * AndroidManifest.xmlのタグによるデバッグ表示フラグ決定とPyxisプラグインの初期化を行なっています。
	 * xmlの設定が正しく行われていない場合、初期化を行いません。
	 * 失敗した場合は以降の呼び出しを全てスルーするようにしてあります。
	 * @since 1.0
	 * @param savedInstanceState
	 * @see CheckDebugFg
	 * @see isInitialized
	 */
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		// Unityの特性上、基底クラスは最初に呼び出す必要があります。
//		super.onCreate(savedInstanceState);
//
//		// 初期化メソッドコールフラグを設定します。
//		isInitialized = false;
//
//		// デバッグフラグをManifestから取り出します。
//		CheckDebugFg();
//
//		// AndroidManifest.xmlに定義された情報を読み出します。
//		ApplicationInfo appliInfo = null;
//		try {
//			appliInfo =
//				getPackageManager().getApplicationInfo(
//										getPackageName(),
//										PackageManager.GET_META_DATA);
//		}
//		catch (NameNotFoundException e) {
//			e("PyxisActivity", "AndroidManifestにMeta定義がありません。");
//			// 失敗した場合は念のため戻します。
//			return;
//		}
//
//		// schemeとhostの文字列をチェックします。
//		String scheme = appliInfo.metaData.getString("scheme");
//		String host = appliInfo.metaData.getString("host");
//		if (scheme != null && !scheme.isEmpty()) {
//			if (host != null && !host.isEmpty()) {
//
//				i("PyxisActivity scheme=", scheme);
//				i("PyxisActivity host=", host);
//
//				// 両方が揃っている場合に限りinitを呼び出します。
//				PyxisTracking.init(
//						getApplication(),
//						getIntent(),
//						scheme,
//						host
//					);
//				// 初期化済フラグを真に設定します。
//				isInitialized = true;
//				i("PyxisActivity", "PyxisTracking::init をコールしました。");
//			}
//			else {
//				e("PyxisActivity", "AndroidManifestのhostが空文字またはnullです。");
//			}
//		}
//		else {
//			e("PyxisActivity", "AndroidManifestのschemeが空文字またはnullです。");
//		}
//	}
//
//	/**
//	 * PyxisライブラリのtrackInstallを呼び出します。
//	 * onCreateにおいて初期化に必要な情報が揃っていなかった場合は処理を行いません。
//	 * @since 1.0
//	 * @param mv
//	 * @param suid
//	 * @param sales
//	 * @param volume
//	 * @param profit
//	 * @param others
//	 * @see onCreate
//	 */
//	public static void trackInstall(
//			String mv,
//			String suid,
//			String sales,
//			String volume,
//			String profit,
//			String others) {
//		i("PyxisActivity", "trackInstallメソッド開始しました。");
//
//// 初期化済フラグで挙動を制御します。Nullが入る可能性があります。
//if (isInitialized) {
//PyxisTracking.trackInstall(
//			mv,
//			suid,
//			sales,
//			volume,
//			profit,
//			others);
//i("PyxisActivity", "PyxisTracking::trackInstall をコールしました。");
//}
//else {
//w("PyxisActivity", "PyxisTracking::trackInstall initが成功していないため呼び出しません。");
//}
//i("PyxisActivity", "trackInstallメソッド終了しました。");
//}
//
//	/**
//	 * saveTrackApp
//	 * PyxisライブラリのsaveTrackAppを呼び出します。
//	 * onCreateにおいて初期化に必要な情報が揃っていなかった場合は処理を行いません。
//	 * @since 1.0
//	 * @param pyxisMv
//	 * @param suid
//	 * @param sales
//	 * @param volume
//	 * @param profit
//	 * @param others
//	 * @see onCreate
//	 */
//	public static void saveTrackApp(
//							String pyxisMv,
//							String suid,
//							String sales,
//							String volume,
//							String profit,
//							String others) {
//		// sales
//		Integer iSales = null;
//		if ((sales == null) || (1 > sales.length())) {
//			// Nullポインタで渡します。
//		}
//		else {
//			iSales = new Integer(sales);
//		}
//		// volume
//		Integer iVolume = null;
//		if ((volume == null) || (1 > volume.length())) {
//			// Nullポインタで渡します。
//		}
//		else {
//			iVolume = new Integer(volume);
//		}
//		// profit
//		Integer iProfit = null;
//		if ((profit == null) || (1 > profit.length())) {
//			// Nullポインタで渡します。
//		}
//		else {
//			iProfit = new Integer(profit);
//		}		// 初期化済フラグで挙動を制御します。
//		// Pyxisクラスを呼び出します。Nullが入る可能性があります。
//		if (isInitialized) {
//			PyxisTracking.saveTrackApp(
//							pyxisMv,
//							suid,
//							iSales,
//							iVolume,
//							iProfit,
//							others);
//			i("PyxisActivity", "PyxisTracking::saveTrackApp をコールしました。");
//		}
//		else {
//			w("PyxisActivity", "PyxisTracking::saveTrackApp initが成功していないため呼び出しません。");
//		}
//	}
//
//	/**
//	 * sendTrackApp
//	 * PyxisライブラリのsendTrackAppを呼び出します。
//	 * onCreateにおいて初期化に必要な情報が揃っていなかった場合は処理を行いません。
//	 * @since 1.0
//	 * @see onCreate
//	 */
//	public void sendTrackApp() {
//	    runOnUiThread(new Runnable() {
//	        public void run() {
//	    		// 初期化済フラグで挙動を制御します。
//	    		if (isInitialized) {
//	    			PyxisTracking.sendTrackApp();
//	    			i("PyxisActivity", "PyxisTracking::sendTrackApp をコールしました。");
//	    		}
//	    		else {
//	    			w("PyxisActivity", "PyxisTracking::sendTrackApp initが成功していないため呼び出しません。");
//	    		}
//	        }
//	    });
//
//	}
//
//	/**
//	 * copyDBtoExternalStorage
//	 * saveTrackAppで保存したDBをSDカードにコピー保存します。
//	 * @since 1.0
//	 */
//	public static void copyDBtoExternalStorage() {
//		// 初期化済フラグで挙動を制御します。
//		if (isInitialized) {
//			PyxisTracking.copyDBtoExternalStorage();
//			i("PyxisActivity", "PyxisTracking::copyDBtoExternalStorage をコールしました。");
//		}
//		else {
//			w("PyxisActivity", "PyxisTracking::copyDBtoExternalStorage initが成功していないため呼び出しません。");
//		}
//	}
}
