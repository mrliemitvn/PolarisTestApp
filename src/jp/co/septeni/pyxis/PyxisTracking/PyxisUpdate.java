package jp.co.septeni.pyxis.PyxisTracking;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class PyxisUpdate {

	/**
	 * DBのアップデート処理
	 *
	 * @throws Exception
	 */
	protected void updateDB () throws Exception {

		PyxisConst pyxisConst = new PyxisConst();

		PyxisDBHelper dbHelper = new PyxisDBHelper();

		boolean isNeedUpdate = false;

		// USERテーブルにバージョンカラムが存在するかチェック

		List<HashMap<String, String>> userColmunInfoList = dbHelper.getTableColmunInfo(pyxisConst.DB_TABLE_NAME_USER);

		String[] colmunNamesOnUser = dbHelper.getColmunName(userColmunInfoList);

		Arrays.sort(colmunNamesOnUser);
		if(Arrays.binarySearch(colmunNamesOnUser, pyxisConst.DB_VERSION_COLMUN) == -1){
			isNeedUpdate = true;
		}

		// バージョンカラムが存在する場合はバージョンチェック
		if(!isNeedUpdate) {
			isNeedUpdate = !dbHelper.checkDBVersion();
		}

		// DBのバージョンアップが必要な場合はアップデート
		if(isNeedUpdate) {
			dbHelper.updateDatabase();
		}

		dbHelper.close();
	}

	/**
	 * アプリ内計測の送信中フラグを初期化
	 */
	protected void initAppSending() {

		/** ################# tracelog 初回起動チェック ################# */
		PyxisUtils.recordLog(this.getClass().getSimpleName(),(new Throwable()).getStackTrace()[0].getMethodName(),(new Throwable()).getStackTrace()[0].getLineNumber()
				, "アプリ内計測送信中フラグ初期化開始", 1,false);

		PyxisDBHelper dbHelper = new PyxisDBHelper();

		dbHelper.updActionSending(null, 0);

		dbHelper.close();

		/** ################# tracelog 初回起動チェック ################# */
		PyxisUtils.recordLog(this.getClass().getSimpleName(),(new Throwable()).getStackTrace()[0].getMethodName(),(new Throwable()).getStackTrace()[0].getLineNumber()
				, "アプリ内計測送信中フラグ初期化完了", 1,false);

	}
}
