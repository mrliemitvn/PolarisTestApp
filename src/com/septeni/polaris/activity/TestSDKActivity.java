package com.septeni.polaris.activity;

import java.io.File;
import java.util.Calendar;
import java.util.HashMap;

import jp.co.septeni.pyxis.PyxisTracking.PyxisDBHelper;
import jp.co.septeni.pyxis.PyxisTracking.PyxisTracking;
import jp.co.septeni.pyxis.PyxisTracking.PyxisTracking.AppLimitMode;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.septeni.polaris.R;
import com.septeni.polaris.provider.SharePrefs;
import com.septeni.polaris.utils.LogUtils;
import com.septeni.polaris.utils.Utility;

public class TestSDKActivity extends FragmentActivity implements OnClickListener {

	/* Variable store list mv. */
	/* List MV of PID 186. */
	private static final String MV_INSTALL_1 = "54ad5fc30b975c155be2db9f3b7a8d2e"; // install 1
	private static final String MV_INSTALL_3 = "ecc90491076febe4c3fd5e455463cfa9"; // install 3
	private static final String MV_INSTALL_4 = "edf107fb5c69b3fdd3eb7729448fdd98"; // install 4
	private static final String MV_INSTALL_5 = "446935140f3321cdf8288d33458a4921"; // install 5
	private static final String MV_ACTIVE_DAU = "5758a92c94d17208c22702e9f0e35620"; // active DAU.
	private static final String MV_PURCHASE_1 = "0a8309648e12dcf7768b9a02f6a04e94"; // purchase 1
	private static final String MV_PURCHASE_3 = "03be221adcc43a190f3fc84369f10521"; // purchase 3
	private static final String MV_PURCHASE_4 = "18171f13db12005d0fe6ba3af261dca0"; // purchase 4

	/* List MV of PID 179. */
	// private static final String MV_INSTALL_1 = "1818ff3e2e23f5e81960333aeed64e5c"; // install 1
	// private static final String MV_INSTALL_3 = "acb1f31d647bea6ae0052af3f30bdfa6"; // install 3
	// private static final String MV_INSTALL_4 = "6173c63104ba142e6dcbdab3aa09b905"; // install 4
	// private static final String MV_INSTALL_5 = "7379726fcebe82df4702acc903dd5372"; // install 5
	// private static final String MV_ACTIVE_DAU = "e66e412194ca5935e9332f855af1facf"; // active DAU.
	// private static final String MV_PURCHASE_1 = "23f23432adb822dbde9ee14a654593b4"; // purchase 1
	// private static final String MV_PURCHASE_3 = "04c97ad4355694ccc1381a123d4cc96d"; // purchase 3
	// private static final String MV_PURCHASE_4 = "154f9138865aeeddda6adb396399d00b"; // purchase 4

	private SharePrefs mSharePrefs = SharePrefs.getInstance();
	private Handler mHandler;
	private int mSUID = 0;

	// ======================================================
	// View elements
	// ======================================================
	private ProgressDialog mProgressDialog;
	private AlertDialog mAlertDialog;
	private TextView mTvDescription;
	private TextView mTvConversionTest;
	private TextView mTvInstallTest;
	private TextView mTvOnlineOfflineTest;
	private TextView mTvDuplicationTest;
	private TextView mTvPurchase;
	private TextView mTvNormalMode;
	private TextView mTvStart;
	private EditText mEtVerify;
	private EditText mEtSuid;
	private EditText mEtSales;
	private EditText mEtOthers;
	private EditText mEtEmail;
	private EditText mEtPhone;
	private EditText mEtFUI;
	private EditText mEtFAI;

	/**
	 * Define view will be used.
	 */
	private void initializeView() {
		mTvDescription = (TextView) findViewById(R.id.tvDescription);
		mTvConversionTest = (TextView) findViewById(R.id.tvConversionTest);
		mTvInstallTest = (TextView) findViewById(R.id.tvInstallTest);
		mTvOnlineOfflineTest = (TextView) findViewById(R.id.tvOnlineOfflineTest);
		mTvDuplicationTest = (TextView) findViewById(R.id.tvDuplicationTest);
		mTvPurchase = (TextView) findViewById(R.id.tvPurchase);
		mTvNormalMode = (TextView) findViewById(R.id.tvNormalMode);
		mTvStart = (TextView) findViewById(R.id.tvStart);
		mEtVerify = (EditText) findViewById(R.id.etVerify);
		mEtSuid = (EditText) findViewById(R.id.etSuid);
		mEtSales = (EditText) findViewById(R.id.etSales);
		mEtOthers = (EditText) findViewById(R.id.etOthers);
		mEtEmail = (EditText) findViewById(R.id.etEmail);
		mEtPhone = (EditText) findViewById(R.id.etPhone);
		mEtFUI = (EditText) findViewById(R.id.etFUI);
		mEtFAI = (EditText) findViewById(R.id.etFAI);

		/* Set click event. */
		mTvConversionTest.setOnClickListener(this);
		mTvInstallTest.setOnClickListener(this);
		mTvOnlineOfflineTest.setOnClickListener(this);
		mTvDuplicationTest.setOnClickListener(this);
		mTvPurchase.setOnClickListener(this);
		mTvNormalMode.setOnClickListener(this);
		mTvStart.setOnClickListener(this);

		/* Update view state. */
		updateView();
	}

