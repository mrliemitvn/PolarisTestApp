package com.septeni.polaris.activity;

import java.io.File;

import jp.co.septeni.pyxis.PyxisTracking.PyxisTracking;
import jp.co.septeni.pyxis.PyxisTracking.PyxisTracking.AppLimitMode;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.septeni.polaris.R;
import com.septeni.polaris.data.MvPyxisAppInfo;
import com.septeni.polaris.data.SpinnerMV;
import com.septeni.polaris.fragment.BaseFragment;
import com.septeni.polaris.fragment.CustomAudienceFragment;
import com.septeni.polaris.fragment.OthersFragment;
import com.septeni.polaris.fragment.PurchaseFragment;
import com.septeni.polaris.provider.SharePrefs;
import com.septeni.polaris.utils.LogUtils;

public class TestSDKActivity extends FragmentActivity implements OnClickListener {

	private static final String NO_TYPE = "Select MV";
	private static final String OTHERS_TYPE = "Other MV"; // For active testing.
	private static final String CUSTOM_AUDIENCE_TYPE = "Custom Audience MV"; // For custom audience testing.
	private static final String PURCHASE_TYPE = "Purchase MV"; // For purchase testing.

	/* Variable store list mv. */
	private static final MvPyxisAppInfo MV_APP_INFO_LIEM_TEST_STAGING_201 = new MvPyxisAppInfo(
			"c2faf3dbae68e153f7e3c2527e6ca75b",// install
			"d4b9b018a07b59bbd8fb211b6d6e10b5",// active
			"d27461cbc3b98ba12396061693c5f7d4",// custom audience
			"64a3e8dc99e08c9fd9a8d07248bce9c6", // purchase
			"d27461cbc3b98ba12396061693c5f7d4");// app event.

	private static final MvPyxisAppInfo MV_APP_INFO_YEN_TEST_PRODUCTION_120 = new MvPyxisAppInfo(
			"ef8a3d8c6db2d81d9f7452e99e8fb32f",// install
			"11dbb77ea7fddc4ccf0c75542ea318af",// active
			"815c1727c3fc78a745c90e6413b6cba9",// custom audience
			"01faa75c33d9b62148764dd895fe23d8", // purchase
			"815c1727c3fc78a745c90e6413b6cba9");// app event.

	private static final MvPyxisAppInfo MV_APP_INFO_YEN_TEST_STAGING_60560 = new MvPyxisAppInfo(
			"33bbb2bfe7c1ce21091dcdb8f961e3bb",// install
			"8b81f516f53ae934946547a51f22459d",// active
			"93987374d446514e0d56282e6f1c9c42",// custom audience
			"613ef5d9acc3acc6ecbae64471d5d76c", // purchase
			"93987374d446514e0d56282e6f1c9c42");// app event.

	private static final MvPyxisAppInfo MV_APP_INFO_YEN_TEST_STAGING_62294 = new MvPyxisAppInfo(
			"7542b3dcf8ea3f9571774c726720153c",// install
			"ba740c7c40aee305bbed20000b9652ba",// active
			"5afbb9407430320790ffc05f6074105b",// custom audience
			"7925478c94dcfa2ef56f3c4f014c76dc", // purchase
			"5afbb9407430320790ffc05f6074105b");// app event.

	private static final MvPyxisAppInfo MV_APP_INFO_YEN_TEST_STAGING_151 = new MvPyxisAppInfo(
			"64ceb90493f82a0e0a25e855e5d9550f",// install
			"73fc81f0c8f77c04e03a86c87ca27e16",// active
			"89c839b575bf6481d2453c986e925490",// custom audience
			"f2d52eea464cc2e29c31ceb188e72934", // purchase
			"89c839b575bf6481d2453c986e925490");// app event.

	private static final MvPyxisAppInfo MV_APP_INFO_YEN_TEST_STAGING_174 = new MvPyxisAppInfo(
			"4bfdd023f43ea8a50d06e57d33f91ba6",// install
			"b52a1a262b820774ab25ba027ec97dc9",// active
			"510f8fc73cd7658569a35c1a6ea2ba5d",// custom audience
			"0828419cdb48628f3b3480c2b6512571", // purchase
			"510f8fc73cd7658569a35c1a6ea2ba5d");// app event.
	private static final MvPyxisAppInfo MV_APP_INFO_YEN_TEST_STAGING_61 = new MvPyxisAppInfo(
			"0f72cfceb405ef9eb4e9344032414280",// install
			"5ab345b9d7549810b66f6431c0ec309d",// active
			"",// custom audience
			"7dee4d46cb21ed8ed5f32016a2a3e116", // purchase
			"");// app event.

