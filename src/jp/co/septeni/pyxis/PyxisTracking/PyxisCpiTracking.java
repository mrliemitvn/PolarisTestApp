package jp.co.septeni.pyxis.PyxisTracking;

import java.util.ArrayList;
import java.util.HashMap;

import jp.co.septeni.pyxis.PyxisTracking.PyxisDBHelper;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.StrictMode;
import android.text.TextUtils;

public class PyxisCpiTracking extends AsyncTask<String, Void, String> {

	private PyxisUtils pyxisUtils = new PyxisUtils();
	private String className = this.getClass().getSimpleName();

	/**
	 * AES 暗号化キー TODO:PyxisCpiTrackingにも同じ設定値あり ※
	 * 外部からアクセス出来ない様にクラス毎にprivate変数としている
	 * */
	private String AES_KEY = "nH9Lvmxb4VstiGhr";

	private boolean isReceive = false;
	private String pyxisVerify = "";
	private String sesid = "";

	@Override
	protected String doInBackground(String... params) {
		// Receiver経由か、そうでないかの設定
		isReceive = params[0].equals("1");
		pyxisVerify = params[1];
		sesid = params[2];
		trackInstallAlias();
		return null;
	}

	/**
	 * CPI計測
	 *
	 * @since 1.0.0
	 * @param Context
	 *            fromContext アプリケーションコンテキスト
	 * @param Intent
	 *            fromIntent アプリケーションインテント
	 * @param String
	 *            pyxisAppScheme AndroidManifest.xmlで定義したPyxis用のschme
	 * @param String
	 *            pyxisAppHost AndroidManifest.xmlで定義したPyxis用のhost
	 * @param boolean pyxisInstallFlg true 固定
	 * @param String
	 *            pyxisMv Pyxisで発行されたmv
	 * @param String
	 *            pyxisVerify 任意のVerify値(NULL許可)
	 * @param String
	 *            suid 任意のユーザ識別子(NULL許可)
	 * @param String
	 *            pyxisSiteId Pyxisで発行されたサイトID
	 * @param Integer
	 *            sales 金額
	 * @param Integer
	 *            volume 数量
	 * @param Integer
	 *            value 単価
	 */
	protected void trackInstallAlias() {

		// Change mode to allow network thread runing in Gui thread
		if (android.os.Build.VERSION.SDK_INT > 9) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
					.permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}

		Context fromContext = PyxisTracking.context;
		Intent fromIntent = PyxisTracking.intent;

		PyxisConst pyxisConst = new PyxisConst();

		String action = fromIntent.getAction();

		PyxisTracking.isEnableCpi = true;

		// Intstall情報取得
		PyxisDBHelper dbHelper = new PyxisDBHelper();
		HashMap<String, Object> installMap = dbHelper.selInstall();
		dbHelper.close();

		int isBrowserUped = 0;
		if (installMap.get("IS_BROWSER_UPED") != null) {
			isBrowserUped = (Integer) installMap.get("IS_BROWSER_UPED");
		}

		// ブラウザから起動した場合はSharedPreferenceから成果点情報を取得
		if (isBrowserUped == 1) {
			SharedPreferences brefpref = fromContext.getSharedPreferences(
					pyxisConst.PREF_PYXIS, Context.MODE_PRIVATE);

			PyxisTracking.actionPoint = new HashMap<String, String>();

			if (!brefpref.getString("referrer_flg", "").equals("")) {
				PyxisTracking.actionPoint.put("referrer_flg",
						brefpref.getString("referrer_flg", ""));
			}

			if (!brefpref.getString("browser_up_flg", "").equals("")) {
				PyxisTracking.actionPoint.put("browser_up_flg",
						brefpref.getString("browser_up_flg", ""));
			}

			if (!brefpref.getString("welcome_type", "").equals("")) {
				PyxisTracking.actionPoint.put("welcome_type",
						brefpref.getString("welcome_type", "0"));
			}

			if (!brefpref.getString("welcome_url", "").equals("")) {
				PyxisTracking.actionPoint.put("welcome_url",
						brefpref.getString("welcome_url", ""));
			}

			if (!brefpref.getString("cv_mode", "").equals("")) {
				PyxisTracking.actionPoint.put("cv_mode",
						brefpref.getString("cv_mode", ""));
			}
		}

