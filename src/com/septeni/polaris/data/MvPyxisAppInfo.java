package com.septeni.polaris.data;

/**
 * MV to track: <br>
 * - Install - Active DAU - Custom Audience - Purchase - In App Event
 */
public class MvPyxisAppInfo {
	private String installMV; // install mv.
	private String inAppActiveDauMV; // in app active DAU mv.
	private String inAppCustomAudienceMV; // in app custom audience mv.
	private String inAppPurchaseMV; // in app purchase mv.
	private String inAppEventMV; // in app event mv.

	/**
	 * Constructor initialize all mv values.
	 * 
	 * @param installMV
	 *            the installMV to set.
	 * @param inAppActiveDauMV
	 *            the inAppActiveDauMV to set.
	 * @param inAppCustomAudienceMV
	 *            the inAppCustomAudienceMV to set.
	 * @param inAppPurchaseMV
	 *            the inAppPurchaseMV to set.
	 * @param inAppEventMV
	 *            the inAppEventMV to set.
	 */
	public MvPyxisAppInfo(String installMV, String inAppActiveDauMV, String inAppCustomAudienceMV,
			String inAppPurchaseMV, String inAppEventMV) {
		this.installMV = installMV;
		this.inAppActiveDauMV = inAppActiveDauMV;
		this.inAppCustomAudienceMV = inAppCustomAudienceMV;
		this.inAppPurchaseMV = inAppPurchaseMV;
		this.inAppEventMV = inAppEventMV;
	}

	/**
	 * @return the installMV
	 */
	public String getInstallMV() {
		return installMV;
	}

	/**
	 * @param installMV
	 *            the installMV to set
	 */
	public void setInstallMV(String installMV) {
		this.installMV = installMV;
	}

	/**
	 * @return the inAppActiveDauMV
	 */
	public String getInAppActiveDauMV() {
		return inAppActiveDauMV;
	}

	/**
	 * @param inAppActiveDauMV
	 *            the inAppActiveDauMV to set
	 */
	public void setInAppDauMV(String inAppActiveDauMV) {
		this.inAppActiveDauMV = inAppActiveDauMV;
	}

	/**
	 * @return the inAppCustomAudienceMV
	 */
	public String getInAppCustomAudienceMV() {
		return inAppCustomAudienceMV;
	}

	/**
	 * @param inAppCustomAudienceMV
	 *            the inAppCustomAudienceMV to set
	 */
	public void setInAppCustomAudienceMV(String inAppCustomAudienceMV) {
		this.inAppCustomAudienceMV = inAppCustomAudienceMV;
	}

	/**
	 * @return the inAppPurchaseMV
	 */
	public String getInAppPurchaseMV() {
		return inAppPurchaseMV;
	}

	/**
	 * @param inAppPurchaseMV
	 *            the inAppPurchaseMV to set
	 */
	public void setInAppPurchaseMV(String inAppPurchaseMV) {
		this.inAppPurchaseMV = inAppPurchaseMV;
	}

	/**
	 * @return the inAppEventMV
	 */
	public String getInAppEventMV() {
		return inAppEventMV;
	}

	/**
	 * @param inAppEventMV
	 *            the inAppEventMV to set
	 */
	public void setInAppEventMV(String inAppEventMV) {
		this.inAppEventMV = inAppEventMV;
	}

}
