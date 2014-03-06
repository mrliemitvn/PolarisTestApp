package jp.co.septeni.pyxis.PyxisTracking;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

public class PyxisDBHelper extends SQLiteOpenHelper {

	protected static SQLiteDatabase db;
	private String className = this.getClass().getSimpleName();
	private PyxisConst pyxisConst = new PyxisConst();

	public PyxisDBHelper() {
		super(PyxisTracking.context, PyxisConst.DB_NAME, null, 1);
	}

	public PyxisDBHelper(Context context, String paramString) {
		super(PyxisTracking.context, paramString, null, 1);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {

		/** ################# tracelog 処理開始 ################# */
		PyxisUtils.recordLog(className,
				(new Throwable()).getStackTrace()[0].getMethodName(),
				(new Throwable()).getStackTrace()[0].getLineNumber(),
				"テーブルの作成開始", 1, false);

		try {
			// ユーザー情報TBL
			db.execSQL("CREATE TABLE USER(SESID TEXT, FB_SESID TEXT, UUID TEXT ,DB_VERSION TEXT, OPTOUT TEXT)");

			// アプリインストール計測TBL
			db.execSQL("CREATE TABLE INSTALL(START_FLAG INTEGER, FB_START_FLAG INTEGER, START_DATE TEXT ,IS_BROWSER_UPED INTEGER)");

			// アプリ内計測TBL
			db.execSQL("CREATE TABLE ACTION(ACT_ID INTEGER PRIMARY KEY"
					+ ", ACT_MV TEXT" + ", ACT_VERIFY TEXT"
					+ ", ACT_SESID TEXT" + ", ACT_SUID TEXT"
					+ ", ACT_DATE TEXT" + ", ACT_SENDING INTEGER"
					+ ", ACT_FST_FLAG INTEGER" + ", ACT_SALES INTEGER"
					+ ", ACT_VOLUME INTEGER" + ", ACT_PROFIT INTEGER"
					+ ", ACT_OTHERS TEXT" + ", ACT_ATTRIBUTION TEXT"
					+ ", ACT_FID TEXT)");

			// バージョン番号を付与
			ContentValues val = new ContentValues();
			val.put(pyxisConst.DB_VERSION_COLMUN, pyxisConst.SDK_VERSION);

			db.insert("USER", null, val);

			/** ################# tracelog 処理成功 ################# */
			PyxisUtils.recordLog(className,
					(new Throwable()).getStackTrace()[0].getMethodName(),
					(new Throwable()).getStackTrace()[0].getLineNumber(),
					"テーブルの作成成功", 1, false);
		} catch (Exception e) {
			/** ################# errorlog 処理失敗 ################# */
			PyxisUtils.recordErrorLog(className,
					(new Throwable()).getStackTrace()[0].getMethodName(),
					(new Throwable()).getStackTrace()[0].getLineNumber(),
					"テーブルの作成失敗 : ", 2, false, e);
		}

		/** ################# tracelog 処理終了 ################# */
		PyxisUtils.recordLog(className,
				(new Throwable()).getStackTrace()[0].getMethodName(),
				(new Throwable()).getStackTrace()[0].getLineNumber(),
				"テーブルの作成終了", 1, false);
	}

	// アップデート時
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// ここではアップデートを行わない
	}

	// ユーザー情報参照
	protected HashMap<String, Object> selUser() {

		/** ################# tracelog 処理開始 ################# */
		PyxisUtils.recordLog(className,
				(new Throwable()).getStackTrace()[0].getMethodName(),
				(new Throwable()).getStackTrace()[0].getLineNumber(),
				"USERテーブル参照開始", 1, false);

		Cursor cursor = null;
		HashMap<String, Object> resHashMap = new HashMap<String, Object>();
		try {
			db = this.getReadableDatabase();
			cursor = db.query("USER", new String[] { "SESID", "FB_SESID",
					"UUID", pyxisConst.DB_VERSION_COLMUN, "OPTOUT" }, null,
					null, null, null, null);

			if (cursor.getCount() > 0) {
				cursor.moveToFirst();
				String sesid = cursor.getString(cursor.getColumnIndex("SESID"));
				String fbSesid = cursor.getString(cursor
						.getColumnIndex("FB_SESID"));
				String uuid = cursor.getString(cursor.getColumnIndex("UUID"));
				String version = cursor.getString(cursor
						.getColumnIndex(pyxisConst.DB_VERSION_COLMUN));
				String optOut = cursor.getString(cursor
						.getColumnIndex("OPTOUT"));

				resHashMap.put("SESID", sesid);
				resHashMap.put("FB_SESID", fbSesid);
				resHashMap.put("UUID", uuid);
				resHashMap.put(pyxisConst.DB_VERSION_COLMUN, version);
				resHashMap.put("OPTOUT", optOut);
			}

			cursor.close();

			/** ################# tracelog 処理成功 ################# */
			PyxisUtils.recordLog(className,
					(new Throwable()).getStackTrace()[0].getMethodName(),
					(new Throwable()).getStackTrace()[0].getLineNumber(),
					"USERテーブル取得成功", 1, false);

		} catch (Exception e) {
			/** ################# errorlog 処理失敗 ################# */
			PyxisUtils.recordErrorLog(className,
					(new Throwable()).getStackTrace()[0].getMethodName(),
					(new Throwable()).getStackTrace()[0].getLineNumber(),
					"USERテーブル取得失敗 : ", 2, false, e);
		}

		/** ################# tracelog 処理終了 ################# */
		PyxisUtils.recordLog(className,
				(new Throwable()).getStackTrace()[0].getMethodName(),
				(new Throwable()).getStackTrace()[0].getLineNumber(),
				"USERテーブル参照終了", 1, false);

		return resHashMap;
	}

	// ユーザー情報追加
	protected void insUser(String sesid, String fbSesid, String uuid,
			String version, String optOut) {

		/** ################# tracelog 処理開始 ################# */
		PyxisUtils.recordLog(className,
				(new Throwable()).getStackTrace()[0].getMethodName(),
				(new Throwable()).getStackTrace()[0].getLineNumber(),
				"USERテーブル登録開始", 1, false);

		for (int i = 0; i < 10; i++) {
			try {

				if (TextUtils.isEmpty(sesid) && TextUtils.isEmpty(fbSesid)
						&& TextUtils.isEmpty(uuid)
						&& TextUtils.isEmpty(version)
						&& TextUtils.isEmpty(optOut)) {
					return;
				}

				// 使用禁止文字を取り除く
				sesid = replaceChar(sesid);
				sesid = replaceChar(fbSesid);
				uuid = replaceChar(uuid);

				ContentValues val = new ContentValues();
				if (!TextUtils.isEmpty(sesid)) {
					val.put("SESID", sesid);
				}
				if (!TextUtils.isEmpty(fbSesid)) {
					val.put("FB_SESID", fbSesid);
				}
				if (!TextUtils.isEmpty(uuid)) {
					val.put("UUID", uuid);
				}
				if (!TextUtils.isEmpty(version)) {
					val.put(pyxisConst.DB_VERSION_COLMUN, version);
				}
				if (!TextUtils.isEmpty(optOut)) {
					val.put("OPTOUT", optOut);
				}

				db = this.getWritableDatabase();
				db.insert("USER", null, val);

				/** ################# tracelog 処理成功 ################# */
				PyxisUtils.recordLog(className,
						(new Throwable()).getStackTrace()[0].getMethodName(),
						(new Throwable()).getStackTrace()[0].getLineNumber(),
						"USERテーブル登録成功", 1, false);
				break;

			} catch (Exception e) {
				/** ################# errorlog 処理失敗 ################# */
				PyxisUtils.recordErrorLog(className,
						(new Throwable()).getStackTrace()[0].getMethodName(),
						(new Throwable()).getStackTrace()[0].getLineNumber(),
						"USERテーブル登録失敗。リトライ : ", 2, false, e);
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				continue;
			}
		}

		/** ################# tracelog 処理終了 ################# */
		PyxisUtils.recordLog(className,
				(new Throwable()).getStackTrace()[0].getMethodName(),
				(new Throwable()).getStackTrace()[0].getLineNumber(),
				"USERテーブル登録終了", 1, false);
	}

