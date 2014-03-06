package jp.co.septeni.pyxis.PyxisTracking;

public class PyxisConst {

	/**
	 * --------- 定数定義 ---------
	 * */
	// SDK更新時に手動で更新 start >
	// ----------------------------------
	/** Pyxis SDK Version ※バージョンアップごとに更新が必要 */
	protected final String SDK_VERSION = "1.1.0";

	// end > SDK更新時に手動で更新
	// -----------------------------------

	// 以下、値固定
	// -----------------------------------

	/** パッケージネーム */
	protected final String PYXIS_PACKAGE_NAME = "jp.co.septeni.pyxis";

	/** アプリ内計測の保存数のシステム的な最大値 */
	protected final int PYXIS_APP_LIMIT_SAVE_MAX = 9999;

	/** アプリ内計測の保存モード（追記なし） */
	protected final int PYXIS_APP_SAVE_NON_ADD = 0;

	/** アプリ内計測の保存モード（古いレコードを上書き） */
	protected final int PYXIS_APP_SAVE_OLD_REWRITE = 1;

	/** セッションID保存期間 (24時間) */
	protected final int PYXIS_APP_SAVE_SESSION_HOUR = 24;

	/** 圧縮上限数 */
	protected final int PYXIS_APP_COMPRESS_LIMIT = 100;

	// 通信関連

	// TODO テスト用ドメインを指定　★☆★リリース時には本番用(msr.pyxis-social.com)に切り替えること★☆★
	/** Pyxis development ドメイン */
	// protected final String PYXIS_HOST = "plrs-d-msr.devsep.com";

	/** Pyxis stagingドメイン */
	protected final String PYXIS_HOST = "plrs-s-msr.devsep.com";

	/** Pyxis ドメイン */
	// protected final String PYXIS_HOST = "msr.pyxis-social.com";

	/** UUID取得用URI */
	protected final String PATH_UUID = PYXIS_HOST + "/info/get_data/";

	/** 成果点情報取得用URI */
	protected final String PATH_ACTION_POINT = PYXIS_HOST + "/info/get_cv/";

	/** CPI計測通知用URI */
	protected final String PATH_CPI_TRACKING = PYXIS_HOST + "/cv/cpi/";

	/** FB CPI計測通知用URI */
	protected final String PATH_FB_CPI_TRACKING = PYXIS_HOST + "/cv/mai/";

	/** アプリ内計測通知用URI */
	protected final String PATH_APP_TRACKING = PYXIS_HOST + "/cv/app/";

	/** UUID取得用HTTP URL */
	protected final String HTTP_PATH_UUID = "http://" + PATH_UUID;

	/** 成果点情報取得用HTTP URL */
	protected final String HTTP_PATH_ACTION_POINT = "http://" + PATH_ACTION_POINT;

	/** CPI計測通知用HTTP URL */
	protected final String HTTP_PATH_TRACKING = "http://" + PATH_CPI_TRACKING;

	/** FB CPI計測通知用HTTP URL */
	protected final String HTTP_PATH_FB_TRACKING = "http://" + PATH_FB_CPI_TRACKING;

	/** アプリ内計測通知用HTTP URL */
	protected final String HTTP_PATH_APP_TRACKING = "http://" + PATH_APP_TRACKING;

	// AndroidManifest.xml 関連

	/** SiteIdの項目名 */
	protected final String XML_PARAM_NAME_PYXIS_SITE_ID = "PYXIS_SITE_ID";

	/** レコード保存数の最大の項目名 */
	protected final String XML_PARAM_NAME_PYXIS_APP_SAVE_MAX = "PYXIS_APP_SAVE_MAX";

	/** 保存モードの項目名 */
	protected final String XML_PARAM_NAME_PYXIS_APP_SAVE_MODE = "PYXIS_APP_SAVE_MODE";

	/** ログモードの項目名 */
	protected final String XML_PARAM_NAME_PYXIS_LOG_MODE = "PYXIS_LOG_MODE";

	/** MVの項目名 */
	protected final String XML_PARAM_NAME_PYXIS_INSTALL_MV = "PYXIS_INSTALL_MV";

	/** suidの項目名 */
	protected final String XML_PARAM_NAME_PYXIS_INSTALL_SUID = "PYXIS_INSTALL_SUID";

	/** salesの項目名 */
	protected final String XML_PARAM_NAME_PYXIS_INSTALL_SALES = "PYXIS_INSTALL_SALES";

	/** volumeの項目名 */
	protected final String XML_PARAM_NAME_PYXIS_INSTALL_VOLUME = "PYXIS_INSTALL_VOLUME";

	/** profitの項目名 */
	protected final String XML_PARAM_NAME_PYXIS_INSTALL_PROFIT = "PYXIS_INSTALL_PROFIT";