		if (PyxisTracking.actionPoint == null
				|| PyxisTracking.actionPoint.isEmpty()) {
			// 成果点情報取得
			/** ################# tracelog ################# */
			PyxisUtils.recordLog(className,
					(new Throwable()).getStackTrace()[0].getMethodName(),
					(new Throwable()).getStackTrace()[0].getLineNumber(),
					"CPI計測処理 : 成果点情報取得", 1, false);

			System.gc();

			PyxisTracking.actionPoint = pyxisUtils
					.getActionPoint(PyxisTracking.pyxisMv);

			if (PyxisTracking.actionPoint == null
					|| PyxisTracking.actionPoint.isEmpty()) {
				// 成果点情報が取得できなければ終了
				/** ################# errorlog ################# */
				PyxisUtils.recordLog(className,
						(new Throwable()).getStackTrace()[0].getMethodName(),
						(new Throwable()).getStackTrace()[0].getLineNumber(),
						"CPI計測処理 : 成果点情報取得エラーにより計測終了 ", 2, false);
				return;
			}

			String message = "referrer_flg = "
					+ PyxisTracking.actionPoint.get("referrer_flg")
					+ " ,browser_up_flg = "
					+ PyxisTracking.actionPoint.get("browser_up_flg")
					+ " ,welcome_type = "
					+ PyxisTracking.actionPoint.get("welcome_type")
					+ " ,welcome_url = "
					+ PyxisTracking.actionPoint.get("welcome_url")
					+ " ,cv_mode = " + PyxisTracking.actionPoint.get("cv_mode");

			/** ################# tracelog ################# */
			PyxisUtils.recordLog(className,
					(new Throwable()).getStackTrace()[0].getMethodName(),
					(new Throwable()).getStackTrace()[0].getLineNumber(),
					message, 1, false);

		}

		String referrerFlg = PyxisTracking.actionPoint.get("referrer_flg");
		PyxisTracking.referrerFlg = referrerFlg;
		String cvMode = PyxisTracking.actionPoint.get("cv_mode");

		if (TextUtils.isEmpty(cvMode)) {
			cvMode = PyxisTracking.cvMode;
			PyxisTracking.actionPoint.put("cv_mode", cvMode);
		}

		// 引数&パラメータチェック
		if (!pyxisUtils.checkParamsForCpi()) {
			return;
		}

		// User情報取得
		dbHelper = new PyxisDBHelper();
		HashMap<String, Object> userMap = dbHelper.selUser();
		dbHelper.close();

		if (!TextUtils.isEmpty(pyxisVerify)) {
			// verifyが指定されている場合

			// UUIDが取得できたら保存
			dbHelper = new PyxisDBHelper();
			dbHelper.updUser(null, null, pyxisVerify, null, null);
			dbHelper.close();

		} else {
			// verifyが指定されていない場合

			if (!TextUtils.isEmpty((String) userMap.get("UUID"))) {
				// UUIDが保存されている場合はセット
				pyxisVerify = (String) userMap.get("UUID");
			}
		}

		Integer installFlg = 0;
		if (installMap.get("START_FLAG") != null) {
			installFlg = (Integer) installMap.get("START_FLAG");
		}

		Integer fbInstallFlg = 0;
		if (installMap.get("FB_START_FLAG") != null) {
			fbInstallFlg = (Integer) installMap.get("FB_START_FLAG");
		}

		// 通常計測のみの場合で計測済みの場合はスキップ
		if (cvMode.equals("0") && installFlg == 1 && !isReceive) {
			/** ################# tracelog ################# */
			PyxisUtils.recordLog(className,
					(new Throwable()).getStackTrace()[0].getMethodName(),
					(new Throwable()).getStackTrace()[0].getLineNumber(),
					"CPI計測[通知済み]", 1, false);
			return;
		}

		// FB計測（Mobile App INstall）のみの場合で計測済みの場合はスキップ
		if (cvMode.equals("1") && fbInstallFlg == 1) {
			/** ################# tracelog ################# */
			PyxisUtils.recordLog(className,
					(new Throwable()).getStackTrace()[0].getMethodName(),
					(new Throwable()).getStackTrace()[0].getLineNumber(),
					"FB (Mobile App Install Ad) CPI計測[通知済み]", 1, false);
			return;
		}

		// 通常計測 & FB計測（Mobile App INstall）の場合で計測済みの場合はスキップ
		if (cvMode.equals("2") && installFlg == 1 && fbInstallFlg == 1
				&& !isReceive) {
			/** ################# tracelog ################# */
			PyxisUtils.recordLog(className,
					(new Throwable()).getStackTrace()[0].getMethodName(),
					(new Throwable()).getStackTrace()[0].getLineNumber(),
					"通常CPI計測 & FB (Mobile App Install Ad) CPI計測[通知済み]", 1,
					false);
			return;
		}