	// ユーザー情報更新
	protected void updUser(String sesid, String fbSesid, String uuid,
			String version, String optOut) {

		/** ################# tracelog 処理開始 ################# */
		PyxisUtils.recordLog(className,
				(new Throwable()).getStackTrace()[0].getMethodName(),
				(new Throwable()).getStackTrace()[0].getLineNumber(),
				"USERテーブル更新開始", 1, false);

		for (int i = 0; i < 10; i++) {
			try {

				if (TextUtils.isEmpty(sesid) && TextUtils.isEmpty(fbSesid)
						&& TextUtils.isEmpty(uuid)
						&& TextUtils.isEmpty(version)
						&& TextUtils.isEmpty(optOut)) {
					return;
				}

				// 使用禁止文字を取り除く
				sesid = replaceChar(sesid);
				fbSesid = replaceChar(fbSesid);
				uuid = replaceChar(uuid);

				ContentValues val = new ContentValues();
				if (!TextUtils.isEmpty(sesid)) {
					val.put("SESID", sesid);
				}

				if (!TextUtils.isEmpty(fbSesid)) {
					val.put("FB_SESID", fbSesid);
				}

				if (!TextUtils.isEmpty(uuid)) {
					val.put("UUID", uuid);
				}

				if (!TextUtils.isEmpty(version)) {
					val.put(pyxisConst.DB_VERSION_COLMUN, version);
				}

				if (!TextUtils.isEmpty(optOut)) {
					val.put("OPTOUT", optOut);
				}

				db = this.getWritableDatabase();
				db.update("USER", val, null, null);

				/** ################# tracelog 処理成功 ################# */
				PyxisUtils.recordLog(className,
						(new Throwable()).getStackTrace()[0].getMethodName(),
						(new Throwable()).getStackTrace()[0].getLineNumber(),
						"USERテーブル更新成功", 1, false);

				break;
			} catch (Exception e) {
				/** ################# errorlog 処理失敗 ################# */
				PyxisUtils.recordErrorLog(className,
						(new Throwable()).getStackTrace()[0].getMethodName(),
						(new Throwable()).getStackTrace()[0].getLineNumber(),
						"USERテーブル更新失敗。リトライ : ", 2, false, e);
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				continue;
			}
		}

		/** ################# tracelog 処理終了 ################# */
		PyxisUtils.recordLog(className,
				(new Throwable()).getStackTrace()[0].getMethodName(),
				(new Throwable()).getStackTrace()[0].getLineNumber(),
				"USERテーブル更新終了", 1, false);
	}

	// アプリインストール計測参照
	protected HashMap<String, Object> selInstall() {

		/** ################# tracelog 処理開始 ################# */
		PyxisUtils.recordLog(className,
				(new Throwable()).getStackTrace()[0].getMethodName(),
				(new Throwable()).getStackTrace()[0].getLineNumber(),
				"INSTALLテーブル参照開始", 1, false);

		Cursor cursor = null;
		HashMap<String, Object> installMap = new HashMap<String, Object>();
		try {
			db = this.getReadableDatabase();
			cursor = db.query("INSTALL", new String[] { "START_FLAG",
					"FB_START_FLAG", "START_DATE", "IS_BROWSER_UPED", }, null,
					null, null, null, null);

			cursor.moveToFirst();
			if (cursor.getCount() > 0) {

				Integer startFlg = cursor.getInt(cursor
						.getColumnIndex("START_FLAG"));
				Integer fbStartFlg = cursor.getInt(cursor
						.getColumnIndex("FB_START_FLAG"));
				String startDate = cursor.getString(cursor
						.getColumnIndex("START_DATE"));
				Integer isBrowserUped = cursor.getInt(cursor
						.getColumnIndex("IS_BROWSER_UPED"));

				installMap.put("START_FLAG", startFlg);
				installMap.put("FB_START_FLAG", fbStartFlg);
				installMap.put("START_DATE", startDate);
				installMap.put("IS_BROWSER_UPED", isBrowserUped);

				/** ################# tracelog 処理成功 ################# */
				PyxisUtils.recordLog(className,
						(new Throwable()).getStackTrace()[0].getMethodName(),
						(new Throwable()).getStackTrace()[0].getLineNumber(),
						"INSTALLテーブル取得成功", 1, false);

			}
		} catch (Exception e) {
			/** ################# errorlog 処理失敗 ################# */
			PyxisUtils.recordErrorLog(className,
					(new Throwable()).getStackTrace()[0].getMethodName(),
					(new Throwable()).getStackTrace()[0].getLineNumber(),
					"INSTALLテーブル取得失敗 : ", 2, false, e);
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}

		/** ################# tracelog 処理終了 ################# */
		PyxisUtils.recordLog(className,
				(new Throwable()).getStackTrace()[0].getMethodName(),
				(new Throwable()).getStackTrace()[0].getLineNumber(),
				"INSTALLテーブル参照終了", 1, false);

		return installMap;
	}

	// アプリインストール計測追加
	protected void insInstall(Integer startFlg, Integer fbStartFlg,
			Double startDate, Integer isBrowserUped) {

		/** ################# tracelog 処理開始 ################# */
		PyxisUtils.recordLog(className,
				(new Throwable()).getStackTrace()[0].getMethodName(),
				(new Throwable()).getStackTrace()[0].getLineNumber(),
				"INSTALLテーブル登録開始", 1, false);

		for (int i = 0; i < 10; i++) {
			try {

				if (startFlg == null && fbStartFlg == null && startDate == null
						&& isBrowserUped == null) {
					return;
				}

				ContentValues val = new ContentValues();
				if (startFlg != null) {
					val.put("START_FLAG", startFlg.intValue());
				}

				if (fbStartFlg != null) {
					val.put("FB_START_FLAG", fbStartFlg.intValue());
				}

				if (startDate != null) {
					val.put("START_DATE", startDate);
				}

				if (isBrowserUped != null) {
					val.put("IS_BROWSER_UPED", isBrowserUped.intValue());
				}

				db = this.getWritableDatabase();
				db.insert("INSTALL", null, val);

				/** ################# tracelog 処理成功 ################# */
				PyxisUtils.recordLog(className,
						(new Throwable()).getStackTrace()[0].getMethodName(),
						(new Throwable()).getStackTrace()[0].getLineNumber(),
						"INSTALLテーブル登録成功", 1, false);

				break;
			} catch (Exception e) {
				/** ################# errorlog 処理失敗 ################# */
				PyxisUtils.recordErrorLog(className,
						(new Throwable()).getStackTrace()[0].getMethodName(),
						(new Throwable()).getStackTrace()[0].getLineNumber(),
						"INSTALLテーブル登録失敗。リトライ : ", 2, false, e);
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				continue;
			}
		}

		/** ################# tracelog 処理終了 ################# */
		PyxisUtils.recordLog(className,
				(new Throwable()).getStackTrace()[0].getMethodName(),
				(new Throwable()).getStackTrace()[0].getLineNumber(),
				"INSTALLテーブル登録終了", 1, false);
	}

	// アプリインストール計測更新
	protected void updInstall(Integer startFlg, Integer fbStartFlg,
			Double startDate, Integer isBrowserUped) {

		/** ################# tracelog 処理開始 ################# */
		PyxisUtils.recordLog(className,
				(new Throwable()).getStackTrace()[0].getMethodName(),
				(new Throwable()).getStackTrace()[0].getLineNumber(),
				"INSTALLテーブル更新開始", 1, false);

		for (int i = 0; i < 10; i++) {
			try {

				if (startFlg == null && fbStartFlg == null && startDate == null
						&& isBrowserUped == null) {
					return;
				}

				ContentValues val = new ContentValues();
				if (startFlg != null) {
					val.put("START_FLAG", startFlg.intValue());
				}

				if (fbStartFlg != null) {
					val.put("FB_START_FLAG", fbStartFlg.intValue());
				}

				if (startDate != null) {
					val.put("START_DATE", fbStartFlg.longValue());
				}

				if (isBrowserUped != null) {
					val.put("IS_BROWSER_UPED", isBrowserUped.intValue());
				}

				db = this.getWritableDatabase();
				db.update("INSTALL", val, null, null);

				/** ################# tracelog 処理成功 ################# */
				PyxisUtils.recordLog(className,
						(new Throwable()).getStackTrace()[0].getMethodName(),
						(new Throwable()).getStackTrace()[0].getLineNumber(),
						"INSTALLテーブル更新成功", 1, false);

				break;
			} catch (Exception e) {
				/** ################# errorlog 処理失敗 ################# */
				PyxisUtils.recordErrorLog(className,
						(new Throwable()).getStackTrace()[0].getMethodName(),
						(new Throwable()).getStackTrace()[0].getLineNumber(),
						"INSTALLテーブル更新失敗。リトライ : ", 2, false, e);
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				continue;
			}
		}

		/** ################# tracelog 処理終了 ################# */
		PyxisUtils.recordLog(className,
				(new Throwable()).getStackTrace()[0].getMethodName(),
				(new Throwable()).getStackTrace()[0].getLineNumber(),
				"INSTALLテーブル更新終了", 1, false);
	}

