package jp.co.septeni.pyxis.pyxiscontroller;

import java.lang.reflect.Field;

import org.apache.http.util.EncodingUtils;

import android.R.bool;
import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.util.Xml.Encoding;
import jp.co.septeni.pyxis.PyxisTracking.PyxisFbEventParameterConfig;
import jp.co.septeni.pyxis.PyxisTracking.PyxisTracking;
import jp.co.septeni.pyxis.PyxisTracking.PyxisTracking.AppLimitMode;

/**
 * プラグインサポートクラスサンプル
 * @author ANGEWORK
 */
public class PyxisPlugin {

	private static Activity m_activity;
	private Class<?> m_unityplayer;
	private Field m_unityplayer_activity_field;

	/**
	 * ctor
	 */
	public PyxisPlugin() {
		try {
			//
			this.m_unityplayer = Class.forName("com.unity3d.player.UnityPlayer");
			this.m_unityplayer_activity_field = this.m_unityplayer.getField("currentActivity");
		}
		catch (ClassNotFoundException e) {
			PyxisActivity.i("PyxisPlugin", "UnityPlayerがありません" + e.getMessage());
		}
		catch (NoSuchFieldException e) {
			PyxisActivity.i("PyxisPlugin", "currentActivity fieldがありません " + e.getMessage());
		}
		catch (Exception e) {
			PyxisActivity.i("PyxisPlugin", "例外が発生しました" + e.getMessage());
		}
		// Pyxisを開始します。
		this.startPyxisActivity();
	}

	/*
	 * 直接呼び出しても問題ないか、別途呼び出しが良いか要検討です。
	 */
	public void startPyxisActivity()
	{
		String scheme = null;
		String host = null;

		// AndroidManifest.xmlに定義された情報を読み出します。
		ApplicationInfo appliInfo = null;
		try {
			appliInfo =
					getCurrentActivity().getPackageManager().getApplicationInfo(
							getCurrentActivity().getPackageName(),
							PackageManager.GET_META_DATA);
			// schemeとhostの文字列をチェックします。
			scheme = appliInfo.metaData.getString("scheme");
			host = appliInfo.metaData.getString("host");
		}
		catch (NameNotFoundException e) {
			PyxisActivity.e("PyxisActivity", "AndroidManifestにMeta定義がありません。");
			// 失敗した場合は念のため戻します。
		}
		catch (Exception e) {
			PyxisActivity.e("PyxisActivity", "startPyxisActivityでエラー : " + e.getMessage());
		}

		try {
			// Pyxisを初期化します。
			Intent intent = getCurrentActivity().getIntent();
			Application app = getCurrentActivity().getApplication();
			PyxisTracking.init(
					app,
					intent,
					scheme,
					host
					);
			isInitialized = true;
		}
		catch(Exception e) {
			// Catchはいい加減です、大変申し訳ございません。
		}
	}

	/**
	 * カレントアクティビティを返します。
	 * UnityまたはUnity派生のお客様環境独自になりますがどちらでも大丈夫かと思われます。
	 * @return
	 */
	protected Activity getCurrentActivity() {
		if (this.m_unityplayer_activity_field != null) {
			try {
				m_activity = (Activity)this.m_unityplayer_activity_field.get(this.m_unityplayer);
				if (m_activity == null) {
					PyxisActivity.e("PyxisActivity", "アクティビティがヌルでした");
				}
	    	}
	    	catch (Exception e) {
	    		PyxisActivity.i("PyxisActivity", "アクティビティ取得失敗 " + e.getMessage());
	    	}
		}
		return m_activity;
	}

	private static boolean isDebug = true;
	protected static int i(final String tag, final String msg) {
		if (isDebug) {
			return Log.i(tag, msg);
		}
		return 0;
	}
	protected static int d(final String tag, final String msg) {
		if (isDebug) {
			return Log.d(tag, msg);
		}
		return 0;
	}
	protected static int w(final String tag, final String msg) {
		if (isDebug) {
			return Log.w(tag, msg);
		}
		return 0;
	}
	protected static int e(final String tag, final String msg) {
		if (isDebug) {
			return Log.e(tag, msg);
		}
		return 0;
	}
	private static boolean isInitialized;