		// INSTALL_REFERRERで計測する、且つPyxisReceiver経由で入ってきた場合
		if (referrerFlg.equals("1")) {

			if (isReceive == true) {
				// install referrer経由の場合、インストール済みかどうかにかかわらず計測

				/** ################# tracelog ################# */
				PyxisUtils.recordLog(className,
						(new Throwable()).getStackTrace()[0].getMethodName(),
						(new Throwable()).getStackTrace()[0].getLineNumber(),
						"INSTALL_REFERRER経由 : OS 3.1以上", 1, false);
			} else if (Build.VERSION.SDK_INT < 12 && installFlg != 1) {
				// OS3.1未満で、インストール未実施の場合、計測実施

				/** ################# tracelog ################# */
				PyxisUtils.recordLog(className,
						(new Throwable()).getStackTrace()[0].getMethodName(),
						(new Throwable()).getStackTrace()[0].getLineNumber(),
						"OS 3.1未満なので計測実施", 1, false);
			} else if (installFlg != 1) {
				// OS3.1以上でreferrer経由ではない、かつインストール未実施の場合、計測実施

				/** ################# tracelog ################# */
				PyxisUtils
						.recordLog(
								className,
								(new Throwable()).getStackTrace()[0]
										.getMethodName(),
								(new Throwable()).getStackTrace()[0]
										.getLineNumber(),
								"OS 3.1以上でINSTALL_REFERRER経由ではない。かつ計測未実施なのでorganicログの計測実施 ",
								1, false);
			} else {
				// OS3.1以上でreferrer経由ではない、かつインストール実施済みの場合、スキップ

				/** ################# tracelog ################# */
				PyxisUtils.recordLog(className,
						(new Throwable()).getStackTrace()[0].getMethodName(),
						(new Throwable()).getStackTrace()[0].getLineNumber(),
						"OS 3.1以上でINSTALL_REFERRER経由ではない。かつ計測済みなので、スキップ ", 1,
						false);
				return;
			}

			if (isReceive == true
					|| (Build.VERSION.SDK_INT < 12 && installFlg != 1)) {
				// セッションID、verifyの値は、インストール時にテキストファイルに保存しておいたものを使用
				String[] referrerArr = PyxisUtils.readReferrer(fromContext);
				if (referrerArr == null) {
					// verifyが読み取れない場合(オーガニックの場合)、verifyのみ生成
					pyxisVerify = PyxisUtils.generateUuid();
				} else {
					sesid = referrerArr[0];
					pyxisVerify = referrerArr[1];
				}
			}

			dbHelper = new PyxisDBHelper();

			dbHelper.updUser(null, sesid, pyxisVerify, null, null);
			dbHelper.close();
			sendInstall(fromContext, PyxisTracking.pyxisMv, sesid, pyxisVerify,
					PyxisTracking.pyxisSuid, PyxisTracking.pyxisSales,
					PyxisTracking.pyxisVolume, PyxisTracking.pyxisProfit,
					PyxisTracking.pyxisOthers, 0);

			// cvMode = 2 の場合は、FB計測も行う
			if (cvMode.equals("2")) {
				sendFbInstall(cvMode, dbHelper, fromContext);
			}

			return;
		}

		// ブラウザーから起動された場合
		if (Intent.ACTION_VIEW.equals(action) && installFlg != 1) {

			fromBrowser();

			// ブラウザ起動とFB計測併用の場合、FB計測情報を送信
			if (cvMode.equals("2")) {
				sendFbInstall(cvMode, dbHelper, fromContext);
			}
			return;
		}

		// 送信リトライ(ブラウザ起動済みで送信失敗)
		if (isBrowserUped == 1 && installFlg != 1) {

			/** ################# tracelog ################# */
			PyxisUtils.recordLog(className,
					(new Throwable()).getStackTrace()[0].getMethodName(),
					(new Throwable()).getStackTrace()[0].getLineNumber(),
					"送信リトライ", 1, false);

			sesid = (String) userMap.get("SESID");
			pyxisVerify = (String) userMap.get("VERIFY");

			sendInstall(fromContext, PyxisTracking.pyxisMv, sesid, pyxisVerify,
					PyxisTracking.pyxisSuid, PyxisTracking.pyxisSales,
					PyxisTracking.pyxisVolume, PyxisTracking.pyxisProfit,
					PyxisTracking.pyxisOthers, 0);

			// cvMode = 2 の場合は、FB計測も行う
			if (cvMode.equals("2")) {
				sendFbInstall(cvMode, dbHelper, fromContext);
			}

			return;
		}