	// アプリ内計測参照
	protected int selCntAction(String condition) {

		/** ################# tracelog 処理開始 ################# */
		PyxisUtils.recordLog(className,
				(new Throwable()).getStackTrace()[0].getMethodName(),
				(new Throwable()).getStackTrace()[0].getLineNumber(),
				"ACTIONテーブル件数取得処理開始", 1, false);

		int count = 0;

		try {
			db = this.getReadableDatabase();
			StringBuilder sql = new StringBuilder(
					"SELECT COUNT(ACT_ID) AS COUNT FROM ACTION");

			if (!TextUtils.isEmpty(condition)) {
				sql.append(" " + condition);
			}

			Cursor cursor = db.rawQuery(sql.toString(), null);
			cursor.moveToFirst();

			if (cursor.getCount() > 0) {
				count = cursor.getInt(cursor.getColumnIndex("COUNT"));
			}
			cursor.close();

			/** ################# tracelog 処理成功 ################# */
			PyxisUtils.recordLog(className,
					(new Throwable()).getStackTrace()[0].getMethodName(),
					(new Throwable()).getStackTrace()[0].getLineNumber(),
					"ACTIONテーブル件数取得処理成功", 1, false);
		} catch (Exception e) {
			/** ################# errorlog 処理失敗 ################# */
			PyxisUtils.recordErrorLog(className,
					(new Throwable()).getStackTrace()[0].getMethodName(),
					(new Throwable()).getStackTrace()[0].getLineNumber(),
					"ACTIONテーブル件数取得処理失敗 : ", 2, false, e);
		}

		/** ################# tracelog 処理終了 ################# */
		PyxisUtils.recordLog(className,
				(new Throwable()).getStackTrace()[0].getMethodName(),
				(new Throwable()).getStackTrace()[0].getLineNumber(),
				"ACTIONテーブル件数取得処理終了", 1, false);

		return count;

	}

	// アプリ内計測参照
	protected List<HashMap<String, Object>> selAction() {

		/** ################# tracelog 処理開始 ################# */
		PyxisUtils.recordLog(className,
				(new Throwable()).getStackTrace()[0].getMethodName(),
				(new Throwable()).getStackTrace()[0].getLineNumber(),
				"ACTIONテーブル参照開始", 1, false);

		List<HashMap<String, Object>> returnList = new ArrayList<HashMap<String, Object>>();

		try {
			db = this.getReadableDatabase();

			String sql = "SELECT ACT_ID, ACT_MV, ACT_VERIFY, ACT_SUID, ACT_SESID, ACT_DATE, ACT_SENDING, ACT_FST_FLAG, ACT_SALES, ACT_VOLUME, ACT_PROFIT, ACT_OTHERS, ACT_ATTRIBUTION, ACT_FID FROM ACTION"
					+ " WHERE ACT_SENDING <> 1 ORDER BY ACT_DATE LIMIT "
					+ pyxisConst.PYXIS_APP_COMPRESS_LIMIT;

			Cursor cursor = db.rawQuery(sql, null);

			cursor.moveToFirst();
			for (int i = 0; i < cursor.getCount(); i++) {
				HashMap<String, Object> actionMap = new HashMap<String, Object>();

				actionMap.put("ACT_ID",
						cursor.getInt(cursor.getColumnIndex("ACT_ID")));
				actionMap.put("ACT_MV",
						cursor.getString(cursor.getColumnIndex("ACT_MV")));
				actionMap.put("ACT_VERIFY",
						cursor.getString(cursor.getColumnIndex("ACT_VERIFY")));
				actionMap.put("ACT_SUID",
						cursor.getString(cursor.getColumnIndex("ACT_SUID")));
				actionMap.put("ACT_SESID",
						cursor.getString(cursor.getColumnIndex("ACT_SESID")));
				actionMap.put("ACT_DATE",
						cursor.getString(cursor.getColumnIndex("ACT_DATE")));
				actionMap.put("ACT_SENDING",
						cursor.getInt(cursor.getColumnIndex("ACT_SENDING")));
				actionMap.put("ACT_FST_FLAG",
						cursor.getInt(cursor.getColumnIndex("ACT_FST_FLAG")));
				actionMap.put("ACT_SALES",
						cursor.getString(cursor.getColumnIndex("ACT_SALES")));
				actionMap.put("ACT_VOLUME",
						cursor.getString(cursor.getColumnIndex("ACT_VOLUME")));
				actionMap.put("ACT_PROFIT",
						cursor.getString(cursor.getColumnIndex("ACT_PROFIT")));
				actionMap.put("ACT_OTHERS",
						cursor.getString(cursor.getColumnIndex("ACT_OTHERS")));
				actionMap.put("ACT_ATTRIBUTION", cursor.getString(cursor
						.getColumnIndex("ACT_ATTRIBUTION")));
				actionMap.put("ACT_FID",
						cursor.getString(cursor.getColumnIndex("ACT_FID")));

				returnList.add(i, actionMap);
				cursor.moveToNext();
			}
			cursor.close();

			/** ################# tracelog 処理成功 ################# */
			PyxisUtils.recordLog(className,
					(new Throwable()).getStackTrace()[0].getMethodName(),
					(new Throwable()).getStackTrace()[0].getLineNumber(),
					"ACTIONテーブル参照成功", 1, false);

		} catch (Exception e) {
			/** ################# errorlog 処理失敗 ################# */
			PyxisUtils.recordErrorLog(className,
					(new Throwable()).getStackTrace()[0].getMethodName(),
					(new Throwable()).getStackTrace()[0].getLineNumber(),
					"ACTIONテーブル参照失敗 : ", 2, false, e);
		}

		/** ################# tracelog 処理終了 ################# */
		PyxisUtils.recordLog(className,
				(new Throwable()).getStackTrace()[0].getMethodName(),
				(new Throwable()).getStackTrace()[0].getLineNumber(),
				"ACTIONテーブル参照終了", 1, false);

		return returnList;
	}

	/**
	 * アプリ内計測追加
	 *
	 * @param act_mv
	 * @param act_verify
	 * @param act_suid
	 * @param act_sesid
	 */
	protected void insAction(String act_mv, String act_verify, String act_suid,
			String act_sesid, Integer sales, Integer volume, Integer profit,
			String others, String attribution, String fid, String act_date,
			int act_fst_flg) {

		/** ################# tracelog 処理開始 ################# */
		PyxisUtils.recordLog(className,
				(new Throwable()).getStackTrace()[0].getMethodName(),
				(new Throwable()).getStackTrace()[0].getLineNumber(),
				"ACTIONテーブル登録開始", 1, false);

		try {

			// 使用禁止文字を取り除く
			act_verify = replaceChar(act_verify);
			act_suid = replaceChar(act_suid);
			act_sesid = replaceChar(act_sesid);

			ContentValues val = new ContentValues();
			val.put("ACT_MV", act_mv);
			val.put("ACT_VERIFY", act_verify);
			val.put("ACT_SUID", act_suid);
			// セッションIDがセットされている場合は登録
			if (!TextUtils.isEmpty(act_sesid)) {
				val.put("ACT_SESID", act_sesid);
			}
			if (sales != null) {
				val.put("ACT_SALES", sales);
			}
			if (volume != null) {
				val.put("ACT_VOLUME", volume);
			}
			if (profit != null) {
				val.put("ACT_PROFIT", profit);
			}
			if (others != null) {
				val.put("ACT_OTHERS", others);
			}
			if (attribution != null) {
				val.put("ACT_ATTRIBUTION", attribution);
			}
			if (fid != null) {
				val.put("ACT_FID", fid);
			}
			val.put("ACT_DATE", act_date);
			val.put("ACT_SENDING", 0);
			val.put("ACT_FST_FLAG", act_fst_flg);

			db = this.getWritableDatabase();
			db.insert("ACTION", null, val);
			db.execSQL("vacuum");

			/** ################# tracelog 処理成功 ################# */
			PyxisUtils.recordLog(className,
					(new Throwable()).getStackTrace()[0].getMethodName(),
					(new Throwable()).getStackTrace()[0].getLineNumber(),
					"ACTIONテーブル登録成功", 1, false);

		} catch (Exception e) {
			/** ################# errorlog 処理失敗 ################# */
			PyxisUtils.recordErrorLog(className,
					(new Throwable()).getStackTrace()[0].getMethodName(),
					(new Throwable()).getStackTrace()[0].getLineNumber(),
					"ACTIONテーブル登録失敗 : ", 2, false, e);
		}

		/** ################# tracelog 処理終了 ################# */
		PyxisUtils.recordLog(className,
				(new Throwable()).getStackTrace()[0].getMethodName(),
				(new Throwable()).getStackTrace()[0].getLineNumber(),
				"ACTIONテーブル登録終了", 1, false);

	}

	/**
	 * アプリ内計測更新（対象：古いレコード）
	 *
	 * @param act_mv
	 * @param act_verify
	 * @param act_suid
	 * @param act_sesid
	 */
	protected void updActionTargetOld(String act_mv, String act_verify,
			String act_suid, String act_sesid, String act_date, int act_fst_flg) {

		/** ################# tracelog 処理開始 ################# */
		PyxisUtils.recordLog(className,
				(new Throwable()).getStackTrace()[0].getMethodName(),
				(new Throwable()).getStackTrace()[0].getLineNumber(),
				" ACTIONテーブル更新（対象：古いレコード）開始", 1, false);

		String condition = "ACT_ID = (SELECT ACT_ID FROM ACTION WHERE ACT_SENDING = 0 ORDER BY ACT_DATE LIMIT 1)";
		updAction(act_mv, act_verify, act_suid, act_sesid, act_date,
				act_fst_flg, condition);

		/** ################# tracelog 処理終了 ################# */
		PyxisUtils.recordLog(className,
				(new Throwable()).getStackTrace()[0].getMethodName(),
				(new Throwable()).getStackTrace()[0].getLineNumber(),
				" ACTIONテーブル更新（対象：古いレコード）終了", 1, false);
	}

