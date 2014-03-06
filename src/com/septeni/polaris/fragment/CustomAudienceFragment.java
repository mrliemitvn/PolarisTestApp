package com.septeni.polaris.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.septeni.polaris.R;

public class CustomAudienceFragment extends BaseFragment {

	private EditText mEtEmail;
	private EditText mEtPhoneNumber;
	private EditText mEtFBUserId;
	private EditText mEtAppUserId;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_custom_audience, container, false);

		// Initialize view wil be used.
		mEtEmail = (EditText) view.findViewById(R.id.etEmail);
		mEtPhoneNumber = (EditText) view.findViewById(R.id.etPhoneNumber);
		mEtFBUserId = (EditText) view.findViewById(R.id.etFBUserId);
		mEtAppUserId = (EditText) view.findViewById(R.id.etAppUserId);

		return view;
	}

	/**
	 * @return email
	 */
	public String getEmail() {
		return mEtEmail.getText().toString();
	}

	/**
	 * @return phone number
	 */
	public String getPhoneNumber() {
		return mEtPhoneNumber.getText().toString();
	}

	/**
	 * @return facebook user id
	 */
	public String getFBUserId() {
		return mEtFBUserId.getText().toString();
	}

	/**
	 * @return app user id
	 */
	public String getAppUserId() {
		return mEtAppUserId.getText().toString();
	}
}
