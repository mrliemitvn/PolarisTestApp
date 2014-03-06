/**
 *
 */
package jp.co.septeni.pyxis.PyxisTracking;

import java.util.HashMap;

import android.content.Context;

/**
 * @author t_kono
 *
 */
public class PyxisAppTracking {

	String className = this.getClass().getSimpleName();

	/**
	 * AES 暗号化キー TODO:PyxisCpiTrackingにも同じ設定値あり ※
	 * 外部からアクセス出来ない様にクラス毎にprivate変数としている
	 * */
	private String AES_KEY = "nH9Lvmxb4VstiGhr";

	/**
	 * アプリ内計測情報保存処理
	 *
	 * @param fromContext
	 * @param fromIntent
	 * @param mv
	 * @param verify
	 * @param suid
	 * @param siteId
	 */
	protected void save(String mv, String verify, String suid, String siteId,
			Integer sales, Integer volume, Integer profit, String others,
			Integer saveMax, Integer saveMaxMode) {

		PyxisUtils pyxisUtils = new PyxisUtils();
		PyxisConst pyxisConst = new PyxisConst();
		Context fromContext = PyxisTracking.context;

		// 設定情報のチェック
		if (!pyxisUtils
				.checkParamasForAppSave(mv, siteId, saveMax, saveMaxMode)) {
			PyxisUtils.recordLog(className,
					(new Throwable()).getStackTrace()[0].getMethodName(),
					(new Throwable()).getStackTrace()[0].getLineNumber(),
					"アプリ内計測[設定情報チェックNG]", 2, false);
			throw new IllegalArgumentException("[設定情報チェックNG]mv:" + mv
					+ ",siteId:" + siteId + ",saveMax:" + saveMax
					+ ",saveMaxMode:" + saveMaxMode);
		}

		// 最大保存件数が設定されていないか、システム上の最大値以上、1未満の場合はデフォルト（999）で設定
		if (saveMax == null || saveMax > pyxisConst.PYXIS_APP_LIMIT_SAVE_MAX
				|| saveMax < 1) {

			saveMax = pyxisConst.PYXIS_APP_DEFAULT_SAVE_MAX;
		}

		// 保存モードが設定されていないか、システム上の規定値以外の場合はデフォルト（古いレコードを上書き）で設定
		if (saveMaxMode == null || saveMaxMode > 1 || saveMaxMode < 0) {
			saveMaxMode = pyxisConst.PYXIS_APP_DEFAULT_MAX_MODE;
		}

		PyxisDBHelper dbHelper = new PyxisDBHelper();

		// FBattributionIdの取得
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

		// AndroidIdの取得
		String fid = "";
		fid = pyxisUtils.generateFid(AES_KEY);

		// セッションIDをセット
		// ※CPI計測有りで初回起動時、且つ初回起動日時から規定時間以内の場合
		HashMap<String, Object> installMap = dbHelper.selInstall();

		long currentDateLong = System.currentTimeMillis() / 1000;

		String sesid = null;
		int actFstFlg = 0;
		if (PyxisTracking.isFirst && PyxisTracking.isEnableCpi) {
			actFstFlg = 1;
			sesid = PyxisTracking.sesid;
		}

		String currentDateString = String.valueOf(currentDateLong);

		// 保存件数取得
		int nowSaveCnt = dbHelper.selCntAction(null);
		PyxisUtils.recordLog(className,
				(new Throwable()).getStackTrace()[0].getMethodName(),
				(new Throwable()).getStackTrace()[0].getLineNumber(),
				"アプリ内計測[現在の保存件数 = " + Integer.toString(nowSaveCnt) + " ]", 1,
				false);

		// 最大件数と比較
		if (saveMax <= nowSaveCnt) {
			/** ################# tracelog ################# */
			PyxisUtils.recordLog(className,
					(new Throwable()).getStackTrace()[0].getMethodName(),
					(new Throwable()).getStackTrace()[0].getLineNumber(),
					"アプリ内計測[最大保存件数超過]", 1, false);
			// 古いデータを上書き
			if (saveMaxMode == pyxisConst.PYXIS_APP_SAVE_OLD_REWRITE) {
				PyxisUtils.recordLog(className,
						(new Throwable()).getStackTrace()[0].getMethodName(),
						(new Throwable()).getStackTrace()[0].getLineNumber(),
						"アプリ内計測[過去データ上書き]", 1, false);
				// DBへ更新
				dbHelper.updActionTargetOld(mv, verify, suid, sesid,
						currentDateString, actFstFlg);
			} else {
				PyxisUtils.recordLog(className,
						(new Throwable()).getStackTrace()[0].getMethodName(),
						(new Throwable()).getStackTrace()[0].getLineNumber(),
						"アプリ内計測[追記なし]", 1, false);
			}

		} else {
			// DBへ保存
			dbHelper.insAction(mv, verify, suid, sesid, sales, volume, profit,
					others, attributionId, fid, currentDateString, actFstFlg);
		}

		dbHelper.close();
	}

	/**
	 * アプリ内計測情報送信
	 *
	 * @param fromContext
	 */
	protected void send() {

		// アプリ内計測情報送信
		PyxisSendTask task = new PyxisSendTask();
		task.execute();
	}

}
