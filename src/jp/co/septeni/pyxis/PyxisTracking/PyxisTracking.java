package jp.co.septeni.pyxis.PyxisTracking;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import jp.co.septeni.pyxis.PyxisTracking.PyxisFbEventParameterConfig.FbEventParameter;
import jp.co.septeni.pyxis.PyxisTracking.PyxisFbEventParameterConfig.StringLengthValidation;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.text.TextUtils;

/**
 * Pyxis-SDK for Android.
 * 
 * @author (C)SEPTENI CO.,LTD.
 * @version 1.0.0, 11 Jul 2011
 */
public class PyxisTracking {

	protected static Context context;
	protected static Intent intent;
	protected static String siteId;
	protected static Integer saveMax;
	protected static Integer saveMode;
	protected static Boolean logMode;
	protected static Boolean aidGet;
	protected static String pid;
	protected static String scheme;
	protected static String appHost;
	protected static boolean initExeced = false;
	protected static String sesid;
	protected static String attributionId;
	protected static boolean isFirst;
	protected static boolean isEnableCpi;
	protected static Boolean enableSDCard;
	protected static String pyxisHost;
	protected static String pyxisAppScheme = null;
	protected static String pyxisAppHost = null;
	protected static String pyxisMv;
	protected static String pyxisVerify;
	protected static String pyxisSuid;
	protected static String pyxisSales;
	protected static String pyxisVolume;
	protected static String pyxisProfit;
	protected static String pyxisOthers;
	protected static Boolean isReceive;
	protected static HashMap<String, String> actionPoint;
	protected static int sendInstallMode;
	protected static String cvMode;
	protected static String referrerFlg;
	protected static String ir_id;

	private final static String XML_PARAM_NAME_PYXIS_HOST = "PYXIS_HOST";
	private final static String XML_PARAM_NAME_PYXIS_SCHEME = "PYXIS_SCHEME";
	private static String className = new PyxisTracking().getClass()
			.getSimpleName();

	/** App成果送信の制限モード */
	public static enum AppLimitMode {
		/** 制限なし */
		NONE(0, "制限なし"),
		/** 1日一回 */
		DAILY(1, "成果送信は1日一回まで"),
		/** 同日付でのApp成果の送信は行わない。 */
		DAU(2, "同日付でのApp成果の送信は行わない");

		private int val;
		private String description;

		AppLimitMode(int val, String description) {
			this.val = val;
			this.description = description;
		}

		protected int getVal() {
			return this.val;
		}

		protected String getDescription() {
			return this.description;
		}
	}

	/** App成果送信制限 */
	protected static AppLimitMode appLimitMode = AppLimitMode.NONE;

