package com.septeni.polaris.fragment;

import jp.co.septeni.pyxis.PyxisTracking.PyxisTracking.AppLimitMode;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.septeni.polaris.R;

public class OthersFragment extends BaseFragment {

	private static final String[] APP_LIMIT_MODE = new String[] { "NONE", "DAILY", "DAU" };

	private View mParentView;
	private EditText mEtSUID;
	private EditText mEtSales;
	private EditText mEtVolume;
	private EditText mEtProfit;
	private EditText mEtOthers;
	private Spinner mSpLimitMode;

	/**
	 * Initialize view will be used.
	 */
	private void initializeView() {
		mEtSUID = (EditText) mParentView.findViewById(R.id.etSuid);
		mEtSales = (EditText) mParentView.findViewById(R.id.etSales);
		mEtVolume = (EditText) mParentView.findViewById(R.id.etVolume);
		mEtProfit = (EditText) mParentView.findViewById(R.id.etProfit);
		mEtOthers = (EditText) mParentView.findViewById(R.id.etOthers);
		mSpLimitMode = (Spinner) mParentView.findViewById(R.id.spLimitMode);

		mSpLimitMode.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item,
				APP_LIMIT_MODE));
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mParentView = inflater.inflate(R.layout.fragment_active, container, false);

		initializeView();
		return mParentView;
	}

	/**
	 * @return suid
	 */
	public String getSUID() {
		String suid = "";
		if (!TextUtils.isEmpty(mEtSUID.getText().toString())) suid = mEtSUID.getText().toString();
		return suid;
	}

	/**
	 * @return sales
	 */
	public String getSales() {
		String sales = "";
		if (!TextUtils.isEmpty(mEtSales.getText().toString())) sales = mEtSales.getText().toString();
		return sales;
	}

	/**
	 * @return volume
	 */
	public String getVolume() {
		String volume = "";
		if (!TextUtils.isEmpty(mEtVolume.getText().toString())) volume = mEtVolume.getText().toString();
		return volume;
	}

	/**
	 * @return profit
	 */
	public String getProfit() {
		String profit = "";
		if (!TextUtils.isEmpty(mEtProfit.getText().toString())) profit = mEtProfit.getText().toString();
		return profit;
	}

	/**
	 * @return others
	 */
	public String getOthers() {
		String others = "";
		if (!TextUtils.isEmpty(mEtOthers.getText().toString())) others = mEtOthers.getText().toString();
		return others;
	}

	/**
	 * @return app limit mode
	 */
	public AppLimitMode getLimitMode() {
		AppLimitMode limitMode = AppLimitMode.NONE;
		String strLimitMode = ((String) mSpLimitMode.getSelectedItem()).toString();
		if (APP_LIMIT_MODE[1].equals(strLimitMode)) {
			limitMode = AppLimitMode.DAILY;
		} else if (APP_LIMIT_MODE[2].equals(strLimitMode)) {
			limitMode = AppLimitMode.DAU;
		} else {
			limitMode = AppLimitMode.NONE;
		}
		return limitMode;
	}
}