	private static final MvPyxisAppInfo MV_APP_INFO_HAI_ANH_TEST_STAGING_60116 = new MvPyxisAppInfo(
			"a44da247c848d6ae3873a41db5ff0f56",// install
			"5a323472235cbabef5e582e26adcbcbf",// active
			"ed1c0a6f169656680b93bce05ef7ec11",// custom audience
			"6e94713b20721a88b317671683df0e7a", // purchase
			"ed1c0a6f169656680b93bce05ef7ec11");// app event.

	private static final MvPyxisAppInfo MV_APP_INFO_HAI_ANH_TEST_STAGING_62304 = new MvPyxisAppInfo(
			"56f7989282fd02c4c99dbd922b906d1a",// install
			"9b47645379d025231f287394faf7bcd9",// active
			"ef05e403476e674d57716f0ef0423e58",// custom audience
			"faebb20b94709d40a98d283367228dbb", // purchase
			"ef05e403476e674d57716f0ef0423e58");// app event.

	private static final MvPyxisAppInfo MV_APP_INFO_HAI_ANH_TEST_STAGING_171 = new MvPyxisAppInfo(
			"7ee4ce4521ad003b2fe02dc0183c7045",// install
			"55926571a903ca408a32c8dcd3d7f79b",// active
			"41ea758840e6c5203612ee827f306f07",// custom audience
			"04ad018ca38bb2f77ef1e3332ea7bd13", // purchase
			"41ea758840e6c5203612ee827f306f07");// app event.

	private static final MvPyxisAppInfo MV_APP_INFO_CAO_HANG_TEST_STAGING_175 = new MvPyxisAppInfo(
			"d4c5a163ac2eee421bd4b3de1b7a761d",// install
			"f046fca97ef4b280f15b0ef285627e6a",// active
			"525e6128575efdf07a84ebc40efd969b",// custom audience
			"88989b4716f1bb4dc5ae936c76409af4", // purchase
			"525e6128575efdf07a84ebc40efd969b");// app event.

	private static final MvPyxisAppInfo MV_APP_INFO_CAO_HANG_TEST_STAGING_177 = new MvPyxisAppInfo(
			"5eb28736c45e89e71697fb0997b8b133",// install
			"73a8289c2ba8ad577514e9c6a3abe431",// active
			"ba5bd9e6988beee610e4f078e6db07c7",// custom audience
			"f43e39e8a861a8330c7337514fa3c68f", // purchase
			"ba5bd9e6988beee610e4f078e6db07c7");// app event.

	private static final MvPyxisAppInfo MV_APP_INFO_CAO_HANG_TEST_STAGING_173 = new MvPyxisAppInfo(
			"98bcd46e822e05245513c27b4b93cad8",// install
			"0320fcc3f726654d8f9fc913261e80b7",// active
			"43b14e70c6397f8c6c9cb2d122848e92",// custom audience
			"cadedb768b0a21bccb1a2942611aa5a3", // purchase
			"43b14e70c6397f8c6c9cb2d122848e92");// app event.

	private MvPyxisAppInfo mvAppTest = MV_APP_INFO_YEN_TEST_STAGING_174; // Define which list mv will use.
	/* For mv spinner adapter. */
	private SpinnerMV[] mSpinnerMvArray = new SpinnerMV[] { new SpinnerMV(NO_TYPE, ""),
			new SpinnerMV(OTHERS_TYPE, mvAppTest.getInAppEventMV()),
			new SpinnerMV(PURCHASE_TYPE, mvAppTest.getInAppPurchaseMV()),
			new SpinnerMV(CUSTOM_AUDIENCE_TYPE, mvAppTest.getInAppCustomAudienceMV()) };

	private SharePrefs mSharePrefs = SharePrefs.getInstance();
	private String mType = ""; // Type of mv.

	// ======================================================
	// View elements
	// ======================================================
	private FrameLayout mFlContent;
	private Spinner mSpMv;
	private Button mBtnSend;
	private Button mBtnDelDatabase;
	private Button mBtnExit;
	private TextView mTvDescription;
	private BaseFragment mFragmentContent;