		if (!referrerFlg.equals("1") && installFlg != 1) {

			// ブラウザを開く場合
			/** ################# tracelog ################# */
			PyxisUtils.recordLog(className,
					(new Throwable()).getStackTrace()[0].getMethodName(),
					(new Throwable()).getStackTrace()[0].getLineNumber(),
					"ブラウザを開く", 1, false);

			String uri = "";
			if (PyxisTracking.actionPoint.get("welcome_type").equals("0")) {

				dbHelper = new PyxisDBHelper();
				dbHelper.updInstall(null, null, null, 1);
				dbHelper.close();

				// 白紙ページを開く
				/** ################# tracelog ################# */
				PyxisUtils.recordLog(className,
						(new Throwable()).getStackTrace()[0].getMethodName(),
						(new Throwable()).getStackTrace()[0].getLineNumber(),
						"welcome_type=0", 1, false);

				uri = pyxisConst.HTTP_PATH_UUID + "?pid=" + PyxisTracking.pid
						+ "&scm=" + PyxisTracking.pyxisAppScheme + "&idn="
						+ PyxisTracking.pyxisAppHost + "&expr="
						+ (System.currentTimeMillis() / 1000);

			} else {
				// welcomeページかカスタムページを開く
				try {
					/** ################# tracelog ################# */
					PyxisUtils.recordLog(
							className,
							(new Throwable()).getStackTrace()[0]
									.getMethodName(),
							(new Throwable()).getStackTrace()[0]
									.getLineNumber(),
							"welcome_url="
									+ PyxisTracking.actionPoint
											.get("welcome_url"), 1, false);

					PyxisUtils pyxisUtils = new PyxisUtils();

					// welcomeページを使用する場合は、verifyを生成する
					String verify = PyxisUtils.generateUuid();
					pyxisVerify = verify;
					// UUIDを保存
					dbHelper = new PyxisDBHelper();
					dbHelper.updUser(null, null, pyxisVerify, null, null);
					dbHelper.close();
					String param = "verify#"
							+ pyxisUtils.base64Encode(verify.getBytes())
							+ "&pyxis&suid#"
							+ pyxisUtils.base64Encode(PyxisTracking.pyxisSuid
									.getBytes())
							+ "&pyxis&scm#"
							+ PyxisTracking.pyxisAppScheme
							+ "&pyxis&idn#"
							+ PyxisTracking.pyxisAppHost
							+ "&pyxis&site_id#"
							+ PyxisTracking.siteId
							+ "&pyxis&mv#"
							+ PyxisTracking.pyxisMv
							+ "&pyxis&expr#"
							+ (System.currentTimeMillis() / 1000)
							+ "&pyxis&sales#"
							+ (PyxisTracking.pyxisSales == null ? ""
									: PyxisTracking.pyxisSales)
							+ "&pyxis&volume#"
							+ (PyxisTracking.pyxisVolume == null ? ""
									: PyxisTracking.pyxisVolume)
							+ "&pyxis&profit#"
							+ (PyxisTracking.pyxisProfit == null ? ""
									: PyxisTracking.pyxisProfit);

					/** ################# tracelog ################# */
					PyxisUtils
							.recordLog(
									className,
									(new Throwable()).getStackTrace()[0]
											.getMethodName(),
									(new Throwable()).getStackTrace()[0]
											.getLineNumber(),
									"PARAM : "
											+ pyxisUtils.base64Encode(param
													.getBytes()), 1, false);

					uri = PyxisTracking.actionPoint.get("welcome_url")
							+ "?param="
							+ pyxisUtils.base64Encode(param.getBytes());
					dbHelper = new PyxisDBHelper();
					dbHelper.updInstall(1, null, null, null);
					dbHelper.close();

					/** ################# tracelog ################# */
					PyxisUtils.recordLog(className, (new Throwable())
							.getStackTrace()[0].getMethodName(),
							(new Throwable()).getStackTrace()[0]
									.getLineNumber(), "ウェルカムページでの通知成功", 0,
							false);

					/** ################# tracelog ################# */
					PyxisUtils.recordLog(className, (new Throwable())
							.getStackTrace()[0].getMethodName(),
							(new Throwable()).getStackTrace()[0]
									.getLineNumber(), "URI : " + uri, 1, false);
				} catch (Exception e) {
					/** ################# tracelog ################# */
					PyxisUtils.recordLog(className, (new Throwable())
							.getStackTrace()[0].getMethodName(),
							(new Throwable()).getStackTrace()[0]
									.getLineNumber(), "URI生成エラー", 1, false);

					/** ################# errorlog ################# */
					PyxisUtils.recordErrorLog(className, (new Throwable())
							.getStackTrace()[0].getMethodName(),
							(new Throwable()).getStackTrace()[0]
									.getLineNumber(), "error", 2, false, e);
				}
			}

			// verifyを外部から受け取る場合、FB計測を行う
			// FB計測（Mobile App Install）が有効の場合は通知
			if (!cvMode.equals("0") && fbInstallFlg != 1) {
				/** ################# tracelog ################# */
				PyxisUtils.recordLog(className,
						(new Throwable()).getStackTrace()[0].getMethodName(),
						(new Throwable()).getStackTrace()[0].getLineNumber(),
						"FB計測（FB Mobile App Install Ad）有効", 1, false);

				// FB＋通常計測の組み合わせで、ブラウザ起動以外の場合のみFB計測実行
				// ブラウザ起動の場合は、ブラウザから呼び出された後(fromBrowserの後)FB計測実施
				if (cvMode.equals("2")
						&& (referrerFlg.equals("1") || PyxisTracking.actionPoint
								.get("welcome_type").equals("1"))) {

					/** ################# tracelog ################# */
					PyxisUtils.recordLog(className, (new Throwable())
							.getStackTrace()[0].getMethodName(),
							(new Throwable()).getStackTrace()[0]
									.getLineNumber(),
							"cvMode = 2 でブラウザ起動以外。FB計測を実施", 1, false);
					sendFbInstall(cvMode, dbHelper, fromContext);
				}

				// FB計測のみの場合はFB計測を行って終了
				if (cvMode.equals("1")) {
					/** ################# tracelog ################# */
					PyxisUtils.recordLog(className, (new Throwable())
							.getStackTrace()[0].getMethodName(),
							(new Throwable()).getStackTrace()[0]
									.getLineNumber(), "cvMode = 1。FB計測を実施", 1,
							false);

					sendFbInstall(cvMode, dbHelper, fromContext);
					return;
				}
			}

			// ブラウザー呼出（初回起動情報送信）
			Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
			intent.setClassName("com.android.browser",
					"com.android.browser.BrowserActivity");
			intent.addCategory(Intent.CATEGORY_DEFAULT);
			intent.addCategory(Intent.CATEGORY_BROWSABLE);
			intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY
					| Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS
					| Intent.FLAG_ACTIVITY_NO_ANIMATION
					| Intent.FLAG_ACTIVITY_NEW_TASK);
			try {
				// SharedPreferenceにセッション生存フラグ保存
				if (PyxisTracking.isFirst) {
					SharedPreferences pref = fromContext.getSharedPreferences(
							pyxisConst.PREF_PYXIS, Context.MODE_PRIVATE);
					Editor e = pref.edit();
					if (PyxisTracking.isFirst) {
						// 1はセッション保存
						e.putInt(pyxisConst.PREF_PYXIS_SESSION_ALIVE_FLG, 1);
						e.putString("referrer_flg",
								PyxisTracking.actionPoint.get("referrer_flg"));
						e.putString("browser_up_flg",
								PyxisTracking.actionPoint.get("browser_up_flg"));
						e.putString("welcome_type",
								PyxisTracking.actionPoint.get("welcome_type"));
						e.putString("cv_mode",
								PyxisTracking.actionPoint.get("cv_mode"));
						e.commit();
					}

					// 念のため、別個にインスタンス生成
					SharedPreferences refpref = fromContext
							.getSharedPreferences(pyxisConst.PREF_PYXIS,
									Context.MODE_PRIVATE);

					boolean isRead = false;
					int count = 0;
					while (!isRead) {

						// 2はunknown
						if (refpref.getInt(
								pyxisConst.PREF_PYXIS_SESSION_ALIVE_FLG, 2) == 1) {
							// あんまり意味ないけど一応
							isRead = true;
							break;
						} else {
							count++;
							// 0.5秒ウェイト
							Thread.sleep(500);
						}

						if (count > 3) {
							break;
						}
					}
				}

				fromContext.startActivity(intent);

				// ここで一旦プロセスを落とす
				android.os.Process.killProcess(android.os.Process.myPid());

			} catch (Exception e) {
				/** ################# errorlog ################# */
				PyxisUtils.recordErrorLog(className,
						(new Throwable()).getStackTrace()[0].getMethodName(),
						(new Throwable()).getStackTrace()[0].getLineNumber(),
						e.toString(), 2, false, e);
			}
		}
	}

	/**
	 * Install Tracking
	 *
	 * @param fromContext
	 * @param mv
	 * @param sesid
	 * @param verify
	 * @param suid
	 * @param sales
	 * @param volume
	 * @param profit
	 */
	private void sendInstall(Context fromContext, String mv, String sesid,
			String verify, String suid, String sales, String volume,
			String profit, String others, int sendInstallMode) {

		PyxisConst pyxisConst = new PyxisConst();

		String processName = null;

		if (sendInstallMode == 1) {
			processName = "FBインストール";
		} else {
			processName = "インストール";
		}

		/** ################# tracelog ################# */
		PyxisUtils.recordLog(className,
				(new Throwable()).getStackTrace()[0].getMethodName(),
				(new Throwable()).getStackTrace()[0].getLineNumber(),
				processName + "計測通知処理開始", 1, false);

		// 回線状態取得
		if (!pyxisUtils.getNetworkStatus()) {
			/** ################# tracelog ################# */
			PyxisUtils.recordLog(className,
					(new Throwable()).getStackTrace()[0].getMethodName(),
					(new Throwable()).getStackTrace()[0].getLineNumber(),
					processName + "計測通知処理無効:理由[オフライン]", 1, false);
		}

		// 端末ID(fid)の取得
		String fid = "";
		if (PyxisTracking.aidGet) {
			// Manifestファイルで端末ID取得設定がされている場合のみfid生成
			fid = pyxisUtils.generateFid(AES_KEY);
		}

		ArrayList<NameValuePair> param = new ArrayList<NameValuePair>();

		String message = null;

		if (verify == null || verify.isEmpty()) {
			// verifyが取得できなかった場合は生成する
			verify = pyxisUtils.generateUuid();
			pyxisVerify = verify;
			/** ################# tracelog ################# */
			PyxisUtils.recordLog(className,
					(new Throwable()).getStackTrace()[0].getMethodName(),
					(new Throwable()).getStackTrace()[0].getLineNumber(),
					"verifyが取得できなかったため、verifyを生成：" + verify, 1, false);
		}

		param.add(new BasicNameValuePair("mv", mv)); // Pyxis上で発行されるmv
		param.add(new BasicNameValuePair("verify", verify)); // アプリ上に保持するUUIDもしくは引数の値
		param.add(new BasicNameValuePair("suid", pyxisUtils.base64Encode(suid
				.getBytes()))); // 任意のユーザ識別子
		param.add(new BasicNameValuePair("fid", fid)); // 端末ID
		param.add(new BasicNameValuePair("pid", PyxisTracking.pid)); // PID
		param.add(new BasicNameValuePair("dn", pyxisUtils.getPhoneName())); // 端末名
		param.add(new BasicNameValuePair("os", pyxisUtils.getOsVersion())); // OSバージョン

		if (PyxisTracking.referrerFlg.equals("1")
				&& !PyxisTracking.cvMode.equals("1")
				&& Build.VERSION.SDK_INT >= 12) {
			// リファラー計測 && cvMode != 1 && Android3.1以上の場合、パラメータを追加
			PyxisTracking.ir_id = PyxisUtils.getIrId(fromContext);
			param.add(new BasicNameValuePair("ir_id", PyxisTracking.ir_id)); // organicとreceiver経由をひも付けるためのir_idパラメータ
			if (isReceive) {
				param.add(new BasicNameValuePair("rec",
						PyxisTracking.referrerFlg)); // レシーバ経由フラグ
			}
		}

		String cpiUrl = null;

		if (sendInstallMode == 1) {
			cpiUrl = pyxisConst.HTTP_PATH_FB_TRACKING;

			if (sesid != null && !sesid.isEmpty()) {
				param.add(new BasicNameValuePair("sesid", sesid != null ? sesid
						: "")); // FBセッションID
			}
			param.add(new BasicNameValuePair(
					"atid",
					PyxisTracking.attributionId != null ? PyxisTracking.attributionId
							: "")); // FBアトリビューションID
			param.add(new BasicNameValuePair("sales", sales != null ? sales
					: "")); // 金額
			param.add(new BasicNameValuePair("volume", volume != null ? volume
					: "")); // 数量
			param.add(new BasicNameValuePair("profit", profit != null ? profit
					: "")); // 単価
			param.add(new BasicNameValuePair("others", others != null ? pyxisUtils.base64Encode(others.getBytes())
					: "")); // その他
			String ate = PyxisUtils.getStrOptOut();
			param.add(new BasicNameValuePair("application_tracking_enabled",
					ate)); // application_tracking_enable

			message = processName
					+ "計測通知リクエストパラメータ[ mv = "
					+ mv
					+ " / verify = "
					+ (TextUtils.isEmpty(verify) ? "" : verify)
					+ " / sesid = "
					+ (TextUtils.isEmpty(sesid) ? "" : sesid)
					+ " / atid = "
					+ (TextUtils.isEmpty(PyxisTracking.attributionId) ? ""
							: PyxisTracking.attributionId)
					+ " / suid = "
					+ (TextUtils.isEmpty(suid) ? "" : suid)
					+ " / sales = "
					+ (TextUtils.isEmpty(sales) ? "" : sales)
					+ " / volume = "
					+ (TextUtils.isEmpty(volume) ? "" : volume)
					+ " / profit = "
					+ (profit == null ? "" : profit)
					+ " / others = "
					+ (others == null ? "" : others)
					+ " / fid = "
					+ (TextUtils.isEmpty(fid) ? "" : fid)
					+ " / pid = "
					+ (TextUtils.isEmpty(PyxisTracking.pid) ? ""
							: PyxisTracking.pid)
					+ " / ir_id = "
					+ (TextUtils.isEmpty(PyxisTracking.ir_id) ? ""
							: PyxisTracking.ir_id)
					+ " / rec = "
					+ (TextUtils.isEmpty(PyxisTracking.referrerFlg) ? ""
							: PyxisTracking.referrerFlg)
					+ " / application_tracking_enabled = "
					+ (TextUtils.isEmpty(ate) ? "" : ate)
					+ " / dn = "
					+ (TextUtils.isEmpty(pyxisUtils.getPhoneName()) ? ""
							: pyxisUtils.getPhoneName())
					+ " / os = "
					+ (TextUtils.isEmpty(pyxisUtils.getOsVersion()) ? ""
							: pyxisUtils.getOsVersion());
		} else {
			cpiUrl = pyxisConst.HTTP_PATH_TRACKING;

			param.add(new BasicNameValuePair("sesid", sesid)); // セッションID
			param.add(new BasicNameValuePair("sales", sales != null ? sales
					: "")); // 金額
			param.add(new BasicNameValuePair("volume", volume != null ? volume
					: "")); // 数量
			param.add(new BasicNameValuePair("profit", profit != null ? profit
					: "")); // 単価
			param.add(new BasicNameValuePair("others", others != null ? pyxisUtils.base64Encode(others.getBytes())
					: "")); // 単価

			message = processName
					+ "計測通知リクエストパラメータ[ mv = "
					+ mv
					+ " / verify = "
					+ (TextUtils.isEmpty(verify) ? "" : verify)
					+ " / sesid = "
					+ (TextUtils.isEmpty(sesid) ? "" : sesid)
					+ " / suid = "
					+ (TextUtils.isEmpty(suid) ? "" : suid)
					+ " / sales = "
					+ (sales == null ? "" : sales)
					+ " / volume = "
					+ (volume == null ? "" : volume)
					+ " / profit = "
					+ (profit == null ? "" : profit)
					+ " / others = "
					+ (others == null ? "" : others)
					+ " / fid = "
					+ (TextUtils.isEmpty(fid) ? "" : fid)
					+ " / pid = "
					+ (TextUtils.isEmpty(PyxisTracking.pid) ? ""
							: PyxisTracking.pid)
					+ " / ir_id = "
					+ (TextUtils.isEmpty(PyxisTracking.ir_id) ? ""
							: PyxisTracking.ir_id)
					+ " / rec = "
					+ (TextUtils.isEmpty(PyxisTracking.referrerFlg) ? ""
							: PyxisTracking.referrerFlg)
					+ " / dn = "
					+ (TextUtils.isEmpty(pyxisUtils.getPhoneName()) ? ""
							: pyxisUtils.getPhoneName())
					+ " / os = "
					+ (TextUtils.isEmpty(pyxisUtils.getOsVersion()) ? ""
							: pyxisUtils.getOsVersion());
		}

		/** ################# tracelog ################# */
		PyxisUtils.recordLog(className,
				(new Throwable()).getStackTrace()[0].getMethodName(),
				(new Throwable()).getStackTrace()[0].getLineNumber(), message,
				1, false);

		PyxisDBHelper dbHelper = new PyxisDBHelper();
		// 送信完了の場合DBのフラグ制御

		if (pyxisUtils.sendCpiBySocket(cpiUrl, param)) {

			if (sendInstallMode == 0) {
				/** ################# tracelog ################# */
				PyxisUtils.recordLog(className,
						(new Throwable()).getStackTrace()[0].getMethodName(),
						(new Throwable()).getStackTrace()[0].getLineNumber(),
						processName + "計測の通知に成功", 0, false);
				dbHelper.updInstall(1, null, null, 0);
			} else {
				/** ################# tracelog ################# */
				PyxisUtils.recordLog(className,
						(new Throwable()).getStackTrace()[0].getMethodName(),
						(new Throwable()).getStackTrace()[0].getLineNumber(),
						processName + "計測の通知に成功", 0, false);
				dbHelper.updInstall(null, 1, null, 0);
			}

		} else {
			/** ################# tracelog ################# */
			PyxisUtils.recordLog(className,
					(new Throwable()).getStackTrace()[0].getMethodName(),
					(new Throwable()).getStackTrace()[0].getLineNumber(),
					processName + "計測の通知に失敗", 2, false);
		}

		dbHelper.close();

		// 成果点情報一時情報削除
		SharedPreferences dpref = fromContext.getSharedPreferences(
				pyxisConst.PREF_PYXIS, Context.MODE_PRIVATE);
		dpref.edit().clear().commit();

		/** ################# tracelog ################# */
		PyxisUtils.recordLog(className,
				(new Throwable()).getStackTrace()[0].getMethodName(),
				(new Throwable()).getStackTrace()[0].getLineNumber(),
				processName + "計測通知処理終了", 1, false);

	}

	private void fromBrowser() {

		/** ################# tracelog ################# */
		PyxisUtils.recordLog(className,
				(new Throwable()).getStackTrace()[0].getMethodName(),
				(new Throwable()).getStackTrace()[0].getLineNumber(),
				"CPI計測[ブラウザ起動時の処理]", 1, false);

		Uri uri = PyxisTracking.intent.getData();
		/** ################# tracelog ################# */
		PyxisUtils.recordLog(className,
				(new Throwable()).getStackTrace()[0].getMethodName(),
				(new Throwable()).getStackTrace()[0].getLineNumber(), "uri = "
						+ uri.toString(), 1, false);

		String q_scheme = "";
		String q_host = "";

		if (uri.getScheme() != null) {
			q_scheme = uri.getScheme();
		}

		if (uri.getHost() != null) {
			q_host = uri.getHost();
		}

		// schemeチェック
		if (!PyxisTracking.pyxisAppScheme.equals(q_scheme)
				|| !PyxisTracking.pyxisAppHost.equals(q_host)) {
			/** ################# tracelog ################# */
			PyxisUtils.recordLog(className,
					(new Throwable()).getStackTrace()[0].getMethodName(),
					(new Throwable()).getStackTrace()[0].getLineNumber(),
					"CPI計測 [ブラウザから起動] : Pyxis以外のscheme or hostから起動 [scheme = "
							+ q_scheme + " / host = " + q_host + "]", 1, false);
			return;
		}

		String sesid = uri.getQueryParameter("sesid");
		String verify = uri.getQueryParameter("verify");
		pyxisVerify = verify;

		/** ################# tracelog ################# */
		PyxisUtils.recordLog(className,
				(new Throwable()).getStackTrace()[0].getMethodName(),
				(new Throwable()).getStackTrace()[0].getLineNumber(),
				"ブラウザからリクエストパラメータ取得[sesid = "
						+ (TextUtils.isEmpty(sesid) ? "" : sesid) + "verify = "
						+ (TextUtils.isEmpty(verify) ? "" : verify) + "]", 1,
				false);

		if (sesid == null) {
			sesid = "";
		}
		if (verify == null) {
			verify = "";
		}

		PyxisDBHelper dbHelper;
		dbHelper = new PyxisDBHelper();
		dbHelper.updUser(sesid, null, verify, null, null);
		dbHelper.close();

		sendInstall(PyxisTracking.context, PyxisTracking.pyxisMv, sesid,
				verify, PyxisTracking.pyxisSuid, PyxisTracking.pyxisSales,
				PyxisTracking.pyxisVolume, PyxisTracking.pyxisProfit,
				PyxisTracking.pyxisOthers, 0);
	}

	private void sendFbInstall(String cvMode, PyxisDBHelper dbHelper,
			Context fromContext) {
		String attributionId = "";

		try {
			attributionId = FbUtils.getAttributionId(PyxisTracking.context
					.getContentResolver());
		} catch (Exception e) {
			/** ################# tracelog ################# */
			PyxisUtils.recordLog(className,
					(new Throwable()).getStackTrace()[0].getMethodName(),
					(new Throwable()).getStackTrace()[0].getLineNumber(),
					"FB計測（FB Mobile App Install Ad）: attribution_id 取得失敗"
							+ e.getStackTrace().toString(), 1, false);
		}

		if (TextUtils.isEmpty(attributionId)) {
			/** ################# tracelog ################# */
			PyxisUtils.recordLog(className,
					(new Throwable()).getStackTrace()[0].getMethodName(),
					(new Throwable()).getStackTrace()[0].getLineNumber(),
					"attributionId is empty", 1, false);
		} else {
			/** ################# tracelog ################# */
			PyxisUtils.recordLog(className,
					(new Throwable()).getStackTrace()[0].getMethodName(),
					(new Throwable()).getStackTrace()[0].getLineNumber(),
					"attributionId is [" + attributionId + "]", 1, false);
			PyxisTracking.attributionId = attributionId;
		}

		// verifyの取得
		String verify = null;
		if (!cvMode.equals("1") && !pyxisVerify.isEmpty()) {
			// 通常計測と組み合わせ、かつverifyを取得できている場合は、取得したverifyを使用
			verify = pyxisVerify;

			/** ################# tracelog ################# */
			PyxisUtils.recordLog(className,
					(new Throwable()).getStackTrace()[0].getMethodName(),
					(new Throwable()).getStackTrace()[0].getLineNumber(),
					"[CV_MODE != 1]外部から取得したverifyを使用", 1, false);
		} else {
			// FBのみの場合は、verifyをSDKで生成
			verify = PyxisUtils.generateUuid();
			pyxisVerify = verify;

			/** ################# tracelog ################# */
			PyxisUtils.recordLog(className,
					(new Throwable()).getStackTrace()[0].getMethodName(),
					(new Throwable()).getStackTrace()[0].getLineNumber(),
					"[CV_MODE = 1]SDKで生成したverifyを使用", 1, false);
		}

		dbHelper = new PyxisDBHelper();
		dbHelper.updUser(null, sesid, verify, null, null);
		dbHelper.close();

		sendInstall(fromContext, PyxisTracking.pyxisMv, sesid, verify,
				PyxisTracking.pyxisSuid, PyxisTracking.pyxisSales,
				PyxisTracking.pyxisVolume, PyxisTracking.pyxisProfit,
				PyxisTracking.pyxisOthers, 1);

	}

}
