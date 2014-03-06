package com.septeni.polaris.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.septeni.polaris.R;

public class PurchaseFragment extends BaseFragment {

	private EditText mEtSales;
	private EditText mEtVolume;
	private EditText mEtProfit;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// Inflater layout view and initialize edittext.
		View view = inflater.inflate(R.layout.fragment_purchase, container, false);
		mEtSales = (EditText) view.findViewById(R.id.etSales);
		mEtVolume = (EditText) view.findViewById(R.id.etVolume);
		mEtProfit = (EditText) view.findViewById(R.id.etProfit);
		return view;
	}

	/**
	 * @return sales number.
	 */
	public int getSales() {
		if (TextUtils.isEmpty(mEtSales.getText().toString())) return -1;
		return Integer.parseInt(mEtSales.getText().toString());
	}

	/**
	 * @return volume number.
	 */
	public int getVolume() {
		if (TextUtils.isEmpty(mEtVolume.getText().toString())) return -1;
		return Integer.parseInt(mEtVolume.getText().toString());
	}

	/**
	 * @return profit number.
	 */
	public int getProfit() {
		if (TextUtils.isEmpty(mEtProfit.getText().toString())) return -1;
		return Integer.parseInt(mEtProfit.getText().toString());
	}
}