	/**
	 * 初期化処理（必須） クラス変数のセット、DBのアップデート等初期処理を行う
	 * 
	 * @param fromContext
	 * @param fromIntent
	 * @param pyxisAppScheme
	 * @param pyxisAppHost
	 */
	public static void init(Context fromContext, Intent fromIntent,
			String appScheme, String host) {

		try {
			// 渡されたContext、Intentをクラス変数にセット(必ず初期化する)
			context = fromContext;
			intent = fromIntent;
			pyxisAppScheme = appScheme;
			pyxisAppHost = host;

			if (appScheme != null) {
				pyxisAppScheme = appScheme;
			}
			if (host != null) {
				pyxisAppHost = host;
			}

			if (pyxisHost == null) {
				pyxisHost = getManifestValueString(XML_PARAM_NAME_PYXIS_HOST,
						false);
			}

			if (pyxisAppScheme == null) {
				pyxisAppScheme = getManifestValueString(
						XML_PARAM_NAME_PYXIS_SCHEME, false);
			}

			PyxisConst pyxisConst = new PyxisConst();

			// Manifest.xmlの定義をクラス変数にセット
			if (TextUtils.isEmpty(siteId)) {
				siteId = getManifestValueInteger(
						pyxisConst.XML_PARAM_NAME_PYXIS_SITE_ID).toString();
			}
			if (logMode == null) {
				logMode = getManifestValueBool(pyxisConst.XML_PARAM_NAME_PYXIS_LOG_MODE);
			}

			if (saveMax == null) {
				saveMax = getManifestValueInteger(pyxisConst.XML_PARAM_NAME_PYXIS_APP_SAVE_MAX);
			}

			if (saveMode == null) {
				saveMode = getManifestValueInteger(pyxisConst.XML_PARAM_NAME_PYXIS_APP_SAVE_MODE);
			}

			if (enableSDCard == null) {
				enableSDCard = false;
				int SdPermit = context
						.checkCallingOrSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);

				if (SdPermit == PackageManager.PERMISSION_GRANTED) {
					enableSDCard = true;
				}
			}

			if (cvMode == null) {
				cvMode = getManifestValueInteger(
						pyxisConst.XML_PARAM_NAME_PYXIS_CV_MODE).toString();
			}

			if (TextUtils.isEmpty(scheme)) {
				scheme = pyxisAppScheme;
			}

			if (TextUtils.isEmpty(appHost)) {
				appHost = pyxisAppHost;
			}

			if (aidGet == null) {
				aidGet = getManifestValueBool(pyxisConst.XML_PARAM_NAME_PYXIS_AID_GET);
			}

			if (pid == null) {
				pid = getManifestValueInteger(
						pyxisConst.XML_PARAM_NAME_PYXIS_PID).toString();
			}

			// AndroidManifestに設定されたインストール通知用の引数を取得
			if (pyxisMv == null) {
				pyxisMv = getManifestValueString(
						pyxisConst.XML_PARAM_NAME_PYXIS_INSTALL_MV, false);
			}

			if (pyxisSuid == null) {
				pyxisSuid = getManifestValueString(
						pyxisConst.XML_PARAM_NAME_PYXIS_INSTALL_SUID, false);
			}

			if (pyxisSales == null) {
				pyxisSales = getManifestValueString(
						pyxisConst.XML_PARAM_NAME_PYXIS_INSTALL_SALES, false);
			}

			if (pyxisVolume == null) {
				pyxisVolume = getManifestValueString(
						pyxisConst.XML_PARAM_NAME_PYXIS_INSTALL_VOLUME, false);
			}

			if (pyxisProfit == null) {
				pyxisProfit = getManifestValueString(
						pyxisConst.XML_PARAM_NAME_PYXIS_INSTALL_PROFIT, false);
			}

			if (pyxisOthers == null) {
				pyxisOthers = getManifestValueString(
						pyxisConst.XML_PARAM_NAME_PYXIS_INSTALL_OTHERS, false);
			}

			if (initExeced) {
				/** ################# tracelog 処理開始 ################# */
				PyxisUtils.recordLog(className,
						(new Throwable()).getStackTrace()[0].getMethodName(),
						(new Throwable()).getStackTrace()[0].getLineNumber(),
						"初期化処理実行済みなので、初期化処理中止。", 1, false);
				return;
			}

			if (referrerFlg == null) {
				referrerFlg = "0";
			}

			initExeced = true;

			// ログの初期化有無
			PyxisUtils pyxisUtils = new PyxisUtils();

			// ブラウザから起動された場合は初期化しない
			/** ################# tracelog 処理開始 ################# */
			PyxisUtils.recordLog(className,
					(new Throwable()).getStackTrace()[0].getMethodName(),
					(new Throwable()).getStackTrace()[0].getLineNumber(),
					"初期化処理開始", 1, !pyxisUtils.isStartupFromBrowser());

			/** ################# tracelog パラメータ ################# */
			PyxisUtils.recordLog(className,
					(new Throwable()).getStackTrace()[0].getMethodName(),
					(new Throwable()).getStackTrace()[0].getLineNumber(),
					"初期化時パラメータ情報 [pid = " + pid + " / logMode = " + logMode
							+ " / saveMax = " + saveMax + " / saveMode = "
							+ saveMode + " / enableSDCard = " + enableSDCard
							+ " / scheme = " + scheme + " / appHost = "
							+ appHost + " ]", 1, false);

			if (logMode) {
				/** ################# SDカードチェック ################# */
				if (!PyxisTracking.enableSDCard) {
					PyxisUtils
							.recordLog(
									className,
									(new Throwable()).getStackTrace()[0]
											.getMethodName(),
									(new Throwable()).getStackTrace()[0]
											.getLineNumber(),
									"AndroidManifest.xml に WRITE_EXTERNAL_STORAGE のパーミッションが設定されていません。",
									1, false);
				} else if (!Environment.MEDIA_MOUNTED.equals(Environment
						.getExternalStorageState())) {
					enableSDCard = false;
					PyxisUtils
							.recordLog(
									className,
									(new Throwable()).getStackTrace()[0]
											.getMethodName(),
									(new Throwable()).getStackTrace()[0]
											.getLineNumber(),
									"SDカードへの出力が出来ません。理由：SDカードが存在しない。又はSDカードがPCにマウントされている等。",
									1, false);
				}
			}

			// インストール情報取得
			PyxisDBHelper dbHelper = new PyxisDBHelper();
			HashMap<String, Object> installMap = dbHelper.selInstall();

			isEnableCpi = false;

			// 初回起動かチェック
			if (installMap.get("START_DATE") != null) {

				SharedPreferences pref = fromContext.getSharedPreferences(
						pyxisConst.PREF_PYXIS, Context.MODE_PRIVATE);
				int aliveFlg = pref.getInt(
						pyxisConst.PREF_PYXIS_SESSION_ALIVE_FLG, 2);

				/** ################# tracelog セッション状態 ################# */
				PyxisUtils.recordLog(className,
						(new Throwable()).getStackTrace()[0].getMethodName(),
						(new Throwable()).getStackTrace()[0].getLineNumber(),
						"セッション : " + (aliveFlg == 1 ? "有効" : "無効"), 1, false);

				if (aliveFlg == 1) {
					isFirst = true;

					/** ################# tracelog 初回起動チェック ################# */
					PyxisUtils.recordLog(className, (new Throwable())
							.getStackTrace()[0].getMethodName(),
							(new Throwable()).getStackTrace()[0]
									.getLineNumber(), "アプリ初回起動", 1, false);
				} else {
					isFirst = false;

					/** ################# tracelog 初回起動チェック ################# */
					PyxisUtils.recordLog(className, (new Throwable())
							.getStackTrace()[0].getMethodName(),
							(new Throwable()).getStackTrace()[0]
									.getLineNumber(), "アプリ初回起動以外", 1, false);
				}

				if (aliveFlg != 0) {
					// フラグを戻す(0はセッション生存無効)
					Editor e = pref.edit();
					e.putInt(pyxisConst.PREF_PYXIS_SESSION_ALIVE_FLG, 0);
					e.commit();

					// 書けるまで待つ
					// 念のため、別個にインスタンス生成
					SharedPreferences refpref = fromContext
							.getSharedPreferences(pyxisConst.PREF_PYXIS,
									Context.MODE_PRIVATE);

					boolean isRead = false;
					int count = 0;
					while (!isRead) {

						// 2はunknown
						if (refpref.getInt(
								pyxisConst.PREF_PYXIS_SESSION_ALIVE_FLG, 2) == 0) {
							// あんまり意味ないけど一応
							isRead = true;
							break;
						} else {
							count++;
							// 0.5秒ウェイト
							Thread.sleep(500);
						}

						// カウンタを超えたら諦める
						if (count > 3) {
							/**
							 * ################# errorlog Shared Preference
							 * 書き込み失敗 #################
							 */
							PyxisUtils
									.recordLog(
											className,
											(new Throwable()).getStackTrace()[0]
													.getMethodName(),
											(new Throwable()).getStackTrace()[0]
													.getLineNumber(),
											"Shared Preferenceへの書き込み失敗の可能性 : param ["
													+ pyxisConst.PREF_PYXIS_SESSION_ALIVE_FLG
													+ "]", 2, false);
							break;
						}
					}
				}

			} else {

				isFirst = true;

				/** ################# tracelog 初回起動チェック ################# */
				PyxisUtils.recordLog(className,
						(new Throwable()).getStackTrace()[0].getMethodName(),
						(new Throwable()).getStackTrace()[0].getLineNumber(),
						"アプリ初回起動", 1, false);

				// INSTALLテーブルに起動日時を登録
				Double dateTimeLong = (double) (System.currentTimeMillis() / 1000);
				dbHelper.insInstall(null, null, dateTimeLong, null);
			}

			dbHelper.close();

			// DBのアップデート
			PyxisUpdate pyxisUpdate = new PyxisUpdate();
			pyxisUpdate.updateDB();

			// アプリ内計測の送信中フラグを初期化
			pyxisUpdate.initAppSending();

		} catch (Exception e) {
			/** ################# errorlog ################# */
			PyxisUtils.recordErrorLog(className,
					(new Throwable()).getStackTrace()[0].getMethodName(),
					(new Throwable()).getStackTrace()[0].getLineNumber(),
					"初期化処理に失敗", 2, false, e);

			// 原因追求のため可能な場合はsqliteファイルをSDカードにコピー
			copyDBtoExternalStorage();
		}