	/**
	 * アプリ内計測更新（対象：新しいレコード）
	 *
	 * @param act_mv
	 * @param act_verify
	 * @param act_suid
	 * @param act_sesid
	 */
	protected void updActionTargetNew(String act_mv, String act_verify,
			String act_suid, String act_sesid, String act_date, int act_fst_flg) {

		/** ################# tracelog 処理開始 ################# */
		PyxisUtils.recordLog(className,
				(new Throwable()).getStackTrace()[0].getMethodName(),
				(new Throwable()).getStackTrace()[0].getLineNumber(),
				" ACTIONテーブル更新（対象：新しいレコード）開始", 1, false);

		String condition = "ACT_ID = (SELECT ACT_ID FROM ACTION WHERE ACT_SENDING = 0 ORDER BY ACT_DATE DESC LIMIT 1)";
		updAction(act_mv, act_verify, act_suid, act_sesid, act_date,
				act_fst_flg, condition);

		/** ################# tracelog 処理終了 ################# */
		PyxisUtils.recordLog(className,
				(new Throwable()).getStackTrace()[0].getMethodName(),
				(new Throwable()).getStackTrace()[0].getLineNumber(),
				" ACTIONテーブル更新（対象：新しいレコード）終了", 1, false);
	}

	/**
	 * アプリ内計測更新
	 *
	 * @param act_mv
	 * @param act_verify
	 * @param act_suid
	 * @param act_sesid
	 * @param condition
	 */
	protected void updAction(String act_mv, String act_verify, String act_suid,
			String act_sesid, String act_date, int act_fst_flg, String condition) {

		try {
			// 使用禁止文字を取り除く
			act_verify = replaceChar(act_verify);
			act_suid = replaceChar(act_suid);
			act_sesid = replaceChar(act_sesid);

			ContentValues val = new ContentValues();
			val.put("ACT_MV", act_mv);
			val.put("ACT_VERIFY", act_verify);
			val.put("ACT_SUID", act_suid);
			// セッションIDがセットされている場合は登録
			if (!TextUtils.isEmpty(act_sesid)) {
				val.put("ACT_SESID", act_sesid);
			}
			val.put("ACT_DATE", act_date);
			val.put("ACT_SENDING", 0);
			val.put("ACT_FST_FLAG", act_fst_flg);

			db = this.getWritableDatabase();
			db.update("ACTION", val, condition, null);
			db.execSQL("vacuum");

			/** ################# tracelog 処理成功 ################# */
			PyxisUtils.recordLog(className,
					(new Throwable()).getStackTrace()[0].getMethodName(),
					(new Throwable()).getStackTrace()[0].getLineNumber(),
					"ACTIONテーブル更新成功", 1, false);
		} catch (Exception e) {
			/** ################# errorlog 処理失敗 ################# */
			PyxisUtils.recordErrorLog(className,
					(new Throwable()).getStackTrace()[0].getMethodName(),
					(new Throwable()).getStackTrace()[0].getLineNumber(),
					"ACTIONテーブル更新失敗 : ", 2, false, e);
		}
	}

	// アプリ内計測更新（データ送信時）
	// ※注意 (第一引数がnullの場合は強制的にACT_SENDING = １のデータを更新する）
	protected void updActionSending(List<String> act_id, int act_sending) {

		/** ################# tracelog 処理開始 ################# */
		PyxisUtils.recordLog(className,
				(new Throwable()).getStackTrace()[0].getMethodName(),
				(new Throwable()).getStackTrace()[0].getLineNumber(),
				" ACTIONテーブル更新（送信フラグ）開始", 1, false);

		try {

			ContentValues val = new ContentValues();
			val.put("ACT_SENDING", act_sending);

			db = this.getWritableDatabase();

			if (act_id != null) {
				String bindString = createBindString(act_id.size());
				db.update("ACTION", val, "ACT_ID IN (" + bindString + ")",
						(String[]) act_id.toArray(new String[0]));
			} else {
				db.update("ACTION", val, "ACT_SENDING = 1", null);
			}

			db.execSQL("vacuum");

			/** ################# tracelog 処理成功 ################# */
			PyxisUtils.recordLog(className,
					(new Throwable()).getStackTrace()[0].getMethodName(),
					(new Throwable()).getStackTrace()[0].getLineNumber(),
					"ACTIONテーブル更新（送信フラグ）成功", 1, false);
		} catch (Exception e) {

			/** ################# errorlog 処理失敗 ################# */
			PyxisUtils.recordErrorLog(className,
					(new Throwable()).getStackTrace()[0].getMethodName(),
					(new Throwable()).getStackTrace()[0].getLineNumber(),
					"ACTIONテーブル更新（送信フラグ）失敗 : ", 2, false, e);
		}

		/** ################# tracelog 処理終了 ################# */
		PyxisUtils.recordLog(className,
				(new Throwable()).getStackTrace()[0].getMethodName(),
				(new Throwable()).getStackTrace()[0].getLineNumber(),
				" ACTIONテーブル更新（送信フラグ）終了", 1, false);

	}

	// アプリ内計測削除
	protected void delAction(List<String> act_id) {

		/** ################# tracelog 処理開始 ################# */
		PyxisUtils.recordLog(className,
				(new Throwable()).getStackTrace()[0].getMethodName(),
				(new Throwable()).getStackTrace()[0].getLineNumber(),
				" ACTIONテーブル削除開始", 1, false);

		try {
			if (act_id == null) {
				return;
			}

			String bindString = createBindString(act_id.size());

			db = this.getWritableDatabase();

			db.delete("ACTION", "ACT_ID IN (" + bindString + ")",
					(String[]) act_id.toArray(new String[0]));
			db.execSQL("REINDEX ACTION;");

			/** ################# tracelog 処理成功 ################# */
			PyxisUtils.recordLog(className,
					(new Throwable()).getStackTrace()[0].getMethodName(),
					(new Throwable()).getStackTrace()[0].getLineNumber(),
					"ACTIONテーブル削除成功", 1, false);
		} catch (Exception e) {
			/** ################# errorlog 処理失敗 ################# */
			PyxisUtils.recordErrorLog(className,
					(new Throwable()).getStackTrace()[0].getMethodName(),
					(new Throwable()).getStackTrace()[0].getLineNumber(),
					"ACTIONテーブル削除失敗 : ", 2, false, e);
		}

		/** ################# tracelog 処理終了 ################# */
		PyxisUtils.recordLog(className,
				(new Throwable()).getStackTrace()[0].getMethodName(),
				(new Throwable()).getStackTrace()[0].getLineNumber(),
				" ACTIONテーブル削除終了", 1, false);
	}

	/**
	 * テーブル一覧を取得
	 *
	 * @return テーブル一覧
	 */
	protected String[] getTableName() {

		/** ################# tracelog 処理開始 ################# */
		PyxisUtils.recordLog(className,
				(new Throwable()).getStackTrace()[0].getMethodName(),
				(new Throwable()).getStackTrace()[0].getLineNumber(),
				" テーブル一覧取得開始", 1, false);

		String sql = "select name from sqlite_master";
		List<String> returnList = new ArrayList<String>();
		try {
			db = this.getReadableDatabase();
			Cursor c = db.rawQuery(sql, null);
			c.moveToFirst();
			for (int i = 0; i < c.getCount(); i++) {
				returnList.add(c.getString(c.getColumnIndex("name")));
				c.moveToNext();
			}
			c.close();

			/** ################# tracelog 処理成功 ################# */
			PyxisUtils.recordLog(className,
					(new Throwable()).getStackTrace()[0].getMethodName(),
					(new Throwable()).getStackTrace()[0].getLineNumber(),
					" テーブル一覧取得成功", 1, false);

		} catch (Exception e) {
			/** ################# errorlog 処理失敗 ################# */
			PyxisUtils.recordErrorLog(className,
					(new Throwable()).getStackTrace()[0].getMethodName(),
					(new Throwable()).getStackTrace()[0].getLineNumber(),
					" テーブル一覧取得失敗 : ", 2, false, e);
		}

		/** ################# tracelog 処理終了 ################# */
		PyxisUtils.recordLog(className,
				(new Throwable()).getStackTrace()[0].getMethodName(),
				(new Throwable()).getStackTrace()[0].getLineNumber(),
				" テーブル一覧取得終了", 1, false);

		return (String[]) returnList.toArray(new String[0]);

	}

	/**
	 * テーブルのカラム情報からカラム名のみ抽出
	 *
	 * @param colmunList
	 * @return テーブルのカラム名一覧
	 */
	protected String[] getColmunName(List<HashMap<String, String>> colmunList) {

		/** ################# tracelog 処理開始 ################# */
		PyxisUtils.recordLog(className,
				(new Throwable()).getStackTrace()[0].getMethodName(),
				(new Throwable()).getStackTrace()[0].getLineNumber(),
				" カラム名取得処理開始", 1, false);

		List<String> colmunNameList = new ArrayList<String>();
		try {

			for (HashMap<String, String> hashMap : colmunList) {
				for (String colmunNmae : hashMap.keySet()) {
					colmunNameList.add(colmunNmae);
				}
			}
		} catch (Exception e) {
			/** ################# errorlog 処理失敗 ################# */
			PyxisUtils.recordErrorLog(className,
					(new Throwable()).getStackTrace()[0].getMethodName(),
					(new Throwable()).getStackTrace()[0].getLineNumber(),
					" カラム名取得失敗 : ", 2, false, e);
		}

		/** ################# tracelog 処理終了 ################# */
		PyxisUtils.recordLog(className,
				(new Throwable()).getStackTrace()[0].getMethodName(),
				(new Throwable()).getStackTrace()[0].getLineNumber(),
				" カラム名取得処理終了", 1, false);

		return (String[]) colmunNameList.toArray(new String[0]);

	}