	/**
	 * Define view will be used.
	 */
	private void initializeView() {
		mFlContent = (FrameLayout) findViewById(R.id.flContent);
		mTvDescription = (TextView) findViewById(R.id.tvDescription);
		mBtnSend = (Button) findViewById(R.id.btnSend);
		mBtnDelDatabase = (Button) findViewById(R.id.btnDeleteDatabase);
		mBtnExit = (Button) findViewById(R.id.btnExit);
		mSpMv = (Spinner) findViewById(R.id.spListMv);
		ArrayAdapter<SpinnerMV> adapter = new ArrayAdapter<SpinnerMV>(this,
				android.R.layout.simple_spinner_dropdown_item, mSpinnerMvArray);
		mSpMv.setAdapter(adapter);

		mBtnSend.setOnClickListener(this);
		mBtnDelDatabase.setOnClickListener(this);
		mBtnExit.setOnClickListener(this);

		// Handler event when select item on spinner.
		mSpMv.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
				SpinnerMV spinnerMv = (SpinnerMV) adapterView.getItemAtPosition(position);
				mBtnSend.setVisibility(View.VISIBLE); // Show send button to send tracking.
				if (OTHERS_TYPE.equals(spinnerMv.getType())) { // Others mv.
					/*
					 * Type tracking is OTHERS_TYPE.
					 * Set description.
					 * OthersFragment will be used.
					 */
					mType = OTHERS_TYPE;
					mTvDescription.setText(R.string.description_others_test);
					mFlContent.setVisibility(View.VISIBLE);
					mFragmentContent = new OthersFragment();
				} else if (CUSTOM_AUDIENCE_TYPE.equals(spinnerMv.getType())) { // Custom audience mv.
					/*
					 * Type tracking is CUSTOM_AUDIENCE_TYPE.
					 * Set description.
					 * CustomAudienceFragment will be used.
					 */
					mType = CUSTOM_AUDIENCE_TYPE;
					mTvDescription.setText(R.string.description_notification_test);
					mFlContent.setVisibility(View.VISIBLE);
					mFragmentContent = new CustomAudienceFragment();
				} else if (PURCHASE_TYPE.equals(spinnerMv.getType())) { // Purchase mv.
					/*
					 * Type tracking is PURCHASE_TYPE.
					 * Set description.
					 * PurchaseFragment will be used.
					 */
					mType = PURCHASE_TYPE;
					mTvDescription.setText(R.string.description_purchase_test);
					mFlContent.setVisibility(View.VISIBLE);
					mFragmentContent = new PurchaseFragment();
				} else { // No mv is selected, hide send button, layout content and reset description.
					mFlContent.setVisibility(View.GONE);
					mBtnSend.setVisibility(View.GONE);
					mTvDescription.setText("");
					mFragmentContent = null;
				}
				if (mFragmentContent != null) {
					getSupportFragmentManager().beginTransaction().replace(R.id.flContent, mFragmentContent).commit();
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> adapterView) {
			}
		});
	}

	/**
	 * Track first installation.
	 */
	private void initAndTrackInstall() {
		LogUtils.logInfo("Init Track install");
		PyxisTracking.init(this, getIntent(), "schemea", "com.septeni.polaris");
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		// Tracking install.
		PyxisTracking.trackInstall(mvAppTest.getInstallMV(), null, null, null, null, null);
	}

	/**
	 * Send tracking in-app purchase.
	 * 
	 * @param sales
	 *            the sales.
	 */
	private void trackPurchase(int sales) {
		PyxisTracking.saveTrackApp(mvAppTest.getInAppPurchaseMV(), "", sales, 10, 5, null, AppLimitMode.NONE);
		PyxisTracking.sendTrackApp();
		Toast.makeText(getApplicationContext(), "Send purchase tracking.", Toast.LENGTH_SHORT).show();
	}

	/**
	 * Send tracking custom audience.
	 * 
	 * @param email
	 * @param phone
	 * @param fbId
	 * @param appId
	 */
	private void trackCustomAudience(String email, String phone, String fbId, String appId) {
		PyxisTracking.sendCAParam(mvAppTest.getInAppCustomAudienceMV(), email, phone, fbId, appId);
		Toast.makeText(getApplicationContext(), "Send custom audience.", Toast.LENGTH_SHORT).show();
	}

	/**
	 * Send tracking active.
	 * 
	 * @param suid
	 * @param sales
	 * @param volume
	 * @param profit
	 * @param others
	 * @param limitMode
	 */
	private void trackActive(String mv, String suid, String sales, String volume, String profit, String others,
			AppLimitMode limitMode) {
		// Save info to shared preference.
		mSharePrefs.saveSUID(suid);
		mSharePrefs.saveSales(sales);
		mSharePrefs.saveVolume(volume);
		mSharePrefs.saveProfit(profit);
		mSharePrefs.saveOthers(others);

		// Send active.
		PyxisTracking.saveTrackApp(mv, suid, sales, volume, profit, others, limitMode);
		PyxisTracking.sendTrackApp();
	}

	/**
	 * Prepare data to track custom audience.
	 */
	private void prepareTrackCA() {
		String email = ((CustomAudienceFragment) mFragmentContent).getEmail();
		String phoneNumber = ((CustomAudienceFragment) mFragmentContent).getPhoneNumber();
		String fbUserId = ((CustomAudienceFragment) mFragmentContent).getFBUserId();
		String appUserId = ((CustomAudienceFragment) mFragmentContent).getAppUserId();

		// If one field is invalid, show toast message.
		if (TextUtils.isEmpty(email) || TextUtils.isEmpty(phoneNumber) || TextUtils.isEmpty(fbUserId)
				|| TextUtils.isEmpty(appUserId)) {
			Toast.makeText(this, R.string.msg_info_some_fields_empty, Toast.LENGTH_SHORT).show();
		} else { // All fields is valid.
			trackCustomAudience(email, phoneNumber, fbUserId, appUserId);
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_test_sdk);

		initializeView();

		// Initialize track install.
		initAndTrackInstall();
	}

	@Override
	protected void onResume() {
		super.onResume();
		// Track DAU active.
		// trackActive(mvAppTest.getInAppActiveDauMV(), mSharePrefs.getSUID(), mSharePrefs.getSales(),
		// mSharePrefs.getVolume(), mSharePrefs.getProfit(), mSharePrefs.getOthers(), AppLimitMode.DAU);
	}

	@Override
	public void onClick(View v) {
		if (v == mBtnSend) { // Click on Send button.
			if (OTHERS_TYPE.equals(mType)) { // Tracking with all field.
				String suid = ((OthersFragment) mFragmentContent).getSUID();
				String salesStr = ((OthersFragment) mFragmentContent).getSales();
				String volume = ((OthersFragment) mFragmentContent).getVolume();
				String profit = ((OthersFragment) mFragmentContent).getProfit();
				String others = ((OthersFragment) mFragmentContent).getOthers();
				AppLimitMode limitMode = ((OthersFragment) mFragmentContent).getLimitMode();

				if (limitMode == null) limitMode = AppLimitMode.NONE;
				Toast.makeText(this, "Send track others", Toast.LENGTH_SHORT).show();
				trackActive(mvAppTest.getInAppEventMV(), suid, salesStr, volume, profit, others, limitMode);
			} else if (CUSTOM_AUDIENCE_TYPE.equals(mType)) { // Tracking custom audience.
				prepareTrackCA();
			} else if (PURCHASE_TYPE.equals(mType)) { // Tracking purchase.
				int sales = ((PurchaseFragment) mFragmentContent).getSales();
				int volume = ((PurchaseFragment) mFragmentContent).getVolume();
				int profit = ((PurchaseFragment) mFragmentContent).getProfit();
				if (sales > 0 && volume > 0 && profit > 0) { // Has define sale number.
					trackPurchase(sales);
				} else { // Not define sale number.
					Toast.makeText(this, R.string.msg_info_some_fields_empty, Toast.LENGTH_SHORT).show();
				}
			}
		} else if (v == mBtnDelDatabase) { // Delete app database.
			Toast.makeText(this, R.string.msg_info_delete_database, Toast.LENGTH_SHORT).show();
			String filePath = "/data/data/" + getPackageName() + "/databases/pyxisTracking.db";
			File file = new File(filePath);
			if (file.exists()) file.delete();

			File saveFile = new File(Environment.getExternalStorageDirectory().getPath() + "/" + getPackageName() + "/"
					+ "pyxisTracking.db");
			if (saveFile.exists()) saveFile.delete();
			// android.os.Process.killProcess(android.os.Process.myPid());
			PendingIntent intent = PendingIntent.getActivity(this.getBaseContext(), 0, new Intent(getIntent()), getIntent().getFlags());
		    AlarmManager manager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
		    manager.set(AlarmManager.RTC, System.currentTimeMillis() + 2000, intent);
		    System.exit(2);
		} else if (v == mBtnExit) { // Exit app.
			finish();
		}
	}
}
