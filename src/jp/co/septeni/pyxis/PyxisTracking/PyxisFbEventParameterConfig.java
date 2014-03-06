package jp.co.septeni.pyxis.PyxisTracking;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Pyxis-SDK for Android.
 *
 * @author (C)SEPTENI CO.,LTD.
 * @version 1.0.0, 11 Jul 2011
 */
public class PyxisFbEventParameterConfig {

	// 【FBイベント送信用パラメータ】
	// FBイベント送信用パラメータとして設定したい値を、publicなクラス変数として定義します。

	@FbEventParameter
	protected static Float _valueToSum = null;

	@FbEventParameter
	protected static Integer fb_level = null;

	@FbEventParameter
	protected static Boolean fb_success = null;

	@FbEventParameter
	@StringLengthValidation(length = 50)
	protected static String fb_content_type = null;

	@FbEventParameter
	@StringLengthValidation(length = 50)
	protected static String fb_content_id = null;

	@FbEventParameter
	@StringLengthValidation(length = 50)
	protected static String fb_registration_method = null;

	@FbEventParameter
	protected static Boolean fb_payment_info_available = null;

	@FbEventParameter
	protected static Integer fb_max_rating_value = null;

	@FbEventParameter
	protected static Integer fb_num_items = null;

	@FbEventParameter
	@StringLengthValidation(length = 50)
	protected static String fb_search_string = null;

	@FbEventParameter
	@StringLengthValidation(length = 50)
	protected static String fb_description = null;

	// 【FBイベント送信用パラメータここまで】

	// FBイベント送信パラメータになるフィールドリスト
	private static List<Field> fields = new ArrayList<Field>();

	static {
		// クラスに定義された変数をすべて取得
		Field[] myFields = PyxisFbEventParameterConfig.class
				.getDeclaredFields();
		int i = 0;
		for (Field field : myFields) {
			Annotation[] annotations = field.getAnnotations();
			for (Annotation annotation : annotations) {
				if (annotation.annotationType().equals(FbEventParameter.class)) {
					// @FbEventParameterアノテーションのついたフィールドを、対象フィールドのリストに追加
					fields.add(field);
					break;
				}
			}
			i++;
		}
	}

	/**
	 * FBイベント送信用パラメータとしてクラス変数に設定された値の検証を行うメソッドです。
	 *
	 * @return 検証結果　true/false
	 */
	protected static boolean validate() {

		boolean result = true;

		for (Field field : fields) {
			// 各フィールドごとに必要なバリデーションを行う
			field.setAccessible(true);
			Annotation[] annotations = field.getAnnotations();
			for (Annotation annotation : annotations) {
				if (annotation.annotationType().equals(
						StringLengthValidation.class)) {
					// String文字数の検証
					if (!stringLengthValidation(field,
							(StringLengthValidation) annotation)) {
						// バリデーションエラー
						result = false;
					}
				}
			}
		}

		return result;
	}

	/**
	 * クラス変数に保持しているFBイベント送信用設定値を、Uri形式に成形するメソッドです。
	 *
	 * @return FBイベント送信用パラメータをUri形式に成形した文字列 ※FBイベント送信用パラメータが設定されていない場合は、空文字
	 */
	protected static String createParamStr() {

		// Uri文字列
		String uriStr = "";
		PyxisUtils pyxiUtils = new PyxisUtils();

		for (Field field : fields) {
			try {
				// フィールドの値を取得
				field.setAccessible(true);
				Object fieldVal = field.get(null);
				if (fieldVal == null) {
					// 値が設定されていない場合は飛ばす
					PyxisUtils.recordLog(PyxisFbEventParameterConfig.class
							.getName(), (new Throwable()).getStackTrace()[0]
							.getMethodName(),
							(new Throwable()).getStackTrace()[0]
									.getLineNumber(),
							"FBイベント送信パラメータ設定なし。パラメータ名：" + field.getName(), 1,
							false);
					continue;
				}

				// 値をString型に変換
				String fieldStr;
				if (fieldVal.getClass().equals(Boolean.class)) {
					// boolean値の場合、「true=1,false=0」
					fieldStr = (Boolean) fieldVal ? "1" : "0";
				} else {
					fieldStr = fieldVal.toString();
				}

				// Uri文字列の生成
				if (!fieldStr.isEmpty()) {
					// [<フィールド名>=<base64エンコードした値>]をUri文字列に追加
					String paramStr = field.getName() + "="
							+ pyxiUtils.base64Encode(fieldStr.getBytes());

					if (!uriStr.isEmpty()) {
						// 2つ目以降のパラメータは「&」で連結
						uriStr += "&";
					}

					PyxisUtils.recordLog(PyxisFbEventParameterConfig.class
							.getName(), (new Throwable()).getStackTrace()[0]
							.getMethodName(),
							(new Throwable()).getStackTrace()[0]
									.getLineNumber(), "FBイベント送信パラメータ追加。パラメータ名："
									+ field.getName() + "、値：" + paramStr, 1,
							false);
					// パラメータを連結
					uriStr += paramStr;
				}
			} catch (IllegalArgumentException e) {
				// get/setメソッドの引数がおかしい場合に発生する例外
				// 基本的には発生しない
				PyxisUtils.recordLog(
						PyxisFbEventParameterConfig.class.getName(),
						(new Throwable()).getStackTrace()[0].getMethodName(),
						(new Throwable()).getStackTrace()[0].getLineNumber(),
						e.getMessage(),
						1, false);
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// フィールドへのアクセス権がない場合に発生する。
				// 時クラスのpublicメソッドにしかアクセスしないので、発生しない。
				PyxisUtils.recordLog(
						PyxisFbEventParameterConfig.class.getName(),
						(new Throwable()).getStackTrace()[0].getMethodName(),
						(new Throwable()).getStackTrace()[0].getLineNumber(),
						e.getMessage(),
						1, false);
				e.printStackTrace();
			}
		}

		PyxisUtils.recordLog(PyxisFbEventParameterConfig.class.getName(),
				(new Throwable()).getStackTrace()[0].getMethodName(),
				(new Throwable()).getStackTrace()[0].getLineNumber(),
				"FBイベント送信パラメータ生成完了。パラメータ：" + uriStr, 1, false);

		// 成形したUri形式の文字列をbase64エンコードして返す ※パラメータ設定なしの場合、空文字を返す
		return uriStr.isEmpty() ? "" : pyxiUtils
				.base64Encode(uriStr.getBytes());
	}