	/**
	 * 対象テーブルの情報を取得
	 *
	 * @param tableName
	 * @return テーブル情報
	 */
	protected List<HashMap<String, String>> getTableColmunInfo(String tableName) {

		/** ################# tracelog 処理開始 ################# */
		PyxisUtils.recordLog(className,
				(new Throwable()).getStackTrace()[0].getMethodName(),
				(new Throwable()).getStackTrace()[0].getLineNumber(),
				" テーブル情報取得処理開始", 1, false);

		String sql = "PRAGMA table_info(" + tableName + ")";
		List<HashMap<String, String>> colmunList = new ArrayList<HashMap<String, String>>();
		try {
			db = this.getReadableDatabase();
			Cursor c = db.rawQuery(sql, null);
			c.moveToFirst();
			for (int i = 0; i < c.getCount(); i++) {
				HashMap<String, String> clmAndType = new HashMap<String, String>();
				clmAndType.put(c.getString(c.getColumnIndex("name")),
						c.getString(c.getColumnIndex("type")));
				colmunList.add(c.getInt(c.getColumnIndex("cid")), clmAndType);
				c.moveToNext();
			}
			c.close();

			/** ################# tracelog 処理成功 ################# */
			PyxisUtils.recordLog(className,
					(new Throwable()).getStackTrace()[0].getMethodName(),
					(new Throwable()).getStackTrace()[0].getLineNumber(),
					" テーブル情報取得成功", 1, false);

		} catch (Exception e) {
			/** ################# errorlog 処理失敗 ################# */
			PyxisUtils.recordErrorLog(className,
					(new Throwable()).getStackTrace()[0].getMethodName(),
					(new Throwable()).getStackTrace()[0].getLineNumber(),
					" ーブル情報取得失敗 : ", 2, false, e);
		}

		/** ################# tracelog 処理終了 ################# */
		PyxisUtils.recordLog(className,
				(new Throwable()).getStackTrace()[0].getMethodName(),
				(new Throwable()).getStackTrace()[0].getLineNumber(),
				" テーブル情報取得処理終了", 1, false);

		return colmunList;

	}

	/**
	 * DBのバージョンチェック
	 *
	 * @return boolean true:現在のSDKのバージョンとイコール false:それ以外
	 */
	protected boolean checkDBVersion() {

		/** ################# tracelog 処理開始 ################# */
		PyxisUtils.recordLog(className,
				(new Throwable()).getStackTrace()[0].getMethodName(),
				(new Throwable()).getStackTrace()[0].getLineNumber(),
				" DBのバージョンチェック処理開始", 1, false);

		boolean isVersionMatch = false;

		try {
			String sql = "SELECT DB_VERSION FROM USER LIMIT 1";
			String currentDBVersion = null;

			db = this.getReadableDatabase();
			Cursor c = db.rawQuery(sql, null);
			c.moveToFirst();
			if (c.getCount() > 0) {
				currentDBVersion = c.getString(c
						.getColumnIndex(pyxisConst.DB_VERSION_COLMUN));
				/** ################# tracelog 処理開始 ################# */
				PyxisUtils
						.recordLog(className,
								(new Throwable()).getStackTrace()[0]
										.getMethodName(), (new Throwable())
										.getStackTrace()[0].getLineNumber(),
								" DBのバージョンチェック処理[DB上のバージョン = "
										+ currentDBVersion + "]", 1, false);
			}
			c.close();

			/** ################# tracelog 処理開始 ################# */
			PyxisUtils.recordLog(className,
					(new Throwable()).getStackTrace()[0].getMethodName(),
					(new Throwable()).getStackTrace()[0].getLineNumber(),
					" DBのバージョンチェック処理[現在のバージョン = " + pyxisConst.SDK_VERSION
							+ "]", 1, false);

			if (!TextUtils.isEmpty(currentDBVersion)) {
				isVersionMatch = currentDBVersion
						.equals(pyxisConst.SDK_VERSION);
			}

			/** ################# tracelog 処理成功 ################# */
			PyxisUtils.recordLog(className,
					(new Throwable()).getStackTrace()[0].getMethodName(),
					(new Throwable()).getStackTrace()[0].getLineNumber(),
					" DBのバージョンチェック処理成功", 1, false);

		} catch (Exception e) {
			/** ################# errorlog 処理失敗 ################# */
			PyxisUtils.recordErrorLog(className,
					(new Throwable()).getStackTrace()[0].getMethodName(),
					(new Throwable()).getStackTrace()[0].getLineNumber(),
					" DBのバージョンチェック処理失敗 : ", 2, false, e);
		}

		/** ################# tracelog 処理終了 ################# */
		PyxisUtils.recordLog(className,
				(new Throwable()).getStackTrace()[0].getMethodName(),
				(new Throwable()).getStackTrace()[0].getLineNumber(),
				" DBのバージョンチェック処理終了", 1, false);

		return isVersionMatch;

	}