	private static String mv;
	private static String sales;
	private static String suid;
	private static String volume;
	private static String profit;
	private static String others;

	public static void trackInstall(
							String mv,
							String suid,
							String sales,
							String volume,
							String profit,
							String others) {
		i("PyxisActivity", "trackInstallメソッド開始したのだ(´・ω・｀)");
		// 初期化済フラグで挙動を制御します。Nullが入る可能性があります。

		PyxisPlugin.mv = mv;
		PyxisPlugin.suid = suid;
		PyxisPlugin.sales = sales;
		PyxisPlugin.volume = volume;
		PyxisPlugin.profit = profit;
		PyxisPlugin.others = others;

		// 代わりにRunnableで実行

		Handler handler = new Handler(Looper.getMainLooper());

		handler.post(new Runnable() {
	        public void run() {
	    		// 初期化済フラグで挙動を制御します。
	    		if (isInitialized) {
	    			i("PyxisActivity", "PyxisTracking::trackInstall をRunnableでコールしました。");
	    			PyxisTracking.trackInstall(
	    					PyxisPlugin.mv,
	    					PyxisPlugin.suid,
	    					PyxisPlugin.sales,
	    					PyxisPlugin.volume,
	    					PyxisPlugin.profit,
	    					PyxisPlugin.others);
	    		}
	    		else {
	    			w("PyxisActivity", "PyxisTracking::trackInstall initが成功していないため呼び出しません。");
	    		}
	        }
	    });
		i("PyxisActivity", "trackInstallメソッド終了しました。");
	}
	public static void saveTrackApp(
							String pyxisMv,
							String suid,
							String sales,
							String volume,
							String profit,
							String others,
							String limitMode) {
		// Pyxisクラスを呼び出します。Nullが入る可能性があります。
		if (isInitialized) {

			AppLimitMode appLimitMode = AppLimitMode.NONE;

			if (limitMode.equals("DAILY")) {
				appLimitMode = AppLimitMode.DAILY;
			} else if (limitMode.equals("DAU")) {
				appLimitMode = AppLimitMode.DAU;
			}

			PyxisTracking.saveTrackApp(
							pyxisMv,
							suid,
							sales,
							volume,
							profit,
							others,
							appLimitMode);
			i("PyxisActivity", "PyxisTracking::saveTrackApp をコールしました。");
		}
		else {
			w("PyxisActivity", "PyxisTracking::saveTrackApp initが成功していないため呼び出しません。");
		}
	}
	public void sendTrackApp() {
		// 以下のコードはActivityでないと駄目
/*		runOnUiThread(new Runnable() {
	        public void run() {
	    		// 初期化済フラグで挙動を制御します。
	    		if (isInitialized) {
	    			PyxisTracking.sendTrackApp();
	    			i("PyxisActivity", "PyxisTracking::sendTrackApp をコールしました。");
	    		}
	    		else {
	    			w("PyxisActivity", "PyxisTracking::sendTrackApp initが成功していないため呼び出しません。");
	    		}
	        }
	    });
*/
		// 代わりにRunnableで実行
		Runnable runnable = new Runnable() {
	        public void run() {
	    		// 初期化済フラグで挙動を制御します。
	    		if (isInitialized) {
	    			PyxisTracking.sendTrackApp();
	    			i("PyxisActivity", "PyxisTracking::sendTrackApp をコールしました。");
	    		}
	    		else {
	    			w("PyxisActivity", "PyxisTracking::sendTrackApp initが成功していないため呼び出しません。");
	    		}
	        }
	    };
	    runnable.run();
	}

