package jp.co.septeni.pyxis.PyxisTracking;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

public class PyxisSendTask extends AsyncTask<String, Void, String> {
	private PyxisUtils pyxisUtils = new PyxisUtils();
	private PyxisConst pyxisConst = new PyxisConst();
	private String className = this.getClass().getSimpleName();

	/**
	 * AES 暗号化キー TODO:PyxisCpiTrackingにも同じ設定値あり ※
	 * 外部からアクセス出来ない様にクラス毎にprivate変数としている
	 * */
	private String AES_KEY = "nH9Lvmxb4VstiGhr";

	// バックグラウンド前処理
	@Override
	protected void onPreExecute() {
	}

	// バックグラウンド処理
	@Override
	protected String doInBackground(String... urls) {

		PyxisDBHelper dbHelper = new PyxisDBHelper();
		HashMap<String, Object> userMap = dbHelper.selUser();
		HashMap<String, Object> installMap = dbHelper.selInstall();

		Object uuidObj = userMap.get("UUID");

		String uuid = null;
		if (uuidObj != null) {
			uuid = userMap.get("UUID").toString();
		}
		Log.i(className, (new Throwable()).getStackTrace()[0].getMethodName()
				+ " " + (new Throwable()).getStackTrace()[0].getLineNumber()
				+ " , uuid1: " + uuid);

		// CPI計測利用なしでUUIDが存在しない場合は取得する
		if (!PyxisTracking.isEnableCpi && TextUtils.isEmpty(uuid)) {
			if (pyxisUtils.getNetworkStatus()) {
				PyxisUtils pyxisUtils = new PyxisUtils();
				uuid = pyxisUtils.getUUID();
				Log.i(className, "get UUID: " + uuid);
				// UUIDが取得できたら保存
				if (!TextUtils.isEmpty(uuid)) {
					dbHelper.updUser(null, null, uuid, null, null);
				}
			}
		} else {
			String cvMode = "0";
			if (PyxisTracking.actionPoint != null
					&& !TextUtils.isEmpty(PyxisTracking.actionPoint
							.get("cv_mode"))) {
				cvMode = PyxisTracking.actionPoint.get("cv_mode");
			} else if (!TextUtils.isEmpty(PyxisTracking.cvMode)) {
				cvMode = PyxisTracking.cvMode;
			}

			if (cvMode.equals("0")
					&& (Integer) installMap.get("START_FLAG") != 1) {

				String message = "アプリ内計測情報送信 [cv_mode = 0] : インストール未計測のためスキップ";
				/** ################# tracelog チェック false ################# */
				PyxisUtils.recordLog(className,
						(new Throwable()).getStackTrace()[0].getMethodName(),
						(new Throwable()).getStackTrace()[0].getLineNumber(),
						message, 1, false);
				return "";

			} else if (cvMode.equals("1")
					&& (Integer) installMap.get("FB_START_FLAG") != 1) {

				String message = "アプリ内計測情報送信 [cv_mode = 1] : インストール未計測のためスキップ";
				/** ################# tracelog チェック false ################# */
				PyxisUtils.recordLog(className,
						(new Throwable()).getStackTrace()[0].getMethodName(),
						(new Throwable()).getStackTrace()[0].getLineNumber(),
						message, 1, false);
				return "";

			} else if (cvMode.equals("2")
					&& (Integer) installMap.get("START_FLAG") != 1
					&& (Integer) installMap.get("FB_START_FLAG") != 1) {

				String message = "アプリ内計測情報送信 [cv_mode = 2] : インストール未計測のためスキップ";
				/** ################# tracelog チェック false ################# */
				PyxisUtils.recordLog(className,
						(new Throwable()).getStackTrace()[0].getMethodName(),
						(new Throwable()).getStackTrace()[0].getLineNumber(),
						message, 1, false);
				return "";
			}

		}
		Log.i(className, (new Throwable()).getStackTrace()[0].getMethodName()
				+ " " + (new Throwable()).getStackTrace()[0].getLineNumber()
				+ " , uuid2: " + uuid);
		// UUIDが送信出来る場合のみ処理をする
		if (!TextUtils.isEmpty(uuid)) {

			// 保存件数取得
			int saveRow = dbHelper.selCntAction("WHERE ACT_SENDING <> 1");

			if (saveRow > 0) {

				// 保存データー取得
				List<HashMap<String, Object>> actionList = dbHelper.selAction();

				// 回線状態取得
				if (pyxisUtils.getNetworkStatus()) {
					// 端末ID(AES暗号＋base64)
					// String base64androidId = new
					// String(pyxisUtils.base64Encode(pyxisUtils.encodeAES128(pyxisUtils.getAndroidId().getBytes(),
					// AES_KEY.getBytes())));

					List<String> actIdList = new ArrayList<String>();

					// リクエストパラメータ生成

					StringBuilder fs = new StringBuilder();

					// アプリのバージョンを取得
					String appVersion = pyxisUtils.getAppVersion();

					int cnt = 0;
					for (HashMap<String, Object> hashMap : actionList) {

						Integer actId = hashMap.get("ACT_ID") != null ? Integer
								.valueOf(hashMap.get("ACT_ID").toString())
								: null;
						String mv = hashMap.get("ACT_MV") != null ? hashMap
								.get("ACT_MV").toString() : "";
						String verify = hashMap.get("ACT_VERIFY") != null ? hashMap
								.get("ACT_VERIFY").toString() : "";
						String dt = hashMap.get("ACT_DATE") != null ? hashMap
								.get("ACT_DATE").toString() : "";
						String suid = hashMap.get("ACT_SUID") != null ? hashMap
								.get("ACT_SUID").toString() : "";
						String sales = hashMap.get("ACT_SALES") != null ? hashMap
								.get("ACT_SALES").toString() : "";
						String volume = hashMap.get("ACT_VOLUME") != null ? hashMap
								.get("ACT_VOLUME").toString() : "";
						String profit = hashMap.get("ACT_PROFIT") != null ? hashMap
								.get("ACT_PROFIT").toString() : "";
						String others = hashMap.get("ACT_OTHERS") != null ? hashMap
								.get("ACT_OTHERS").toString() : "";
						String attribution = hashMap.get("ACT_ATTRIBUTION") != null ? hashMap
								.get("ACT_ATTRIBUTION").toString() : "";
						// オプトアウト設定は、送信時のユーザーテーブルの状態を参照
						String applicationTrackingEnable = userMap
								.get("OPTOUT") != null ? userMap.get("OPTOUT")
								.toString() : "";
						String fid = hashMap.get("ACT_FID") != null ? hashMap
								.get("ACT_FID").toString() : "";

						if (TextUtils.isEmpty(verify)) {
							verify = uuid;
						}

						// 送信データはリクエストパラメータ形式のLF改行
						fs.append("mv=").append(mv).append("&");
						fs.append("verify=").append(verify).append("&");
						fs.append("suid=")
								.append(pyxisUtils.base64Encode(suid.getBytes()))
								.append("&");
						fs.append("dn=").append(pyxisUtils.getPhoneName())
								.append("&");
						fs.append("os=").append(pyxisUtils.getOsVersion())
								.append("&");
						fs.append("dt=").append(dt).append("&");
						fs.append("version=").append(appVersion).append("&");
						fs.append("sales=").append(sales).append("&");
						fs.append("volume=").append(volume).append("&");
						fs.append("profit=").append(profit).append("&");
						fs.append("others=")
								.append(pyxisUtils.base64Encode(others
										.getBytes())).append("&");
						fs.append("attribution=").append(attribution)
								.append("&");
						fs.append("application_tracking_enabled=")
								.append(applicationTrackingEnable).append("&");
						if (!TextUtils.isEmpty(PyxisTracking.ir_id)) {
							fs.append("ir_id=").append(PyxisTracking.ir_id)
									.append("&");
						}
						fs.append("fid=").append(fid)
								.append(pyxisConst.CODE_LF);

						// 処理対象のACT_IDを格納
						actIdList.add(Integer.toString(actId));

						String message = "アプリ内計測 ： 送信データ [" + cnt + "]" + fs;
						cnt++;
						/**
						 * ################# tracelog チェック false
						 * #################
						 */
						PyxisUtils.recordLog(className, (new Throwable())
								.getStackTrace()[0].getMethodName(),
								(new Throwable()).getStackTrace()[0]
										.getLineNumber(), message, 0, false);

					}

					// データを送信する前に送信フラグをOnにする
					dbHelper.updActionSending(actIdList, 1);

					// データ圧縮
					Map<String, String> param = new HashMap<String, String>();
					param.put("fs", pyxisUtils.base64Encode(pyxisUtils
							.compressLzw(fs.toString())));

					fs.delete(0, fs.length());

					// 送信完了の場合DBのデーター削除
					if (pyxisUtils.sendAppBySocket(
							pyxisConst.HTTP_PATH_APP_TRACKING, param)) {
						/**
						 * ################# tracelog チェック false
						 * #################
						 */
						PyxisUtils.recordLog(className, (new Throwable())
								.getStackTrace()[0].getMethodName(),
								(new Throwable()).getStackTrace()[0]
										.getLineNumber(), "アプリ内計測情報送信成功", 0,
								false);
						dbHelper.delAction(actIdList);
					} else {
						/** ################# errorlog ################# */
						PyxisUtils.recordLog(className, (new Throwable())
								.getStackTrace()[0].getMethodName(),
								(new Throwable()).getStackTrace()[0]
										.getLineNumber(),
								"アプリ内計測情報送信失敗:理由[ソケット通信失敗]", 2, false);
						dbHelper.updActionSending(actIdList, 0);
					}
				}
			} else {
				/** ################# tracelog ################# */
				PyxisUtils.recordLog(className,
						(new Throwable()).getStackTrace()[0].getMethodName(),
						(new Throwable()).getStackTrace()[0].getLineNumber(),
						"アプリ内計測情報送信[アプリ内計測情報なし]", 1, false);
			}
		} else {
			/** ################# tracelog ################# */
			PyxisUtils.recordLog(className,
					(new Throwable()).getStackTrace()[0].getMethodName(),
					(new Throwable()).getStackTrace()[0].getLineNumber(),
					"アプリ内計測情報送信[UUIDなし]", 1, false);
		}
		dbHelper.close();
		return "";
	}

	// バックグランド終了後処理
	@Override
	protected void onPostExecute(String result) {
	}
}