	/**
	 * テーブルのアップデート処理 ※ ここではラッパーメソッドは使わず直接SQLを記述
	 *
	 * @throws Exception
	 */
	protected void updateDatabase() throws Exception {

		/** ################# tracelog 処理開始 ################# */
		PyxisUtils.recordLog(className,
				(new Throwable()).getStackTrace()[0].getMethodName(),
				(new Throwable()).getStackTrace()[0].getLineNumber(),
				"テーブルのアップデート処理開始", 1, false);

		boolean isFail = false;

		db = this.getWritableDatabase();

		try {

			// トランザクション開始
			db.beginTransaction();

			/** ################# tracelog ################# */
			PyxisUtils.recordLog(className,
					(new Throwable()).getStackTrace()[0].getMethodName(),
					(new Throwable()).getStackTrace()[0].getLineNumber(),
					"テーブルのアップデート処理[トランザクション開始]", 1, false);

			// USERテーブルからデータ取得 start >
			// ---------------------------

			/** ################# tracelog ################# */
			PyxisUtils.recordLog(className,
					(new Throwable()).getStackTrace()[0].getMethodName(),
					(new Throwable()).getStackTrace()[0].getLineNumber(),
					"テーブルのアップデート処理[USERテーブルからデータ取得 : 開始]", 1, false);

			String userTable = pyxisConst.DB_TABLE_NAME_USER;

			// データ格納用HashMap
			HashMap<String, Object> userMap = new HashMap<String, Object>();

			try {
				// カラム一覧を取得

				List<HashMap<String, String>> userTableHashList = getTableColmunInfo(userTable);

				// SELECT文を生成
				String selectUserSql = "SELECT * FROM " + userTable
						+ " LIMIT 1";

				Cursor userCusor = db.rawQuery(selectUserSql, null);
				if (userCusor.getCount() > 0) {

					userCusor.moveToFirst();
					userMap = getVariableValue(userTableHashList, userCusor);

					/** ################# tracelog ################# */
					PyxisUtils.recordLog(className, (new Throwable())
							.getStackTrace()[0].getMethodName(),
							(new Throwable()).getStackTrace()[0]
									.getLineNumber(),
							"テーブルのアップデート処理[USERテーブルからデータ取得 : 成功]", 1, false);

				} else {
					userMap = null;

					/** ################# tracelog ################# */
					PyxisUtils.recordLog(className, (new Throwable())
							.getStackTrace()[0].getMethodName(),
							(new Throwable()).getStackTrace()[0]
									.getLineNumber(),
							"テーブルのアップデート処理[USERテーブルからデータ取得 : データなし]", 1, false);
				}

				userCusor.close();
			} catch (Exception e) {
				/** ################# errorlog ################# */
				PyxisUtils.recordErrorLog(className,
						(new Throwable()).getStackTrace()[0].getMethodName(),
						(new Throwable()).getStackTrace()[0].getLineNumber(),
						"テーブルのアップデート処理[USERテーブルからデータ取得 : 失敗] ", 2, false, e);
				isFail = true;
			}

			// end > USERテーブルからデータ取得
			// ---------------------------

			// INSTALLテーブルからデータ取得 start >
			// ------------------------------

			/** ################# tracelog ################# */
			PyxisUtils.recordLog(className,
					(new Throwable()).getStackTrace()[0].getMethodName(),
					(new Throwable()).getStackTrace()[0].getLineNumber(),
					"テーブルのアップデート処理[INSTALLテーブルからデータ取得 : 開始]", 1, false);

			// カラム一覧を取得
			String installTable = pyxisConst.DB_TABLE_NAME_INSTALL;

			// データ格納用HashMap
			HashMap<String, Object> installMap = new HashMap<String, Object>();

			try {

				List<HashMap<String, String>> installTableHashList = getTableColmunInfo(installTable);

				// SELECT文を生成
				String selectInstallSql = "SELECT * FROM " + installTable
						+ " LIMIT 1";

				Cursor installCusor = db.rawQuery(selectInstallSql, null);

				if (installCusor.getCount() > 0) {

					installCusor.moveToFirst();
					installMap = getVariableValue(installTableHashList,
							installCusor);

					/** ################# tracelog ################# */
					PyxisUtils.recordLog(className, (new Throwable())
							.getStackTrace()[0].getMethodName(),
							(new Throwable()).getStackTrace()[0]
									.getLineNumber(),
							"テーブルのアップデート処理[INSTALLテーブルからデータ取得 : 成功]", 1, false);

				} else {
					installMap = null;

					/** ################# tracelog ################# */
					PyxisUtils.recordLog(className, (new Throwable())
							.getStackTrace()[0].getMethodName(),
							(new Throwable()).getStackTrace()[0]
									.getLineNumber(),
							"テーブルのアップデート処理[INSTALLテーブルからデータ取得 : データなし]", 1,
							false);
				}

				installCusor.close();
			} catch (Exception e) {
				/** ################# errorlog ################# */
				PyxisUtils.recordErrorLog(className,
						(new Throwable()).getStackTrace()[0].getMethodName(),
						(new Throwable()).getStackTrace()[0].getLineNumber(),
						"テーブルのアップデート処理[INSTALLテーブルからデータ取得 : 失敗] ", 2, false, e);
				isFail = true;
			}

			/** ################# tracelog ################# */
			PyxisUtils.recordLog(className,
					(new Throwable()).getStackTrace()[0].getMethodName(),
					(new Throwable()).getStackTrace()[0].getLineNumber(),
					"テーブルのアップデート処理[INSTALLテーブルからデータ取得 : 終了]", 1, false);

			// end > INSTALLテーブルからデータ取得
			// ------------------------------

			// ACTIONテーブルからデータ取得 start >
			// ------------------------------

			/** ################# tracelog ################# */
			PyxisUtils.recordLog(className,
					(new Throwable()).getStackTrace()[0].getMethodName(),
					(new Throwable()).getStackTrace()[0].getLineNumber(),
					"テーブルのアップデート処理[ACTIONテーブルからデータ取得 : 開始]", 1, false);

			// カラム一覧を取得
			String actionTable = pyxisConst.DB_TABLE_NAME_ACTION;

			// データ格納用List ※ ACTIONテーブルのレコードは複数存在するので
			List<HashMap<String, Object>> actionMapList = new ArrayList<HashMap<String, Object>>();

			try {
				List<HashMap<String, String>> actionTableHashList = getTableColmunInfo(actionTable);

				// SELECT文を生成
				String selectActionSql = "SELECT * FROM " + actionTable;

				Cursor actionCursor = db.rawQuery(selectActionSql, null);
				actionCursor.moveToFirst();
				for (int i = 0; i < actionCursor.getCount(); i++) {

					actionMapList.add(getVariableValue(actionTableHashList,
							actionCursor));
					actionCursor.moveToNext();
				}

				if (actionMapList.size() > 0) {
					/** ################# tracelog ################# */
					PyxisUtils.recordLog(className, (new Throwable())
							.getStackTrace()[0].getMethodName(),
							(new Throwable()).getStackTrace()[0]
									.getLineNumber(),
							"テーブルのアップデート処理[ACTIONテーブルからデータ取得 : 成功]", 1, false);
				} else {
					/** ################# tracelog ################# */
					PyxisUtils.recordLog(className, (new Throwable())
							.getStackTrace()[0].getMethodName(),
							(new Throwable()).getStackTrace()[0]
									.getLineNumber(),
							"テーブルのアップデート処理[ACTIONテーブルからデータ取得 : データなし]", 1,
							false);
				}

				actionCursor.close();
			} catch (Exception e) {
				/** ################# errorlog ################# */
				PyxisUtils.recordErrorLog(className,
						(new Throwable()).getStackTrace()[0].getMethodName(),
						(new Throwable()).getStackTrace()[0].getLineNumber(),
						"テーブルのアップデート処理[ACTIONテーブルからデータ取得 : 失敗] ", 2, false, e);
				isFail = true;
			}

			/** ################# tracelog ################# */
			PyxisUtils.recordLog(className,
					(new Throwable()).getStackTrace()[0].getMethodName(),
					(new Throwable()).getStackTrace()[0].getLineNumber(),
					"テーブルのアップデート処理[ACTIONテーブルからデータ取得 : 終了]", 1, false);

			// end > ACTIONテーブルからデータ取得
			// ------------------------------

			// テンポラリテーブル作成 start >
			// ------------------------------

			/** ################# tracelog ################# */
			PyxisUtils.recordLog(className,
					(new Throwable()).getStackTrace()[0].getMethodName(),
					(new Throwable()).getStackTrace()[0].getLineNumber(),
					"テーブルのアップデート処理[テンポラリテーブル作成 : 開始]", 1, false);

			String tmpUserTable = pyxisConst.DB_TABLE_NAME_TMP_USER;
			String tmpInstallTable = pyxisConst.DB_TABLE_NAME_TMP_INSTALL;
			String tmpActionTable = pyxisConst.DB_TABLE_NAME_TMP_ACTION;

			boolean existUser = (userMap != null && userMap.size() > 0);
			boolean existInstall = (installMap != null && installMap.size() > 0);
			boolean existAction = (actionMapList != null && actionMapList
					.size() > 0);

			try {

				// 作成する前に存在している場合はDROPする
				String[] tableNames = getTableName();
				Arrays.sort(tableNames);

				if (Arrays.binarySearch(tableNames, tmpUserTable) > 0) {
					db.execSQL("DROP TABLE " + tmpUserTable);
				}

				if (Arrays.binarySearch(tableNames, tmpInstallTable) > 0) {
					db.execSQL("DROP TABLE " + tmpInstallTable);
				}

				if (Arrays.binarySearch(tableNames, tmpActionTable) > 0) {
					db.execSQL("DROP TABLE " + tmpActionTable);
				}

				// テーブル作成

				// ユーザー情報TBL
				db.execSQL("CREATE TABLE "
						+ tmpUserTable
						+ "(SESID TEXT, FB_SESID TEXT, UUID TEXT ,DB_VERSION TEXT, OPTOUT TEXT)");
				/** ################# tracelog ################# */
				PyxisUtils.recordLog(className,
						(new Throwable()).getStackTrace()[0].getMethodName(),
						(new Throwable()).getStackTrace()[0].getLineNumber(),
						"テーブルのアップデート処理[テンポラリテーブル(TMP_USER)作成 : 成功]", 1, false);

				// インストール計測TBL
				db.execSQL("CREATE TABLE "
						+ tmpInstallTable
						+ "(START_FLAG INT, FB_START_FLAG INT ,START_DATE TEXT ,IS_BROWSER_UPED INTEGER)");
				/** ################# tracelog ################# */
				PyxisUtils.recordLog(className,
						(new Throwable()).getStackTrace()[0].getMethodName(),
						(new Throwable()).getStackTrace()[0].getLineNumber(),
						"テーブルのアップデート処理[テンポラリテーブル(TMP_INSTALL)作成 : 成功]", 1,
						false);

				// アプリ内計測TBL
				db.execSQL("CREATE TABLE " + tmpActionTable
						+ "(ACT_ID INTEGER PRIMARY KEY" + ", ACT_MV TEXT"
						+ ", ACT_VERIFY TEXT" + ", ACT_SESID TEXT"
						+ ", ACT_SUID TEXT" + ", ACT_DATE TEXT"
						+ ", ACT_SENDING INTEGER" + ", ACT_FST_FLAG INTEGER"
						+ ", ACT_SALES INTEGER" + ", ACT_VOLUME INTEGER"
						+ ", ACT_PROFIT INTEGER" + ", ACT_OTHERS TEXT"
						+ ", ACT_ATTRIBUTION TEXT"
						+ ", ACT_APPLICATION_TRACKING_ENABLE TEXT"
						+ ", ACT_FID TEXT)");

				/** ################# tracelog ################# */
				PyxisUtils
						.recordLog(className,
								(new Throwable()).getStackTrace()[0]
										.getMethodName(), (new Throwable())
										.getStackTrace()[0].getLineNumber(),
								"テーブルのアップデート処理[テンポラリテーブル(TMP_ACTION)作成 : 成功]",
								1, false);

			} catch (Exception e) {
				/** ################# errorlog ################# */
				PyxisUtils.recordErrorLog(className,
						(new Throwable()).getStackTrace()[0].getMethodName(),
						(new Throwable()).getStackTrace()[0].getLineNumber(),
						"テーブルのアップデート処理[ テンポラリテーブル作成 : 失敗] ", 2, false, e);
				isFail = true;
			}

			/** ################# tracelog ################# */
			PyxisUtils.recordLog(className,
					(new Throwable()).getStackTrace()[0].getMethodName(),
					(new Throwable()).getStackTrace()[0].getLineNumber(),
					"テーブルのアップデート処理[テンポラリテーブル作成 : 終了]", 1, false);

			// end > テンポラリテーブル作成
			// ------------------------------

			// USERのテンポラリテーブルにデータINSERT start >
			// -------------------------------------

			/** ################# tracelog ################# */

			if (existUser) {

				PyxisUtils
						.recordLog(className,
								(new Throwable()).getStackTrace()[0]
										.getMethodName(), (new Throwable())
										.getStackTrace()[0].getLineNumber(),
								"テーブルのアップデート処理[テンポラリテーブル(TMP_USER)への登録 : 開始]",
								1, false);

				try {

					// カラム一覧を取得
					List<HashMap<String, String>> tmpUserTableHashList = getTableColmunInfo(tmpUserTable);

					ContentValues val = setContentValues(tmpUserTableHashList,
							userMap);

					db.insert(tmpUserTable, null, val);
					/** ################# tracelog ################# */
					PyxisUtils.recordLog(className, (new Throwable())
							.getStackTrace()[0].getMethodName(),
							(new Throwable()).getStackTrace()[0]
									.getLineNumber(),
							"テーブルのアップデート処理[テンポラリテーブル(TMP_USER)への登録 : 成功]", 1,
							false);
				} catch (Exception e) {
					/** ################# errorlog ################# */
					PyxisUtils.recordErrorLog(className, (new Throwable())
							.getStackTrace()[0].getMethodName(),
							(new Throwable()).getStackTrace()[0]
									.getLineNumber(),
							"テーブルのアップデート処理[ テンポラリテーブル(TMP_USER)への登録 : 失敗] ", 2,
							false, e);
					isFail = true;
				}

				/** ################# tracelog ################# */
				PyxisUtils
						.recordLog(className,
								(new Throwable()).getStackTrace()[0]
										.getMethodName(), (new Throwable())
										.getStackTrace()[0].getLineNumber(),
								"テーブルのアップデート処理[テンポラリテーブル(TMP_USER)への登録 : 終了]",
								1, false);

			}

			// end > USERのテンポラリテーブルにデータINSERT
			// -------------------------------------

			// INSTALLのテンポラリテーブルにデータINSERT start >
			// -------------------------------------

			if (existInstall) {

				/** ################# tracelog ################# */
				PyxisUtils.recordLog(className,
						(new Throwable()).getStackTrace()[0].getMethodName(),
						(new Throwable()).getStackTrace()[0].getLineNumber(),
						"テーブルのアップデート処理[テンポラリテーブル(TMP_INSTALL)への登録 : 開始]", 1,
						false);

				try {

					// カラム一覧を取得
					List<HashMap<String, String>> tmpInstallTableHashList = getTableColmunInfo(tmpInstallTable);

					ContentValues val = setContentValues(
							tmpInstallTableHashList, installMap);

					db.insert(tmpInstallTable, null, val);
					/** ################# tracelog ################# */
					PyxisUtils.recordLog(className, (new Throwable())
							.getStackTrace()[0].getMethodName(),
							(new Throwable()).getStackTrace()[0]
									.getLineNumber(),
							"テーブルのアップデート処理[テンポラリテーブル(TMP_USER)への登録 : 成功]", 1,
							false);
				} catch (Exception e) {
					/** ################# errorlog ################# */
					PyxisUtils.recordErrorLog(className, (new Throwable())
							.getStackTrace()[0].getMethodName(),
							(new Throwable()).getStackTrace()[0]
									.getLineNumber(),
							"テーブルのアップデート処理[ テンポラリテーブル(TMP_INSTALL)への登録 : 失敗] ",
							2, false, e);
					isFail = true;
				}

				/** ################# tracelog ################# */
				PyxisUtils.recordLog(className,
						(new Throwable()).getStackTrace()[0].getMethodName(),
						(new Throwable()).getStackTrace()[0].getLineNumber(),
						"テーブルのアップデート処理[テンポラリテーブル(TMP_INSTALL)への登録 : 終了]", 1,
						false);

			}
			// end > INSTALLのテンポラリテーブルにデータINSERT
			// -------------------------------------

			// ACTIONのテンポラリテーブルにデータINSERT start >
			// -------------------------------------

			if (existAction) {

				/** ################# tracelog ################# */
				PyxisUtils.recordLog(className,
						(new Throwable()).getStackTrace()[0].getMethodName(),
						(new Throwable()).getStackTrace()[0].getLineNumber(),
						"テーブルのアップデート処理[テンポラリテーブル(TMP_ACTION)への登録 : 開始]", 1,
						false);

				try {

					// カラム一覧を取得
					List<HashMap<String, String>> tmpActionTableHashList = getTableColmunInfo(tmpActionTable);

					for (HashMap<String, Object> actionMap : actionMapList) {

						ContentValues val = setContentValues(
								tmpActionTableHashList, actionMap);

						db.insert(tmpActionTable, null, val);

					}
					/** ################# tracelog ################# */
					PyxisUtils.recordLog(className, (new Throwable())
							.getStackTrace()[0].getMethodName(),
							(new Throwable()).getStackTrace()[0]
									.getLineNumber(),
							"テーブルのアップデート処理[テンポラリテーブル(TMP_ACTION)への登録 : 成功]", 1,
							false);
				} catch (Exception e) {
					/** ################# errorlog ################# */
					PyxisUtils.recordErrorLog(className, (new Throwable())
							.getStackTrace()[0].getMethodName(),
							(new Throwable()).getStackTrace()[0]
									.getLineNumber(),
							"テーブルのアップデート処理[ テンポラリテーブル(TMP_ACTIONへの登録 : 失敗] ",
							2, false, e);
					isFail = true;
				}

				/** ################# tracelog ################# */
				PyxisUtils.recordLog(className,
						(new Throwable()).getStackTrace()[0].getMethodName(),
						(new Throwable()).getStackTrace()[0].getLineNumber(),
						"テーブルのアップデート処理[テンポラリテーブル(TMP_INSTALL)への登録 : 終了]", 1,
						false);

			}

			// end > ACTIONのテンポラリテーブルにデータINSERT
			// -------------------------------------

			// 既存テーブル削除 start >
			// ------------------------------

			/** ################# tracelog ################# */
			PyxisUtils.recordLog(className,
					(new Throwable()).getStackTrace()[0].getMethodName(),
					(new Throwable()).getStackTrace()[0].getLineNumber(),
					"テーブルのアップデート処理[既存テーブル削除 : 開始]", 1, false);

			try {
				// ユーザー情報TBL
				db.execSQL("DROP TABLE " + userTable);

				/** ################# tracelog ################# */
				PyxisUtils.recordLog(className,
						(new Throwable()).getStackTrace()[0].getMethodName(),
						(new Throwable()).getStackTrace()[0].getLineNumber(),
						"テーブルのアップデート処理[既存テーブル(USERT)削除 : 成功]", 1, false);

				// インストール計測TBL
				db.execSQL("DROP TABLE " + installTable);
				/** ################# tracelog ################# */
				PyxisUtils.recordLog(className,
						(new Throwable()).getStackTrace()[0].getMethodName(),
						(new Throwable()).getStackTrace()[0].getLineNumber(),
						"テーブルのアップデート処理[既存テーブル(INSTALL)削除 : 成功]", 1, false);

				// アプリ計測TBL
				db.execSQL("DROP TABLE " + actionTable);
				/** ################# tracelog ################# */
				PyxisUtils.recordLog(className,
						(new Throwable()).getStackTrace()[0].getMethodName(),
						(new Throwable()).getStackTrace()[0].getLineNumber(),
						"テーブルのアップデート処理[既存テーブル(ACTION)削除 : 成功]", 1, false);

			} catch (Exception e) {
				/** ################# errorlog ################# */
				PyxisUtils.recordErrorLog(className,
						(new Throwable()).getStackTrace()[0].getMethodName(),
						(new Throwable()).getStackTrace()[0].getLineNumber(),
						"テーブルのアップデート処理[ 既存テーブルの削除 : 失敗] ", 2, false, e);
				isFail = true;
			}

			/** ################# tracelog ################# */
			PyxisUtils.recordLog(className,
					(new Throwable()).getStackTrace()[0].getMethodName(),
					(new Throwable()).getStackTrace()[0].getLineNumber(),
					"テーブルのアップデート処理[既存テーブル削除 : 終了]", 1, false);

			// end > 既存テーブル削除
			// ------------------------------

			// テンポラリテーブル名称変更 start >
			// ------------------------------

			/** ################# tracelog ################# */
			PyxisUtils.recordLog(className,
					(new Throwable()).getStackTrace()[0].getMethodName(),
					(new Throwable()).getStackTrace()[0].getLineNumber(),
					"テーブルのアップデート処理[テンポラリテーブルのリネーム : 開始]", 1, false);

			try {
				// ユーザー情報TBL
				db.execSQL("ALTER TABLE " + tmpUserTable + " RENAME TO "
						+ userTable);
				/** ################# tracelog ################# */
				PyxisUtils.recordLog(className,
						(new Throwable()).getStackTrace()[0].getMethodName(),
						(new Throwable()).getStackTrace()[0].getLineNumber(),
						"テーブルのアップデート処理[テンポラリテーブル(TMP_USER)のリネーム : 成功]", 1,
						false);

				// インストール計測TBL
				db.execSQL("ALTER TABLE " + tmpInstallTable + " RENAME TO "
						+ installTable);
				/** ################# tracelog ################# */
				PyxisUtils.recordLog(className,
						(new Throwable()).getStackTrace()[0].getMethodName(),
						(new Throwable()).getStackTrace()[0].getLineNumber(),
						"テーブルのアップデート処理[テンポラリテーブル(TMP_INSTALL)のリネーム : 成功]", 1,
						false);

				// アプリ計測TBL
				db.execSQL("ALTER TABLE " + tmpActionTable + " RENAME TO "
						+ actionTable);
				/** ################# tracelog ################# */
				PyxisUtils.recordLog(className,
						(new Throwable()).getStackTrace()[0].getMethodName(),
						(new Throwable()).getStackTrace()[0].getLineNumber(),
						"テーブルのアップデート処理[テンポラリテーブル(TMP_ACTION)のリネーム : 成功]", 1,
						false);

			} catch (Exception e) {
				/** ################# errorlog ################# */
				PyxisUtils.recordErrorLog(className,
						(new Throwable()).getStackTrace()[0].getMethodName(),
						(new Throwable()).getStackTrace()[0].getLineNumber(),
						"テーブルのアップデート処理[ テンポラリテーブルのリネーム : 失敗] ", 2, false, e);
				isFail = true;
			}

			/** ################# tracelog ################# */
			PyxisUtils.recordLog(className,
					(new Throwable()).getStackTrace()[0].getMethodName(),
					(new Throwable()).getStackTrace()[0].getLineNumber(),
					"テーブルのアップデート処理[テンポラリテーブルのリネーム : 終了]", 1, false);

			// end > テンポラリテーブル名称変更
			// ------------------------------

			// バージョン番号更新 start >
			// ------------------------------

			/** ################# tracelog ################# */
			PyxisUtils.recordLog(className,
					(new Throwable()).getStackTrace()[0].getMethodName(),
					(new Throwable()).getStackTrace()[0].getLineNumber(),
					"テーブルのアップデート処理[バージョン番号更新 : 開始]", 1, false);

			try {
				// ユーザ情報が存在する場合は更新

				if (existUser) {
					// ユーザー情報TBL
					db.execSQL("UPDATE " + userTable + " SET "
							+ pyxisConst.DB_VERSION_COLMUN + " = '"
							+ pyxisConst.SDK_VERSION + "'");
					/** ################# tracelog ################# */
					PyxisUtils.recordLog(className, (new Throwable())
							.getStackTrace()[0].getMethodName(),
							(new Throwable()).getStackTrace()[0]
									.getLineNumber(),
							"テーブルのアップデート処理[バージョン番号更新(更新) : 成功]", 1, false);
				} else {
					// 存在しない場合は登録
					ContentValues val = new ContentValues();
					val.put(pyxisConst.DB_VERSION_COLMUN,
							pyxisConst.SDK_VERSION);
					db.insert(userTable, null, val);
					/** ################# tracelog ################# */
					PyxisUtils.recordLog(className, (new Throwable())
							.getStackTrace()[0].getMethodName(),
							(new Throwable()).getStackTrace()[0]
									.getLineNumber(),
							"テーブルのアップデート処理[バージョン番号更新(登録) : 成功]", 1, false);
				}
			} catch (Exception e) {
				/** ################# errorlog ################# */
				PyxisUtils.recordErrorLog(className,
						(new Throwable()).getStackTrace()[0].getMethodName(),
						(new Throwable()).getStackTrace()[0].getLineNumber(),
						"テーブルのアップデート処理[ バージョン番号更新 : 失敗] ", 2, false, e);
				isFail = true;
			}

			/** ################# tracelog ################# */
			PyxisUtils.recordLog(className,
					(new Throwable()).getStackTrace()[0].getMethodName(),
					(new Throwable()).getStackTrace()[0].getLineNumber(),
					"テーブルのアップデート処理[バージョン番号更新 : 終了]", 1, false);

			// end > バージョン番号更新
			// ------------------------------

			// コミット
			db.setTransactionSuccessful();

			/** ################# tracelog ################# */
			PyxisUtils.recordLog(className,
					(new Throwable()).getStackTrace()[0].getMethodName(),
					(new Throwable()).getStackTrace()[0].getLineNumber(),
					"テーブルのアップデート処理[コミット]", 1, false);
		} catch (Exception ex) {
			/** ################# errorlog ################# */
			PyxisUtils.recordLog(className,
					(new Throwable()).getStackTrace()[0].getMethodName(),
					(new Throwable()).getStackTrace()[0].getLineNumber(),
					"テーブルのアップデート処理失敗[ロールバック]", 2, false);
		} finally {
			// トランザクション終了
			db.endTransaction();
			/** ################# tracelog ################# */
			PyxisUtils.recordLog(className,
					(new Throwable()).getStackTrace()[0].getMethodName(),
					(new Throwable()).getStackTrace()[0].getLineNumber(),
					"テーブルのアップデート処理[トランザクション終了]", 1, false);

			if (!isFail) {
				/** ################# tracelog 処理終了 ################# */
				PyxisUtils.recordLog(className,
						(new Throwable()).getStackTrace()[0].getMethodName(),
						(new Throwable()).getStackTrace()[0].getLineNumber(),
						"テーブルのアップデート処理成功", 1, false);

			}

			/** ################# tracelog 処理終了 ################# */
			PyxisUtils.recordLog(className,
					(new Throwable()).getStackTrace()[0].getMethodName(),
					(new Throwable()).getStackTrace()[0].getLineNumber(),
					"テーブルのアップデート処理終了", 1, false);

		}
	}