		/** ################# tracelog 処理終了 ################# */
		PyxisUtils.recordLog(className,
				(new Throwable()).getStackTrace()[0].getMethodName(),
				(new Throwable()).getStackTrace()[0].getLineNumber(),
				"初期化処理終了", 1, false);
	}

	/**
	 * CPI計測処理
	 * 
	 * @param pyxisMv
	 * @param suid
	 * @param sales
	 * @param volume
	 * @param profit
	 * @param others
	 */
	@SuppressWarnings("deprecation")
	public static void trackInstall(String mv, String suid, String sales,
			String volume, String profit, String others) {
		trackInstall(mv, suid, sales, volume, profit, others, false);
	}

	/**
	 * CPI計測処理
	 * 
	 * @param pyxisMv
	 * @param suid
	 * @param sales
	 * @param volume
	 * @param profit
	 * @param others
	 * 
	 */
	@SuppressWarnings("deprecation")
	protected static void trackInstall(String mv, String suid, String sales,
			String volume, String profit, String others, boolean receiveFlg) {

		/** ################# tracelog 処理開始 ################# */
		PyxisUtils.recordLog(className,
				(new Throwable()).getStackTrace()[0].getMethodName(),
				(new Throwable()).getStackTrace()[0].getLineNumber(),
				"CPI計測開始", 1, false);

		// INSTALL_REFERRERのonReceiveから起動されかつ3.1未満の場合
		if (receiveFlg == true && Integer.parseInt(VERSION.SDK) < 12) {
			/** ################# tracelog ################# */
			PyxisUtils.recordLog(className,
					(new Throwable()).getStackTrace()[0].getMethodName(),
					(new Throwable()).getStackTrace()[0].getLineNumber(),
					"INSTALL_REFERRER経由", 1, false);

			/** ################# tracelog ################# */
			PyxisUtils.recordLog(className,
					(new Throwable()).getStackTrace()[0].getMethodName(),
					(new Throwable()).getStackTrace()[0].getLineNumber(),
					"3.1未満", 1, false);
			return;
		}
		if (!initExeced) {
			// errorlog
			PyxisUtils.recordLog(className,
					(new Throwable()).getStackTrace()[0].getMethodName(),
					(new Throwable()).getStackTrace()[0].getLineNumber(),
					"初期化メソッド：PyxisTracking.initが呼ばれていません。", 2, false);
			return;
		}

		String salesStr = null;
		if (sales != null) {
			salesStr = sales.toString();
		}

		String volumeStr = null;
		if (volume != null) {
			volumeStr = volume.toString();
		}

		String profitStr = null;
		if (profit != null) {
			profitStr = profit.toString();
		}

		String othersStr = null;
		if (others != null) {
			othersStr = others.toString();
		}

		/** ################# tracelog パラメータ ################# */
		PyxisUtils.recordLog(className,
				(new Throwable()).getStackTrace()[0].getMethodName(),
				(new Throwable()).getStackTrace()[0].getLineNumber(),
				"CPI計測時の引数 [mv = " + mv + " / verify = " + " / suid = " + suid
						+ " / sales = " + salesStr + " / volume = " + volumeStr
						+ " / profit = " + profitStr + " / others = "
						+ othersStr + " ]", 1, false);

		try {
			pyxisMv = mv;

			pyxisVerify = "";

			pyxisSuid = "";
			if (!TextUtils.isEmpty(suid)) {
				pyxisSuid = suid;
			}

			pyxisSales = sales;
			pyxisVolume = volume;
			pyxisProfit = profit;
			pyxisOthers = others;

			PyxisCpiTracking pyxisCpiTracking = new PyxisCpiTracking();
			pyxisCpiTracking
					.execute(receiveFlg ? "1" : "0", pyxisVerify, sesid);

		} catch (Exception e) {
			/** ################# errorlog ################# */
			PyxisUtils.recordErrorLog(className,
					(new Throwable()).getStackTrace()[0].getMethodName(),
					(new Throwable()).getStackTrace()[0].getLineNumber(),
					"CPI計測処理に失敗", 2, false, e);

			// 原因追求のため可能な場合はsqliteファイルをSDカードにコピー
			copyDBtoExternalStorage();
		}

		/** ################# tracelog 処理終了 ################# */
		PyxisUtils.recordLog(className,
				(new Throwable()).getStackTrace()[0].getMethodName(),
				(new Throwable()).getStackTrace()[0].getLineNumber(),
				"CPI計測終了", 1, false);
	}

	/**
	 * アプリ内計測保存処理(制限ありモード、引数String)
	 * 
	 * @param pyxisMv
	 * @param pyxisVerify
	 * @param suid
	 * @param sales
	 * @param volume
	 * @param profit
	 * @param others
	 */
	public static void saveTrackApp(String pyxisMv, String suid, String sales,
			String volume, String profit, String others, AppLimitMode limitMode) {
		try {
			Integer salesInteger = null;
			if (!TextUtils.isEmpty(sales)) {
				salesInteger = Integer.valueOf(sales);
			}
			Integer volumeInteger = null;
			if (!TextUtils.isEmpty(volume)) {
				volumeInteger = Integer.valueOf(volume);
			}
			Integer profitInteger = null;
			if (!TextUtils.isEmpty(profit)) {
				profitInteger = Integer.valueOf(profit);
			}

			saveTrackApp(pyxisMv, suid, salesInteger, volumeInteger,
					profitInteger, others, limitMode);
		} catch (ClassCastException e) {

			PyxisUtils.recordLog(className,
					(new Throwable()).getStackTrace()[0].getMethodName(),
					(new Throwable()).getStackTrace()[0].getLineNumber(),
					"引数の型が不正です。", 1, false);
			return;
		}
	}

	/**
	 * アプリ内計測保存処理
	 * 
	 * @param pyxisMv
	 * @param pyxisVerify
	 * @param suid
	 * @param sales
	 * @param volume
	 * @param profit
	 * @param others
	 */
	public static void saveTrackApp(String pyxisMv, String suid,
			Integer sales, Integer volume, Integer profit, String others) {

		// アプリ内計測保存処理呼び出し
		saveTrackApp(pyxisMv, suid, sales, volume, profit, others,
				AppLimitMode.NONE);
	}

	/**
	 * アプリ内計測保存処理
	 * 
	 * @param pyxisMv
	 * @param pyxisVerify
	 * @param suid
	 * @param sales
	 * @param volume
	 * @param profit
	 * @param others
	 */
	public static void saveTrackApp(String pyxisMv, String suid, String sales,
			String volume, String profit, String others) {
		try {
			Integer salesInteger = null;
			if (!TextUtils.isEmpty(sales)) {
				salesInteger = Integer.valueOf(sales);
			}
			Integer volumeInteger = null;
			if (!TextUtils.isEmpty(volume)) {
				volumeInteger = Integer.valueOf(volume);
			}
			Integer profitInteger = null;
			if (!TextUtils.isEmpty(profit)) {
				profitInteger = Integer.valueOf(profit);
			}
			saveTrackApp(pyxisMv, suid, salesInteger, volumeInteger,
					profitInteger, others);
		} catch (ClassCastException e) {

			PyxisUtils.recordLog(className,
					(new Throwable()).getStackTrace()[0].getMethodName(),
					(new Throwable()).getStackTrace()[0].getLineNumber(),
					"引数の型が不正です。", 1, false);
			return;
		}
	}

	/**
	 * アプリ内計測保存処理
	 * 
	 * @param pyxisMv
	 * @param suid
	 * @param sales
	 * @param volume
	 * @param profit
	 * @param others
	 * @param appLimitMode
	 */
	public static void saveTrackApp(String pyxisMv, String suid,
			Integer sales, Integer volume, Integer profit, String others,
			AppLimitMode limitMode) {

		/** ################# tracelog 処理開始 ################# */
		PyxisUtils.recordLog(className,
				(new Throwable()).getStackTrace()[0].getMethodName(),
				(new Throwable()).getStackTrace()[0].getLineNumber(),
				"アプリ内計測保存処理開始", 1, false);

		// SharedPreferenceとEditorを取得
		SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(context);

		// 引数で渡された制限区分に従い、mvごとの送信日時を確認

		// 制限モードをクラス変数に保持
		appLimitMode = limitMode;
		boolean isLimit = false;
		switch (limitMode) {
		case NONE: // 制限なし
			// do nothing
			break;
		case DAILY: // 一日一回
			// 現在日時を取得
			long currentDateLong = System.currentTimeMillis() / 1000;
			// SharedPrefarenceからmvの前回呼び出し時間を取得
			long lastDateLong = sharedPreferences.getLong(
					PyxisConst.PYXIS_APP_SEND_TIME + pyxisMv, 0);

			PyxisUtils.recordLog(className,
					(new Throwable()).getStackTrace()[0].getMethodName(),
					(new Throwable()).getStackTrace()[0].getLineNumber(),
					"前回送信：" + lastDateLong, 1, false);

			// 取得した時間から1日以内の場合、アプリ内計測は行わない
			isLimit = (currentDateLong - lastDateLong) < 86400;
			break;
		case DAU: // 同日付でのApp成果の保存は行わない
			Calendar calendar = Calendar.getInstance();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			String currentDate = sdf.format(calendar.getTime());
			String lastDate = sharedPreferences.getString(
					PyxisConst.PYXIS_APP_SEND_YMD + pyxisMv, "");

			PyxisUtils.recordLog(className,
					(new Throwable()).getStackTrace()[0].getMethodName(),
					(new Throwable()).getStackTrace()[0].getLineNumber(),
					"前回送信：" + lastDate, 1, false);

			// 前回起動と同じ日付の場合、アプリ内計測は行わない
			isLimit = currentDate.equals(lastDate);
		default:
			break;
		}

		if (isLimit) {
			// 制限に引っ掛かった場合は処理終了
			PyxisUtils.recordLog(
					className,
					(new Throwable()).getStackTrace()[0].getMethodName(),
					(new Throwable()).getStackTrace()[0].getLineNumber(),
					"アプリ内成果保存条件を満たしていないため、処理を終了します。成果送信条件："
							+ limitMode.getDescription(), 1, false);
			return;
		}

		// 文字コード変換
		suid = PyxisUtils.utf8Encode(suid);
		others = PyxisUtils.utf8Encode(others);

		try {
			/** ################# tracelog パラメータ ################# */
			PyxisUtils.recordLog(
					className,
					(new Throwable()).getStackTrace()[0].getMethodName(),
					(new Throwable()).getStackTrace()[0].getLineNumber(),
					"アプリ内計測保存処理パラメータ情報 [mv = " + pyxisMv + " / verify = "
							+ pyxisVerify + " / suid = " + suid + " / sales = "
							+ (sales == null ? "" : Integer.toString(sales))
							+ " / volume = "
							+ (volume == null ? "" : Integer.toString(volume))
							+ " / profit = "
							+ (profit == null ? "" : Integer.toString(profit))
							+ " / others = " + (others == null ? "" : others)
							+ " ]", 1, false);

			if (!initExeced) {
				// errorlog
				PyxisUtils.recordLog(className,
						(new Throwable()).getStackTrace()[0].getMethodName(),
						(new Throwable()).getStackTrace()[0].getLineNumber(),
						"初期化メソッド：PyxisTracking.initが呼ばれていません。", 2, false);
				return;
			}

			PyxisAppTracking pyxisAppTracking = new PyxisAppTracking();
			pyxisAppTracking.save(pyxisMv, pyxisVerify, suid, siteId, sales,
					volume, profit, others, saveMax, saveMode);

			/** ################# errorlog ################# */
			PyxisUtils.recordLog(className,
					(new Throwable()).getStackTrace()[0].getMethodName(),
					(new Throwable()).getStackTrace()[0].getLineNumber(),
					"アプリ内計測保存処理成功", 0, false);

		} catch (Exception e) {
			/** ################# errorlog ################# */
			PyxisUtils.recordErrorLog(className,
					(new Throwable()).getStackTrace()[0].getMethodName(),
					(new Throwable()).getStackTrace()[0].getLineNumber(),
					"アプリ内計測保存処理失敗", 2, false, e);
		}

		/** ################# tracelog 処理終了 ################# */
		PyxisUtils.recordLog(className,
				(new Throwable()).getStackTrace()[0].getMethodName(),
				(new Throwable()).getStackTrace()[0].getLineNumber(),
				"アプリ内計測保存処理終了", 1, false);

		// SharedPreferenceとEditorを取得
		//SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		Editor editor = sharedPreferences.edit();
		// 現在日時を起動時間としてSharedPrefarenceに書き込み
		long currentDateLong = System.currentTimeMillis() / 1000;
		editor.putLong(PyxisConst.PYXIS_APP_SEND_TIME + pyxisMv,
				currentDateLong);
		// 現在日付をSharedPreferencesに書き込み
		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String currentDate = sdf.format(calendar.getTime());
		editor.putString(PyxisConst.PYXIS_APP_SEND_YMD + pyxisMv, currentDate);
		editor.commit();

		// FBイベント送信パラメータの初期化
		PyxisFbEventParameterConfig.flush();

	}

	/**
	 * アプリ内計測送信処理
	 */
	public static void sendTrackApp() {

		/** ################# tracelog 処理開始 ################# */
		PyxisUtils.recordLog(className,
				(new Throwable()).getStackTrace()[0].getMethodName(),
				(new Throwable()).getStackTrace()[0].getLineNumber(),
				"アプリ内計測送信処理開始", 1, false);

		try {

			if (!initExeced) {
				// errorlog
				PyxisUtils.recordLog(className,
						(new Throwable()).getStackTrace()[0].getMethodName(),
						(new Throwable()).getStackTrace()[0].getLineNumber(),
						"初期化メソッド：PyxisTracking.initが呼ばれていません。", 2, false);
				return;
			}

			PyxisAppTracking pyxisAppTracking = new PyxisAppTracking();
			pyxisAppTracking.send();

		} catch (Exception e) {
			/** ################# errorlog ################# */
			PyxisUtils.recordErrorLog(className,
					(new Throwable()).getStackTrace()[0].getMethodName(),
					(new Throwable()).getStackTrace()[0].getLineNumber(),
					"アプリ内計測送信処理失敗", 2, false, e);
		}

		/** ################# tracelog 処理開始 ################# */
		PyxisUtils.recordLog(className,
				(new Throwable()).getStackTrace()[0].getMethodName(),
				(new Throwable()).getStackTrace()[0].getLineNumber(),
				"アプリ内計測送信処理終了", 1, false);

	}
	


	/**
	 * Custom Audience用データ送信
	 * 
	 * @param mv
	 * @param fui
	 * @param fai
	 * @param em
	 * @param pn
	 */
	public static void sendCAParam(String mv, String em, String pn, String fui, String fai){
		/** ################# tracelog 処理開始 ################# */
		PyxisUtils.recordLog(className,
				(new Throwable()).getStackTrace()[0].getMethodName(),
				(new Throwable()).getStackTrace()[0].getLineNumber(),
				"Custom Audience用データ保存＆送信処理開始", 1, false);
		
		if (!initExeced) {
			// errorlog
			PyxisUtils.recordLog(className,
					(new Throwable()).getStackTrace()[0].getMethodName(),
					(new Throwable()).getStackTrace()[0].getLineNumber(),
					"初期化メソッド：PyxisTracking.initが呼ばれていません。", 2, false);
			return;
		}
		
		List<String> otherList = new ArrayList<String>();
		otherList.add("_pyxis_is_ca=1");
		
		if(!TextUtils.isEmpty(fui)) {
			otherList.add("fui=" + fui);
		}
		
		if(!TextUtils.isEmpty(fai)) {
			otherList.add("fai=" + fai);
		}
		
		if(!TextUtils.isEmpty(em)) {
			String hashEm = PyxisUtils.getSHA256(em);
			otherList.add("em=" + hashEm);
		}
		
		if(!TextUtils.isEmpty(pn)) {
			String hashPn = PyxisUtils.getSHA256(pn);
			otherList.add("pn=" + hashPn);
		}
		
		String others = "";
		if(otherList.size() > 0){
			others = PyxisUtils.join((String[])otherList.toArray(new String[otherList.size()]), "&");
		}
		
		/** ################# tracelog 処理開始 ################# */
		PyxisUtils.recordLog(className,
				(new Throwable()).getStackTrace()[0].getMethodName(),
				(new Throwable()).getStackTrace()[0].getLineNumber(),
				"Custom Audience用データ保存開始", 1, false);

		PyxisAppTracking pyxisAppTracking = new PyxisAppTracking();
		pyxisAppTracking.save(mv, pyxisVerify, "", siteId, null,
				null, null, others, saveMax, saveMode);

		/** ################# tracelog 処理開始 ################# */
		PyxisUtils.recordLog(className,
				(new Throwable()).getStackTrace()[0].getMethodName(),
				(new Throwable()).getStackTrace()[0].getLineNumber(),
				"Custom Audience用データ保存終了", 1, false);
		
		/** ################# tracelog 処理開始 ################# */
		PyxisUtils.recordLog(className,
				(new Throwable()).getStackTrace()[0].getMethodName(),
				(new Throwable()).getStackTrace()[0].getLineNumber(),
				"Custom Audience用データ送信開始", 1, false);
		
		pyxisAppTracking.send();
		
		/** ################# tracelog 処理開始 ################# */
		PyxisUtils.recordLog(className,
				(new Throwable()).getStackTrace()[0].getMethodName(),
				(new Throwable()).getStackTrace()[0].getLineNumber(),
				"Custom Audience用データ送信終了", 1, false);
		
		/** ################# tracelog 処理終了 ################# */
		PyxisUtils.recordLog(className,
				(new Throwable()).getStackTrace()[0].getMethodName(),
				(new Throwable()).getStackTrace()[0].getLineNumber(),
				"Custom Audience用データ保存＆送信処理終了", 1, false);
		
	}
	
	/**
	 * sqliteファイルのコピー (手動)
	 */
	public static void copyDBtoExternalStorage() {

		/** ################# tracelog 処理開始 ################# */
		PyxisUtils.recordLog(className,
				(new Throwable()).getStackTrace()[0].getMethodName(),
				(new Throwable()).getStackTrace()[0].getLineNumber(),
				"sqliteファイルのコピー (手動)開始", 1, false);

		if (!initExeced) {
			// errorlog
			PyxisUtils.recordLog(className,
					(new Throwable()).getStackTrace()[0].getMethodName(),
					(new Throwable()).getStackTrace()[0].getLineNumber(),
					"初期化メソッド：PyxisTracking.initが呼ばれていません。", 2, false);
			return;
		}

		PyxisUtils pyxisUtils = new PyxisUtils();
		if (!pyxisUtils.copyDBtoExternalStorage()) {
			PyxisUtils.recordLog(className,
					(new Throwable()).getStackTrace()[0].getMethodName(),
					(new Throwable()).getStackTrace()[0].getLineNumber(),
					"sqliteファイルのSDカードへのコピーに失敗", 2, false);
		}

		/** ################# tracelog 処理終了 ################# */
		PyxisUtils.recordLog(className,
				(new Throwable()).getStackTrace()[0].getMethodName(),
				(new Throwable()).getStackTrace()[0].getLineNumber(),
				"sqliteファイルのコピー (手動)終了", 1, false);

	}

	/**
	 * オプトアウト設定
	 * 
	 * @param optOutFlg
	 *            trueの場合、オプトアウトする falseの場合、オプトアウトしない
	 */
	public static void setOptOut(boolean optOutFlg) {

		// オプトアウト設定をDBに保存
		PyxisUtils.setOptOut(optOutFlg);
	}

	/**
	 * オプトアウト情報参照
	 * 
	 * @return 現在のオプトアウト設定値 trueの場合、オプトアウトする falseの場合、オプトアウトしない
	 */
	public static boolean getOptOut() {
		// オプトアウト設定を返す
		return PyxisUtils.getBoolOptOut();
	}

	public static void setValueToSum(Float _valueToSum) {
		PyxisFbEventParameterConfig._valueToSum = _valueToSum;
	}

	public static void setFbLevel(Integer fb_level) {
		PyxisFbEventParameterConfig.fb_level = fb_level;
	}

	public static void setFbSuccess(Boolean fb_success) {
		PyxisFbEventParameterConfig.fb_success = fb_success;
	}

	public static void setFbContentType(String fb_content_type) {
		PyxisFbEventParameterConfig.fb_content_type = PyxisUtils
				.utf8Encode(fb_content_type);
	}

	public static void setFbContentId(String fb_content_id) {
		PyxisFbEventParameterConfig.fb_content_id = PyxisUtils
				.utf8Encode(fb_content_id);
	}

	public static void setFbRegistrationMethod(String fb_registration_method) {
		PyxisFbEventParameterConfig.fb_registration_method = PyxisUtils
				.utf8Encode(fb_registration_method);
	}

	public static void setFbPaymentInfoAvailable(
			Boolean fb_payment_info_available) {
		PyxisFbEventParameterConfig.fb_payment_info_available = fb_payment_info_available;
	}

	public static void setFbMaxRatingValue(Integer fb_max_rating_value) {
		PyxisFbEventParameterConfig.fb_max_rating_value = fb_max_rating_value;
	}

	public static void setFbNumItems(Integer fb_num_items) {
		PyxisFbEventParameterConfig.fb_num_items = fb_num_items;
	}

	public static void setFbSearchString(String fb_search_string) {
		PyxisFbEventParameterConfig.fb_search_string = PyxisUtils
				.utf8Encode(fb_search_string);
	}

	public static void setFbDescription(String fb_description) {
		PyxisFbEventParameterConfig.fb_description = PyxisUtils
				.utf8Encode(fb_description);
	}

	/**
	 * @Deprecated AndroidManifest.xmlから値を取得(文字列)
	 * 
	 * @param name
	 * @return String
	 */
	protected static String getManifestValueString(String name) {
		return getManifestValueString(name, true);
	}

	protected static String getManifestValueString(String name, Boolean flg) {

		PyxisConst pyxisConst = new PyxisConst();
		String returnStr = null;
		try {
			Object manifestValue = getManifestValue(name,
					pyxisConst.XML_VALUE_TYPE_STRING);
			if (manifestValue != null) {
				returnStr = manifestValue.toString();
			}
		} catch (Exception e) {
			PyxisUtils.recordErrorLog(className,
					(new Throwable()).getStackTrace()[0].getMethodName(),
					(new Throwable()).getStackTrace()[0].getLineNumber(),
					"AndroidManifest.xml から" + name + "の値の取得に失敗しました。", 2,
					false, e);
		}

		if (returnStr == null && flg == true) {
			PyxisUtils.recordLog(className,
					(new Throwable()).getStackTrace()[0].getMethodName(),
					(new Throwable()).getStackTrace()[0].getLineNumber(),
					"AndroidManifest.xml から" + name + "の値が取得出来ません。", 2, false);
		}

		return returnStr;

	}

	/**
	 * AndroidManifest.xmlから値を取得(数値)
	 * 
	 * @param name
	 * @return Integer
	 */
	private static Integer getManifestValueInteger(String name) {

		PyxisConst pyxisConst = new PyxisConst();

		Integer returnInt = null;
		try {
			Object manifestValue = getManifestValue(name,
					pyxisConst.XML_VALUE_TYPE_INT);
			if (manifestValue != null) {
				returnInt = (Integer) manifestValue;
			}
		} catch (Exception e) {
			PyxisUtils.recordErrorLog(className,
					(new Throwable()).getStackTrace()[0].getMethodName(),
					(new Throwable()).getStackTrace()[0].getLineNumber(),
					"AndroidManifest.xml から" + name + "の値の取得に失敗しました。", 2,
					false, e);
		}

		if (returnInt == null) {
			PyxisUtils.recordLog(className,
					(new Throwable()).getStackTrace()[0].getMethodName(),
					(new Throwable()).getStackTrace()[0].getLineNumber(),
					"AndroidManifest.xml から" + name + "の値が取得出来ません。", 2, false);
		}

		return returnInt;
	}

	/**
	 * AndroidManifest.xmlから値を取得(Boolean)
	 * 
	 * @param name
	 * @return
	 */
	private static Boolean getManifestValueBool(String name) {

		PyxisConst pyxisConst = new PyxisConst();

		Boolean returnBool = null;

		try {
			Object manifestValue = getManifestValue(name,
					pyxisConst.XML_VALUE_TYPE_BOOL);
			if (manifestValue != null) {
				returnBool = (Boolean) manifestValue;
			}
		} catch (Exception e) {
			PyxisUtils.recordErrorLog(className,
					(new Throwable()).getStackTrace()[0].getMethodName(),
					(new Throwable()).getStackTrace()[0].getLineNumber(),
					"AndroidManifest.xml から" + name + "の値の取得に失敗しました。", 2,
					false, e);
		}

		if (returnBool == null) {
			PyxisUtils.recordLog(className,
					(new Throwable()).getStackTrace()[0].getMethodName(),
					(new Throwable()).getStackTrace()[0].getLineNumber(),
					"AndroidManifest.xml から" + name + "の値が取得出来ません。", 2, false);
		}

		return returnBool;

	}

	// 未使用 --------------------------------------------------
	// /** @Deprecated
	// * AndroidManifest.xmlから値を取得(浮動小数点)
	// *
	// * @param name
	// * @return
	// */
	// private static Float getManifestValueFloat(String name) {
	//
	// Float returnFloat = null;
	//
	// try {
	// returnFloat = (Float) getManifestValue(name,
	// pyxisConst.XML_VALUE_TYPE_FLOAT);
	// } catch (Exception e) {
	// PyxisUtils.recordLog(className,(new
	// Throwable()).getStackTrace()[0].getMethodName(),(new
	// Throwable()).getStackTrace()[0].getLineNumber() ,
	// "AndroidManifest.xml から" + name + "の値の取得に失敗しました。" + e, 2,false);
	// }
	//
	// if(returnFloat == null) {
	// PyxisUtils.recordLog(className,(new
	// Throwable()).getStackTrace()[0].getMethodName(),(new
	// Throwable()).getStackTrace()[0].getLineNumber() ,
	// "AndroidManifest.xml から" + name + "の値が取得出来ません。", 2,false);
	// }
	//
	// return returnFloat;
	// }
	// 未使用 --------------------------------------------------

	/**
	 * AndroidManifest.xmlから値を取得
	 * 
	 * @param context
	 * @param name
	 *            項目名
	 * @param type
	 *            型
	 * @return Object
	 */
	private static Object getManifestValue(String name, String type) {

		Object returnValue = null;

		try {
			ApplicationInfo info = context.getPackageManager()
					.getApplicationInfo(context.getPackageName(),
							PackageManager.GET_META_DATA);

			Bundle metaData = info.metaData;

			if ("String".equals(type)) {
				returnValue = metaData.getString(name);
			} else if ("int".equals(type)) {
				returnValue = metaData.getInt(name);
			} else if ("boolean".equals(type)) {
				returnValue = metaData.getBoolean(name);
			} else if ("float".equals(type)) {
				returnValue = metaData.getFloat(name);
			}

		} catch (NameNotFoundException e) {
			PyxisUtils
					.recordErrorLog(className, (new Throwable())
							.getStackTrace()[0].getMethodName(),
							(new Throwable()).getStackTrace()[0]
									.getLineNumber(), "AndroidManifest.xml に"
									+ name + "の項目が存在しません。", 2, false, e);
		}

		return returnValue;

	}

}