	public static void copyDBtoExternalStorage() {
		// 初期化済フラグで挙動を制御します。
		if (isInitialized) {
			PyxisTracking.copyDBtoExternalStorage();
			i("PyxisActivity", "PyxisTracking::copyDBtoExternalStorage をコールしました。");
		}
		else {
			w("PyxisActivity", "PyxisTracking::copyDBtoExternalStorage initが成功していないため呼び出しません。");
		}
	}

	public static void setValueToSum(String valueToSum) {

		i("PyxisActivity", "PyxisTracking::setValueToSum をコールしました。");

		try {
			Float _valueToSum = Float.parseFloat(valueToSum);

			PyxisTracking.setValueToSum(_valueToSum);

		} catch (Exception ex) {
			e("PyxisActivity", ex.getMessage());
		}
	}

	public static void setFbLevel(String fb_level){

		i("PyxisActivity", "PyxisTracking::setFbLevel をコールしました。");

		try {
			Integer _fb_level = Integer.parseInt(fb_level);
			PyxisTracking.setFbLevel(_fb_level);
		} catch (Exception ex) {
			e("PyxisActivity", ex.getMessage());
		}

	}

	public static void setFbSuccess(String fb_success){

		i("PyxisActivity", "PyxisTracking::setFbSuccess をコールしました。");

		try {
			boolean _fb_success = Boolean.valueOf(fb_success);

			PyxisTracking.setFbSuccess(_fb_success);
		} catch (Exception ex) {
			e("PyxisActivity", ex.getMessage());
		}
	}

	public static void setFbContentType(String fb_content_type){
		i("PyxisActivity", "PyxisTracking::setFbContentType をコールしました。");
		PyxisTracking.setFbContentType(fb_content_type);
	}

	public static void setFbContentId(String fb_content_id){
		i("PyxisActivity", "PyxisTracking::setFbContentId をコールしました。");
		PyxisTracking.setFbContentId(fb_content_id);
	}

	public static void setFbRegistrationMethod(String fb_registration_method){
		i("PyxisActivity", "PyxisTracking::setFbRegistrationMethod をコールしました。");
		PyxisTracking.setFbRegistrationMethod(fb_registration_method);
	}

	public static void setFbPaymentInfoAvailable(String fb_payment_info_available){
		i("PyxisActivity", "PyxisTracking::setFbPaymentInfoAvailable をコールしました。");

		try {
			boolean _fb_payment_info_available = Boolean.valueOf(fb_payment_info_available);

			PyxisTracking.setFbPaymentInfoAvailable(_fb_payment_info_available);
		} catch (Exception ex) {
			e("PyxisActivity", ex.getMessage());
		}
	}

	public static void setFbMaxRatingValue(String fb_max_rating_value){

		i("PyxisActivity", "PyxisTracking::setFbMaxRatingValue をコールしました。");

		try {
			Integer _fb_max_rating_value = Integer.parseInt(fb_max_rating_value);
			PyxisTracking.setFbMaxRatingValue(_fb_max_rating_value);
		} catch (Exception ex) {
			e("PyxisActivity", ex.getMessage());
		}
	}

	public static void setFbNumItems(String fb_num_items){
		i("PyxisActivity", "PyxisTracking::setFbNumItems をコールしました。");

		try {
			Integer _fb_num_items = Integer.parseInt(fb_num_items);
			PyxisTracking.setFbNumItems(_fb_num_items);
		} catch (Exception ex) {
			e("PyxisActivity", ex.getMessage());
		}
	}

	public static void setFbSearchString(String fb_search_string){
		i("PyxisActivity", "PyxisTracking::fb_search_string をコールしました。");
		PyxisTracking.setFbSearchString(fb_search_string);
	}

	public static void setFbDescription(String fb_description){
		i("PyxisActivity", "PyxisTracking::setFbDescription をコールしました。");
		PyxisTracking.setFbDescription(fb_description);
	}

}