	/**
	 * 構造不明のテーブルからHashMap<カラム名,値>を取得する
	 *
	 * @param colmunList
	 * @param cursor
	 * @return HashMap
	 */
	private HashMap<String, Object> getVariableValue(
			List<HashMap<String, String>> colmunList, Cursor cursor) {

		HashMap<String, Object> colmunValue = new HashMap<String, Object>();

		for (int i = 0; i < colmunList.size(); i++) {

			HashMap<String, String> colmunInfo = colmunList.get(i);

			Set<String> colmunSet = colmunInfo.keySet();
			Iterator<String> colmunIte = colmunSet.iterator();

			while (colmunIte.hasNext()) {
				String colmunName = colmunIte.next();
				String colmuntype = colmunInfo.get(colmunName);
				try {
					// カラムの型がINT[EGER]の場合
					// 注意：sqliteの数値型のカラムはCREATE時にINTEGERで作成すること
					if (colmuntype.startsWith(pyxisConst.DB_COLMUN_TYPE_INT)) {
						colmunValue.put(colmunName, cursor.getInt(cursor
								.getColumnIndex(colmunName)));
						// カラムの型がTEXTの場合
					} else if (colmuntype
							.equals(pyxisConst.DB_COLMUN_TYPE_TEXT)) {
						colmunValue.put(colmunName, cursor.getString(cursor
								.getColumnIndex(colmunName)));
					}
				} catch (CursorIndexOutOfBoundsException e) {
					colmunValue.put(colmunName, null);
				}
			}
		}
		cursor.close();
		return colmunValue;

	}