	/**
	 * クラス変数に保持している設定値を初期化するメソッドです。
	 *
	 */
	protected static void flush() {

		for (Field field : fields) {
			try {
				field.setAccessible(true);
				// 値をnullで初期化
				field.set(null, null);
			} catch (IllegalArgumentException e) {
				// get/setメソッドの引数がおかしい場合に発生する例外
				// 基本的には発生しない
				PyxisUtils.recordLog(
						PyxisFbEventParameterConfig.class.getName(),
						(new Throwable()).getStackTrace()[0].getMethodName(),
						(new Throwable()).getStackTrace()[0].getLineNumber(),
						e.getMessage(),
						1, false);
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// フィールドへのアクセス権がない場合に発生する。
				// 時クラスのpublicメソッドにしかアクセスしないので、発生しない。
				PyxisUtils.recordLog(
						PyxisFbEventParameterConfig.class.getName(),
						(new Throwable()).getStackTrace()[0].getMethodName(),
						(new Throwable()).getStackTrace()[0].getLineNumber(),
						e.getMessage(),
						1, false);
				e.printStackTrace();
			}
		}
		PyxisUtils.recordLog(PyxisFbEventParameterConfig.class.getName(),
				(new Throwable()).getStackTrace()[0].getMethodName(),
				(new Throwable()).getStackTrace()[0].getLineNumber(),
				"FBイベント送信パラメータ初期化完了", 1, false);
	}

	/**
	 * FBイベント送信パラメータ判別用アノテーション
	 * このアノテーションを付けたフィールドが、FBイベント送信パラメータになります。
	 *
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.FIELD)
	protected @interface FbEventParameter {
	}

	/**
	 * String型の文字数バリデーション用アノテーション
	 *
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.FIELD)
	protected @interface StringLengthValidation {
		/** 最大文字数 */
		int length();
	}

	/**
	 * String型クラス変数の文字数検証用メソッド
	 *
	 * @param field
	 * @param annotation
	 * @return
	 */
	private static boolean stringLengthValidation(Field field,
			StringLengthValidation annotation) {

		boolean result = true;
		String val = "";
		int length = 0;
		try {
			// フィールド値の取得
			Object fieldObj = field.get(null);
			if(null == fieldObj){
				return true;
			}
			val = fieldObj.toString();
			// 最大文字数の取得
			length = annotation.length();

			// 文字数の検証
			if (val.length() > length && null != val) {
				result = false;
				// 文字数オーバーの場合は切り捨て
				field.set(null, val.substring(0, length));
				PyxisUtils.recordLog(
						PyxisFbEventParameterConfig.class.getName(),
						(new Throwable()).getStackTrace()[0].getMethodName(),
						(new Throwable()).getStackTrace()[0].getLineNumber(),
						field.getName()
								+ "の値が、"
								+ ((StringLengthValidation) annotation)
										.length() + "を超えています。超過した文字数は切り捨てます。切り捨て後文字列：[" + val.substring(0, length) + "]",
						1, false);
			}
		} catch (IllegalArgumentException e) {
			// get/setメソッドの引数がおかしい場合に発生する例外
			// 基本的には発生しない
			PyxisUtils.recordLog(
					PyxisFbEventParameterConfig.class.getName(),
					(new Throwable()).getStackTrace()[0].getMethodName(),
					(new Throwable()).getStackTrace()[0].getLineNumber(),
					e.getMessage(),
					1, false);
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// フィールドへのアクセス権がない場合に発生する。
			// 時クラスのpublicメソッドにしかアクセスしないので、発生しない。
			PyxisUtils.recordLog(
					PyxisFbEventParameterConfig.class.getName(),
					(new Throwable()).getStackTrace()[0].getMethodName(),
					(new Throwable()).getStackTrace()[0].getLineNumber(),
					e.getMessage(),
					1, false);
			e.printStackTrace();
		}
		return result;
	}
}