	/** verifyの項目名 */
	protected final String XML_PARAM_NAME_PYXIS_INSTALL_OTHERS = "PYXIS_INSTALL_OTHERS";

	/** AndroidId取得可否の項目名 */
	protected final String XML_PARAM_NAME_PYXIS_AID_GET = "PYXIS_AID_GET";

	/** プロモーションIDの項目名 */
	protected final String XML_PARAM_NAME_PYXIS_PID = "PYXIS_PID";

	/**
	 * 計測モードの項目名 0 = 通常CPI計測のみ 1 = FB計測のみ 2 = 通常 & FB計測
	 * */
	protected final String XML_PARAM_NAME_PYXIS_CV_MODE = "PYXIS_CV_MODE";

	/** AndroidManifest.xmlの型（String) */
	protected final String XML_VALUE_TYPE_STRING = "String";

	/** AndroidManifest.xmlの型（int) */
	protected final String XML_VALUE_TYPE_INT = "int";

	/** AndroidManifest.xmlの型（boolean) */
	protected final String XML_VALUE_TYPE_BOOL = "boolean";

	/** AndroidManifest.xmlの型（float) */
	protected final String XML_VALUE_TYPE_FLOAT = "float";

	// DB関連

	/** sqliteファイル名 */
	protected static final String DB_NAME = "pyxisTracking.db";

	/** ユーザ情報保存用テーブル */
	protected final String DB_TABLE_NAME_USER = "USER";

	/** CPI計測保存用テーブル */
	protected final String DB_TABLE_NAME_INSTALL = "INSTALL";

	/** アプリ内計測保存用テーブル */
	protected final String DB_TABLE_NAME_ACTION = "ACTION";

	/** ユーザ情報保存用テンポラリテーブル */
	protected final String DB_TABLE_NAME_TMP_USER = "TMP_USER";

	/** CPI計測保存用テンポラリテーブル */
	protected final String DB_TABLE_NAME_TMP_INSTALL = "TMP_INSTALL";

	/** アプリ内計測保存用テンポラリテーブル */
	protected final String DB_TABLE_NAME_TMP_ACTION = "TMP_ACTION";

	/** DBの型（TEXT） */
	protected final String DB_COLMUN_TYPE_TEXT = "TEXT";

	/** DBの型（INTEGER） */
	protected final String DB_COLMUN_TYPE_INTEGER = "INTEGER";

	/** DBの型（INT） */
	protected final String DB_COLMUN_TYPE_INT = "INT";

	/** バージョン管理用カラム名 */
	protected final String DB_VERSION_COLMUN = "DB_VERSION";

	// SharedPreference関連

	/** Pyxis用Preference */
	protected final String PREF_PYXIS = "pyxis_prefs";

	/** セッションの生存フラグ */
	protected final String PREF_PYXIS_SESSION_ALIVE_FLG = "session_alive_flg";

	/** ir_id */
	protected static final String PREF_PYXIS_IR_ID = "pyxis_ir_id";

	// その他

	/** ログファイル名 */
	protected final String LOG_FILE_NAME = "pyxis.log";

	/** メイン処理の成功用ログのタグ */
	protected final String LOG_SUCCESS_TAG = "PYXIS-SUCCESS_LOG";

	/** traceログのタグ */
	protected final String LOG_TRACE_TAG = "PYXIS-TRACE_LOG";

	/** errorログのタグ */
	protected final String LOG_ERROR_TAG = "PYXIS-ERROR_LOG";

	/** 文字コード タブ */
	protected final String CODE_TAB = "\t";

	/** 文字コード 改行 */
	protected final String CODE_LF = "\n";

	/** アプリ側から任意に設定されるothers値の判別名 */
	protected final String OTHERS_APP_VAL_NAME = "ap=";

	/** othersに設定されるFB送信パラメータ値の判別名 */
	protected final String OTHERS_FB_VAL_NAME = "fb=";

	// デフォルトパラメータ
	/** ログ出力モードの切替（On：true／Off：false） */
	protected final boolean PYXIS_DEFAULT_LOG_MODE = false;

	/** 最大保存件数（推奨値999） */
	protected final int PYXIS_APP_DEFAULT_SAVE_MAX = 999;

	/**
	 * 最大保存件数達成時動作 （追記なし：0／古いデータを上書き：1／新しいデータを上書き：2）
	 * */
	protected final int PYXIS_APP_DEFAULT_MAX_MODE = 1;

	/**
	 * アプリ内送信制限用にSharedPreferenceに保存する送信時間用のキーの先頭文字列
	 */
	protected static final String PYXIS_APP_SEND_TIME = "PYXIS_APP_SEND_TIME_";

	/**
	 * アプリ内送信制限用にSharedPreferenceに保存する送信時間用のキーの先頭文字列
	 */
	protected static final String PYXIS_APP_SEND_YMD = "PYXIS_APP_SEND_YMD_";

}