	/**
	 * ContentValues を生成
	 *
	 * @param colmunList
	 * @param tableMap
	 * @return ContentValues
	 */
	private ContentValues setContentValues(
			List<HashMap<String, String>> colmunList,
			HashMap<String, Object> tableMap) {

		ContentValues val = new ContentValues();

		for (HashMap<String, String> hashMap : colmunList) {

			String colmunName = hashMap.keySet().toArray(new String[0])[0];
			Object value = tableMap.get(colmunName);

			if (value != null) {
				// カラムの型がINT[EGER]の場合
				if (hashMap.get(colmunName).startsWith(
						pyxisConst.DB_COLMUN_TYPE_INT)) {
					val.put(colmunName, Integer.valueOf(value.toString()));
					// カラムの型がTEXTの場合
				} else if (hashMap.get(colmunName).equals(
						pyxisConst.DB_COLMUN_TYPE_TEXT)) {
					val.put(colmunName, value.toString());
				}
			} else {
				val.putNull(colmunName);
			}
		}
		return val;
	}

	// 使用禁止文字削除
	private String replaceChar(String param) {
		if (TextUtils.isEmpty(param)) {
			return param;
		}
		String res = param.replaceAll("\n", "");
		res = res.replaceAll("\r", "");
		res = res.replaceAll("\t", "");
		return res;
	}

	// バインド用テンプレート作成
	private String createBindString(int num) {

		StringBuilder bindStrings = new StringBuilder(num * 2);
		for (int i = 0; i < num; i++) {
			if (i == 0) {
				bindStrings.append("?");
			} else {
				bindStrings.append(",?");
			}
		}

		return bindStrings.toString();

	}

}