	/**
	 * Update view state.
	 */
	private void updateView() {
		/* Update testing type. */
		int type = mSharePrefs.getType();
		mTvConversionTest.setSelected(false);
		mTvInstallTest.setSelected(false);
		mTvOnlineOfflineTest.setSelected(false);
		mTvDuplicationTest.setSelected(false);
		switch (type) {
		case SharePrefs.CONVERSION_TESTING:
			mTvConversionTest.setSelected(true);
			break;
		case SharePrefs.INSTALL_TESTING:
			mTvInstallTest.setSelected(true);
			break;
		case SharePrefs.ONLINE_OFFLINE_TESTING:
			mTvOnlineOfflineTest.setSelected(true);
			break;
		case SharePrefs.DUPLICATION_TESTING:
			mTvDuplicationTest.setSelected(true);
			break;
		default:
			break;
		}

		/* Update testing mode. */
		int mode = mSharePrefs.getMode();
		if (mode == SharePrefs.NORMAL_MODE) {
			mTvNormalMode.setSelected(true);
		} else {
			mTvNormalMode.setSelected(false);
		}
	}

	/**
	 * Initialize SDK.
	 */
	private void initializeSDK() {
		LogUtils.logInfo("Init SDK Tracking");
		PyxisTracking.init(this, getIntent(), "schemea", "com.septeni.polaris");
		sleepApplication(3000);
	}

	/**
	 * Sleep application.
	 * 
	 * @param milliseconds
	 *            pause times.
	 */
	private void sleepApplication(long milliseconds) {
		try {
			Thread.sleep(milliseconds);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Track install.
	 * 
	 * @param installMV
	 *            mv to track.
	 */
	private void trackInstall(String installMV) {
		/* Change verify. */
		changeSDKVerify(false);

		/* Tracking install. */
		PyxisTracking.trackInstall(installMV, null, null, null, null, null);

		/* Show information about browser: open or not open after track install. */
		PyxisDBHelper dbHelper = new PyxisDBHelper();
		HashMap<String, Object> installMap = Utility.selInstall(dbHelper);
		dbHelper.close();

		String description = "";
		int isBrowserUped = 0;
		if (installMap.get("IS_BROWSER_UPED") != null) {
			isBrowserUped = (Integer) installMap.get("IS_BROWSER_UPED");
		}
		if (isBrowserUped == 0) {
			description = "Browser not open";
		} else {
			description = "Browser open";
		}
		mTvDescription.setText(description);
	}

	/**
	 * Send tracking in-app purchase.
	 * 
	 * @param mv
	 *            mv to track.
	 * @param suid
	 *            suid to track.
	 * @param sales
	 *            the sales.
	 * @param volume
	 *            the volume.
	 * @param profit
	 *            the profit.
	 */
	private void trackPurchase(String mv, String suid, int sales, int volume, int profit) {
		PyxisTracking.saveTrackApp(mv, suid, sales, volume, profit, null, AppLimitMode.NONE);
		PyxisTracking.sendTrackApp();
	}

	/**
	 * Send tracking custom audience.
	 * 
	 * @param mv
	 * @param email
	 * @param phone
	 * @param fbId
	 * @param appId
	 */
	private void trackCustomAudience(String mv, String email, String phone, String fbId, String appId) {
		showProgress("Send custom audience ...");
		PyxisTracking.sendCAParam(mv, email, phone, fbId, appId);
		executeHandler(new Runnable() {
			@Override
			public void run() {
				if (mProgressDialog != null && mProgressDialog.isShowing()) mProgressDialog.dismiss();
			}
		}, 3000);
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
		// Send active.
		PyxisTracking.saveTrackApp(mv, suid, sales, volume, profit, others, limitMode);
		PyxisTracking.sendTrackApp();
	}

	/**
	 * Track active DAU.
	 */
	private void trackDAU() {
		/* If verify not generated, need set it. */
		PyxisDBHelper dbHelper = new PyxisDBHelper();
		HashMap<String, Object> userMap = Utility.selUser(dbHelper);
		dbHelper.close();
		if (TextUtils.isEmpty((String) userMap.get("UUID"))) {
			changeSDKVerify(false);
		}
		trackActive(MV_ACTIVE_DAU, "", "", "", "", "", AppLimitMode.DAU);
	}

	/**
	 * Prepare track DAU.<br>
	 * Only track one time per day.
	 */
	private void prepareTrackDAU() {
		Calendar calendar = Calendar.getInstance();
		Calendar calendarTrack = Calendar.getInstance();
		long timeTrackDAU = mSharePrefs.getTrackDAUTime();
		calendarTrack.setTimeInMillis(timeTrackDAU);
		if (calendar.get(Calendar.DAY_OF_MONTH) == calendarTrack.get(Calendar.DAY_OF_MONTH)
				&& calendar.get(Calendar.MONTH) == calendarTrack.get(Calendar.MONTH)
				&& calendar.get(Calendar.YEAR) == calendarTrack.get(Calendar.YEAR)) {
			return; // This day already tracked, do nothing.
		} else {
			trackDAU();
			mSharePrefs.setTrackDAUTime(System.currentTimeMillis());
		}
	}

	/**
	 * Prepare data to track CA.
	 */
	private void prepareTrackCA() {
		/* Get all data. */
		String em = mEtEmail.getText().toString();
		String pn = mEtPhone.getText().toString();
		String fui = mEtFUI.getText().toString();
		String fai = mEtFAI.getText().toString();

		/* If any data not set, show toast. */
		if (TextUtils.isEmpty(em) || TextUtils.isEmpty(pn) || TextUtils.isEmpty(fui) || TextUtils.isEmpty(fai)) {
			Toast.makeText(this, "You must enter em, pn, fui, fai!", Toast.LENGTH_SHORT).show();
		} else {
			validateVerify(); // Need verify before track.
			trackInstall(MV_INSTALL_5);
			// Track CA.
			trackCustomAudience(MV_PURCHASE_1, em, pn, fui, fai);
		}
	}

	/**
	 * Prepare data to track purchase.
	 */
	private void prepareTrackPurchase() {
		/* Get all data. */
		String suid = mEtSuid.getText().toString();
		String sales = mEtSales.getText().toString();
		String others = mEtOthers.getText().toString();

		showProgress("Tracking purchase ... ");
		validateVerify(); // Need verify before track.
		trackInstall(MV_INSTALL_5);
		trackActive(MV_PURCHASE_1, suid, sales, "1000", "1000", others, AppLimitMode.NONE);
		executeHandler(new Runnable() {
			@Override
			public void run() {
				if (mProgressDialog != null && mProgressDialog.isShowing()) mProgressDialog.dismiss();
			}
		}, 3000);
	}

	/**
	 * If user set verify, set it.
	 * else, if verify is empty, generate it automatically.
	 */
	private void validateVerify() {
		/* If user does not set verify or verify not generated, need set it. */
		PyxisDBHelper dbHelper = new PyxisDBHelper();
		if (TextUtils.isEmpty(mEtVerify.getText().toString())) {
			HashMap<String, Object> userMap = Utility.selUser(dbHelper);
			if (TextUtils.isEmpty((String) userMap.get("UUID"))) {
				changeSDKVerify(false);
			}
		} else {
			Utility.updUser(dbHelper, mEtVerify.getText().toString());
		}
		dbHelper.close();
	}

	/**
	 * Send install tracking and set next step.<br>
	 * <ul>
	 * Track install and purchase. After that, delete SDK database, set next step. Then restart application.
	 * </ul>
	 * 
	 * @param installMV
	 *            install MV to track.
	 * @param installNumber
	 *            current install number.
	 * @param nextStep
	 *            next step to set.
	 */
	private void trackConversion(String installMV, int installNumber, final int nextStep) {
		/* Show progress dialog when tracking install. */
		showProgress("Tracking install" + " " + installNumber + " ...");

		trackInstall(installMV); // Track install.

		/* Task to set next step then delete SDK data. */
		final Runnable nextStepAndDeleteSDKData = new Runnable() {
			@Override
			public void run() {
				mSharePrefs.setStep(nextStep); // Set next step testing.
				deleteSDKDataAndRestart(); // Delete SDK data.
			}
		};

		/* Task to track purchase and execute nextStepAndDeleteSDKData task . */
		Runnable trackPurchaseTask = new Runnable() {
			@Override
			public void run() {
				showProgress("Tracking purchase 1 ..."); // Show progress dialog when tracking purchase.
				trackPurchase(MV_PURCHASE_1, mSUID + "", 1000, 2000, 3000); // Track purchase.
				executeHandler(nextStepAndDeleteSDKData, 5000); // Execute nextStepAndDeleteSDKData.
			}
		};

		/* Execute task. */
		executeHandler(trackPurchaseTask, 5000);
	}

	/**
	 * Change wifi on or off.
	 * 
	 * @param isTurnOn
	 *            is true if want to turn wifi on, else set is false.
	 */
	private boolean changeNetworkState(boolean isTurnOn) {
		try {
			/* Show progress dialog turn wifi on or off. */
			if (isTurnOn) {
				showProgress("Set Wifi on ..."); // Show dialog when turn wifi on.
			} else {
				showProgress("Set Wifi off ..."); // Show dialog when turn wifi off.
			}

			/* Turn wifi on or off. */
			WifiManager wifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
			wifiManager.setWifiEnabled(isTurnOn);

			/* If turn on wifi, wait until wifi connected. */
			if (isTurnOn) {
				while (!isNetworkOnline()) {
					/* Do nothing when wifi has not connected. */
					LogUtils.logInfo("Wifi not connected.");
				}
			}
			return true; // Return change wifi state successfully.
		} catch (Exception e) { // Handle exception.
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Check wifi network is online or offline.
	 * 
	 * @return true if wifi is online, else false.
	 */
	public boolean isNetworkOnline() {
		boolean status = false; // Default state.
		try {
			/* Check network state. */
			ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo netInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
			if (netInfo != null && netInfo.getState() == NetworkInfo.State.CONNECTED) {
				status = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return status;

	}

	/**
	 * Change SDK verify.
	 * 
	 * @param showProgress
	 *            true if want to show progress.
	 */
	private void changeSDKVerify(boolean showProgress) {
		/* Show progress dialog when changing SDK verify if need. */
		if (showProgress) showProgress("Changing verify ...");
		String pyxisVerify = Utility.generateUuid();
		PyxisDBHelper dbHelper = new PyxisDBHelper();
		Utility.updUser(dbHelper, pyxisVerify);
		dbHelper.close();
	}

	/**
	 * Change SUID.
	 */
	private void changeSUID() {
		/* Show progress when changing SUID. */
		showProgress("Changing SUID ...");
		mSUID++; // Change SUID.
	}

	/**
	 * Start duplication testing in flow 1:<br>
	 * <ul>
	 * Install 1 > Purchase 1 > Purchase 1 > Purchase 2 > Purchase 2.<br>
	 * Then call flow 2 automatically.
	 * </ul>
	 */
	private void beginDuplicationFlow1() {
		/* Task to next flow. */
		final Runnable nextFlow = new Runnable() {
			@Override
			public void run() {
				beginDuplicationFlow2(); // Begin flow 2.
			}
		};

		/* Task for track purchase 1 again then execute taskTrackPurchase2. */
		final Runnable taskTrackPurchase1Again = new Runnable() {
			@Override
			public void run() {
				showProgress("Tracking purchase 1 again ..."); // Show progress dialog when tracking purchase.
				trackPurchase(MV_PURCHASE_1, mSUID + "", 1000, 2000, 3000); // Track purchase.
				executeHandler(nextFlow, 5000); // Execute next flow.
			}
		};

		/* Task for track purchase 1 then execute taskTrackPurchase1Again. */
		Runnable taskTrackPurchase1 = new Runnable() {
			@Override
			public void run() {
				showProgress("Tracking purchase 1 ..."); // Show progress dialog when tracking purchase.
				trackPurchase(MV_PURCHASE_1, mSUID + "", 1000, 2000, 3000); // Track purchase.
				executeHandler(taskTrackPurchase1Again, 5000); // Execute taskTrackPurchase1Again.
			}
		};

		/* Show progress dialog when tracking install. */
		showProgress("Tracking install 1 ...");

		trackInstall(MV_INSTALL_1); // Track install.

		executeHandler(taskTrackPurchase1, 5000);
	}

	/**
	 * Start duplication testing in flow 2:<br>
	 * <ul>
	 * Change verify > Purchase 3 > Purchase 3.<br>
	 * Then call flow 3 automatically.
	 * </ul>
	 */
	private void beginDuplicationFlow2() {
		/* Change verify. */
		changeSDKVerify(true);

		/* Task to next flow. */
		final Runnable nextFlow = new Runnable() {
			@Override
			public void run() {
				beginDuplicationFlow3(); // Next flow.
			}
		};

		/* Task to track purchase 3 again. */
		final Runnable trackPurchase3Again = new Runnable() {
			@Override
			public void run() {
				showProgress("Tracking purchase 3 again ..."); // Show progress dialog when tracking purchase 3.
				trackPurchase(MV_PURCHASE_3, mSUID + "", 1000, 2000, 3000); // Track purchase 3 again.
				executeHandler(nextFlow, 5000); // Execute next flow.
			}
		};

		/* Task to track purchase. */
		Runnable trackPurchase3 = new Runnable() {
			@Override
			public void run() {
				/* Show progress dialog when tracking purchase 3. */
				showProgress("Tracking purchase 3 ...");

				trackPurchase(MV_PURCHASE_3, mSUID + "", 1000, 2000, 3000); // Track purchase 3.

				/* After track purchase 3, track purchase 3 again and next flow. */
				executeHandler(trackPurchase3Again, 5000);
			}
		};

		executeHandler(trackPurchase3, 3000);
	}

	/**
	 * Start duplication testing in flow 3:<br>
	 * <ul>
	 * Change verify > Purchase 3.<br>
	 * Then call flow 4 automatically.
	 * </ul>
	 */
	private void beginDuplicationFlow3() {
		/* Change verify. */
		changeSDKVerify(true);

		/* Task to next flow. */
		final Runnable nextFlow = new Runnable() {
			@Override
			public void run() {
				beginDuplicationFlow4();
			}
		};

		/* Task to track purchase 3. */
		Runnable trackPurchase3 = new Runnable() {
			@Override
			public void run() {
				/* Show progress dialog when tracking purchase 3. */
				showProgress("Tracking purchase 3 ...");

				trackPurchase(MV_PURCHASE_3, mSUID + "", 1000, 2000, 3000); // Track purchase 3.

				/* Next flow. */
				executeHandler(nextFlow, 5000);
			}
		};

		executeHandler(trackPurchase3, 3000);
	}

	/**
	 * Start duplication testing in flow 4:<br>
	 * <ul>
	 * Change suid > Purchase 4 > Purchase 4.<br>
	 * Then call flow 5 automatically.
	 * </ul>
	 */
	private void beginDuplicationFlow4() {
		/* Change SUID. */
		changeSUID();

		/* Task to next flow. */
		final Runnable nextFlow = new Runnable() {
			@Override
			public void run() {
				beginDuplicationFlow5(); // Next flow.
			}
		};

		/* Task to track purchase 4 again then next flow. */
		final Runnable trackPurchase4AgainAndNextFlow = new Runnable() {
			@Override
			public void run() {
				/* Show progress when tracking purchase 4. */
				showProgress("Tracking purchase 4 again ...");
				trackPurchase(MV_PURCHASE_4, mSUID + "", 1000, 2000, 3000); // Track purchase 4.
				executeHandler(nextFlow, 5000); // Execute next flow.
			}
		};

		/* Task begin track purchase 4. */
		Runnable trackPurchase4 = new Runnable() {
			@Override
			public void run() {
				/* Show progress when tracking purchase 4. */
				showProgress("Tracking purchase 4 ...");
				trackPurchase(MV_PURCHASE_4, mSUID + "", 1000, 2000, 3000); // Track purchase 4.
				executeHandler(trackPurchase4AgainAndNextFlow, 5000); // Execute trackPurchase4AgainAndNextFlow task.
			}
		};
		executeHandler(trackPurchase4, 3000);
	}

	/**
	 * Start duplication testing in flow 5:<br>
	 * <ul>
	 * Change suid > Purchase 4 > Uninstall.<br>
	 * Then reset step.
	 * </ul>
	 */
	private void beginDuplicationFlow5() {
		/* Change SUID. */
		changeSUID();

		/* Task to uninstall applicaton and reset step. */
		final Runnable uninstallAndResetStep = new Runnable() {
			@Override
			public void run() {
				mSharePrefs.setStep(SharePrefs.PREPARE_STEP); // Reset step.
				deleteSDKDataAndRestart(); // Uninstall.
			}
		};

		/* Task to track purchase 4 and finish test. */
		Runnable trackPurchase4 = new Runnable() {
			@Override
			public void run() {
				showProgress("Tracking purchase 4 ..."); // Show progress dialog when tracking purchase 4.
				trackPurchase(MV_PURCHASE_4, mSUID + "", 1000, 2000, 3000); // Track purchase.
				executeHandler(uninstallAndResetStep, 5000); // Execute uninstallAndResetStep task.
			}
		};
		executeHandler(trackPurchase4, 3000);
	}

	/**
	 * Show alert request user update application to test install tracking after update.
	 */
	private void showAlertRequestUpdateApp() {
		if (mAlertDialog == null) {
			AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
			alertBuilder.setTitle("Update version");
			alertBuilder.setMessage("To test this \"Update version\" case, please update application!");
			alertBuilder.setCancelable(false);
			alertBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					dialog.cancel();
				}
			});

			mAlertDialog = alertBuilder.create();
		}
		mAlertDialog.show();
	}

	/**
	 * Work with case 1: conversion testing. <br>
	 * Workflow:<br>
	 * <ul>
	 * <li>Step 1: Install1 > Purchase1 > Uninstall.
	 * <li>Step 2: Install3 > Purchase1 > Uninstall.
	 * <li>Step 3: Install4 > Purchase1 > Uninstall.
	 * <li>Step 4: Install5 > Purchase1.
	 * </ul>
	 */
	private void conversionTesting() {
		/* Get testing step from preferences to work. */
		int step = mSharePrefs.getStep();
		switch (step) {
		case SharePrefs.FIRST_STEP: // Track install 1 and purchase.
			trackConversion(MV_INSTALL_1, 1, SharePrefs.SECOND_STEP);
			break;
		case SharePrefs.SECOND_STEP: // Track install 3 and purchase.
			trackConversion(MV_INSTALL_3, 3, SharePrefs.THIRD_STEP);
			break;
		case SharePrefs.THIRD_STEP: // Track install 4 and purchase.
			trackConversion(MV_INSTALL_4, 4, SharePrefs.FOURTH_STEP);
			break;
		case SharePrefs.FOURTH_STEP: // Track install 5 and purchase.
			trackConversion(MV_INSTALL_5, 5, SharePrefs.PREPARE_STEP);
			break;
		default:
			break;
		}
	}

	/**
	 * Work with case 2: install testing. <br>
	 * Workflow:<br>
	 * <ul>
	 * <li>Step 1: Install1 > Move to background > Launch.
	 * <li>Step 2: Uninstall.
	 * <li>Step 3: Install1 > Kill process > Launch.
	 * <li>Step 4: Uninstall.
	 * <li>Step 5: Install1 > Update version > Launch.
	 * </ul>
	 */
	private void installTesting() {
		/* Get testing step from preferences to work. */
		int step = mSharePrefs.getStep();
		switch (step) {
		case SharePrefs.FIRST_STEP:
			/* Show progress dialog when tracking install. */
			showProgress("Tracking install 1 ...");

			trackInstall(MV_INSTALL_1); // Track install.

			/* Set next step then move application to background and relaunch. */
			executeHandler(new Runnable() {
				@Override
				public void run() {
					mSharePrefs.setStep(SharePrefs.SECOND_STEP); // Set next step.
					moveToBackgroundAndRelaunch(); // Move application to background and relaunch.
				}
			}, 5000);
			break;
		case SharePrefs.SECOND_STEP:
			mSharePrefs.setStep(SharePrefs.THIRD_STEP); // Set next step.
			deleteSDKDataAndRestart(); // Delete SDK data and restart application.
			break;
		case SharePrefs.THIRD_STEP:
			/* Show progress dialog when tracking install. */
			showProgress("Tracking install 1 ...");

			trackInstall(MV_INSTALL_1); // Track install.

			/* Set next step then kill process and relaunch. */
			executeHandler(new Runnable() {
				@Override
				public void run() {
					mSharePrefs.setStep(SharePrefs.FOURTH_STEP); // Set next step.
					killProcessAndRelaunch(); // Kill process and relaunch.
				}
			}, 5000);
			break;
		case SharePrefs.FOURTH_STEP:
			mSharePrefs.setStep(SharePrefs.FIFTH_STEP); // Set next step.
			deleteSDKDataAndRestart(); // Delete SDK data and restart application.
			break;
		case SharePrefs.FIFTH_STEP:
			/* Get application version. */
			String version = "1.0";
			try {
				PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
				version = pInfo.versionName;
			} catch (Exception e) {
				e.printStackTrace();
				LogUtils.logError(e.getMessage());
			}

			if ("1.1".equals(version)) { // Test with version updated.
				showProgress("Finish install testing ...");

				/* Set reset step then delete SDK data and restart application. */
				Runnable resetStep = new Runnable() {
					@Override
					public void run() {
						mSharePrefs.setStep(SharePrefs.PREPARE_STEP); // Set next step.
						deleteSDKDataAndRestart(); // Delete SDK data and restart application.
					}
				};

				executeHandler(resetStep, 3000);
			} else {
				/* Show progress dialog when tracking install. */
				showProgress("Tracking install 1 ...");

				trackInstall(MV_INSTALL_1); // Track install.

				/* Prepare update application version. */
				executeHandler(new Runnable() {
					@Override
					public void run() {
						// Show alert to request user update application.
						if (mProgressDialog != null && mProgressDialog.isShowing()) mProgressDialog.dismiss();
						showAlertRequestUpdateApp();
					}
				}, 5000);
			}

			break;
		default:
			break;
		}
	}

	/**
	 * Work with case 3: online/offline testing. <br>
	 * Workflow:<br>
	 * <ul>
	 * <li>Step 1: Offline > Install1 > Purchase1 > Kill process > Launch.
	 * <li>Step 2: Online > Relaunch.
	 * <li>Step 3: Uninstall.
	 * <li>Step 4: Install1 > Offline > Purchase 1 > Online > Purchase 1.
	 * </ul>
	 */
	private void onlineOfflineTesting() {
		/* Get testing step from preferences to work. */
		int step = mSharePrefs.getStep();
		switch (step) {
		case SharePrefs.FIRST_STEP:
			/* Turn off wifi. */
			changeNetworkState(false);

			/* Task to set next step then kill process and relaunch. */
			final Runnable nextStepAndKillProcess = new Runnable() {
				@Override
				public void run() {
					mSharePrefs.setStep(SharePrefs.SECOND_STEP); // Set next step.
					killProcessAndRelaunch(); // Kill process and relaunch.
				}
			};

			/* Task to track purchase then execute nextStepAndKillProcess task. */
			final Runnable trackPurchaseAndKillProcess = new Runnable() {
				@Override
				public void run() {
					showProgress("Tracking purchase 1 ..."); // Show progress dialog when tracking purchase.
					trackPurchase(MV_PURCHASE_1, mSUID + "", 1000, 2000, 3000); // Track purchase.
					executeHandler(nextStepAndKillProcess, 5000); // Execute nextStepAndKillProcess task.
				}
			};

			/* Task to track install and execute trackPurchaseAndKillProcess task. */
			Runnable trackInstallFirstStep = new Runnable() {
				@Override
				public void run() {
					showProgress("Tracking install 1 ..."); // Show progress dialog when tracking install.
					trackInstall(MV_INSTALL_1); // Track install.
					executeHandler(trackPurchaseAndKillProcess, 5000); // Execute trackPurchaseAndKillProcess task.
				}
			};

			/* Begin testing in this step. */
			executeHandler(trackInstallFirstStep, 5000);
			break;
		case SharePrefs.SECOND_STEP:
			/* Turn on wifi. */
			changeNetworkState(true);

			/* Set next step then move application to background and relaunch. */
			executeHandler(new Runnable() {
				@Override
				public void run() {
					mSharePrefs.setStep(SharePrefs.THIRD_STEP); // Set next step.
					moveToBackgroundAndRelaunch(); // Move application to background and relaunch.
				}
			}, 5000);
			break;
		case SharePrefs.THIRD_STEP:
			mSharePrefs.setStep(SharePrefs.FOURTH_STEP); // Set next step.
			deleteSDKDataAndRestart(); // Delete SDK data and restart application.
			break;
		case SharePrefs.FOURTH_STEP:
			/* Show progress dialog when tracking install. */
			showProgress("Traking install 1 ...");

			trackInstall(MV_INSTALL_1); // Track install.

			/* Task to finish step then delete application and relaunch. */
			final Runnable finishStepAndDeleteSDKData = new Runnable() {
				@Override
				public void run() {
					mSharePrefs.setStep(SharePrefs.PREPARE_STEP); // Set next step.
					deleteSDKDataAndRestart(); // Delete SDK data and restart application.
				}
			};

			/* Task to track purchase then finish this step. */
			final Runnable trackPurchaseAndFinish = new Runnable() {
				@Override
				public void run() {
					showProgress("Tracking purchase 1 ..."); // Show progress dialog when tracking purchase.
					trackPurchase(MV_PURCHASE_1, mSUID + "", 1000, 2000, 3000); // Track purchase.
					executeHandler(finishStepAndDeleteSDKData, 5000); // Execute finishStepAndDeleteSDKData task.
				}
			};

			/* Task to turn on wifi and track purchase. */
			final Runnable turnOnWifiAndTrackPurchase = new Runnable() {
				@Override
				public void run() {
					changeNetworkState(true); // Turn on wifi.
					executeHandler(trackPurchaseAndFinish, 5000); // Execute trackPurchaseAndFinish task.
				}
			};

			/* Task to track purchase then execute turnOnWifiAndTrackPurchase task. */
			final Runnable trackPurchaseThenTurnOnWifi = new Runnable() {
				@Override
				public void run() {
					showProgress("Tracking purchase 1 ..."); // Show progress dialog when tracking purchase.
					trackPurchase(MV_PURCHASE_1, mSUID + "", 1000, 2000, 3000); // Track purchase.
					executeHandler(turnOnWifiAndTrackPurchase, 5000); // Execute turnOnWifiAndTrackPurchase task.
				}
			};

			/* Task to turn off wifi and track purchase. */
			Runnable turnOffWifiAndTrackPurchase = new Runnable() {
				@Override
				public void run() {
					changeNetworkState(false); // Turn off wifi.
					executeHandler(trackPurchaseThenTurnOnWifi, 5000); // Execute trackPurchaseThenTurnOnWifi task.
				}
			};

			/* Begin testing in this test. */
			executeHandler(turnOffWifiAndTrackPurchase, 5000);
			break;
		default:
			break;
		}
	}

	/**
	 * Work with case 5: duplication testing. <br>
	 * Workflow:<br>
	 * <ul>
	 * <li>First: Install 1 > Purchase 1 > Purchase 1 > Purchase 2 > Purchase 2.
	 * <li>Then: Change verify > Purchase 3 > Purchase 3.
	 * <li>Then: Change verify > Purchase 3.
	 * <li>Then: Change suid > Purchase 4 > Purchase 4.
	 * <li>Then: Change suid > Purchase 4 > Uninstall.
	 * </ul>
	 */
	private void duplicationTesting() {
		/* Get testing step from preferences to work. */
		int step = mSharePrefs.getStep();
		switch (step) {
		case SharePrefs.FIRST_STEP:
			beginDuplicationFlow1();
			break;
		default:
			break;
		}
	}

	/**
	 * Prepare relaunch application.
	 * 
	 * @param milliseconds
	 *            application will relaunch after this time (in milliseconds).
	 */
	private void prepareRelaunch(long milliseconds) {
		Intent intentApp = new Intent(this, TestSDKActivity.class);
		PendingIntent intent = PendingIntent.getActivity(this.getBaseContext(), 0, intentApp, getIntent().getFlags());
		AlarmManager manager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
		manager.set(AlarmManager.RTC, System.currentTimeMillis() + milliseconds, intent);
	}

	/**
	 * Delete SDK database and restart application.
	 */
	private void deleteSDKDataAndRestart() {
		showProgress(getResources().getString(R.string.msg_info_delete_database_and_restart));

		/* Delete SDK database. */
		String filePath = "/data/data/" + getPackageName() + "/databases/pyxisTracking.db";
		File file = new File(filePath);
		if (file.exists()) file.delete();

		File saveFile = new File(Environment.getExternalStorageDirectory().getPath() + "/" + getPackageName() + "/"
				+ "pyxisTracking.db");
		if (saveFile.exists()) saveFile.delete();

		/* Restart application. */
		prepareRelaunch(7000);
		executeHandler(new Runnable() {
			@Override
			public void run() {
				System.exit(2);
			}
		}, 5000);
	}

	/**
	 * Move application to background and relaunch.
	 */
	private void moveToBackgroundAndRelaunch() {
		showProgress("Move app to background and relaunch ... ");

		/* Prepare intent to relaunch. */
		prepareRelaunch(7000);

		/* Back to home (move app to background). */
		executeHandler(new Runnable() {
			@Override
			public void run() {
				Intent startMain = new Intent(Intent.ACTION_MAIN);
				startMain.addCategory(Intent.CATEGORY_HOME);
				startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(startMain);
			}
		}, 5000);
	}

	/**
	 * Kill app process and relaunch.
	 */
	private void killProcessAndRelaunch() {
		showProgress("Kill application process and relaunch ... ");
		/* Prepare intent to relaunch. */
		prepareRelaunch(7000);
		/* Kill process. */
		executeHandler(new Runnable() {
			@Override
			public void run() {
				android.os.Process.killProcess(android.os.Process.myPid());
			}
		}, 5000);
	}

	/**
	 * Start testing.
	 */
	private void startTesting() {
		/* Get testing type is saved in preferences. */
		int type = mSharePrefs.getType();
		switch (type) {
		case SharePrefs.CONVERSION_TESTING:
			/* Conversion testing. */
			conversionTesting();
			break;
		case SharePrefs.INSTALL_TESTING:
			/* Install testing. */
			installTesting();
			break;
		case SharePrefs.ONLINE_OFFLINE_TESTING:
			/* Online/Offline testing. */
			onlineOfflineTesting();
			break;
		case SharePrefs.DUPLICATION_TESTING:
			/* Duplication testing. */
			duplicationTesting();
			break;
		default:
			break;
		}
	}

	/**
	 * Show progress dialog with message.
	 * 
	 * @param message
	 *            message to show.
	 */
	private void showProgress(String message) {
		if (mProgressDialog == null) {
			mProgressDialog = new ProgressDialog(this);
			mProgressDialog.setCancelable(false);
			mProgressDialog.setIndeterminate(true);
		}
		mProgressDialog.setMessage(message);
		if (mProgressDialog.isShowing()) mProgressDialog.dismiss();
		mProgressDialog.show();
	}

	/**
	 * Execute handler after delay times.
	 * 
	 * @param runnable
	 *            process to execute.
	 * @param delayMillis
	 *            delay times.
	 */
	private void executeHandler(Runnable runnable, long delayMillis) {
		mHandler.postDelayed(runnable, delayMillis);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_test_sdk);

		initializeView();

		// Initialize SDK.
		initializeSDK();
	}

	@Override
	protected void onResume() {
		super.onResume();
		LogUtils.logInfo("onResume");

		/* Prepare track DAU. */
		prepareTrackDAU();

		if (mHandler == null) mHandler = new Handler(); // Initialize handler.

		/* If not in testing workflow, do nothing. */
		int testingStep = mSharePrefs.getStep();
		LogUtils.logInfo("Step = " + testingStep);
		if (testingStep == SharePrefs.PREPARE_STEP) {
			Toast.makeText(this, "Ready!", Toast.LENGTH_SHORT).show();
			return;
		}

		/* Start automatic testing. */
		startTesting();
	}

	@Override
	protected void onPause() {
		super.onPause();

		/* Dismiss progress dialog and alert dialog when activity pause. */
		if (mProgressDialog != null && mProgressDialog.isShowing()) mProgressDialog.dismiss();
		if (mAlertDialog != null && mAlertDialog.isShowing()) mAlertDialog.dismiss();
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.tvConversionTest: // Choose conversion testing type.
			mSharePrefs.setMode(SharePrefs.AUTO_MODE);
			mSharePrefs.setType(SharePrefs.CONVERSION_TESTING);
			updateView();
			break;
		case R.id.tvInstallTest: // Choose install testing type.
			mSharePrefs.setMode(SharePrefs.AUTO_MODE);
			mSharePrefs.setType(SharePrefs.INSTALL_TESTING);
			updateView();
			break;
		case R.id.tvOnlineOfflineTest: // Choose online/offline testing type.
			mSharePrefs.setMode(SharePrefs.AUTO_MODE);
			mSharePrefs.setType(SharePrefs.ONLINE_OFFLINE_TESTING);
			updateView();
			break;
		case R.id.tvDuplicationTest: // Choose duplication testing type.
			mSharePrefs.setMode(SharePrefs.AUTO_MODE);
			mSharePrefs.setType(SharePrefs.DUPLICATION_TESTING);
			updateView();
			break;
		case R.id.tvPurchase: // Choose purchase testing type.
			prepareTrackPurchase();
			break;
		case R.id.tvStart: // Tap on start button.
			/*
			 * If in normal mode, track manually.
			 * else, track automatically.
			 */
			if (mSharePrefs.getMode() == SharePrefs.NORMAL_MODE) {
				prepareTrackCA();
			} else if (SharePrefs.PREPARE_STEP == mSharePrefs.getStep()) {
				mSharePrefs.setStep(SharePrefs.FIRST_STEP);
				startTesting();
			}
			break;
		case R.id.tvNormalMode: // Tap on normal button.
			mSharePrefs.setMode(SharePrefs.NORMAL_MODE);
			mSharePrefs.setType(SharePrefs.UNKNOWN_TYPE);
			updateView();
			break;
		default:
			break;
		}
	}
}
